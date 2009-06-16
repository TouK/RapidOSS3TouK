package com.ifountain.rcmdb.sms.connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import org.apache.log4j.Logger
import ie.omk.smpp.net.TcpLink
import ie.omk.smpp.Connection
import ie.omk.smpp.event.ConnectionObserver
import ie.omk.smpp.message.SMPPPacket
import ie.omk.smpp.event.SMPPEvent
import com.ifountain.core.connection.exception.ConnectionException
import ie.omk.smpp.message.SubmitSM
import ie.omk.smpp.Address
import ie.omk.smpp.util.GSMConstants

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 5, 2009
* Time: 2:23:57 PM
*/
class SmsConnectionImpl extends BaseConnection implements ConnectionObserver {
    Logger logger = Logger.getLogger(SmsConnectionImpl.class);
    public static final String HOST = "Host"
    public static final String PORT = "Port"
    public static final String USERNAME = "Username"
    public static final String PASSWORD = "Password"

    private String host;
    private Long port;
    private String username;
    private String password;
    private Closure textReceivedCallback;
    private Object connectionLock = new Object();
    private Object bindLock = new Object();
    private Object enquireLock = new Object();
    private Connection conn;
    private TcpLink smscLink;
    private SubmitSM lastSentPacket;
    private Exception messageSendException;
    private int bindResult = NOT_RECEIVED;
    private int unbindResult = NOT_RECEIVED;
    private int enquireResult = NOT_RECEIVED;
    private int sendMessageResult = NOT_RECEIVED;
    private static final int NOT_RECEIVED = 0;
    private static final int FAILED = 1;
    private static final int SUCCESSFUL = 2;
    public void init(ConnectionParam param) {
        super.init(param);
        host = checkParam(HOST);
        username = checkParam(USERNAME)
        password = checkParam(PASSWORD)
        port = checkParam(PORT);
    }

    private Object checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if (!params.getOtherParams().containsKey(parameterName)) {
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return params.getOtherParams().get(parameterName);
    }
    protected void connect() {
        synchronized (connectionLock) {
            logger.debug(getLogPrefix() + "Connecting to host ${host}.")
            InetAddress smscAddr = InetAddress.getByName(host);
            smscLink = new TcpLink(smscAddr, port.intValue());
            smscLink.setTimeout(getTimeout())
            smscLink.open();
            conn = new Connection(smscLink, true);
            conn.addObserver(this);
            logger.info(getLogPrefix() + "Connected to server.");
            logger.info(getLogPrefix() + "Binding to server.");
            bindResult = NOT_RECEIVED;
            conn.bind(Connection.TRANSCEIVER, username, password, null);
        }
        synchronized (bindLock) {
            bindLock.wait(getTimeout())
        }
        if (bindResult == FAILED) {
            throw new ConnectionException("Could not bind to the host ${host}.");
        }
        else if (bindResult == NOT_RECEIVED) {
            throw new ConnectionException("Bind request to the host ${host} timed out.");
        }
    }

    protected void disconnect() {
        if (conn != null) {
            synchronized (connectionLock) {
                unbindResult = NOT_RECEIVED;
                try {
                    conn.unbind();
                }
                catch (e) {}
                synchronized (bindLock) {
                    bindLock.wait(getTimeout());
                }
                if (unbindResult == NOT_RECEIVED) {
                    logger.warn(getLogPrefix() + "Unbind request to server timed out.")
                }
                conn.removeObserver(this);
            }
        }
    }

