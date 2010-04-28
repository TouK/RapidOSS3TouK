package com.ifountain.rcmdb.aol.connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.ifountain.rcmdb.aol.*
import com.ifountain.rcmdb.aol.security.SecureSession
import net.kano.joscar.ByteBlock
import net.kano.joscar.flapcmd.SnacCommand
import net.kano.joscar.snac.SnacRequest
import net.kano.joscar.snac.SnacRequestListener
import net.kano.joscar.snaccmd.icbm.ParamInfoRequest
import net.kano.joscar.snaccmd.icbm.SendImIcbm
import org.apache.log4j.Logger
import javax.net.SocketFactory

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 9, 2009
* Time: 4:37:14 PM
*/
class AolConnectionImpl extends BaseConnection implements IAolConnection {
    Logger logger = Logger.getLogger(AolConnectionImpl.class);
    public static final String HOST = "Host"
    public static final String PORT = "Port"
    public static final String USERNAME = "Username"
    public static final String PASSWORD = "Password"

    private String host;
    private Long port;
    private String username;
    private String password;

    private SecureSession secureSession;
    private LoginConn loginConn;
    private BosFlapConn bosConn;
    private Object messageLock = new Object();
    private Object connectionLock = new Object();
    private Object loginLock = new Object();
    private Object noopLock = new Object();
    private SnacManager snacMgr = new SnacManager();
    private int loginResult = NOT_RECEIVED;
    private int messageResult = NOT_RECEIVED;
    private int noopResult = NOT_RECEIVED;
    private boolean disconnectDetected = false;
    private Closure textReceivedCallback;
    private String disconnectDetectReason;

    private static final int NOT_RECEIVED = 0;
    private static final int FAILED = 1;
    private static final int SUCCESSFUL = 2;
    private String messageError;
    public void init(ConnectionParam param) {
        super.init(param);
        host = checkParam(HOST);
        username = checkParam(USERNAME)
        password = checkParam(PASSWORD)
        port = checkParam(PORT);
        secureSession = SecureSession.getInstance(logger);
    }

    protected void connect() {
        synchronized (connectionLock)
        {
            SocketFactory sFactory = new AolSocketFactory(getTimeout())
            loginConn = new LoginConn(host, port.intValue(), this);
            loginConn.setSocketFactory(sFactory)
            logger.debug(getLogPrefix() + "Trying to login to host ${host} with username ${username}")
            loginConn.connect();
            loginResult = NOT_RECEIVED
            synchronized (loginLock) {
                loginLock.wait(getTimeout())
            }
            if (loginResult == FAILED) {
                throw new ConnectionException("Could not connect to the host ${host}.");
            }
            else if (loginResult == NOT_RECEIVED) {
                if (disconnectDetectReason != null) {
                    throw new ConnectionException(disconnectDetectReason);
                }
                else {
                    throw new ConnectionException("Login request to the host ${host} timed out.");
                }

            }
            logger.info(getLogPrefix() + "Successfully logged in");
            disconnectDetected = false;
            disconnectDetectReason = null;
        }
    }

    protected void disconnect() {
        logger.debug(getLogPrefix() + "Disconnecting from host ${host}")
        synchronized (connectionLock)
        {
            try {
                loginConn.disconnect();
            } catch (Throwable e) {
            }
            try {
                bosConn.disconnect();
            } catch (Throwable e) {
            }
        }
    }

    public boolean checkConnection() {
        synchronized (connectionLock) {
            if (isConnected() && !disconnectDetected) {
                noopResult = NOT_RECEIVED
                request(new ParamInfoRequest())
                synchronized (noopLock) {
                    noopLock.wait(getTimeout())
                }
                return noopResult == SUCCESSFUL;
            }
        }
        return false;
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    public void registerSnacFamilies(BasicConn conn) {
        snacMgr.register(conn)
    }

    public void unRegisterSnacFamilies(BasicConn conn) {
        snacMgr.unregister(conn)
    }

    public SecureSession getSecureSession() {
        return secureSession;
    }

    private SnacRequest request(SnacCommand cmd) throws InterruptedException {
        return request(cmd, null);
    }

    private SnacRequest request(SnacCommand cmd, SnacRequestListener listener) throws InterruptedException {
        SnacRequest req = new SnacRequest(cmd, listener);
        handleRequest(req);
        return req;
    }

    public void sendIM(String target, String text) throws InterruptedException {
        request(new SendImIcbm(target, text));
    }

    public void sendMessage(String target, String text) throws Exception {
        synchronized (connectionLock) {
            if (isConnected()) {
                messageError = null;
                messageResult = NOT_RECEIVED;
                logger.debug(getLogPrefix() + "Sending message <${text}> to target ${target}")
                sendIM(target, text);
                synchronized (messageLock) {
                    messageLock.wait(getTimeout());
                }
                if (messageResult == NOT_RECEIVED) {
                    throw new ConnectException("Sending message to target ${target} timed out.");
                }
                else if (messageResult == FAILED) {
                    throw new Exception(messageError);
                }
                logger.info(getLogPrefix() + "Message successfully sent to  target ${target}")
            }
            else {
                throw new ConnectionException("Sending message to ${target} is failed, because there is no established connection.")
            }

        }
    }

    public void messageRecieved(String target, String text) {
        if (textReceivedCallback) {
            textReceivedCallback(target, text);
        }
    }

    public void clientReady() {
        loginResult = SUCCESSFUL;
        synchronized (loginLock) {
            loginLock.notifyAll();
        }
    }

    public void handleRequest(SnacRequest request) {
        int family = request.getCommand().getFamily();
        while (true)
        {
            if (snacMgr.getConn(family) == null)
            {
                Thread.sleep(100);
            }
            else
            {
                BasicConn conn = snacMgr.getConn(family);
                conn.sendRequest(request);
                break;
            }
        }
    }

    public void disconnectDetected(String reason) {
        disconnectDetected = true;
        disconnectDetectReason = reason;
        synchronized (loginLock) {
            loginLock.notifyAll();
        }
        synchronized (noopLock) {
            noopLock.notifyAll();
        }
        synchronized (messageLock) {
            messageLock.notifyAll();
        }
    }

    public void messageSentSuccessfully() {
        messageResult = SUCCESSFUL;
        synchronized (messageLock) {
            messageLock.notifyAll()
        }
    }

    public void messageFailed(String reason) {
        messageError = reason
        messageResult = FAILED;
        synchronized (messageLock) {
            messageLock.notifyAll()
        }
    }

    public String getScreenname() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setScreenname(String screenname) {
        username = screenname;
    }

    public void startBosConn(String server, int port, ByteBlock cookie) {
        bosConn = new BosFlapConn(server, port, this, cookie);
        SocketFactory sFactory = new AolSocketFactory(getTimeout())
        bosConn.setSocketFactory(sFactory)
        bosConn.connect();
    }

    public void serverAlive() {
        noopResult = SUCCESSFUL;
        synchronized (noopLock) {
            noopLock.notifyAll();
        }
    }

    public LoginConn getLoginConn() {
        return loginConn;
    }

    public BosFlapConn getBosConn() {
        return bosConn;
    }

    public void setTextReceivedCallback(Closure c) {
        textReceivedCallback = c;
    }

    private String getLogPrefix() {
        return "[AolConnection]: "
    }

}