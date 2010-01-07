package com.ifountain.rcmdb.jabber.connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.ConnectionException
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import org.apache.log4j.Logger
import org.jivesoftware.smack.*
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.XMPPError

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jun 1, 2009
* Time: 4:14:56 PM
*/
class JabberConnectionImpl extends BaseConnection implements ConnectionListener, ChatManagerListener, MessageListener {
    Logger logger = Logger.getLogger(JabberConnectionImpl.class);
    public static final String HOST = "Host"
    public static final String PORT = "Port"
    public static final String USERNAME = "Username"
    public static final String PASSWORD = "Password"
    public static final String SERVICENAME = "ServiceName"

    private String host;
    private String serviceName;
    private String username;
    private String password;
    private Long port;

    private XMPPConnection connection;
    private Object connectionLock = new Object();
    private Hashtable chats = new Hashtable();
    private Closure textReceivedCallback;
    private boolean isConnectionActive = false;
    private boolean chatManagerCreated = false;

    public void init(ConnectionParam param) {
        super.init(param);
        host = checkParam(HOST);
        port = checkParam(PORT);
        serviceName = checkParam(SERVICENAME);
        username = checkParam(USERNAME)
        password = checkParam(PASSWORD)
    }

    private Object checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if (!params.getOtherParams().containsKey(parameterName)) {
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return params.getOtherParams().get(parameterName);
    }

    protected void connect() {
        ConnectionConfiguration conf = new ConnectionConfiguration(host, port.intValue(), serviceName);
        conf.setReconnectionAllowed(false);
        connection = new XMPPConnection(conf);
        synchronized (connectionLock) {
            connection.connect();
            connection.addConnectionListener(this)
            logger.info(getLogPrefix() + "Connected to host ${host}");
            logger.debug(getLogPrefix() + "Logging in with username ${username}")
            try{
                connection.login(username, password)
	            logger.info(getLogPrefix() + "Logged in.");
	            Presence presence = new Presence(Presence.Type.available);
	            connection.sendPacket(presence);
	            connection.getChatManager().addChatListener(this)
	            chatManagerCreated = true;
	            isConnectionActive = true;
            }
            catch(e){
            	disconnect();
            	throw e;
            }

        }

    }

    protected void disconnect() {
        synchronized (connectionLock) {
            if (connection != null)
            {
                logger.debug(getLogPrefix() + "Closing xmpp connection.")
                connection.removeConnectionListener(this)
                connection.disconnect();
                logger.info(getLogPrefix() + "Connection successfully closed.")
                if(chatManagerCreated){
                    connection.getChatManager().removeChatListener(this)    
                }
            }
            chats.each {String participant, Chat chat ->
                chat.removeMessageListener(this)
            }
            chats.clear();
            isConnectionActive = false;
        }

    }

    public boolean checkConnection() {
        return isConnectionActive;
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    public void sendImMessage(String to, String messageText) throws Exception {
        synchronized (connectionLock)
        {
            if (isConnected()) {
                Chat chat = getChat(to);
                logger.debug(getLogPrefix() + "Sending message ${messageText} to ${to}")
                chat.sendMessage(messageText);
            }
            else{
                throw new ConnectionException("Sending im to ${to} is failed, because there is no established connection.")
            }

        }
    }

    public void connectionClosed() {
        isConnectionActive = false;
    }

    public void connectionClosedOnError(Exception e) {
        logger.warn(getLogPrefix() + "Connection closed with error. Reason: ${e.getMessage()}")
        isConnectionActive = false;
    }

    public void reconnectingIn(int i) {}
    public void reconnectionSuccessful() {}
    public void reconnectionFailed(Exception e) {}
    public void processMessage(Chat chatOfTheMessage, Message chatMessage) {
        XMPPError error = chatMessage.getError();
        if (error == null)
        {
            String from = getUserIdFromSource(chatOfTheMessage.getParticipant());
            logger.debug(getLogPrefix() + "A message[" + chatMessage.getBody() + "] from " + from + " is received. Chat ID is: " + chatOfTheMessage.getThreadID());
            if (textReceivedCallback != null) {
                textReceivedCallback(from, chatMessage.getBody());
            }
        }
        else
        {
            logger.info(getLogPrefix() + "Dropping received message: " + chatMessage.getBody() + ". Error: " + error.toXML());
        }
    }
    public void chatCreated(Chat newChat, boolean createdLocally) {
        synchronized (connectionLock)
        {
            logger.debug(getLogPrefix() + "Chat received from ${newChat.getParticipant()}. Saving it for later usage.")
            chats.put(newChat.getParticipant(), newChat);
            newChat.addMessageListener(this);
        }
    }

    private Chat getChat(String to)
    {
        synchronized (connectionLock)
        {
            Chat existingChat = (Chat) chats.get(to);
            if (existingChat == null)
            {
                logger.debug(getLogPrefix() + "Chat with the user " + to + " does not exist. Creating new chat.");
                existingChat = connection.getChatManager().createChat(to, null);
                chats.put(to, existingChat);
            }
            else
            {
                logger.debug(getLogPrefix() + "Chat with the user " + to + " already exists. Returning existing one.");
            }
            return existingChat;
        }
    }

    public XMPPConnection getXmppConnection() {
        return connection;
    }

    private String getLogPrefix() {
        return "[JabberConnection]: "
    }

    public void setTextReceivedCallback(Closure c) {
        textReceivedCallback = c;
    }

    protected static String getUserIdFromSource(String source) {
        int indexOfSlash = source.lastIndexOf("/");
        if (indexOfSlash > -1)
        {
            return source.substring(0, indexOfSlash);
        }
        return source;
    }

}