    public boolean checkConnection() {
        synchronized (connectionLock) {
            if (isConnected()) {
                enquireResult = NOT_RECEIVED
                conn.enquireLink();
                synchronized (enquireLock) {
                    enquireLock.wait(getTimeout());
                }
                return enquireResult == SUCCESSFUL;
            }
        }
        return false;
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    public void sendMessage(String target, String message) {
        synchronized (connectionLock) {
            if (isConnected()) {
                lastSentPacket = (SubmitSM) conn.newInstance(SMPPPacket.SUBMIT_SM);
                sendMessageResult = NOT_RECEIVED;
                synchronized (lastSentPacket) {
                    lastSentPacket.setDestination(new Address(GSMConstants.GSM_NPI_UNKNOWN, GSMConstants.GSM_TON_UNKNOWN, target));
                    lastSentPacket.setMessageText(message);
                    conn.sendRequest(lastSentPacket);
                    logger.debug(getLogPrefix() + "Packet with message [" + message + "] processed and sent [To:" + lastSentPacket.getDestination().getAddress() + " With SeqId:" + lastSentPacket.getSequenceNum() + "]");
                    lastSentPacket.wait(getTimeout());
                }
                if (sendMessageResult == NOT_RECEIVED) {
                    throw new ConnectException("Sending message to target ${target} timed out.");
                }
                else if (sendMessageResult == FAILED) {
                    throw new ConnectException("Sending message to target ${target} timed out.");
                }
            }
            else {
                throw new ConnectionException("Sending message to ${target} is failed, because there is no established connection.")
            }
        }
    }

    public void packetReceived(Connection connection, SMPPPacket smppPacket) {
        logger.debug(getLogPrefix() + "Packet received with commandid " + smppPacket.getCommandId());
        switch (smppPacket.getCommandId()) {
            case SMPPPacket.DELIVER_SM:
                String sourceAddress = smppPacket.getSource().getAddress();
                String messageText = smppPacket.getMessageText();
                if (textReceivedCallback) {
                    textReceivedCallback(sourceAddress, messageText);
                }
                break;
            case SMPPPacket.SUBMIT_SM_RESP:
                if (smppPacket.getCommandStatus() != 0) {
                    messageSendException = new Exception("Message was not submitted. Error code: " + smppPacket.getCommandStatus())
                    sendMessageResult = FAILED;
                }
                else {
                    sendMessageResult = SUCCESSFUL;
                    logger.debug(getLogPrefix() + "Message successfully sent [ To:" + lastSentPacket.getDestination().getAddress() + " With SeqId:" + smppPacket.getSequenceNum() + "]");
                    synchronized (lastSentPacket)
                    {
                        lastSentPacket.notifyAll();
                    }
                }
                synchronized (lastSentPacket) {
                    lastSentPacket.notifyAll();
                }
                break;
            case SMPPPacket.UNBIND_RESP:
                if (smppPacket.getCommandStatus() != 0) {
                    unbindResult = FAILED;
                    logger.info(getLogPrefix() + "Could not unbind from server.");
                } else {
                    unbindResult = SUCCESSFUL;
                    try
                    {
                        conn.closeLink();
                    }
                    catch (IOException e) {}
                    logger.info(getLogPrefix() + "Unbind from server successfully.");
                }
                synchronized (bindLock) {
                    bindLock.notifyAll()
                }
                break;
            case SMPPPacket.BIND_TRANSCEIVER_RESP:
                if (smppPacket.getCommandStatus() != 0) {
                    bindResult = FAILED;
                    logger.info(getLogPrefix() + "Could not bind to server. Reason: ${smppPacket.getMessageText()}");

                } else {
                    bindResult = SUCCESSFUL;
                    logger.info(getLogPrefix() + "Bound to server successfully.");

                }
                synchronized (bindLock) {
                    bindLock.notifyAll()
                }
                break;
            case SMPPPacket.ENQUIRE_LINK_RESP:
                if (smppPacket.getCommandStatus() != 0) {
                    enquireResult = FAILED
                }
                else {
                    enquireResult = SUCCESSFUL;
                }
                synchronized (enquireLock) {
                    enquireLock.notifyAll()
                }
        }
    }

    public void update(Connection connection, SMPPEvent event) {}


    private String getLogPrefix() {
        return "[SmsConnection]: "
    }

    public void setTextReceivedCallback(Closure c) {
        textReceivedCallback = c;
    }

    public Connection getSmppConnection() {
        return conn;
    }

    public TcpLink getSmscLink() {
        return smscLink
    }

}