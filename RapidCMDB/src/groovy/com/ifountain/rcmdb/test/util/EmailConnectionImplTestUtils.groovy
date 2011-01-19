package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 24, 2008
 * Time: 4:07:40 PM
 * To change this template use File | Settings | File Templates.
 */

import com.ifountain.comp.test.util.CommonTestUtils
import javax.mail.Message
import javax.mail.Session
import javax.mail.Store
import javax.mail.Folder
import javax.mail.MessagingException
import datasource.SendEmailAction
import connection.EmailConnectionImpl
import javax.mail.Flags.Flag

class EmailConnectionImplTestUtils {
    def final static popParamMap = ["Host": "Pop.Host", "Port": "Pop.Port", "Store": "Pop.Store"]
    def final static smtpParamMap = ["${EmailConnectionImpl.SMTPHOST}": "Smtp.SmtpHost", "${EmailConnectionImpl.SMTPPORT}": "Smtp.SmtpPort", "${EmailConnectionImpl.PROTOCOL}": "Smtp.Protocol"]
    public static Map getPopConnectionParams(String userId) {
        return getPopConnectionParams(userId, "Local");
    }
    public static Map getPopConnectionParams(String userId, String serverId) {
        def userParams = getEmailUserAccountInfo(userId);
        def params = [:]
        popParamMap.each {key, propKey ->
            params[key] = CommonTestUtils.getTestProperty("${serverId}.${propKey}");
        }
        params[EmailConnectionImpl.USERNAME] = userParams[EmailConnectionImpl.USERNAME];
        params[EmailConnectionImpl.PASSWORD] = userParams[EmailConnectionImpl.PASSWORD];
        return params;
    }

    public static Map getSmtpConnectionParams(String userId) {
        return getSmtpConnectionParams(userId, "Local");
    }
    public static Map getSmtpConnectionParams(String userId, String serverId) {
        def params = [:]
        def userParams = getEmailUserAccountInfo(userId);
        smtpParamMap.each {String key, String propKey ->
            params[key] = CommonTestUtils.getTestProperty("${serverId}.${propKey}");
        }
        params[EmailConnectionImpl.USERNAME] = userParams[EmailConnectionImpl.USERNAME];
        params[EmailConnectionImpl.PASSWORD] = userParams[EmailConnectionImpl.PASSWORD];
        return params;
    }

    public static Map getEmailUserAccountInfo(String userIdentifier) {
        def paramMap = ["${EmailConnectionImpl.USERNAME}": "Email.${userIdentifier}.Username", "${EmailConnectionImpl.PASSWORD}": "Email.${userIdentifier}.Password"]
        def params = [:]
        paramMap.each {key, propKey ->
            params[key] = CommonTestUtils.getTestProperty(propKey);
        }
        return params;
    }
    public static Map getSendEmailParams(String from, String to, String subject, String body) {
        def params = [:]
        params[SendEmailAction.FROM_PARAM_NAME] = from;
        params[SendEmailAction.TO_PARAM_NAME] = to;
        params[SendEmailAction.SUBJECT_PARAM_NAME] = subject;
        params[SendEmailAction.BODY_PARAM_NAME] = body;
        return params;
    }
    public static Map getSendEmailParamsForCC(String from, String to, String subject, String body) {
        def params = [:]
        params[SendEmailAction.FROM_PARAM_NAME] = from;
        params[SendEmailAction.CC_PARAM_NAME] = to;
        params[SendEmailAction.SUBJECT_PARAM_NAME] = subject;
        params[SendEmailAction.BODY_PARAM_NAME] = body;
        return params;
    }

    public static void clearMessages(String userId) throws MessagingException
    {
        def receiveParams = EmailConnectionImplTestUtils.getPopConnectionParams(userId);
        EmailConnectionImplTestUtils.clearMessages(receiveParams.Host, Integer.parseInt(receiveParams.Port), receiveParams[EmailConnectionImpl.USERNAME], receiveParams[EmailConnectionImpl.PASSWORD], receiveParams.Store);
    }
    public static void clearMessages(String popHost, int popPort, String rcvUsername, String rcvPassword, String storeName) throws MessagingException
    {
        getMessages(popHost, popPort, rcvUsername, rcvPassword, storeName, {messages->
            messages.each{Message m->
                m.setFlag (Flag.SEEN, true)
                m.setFlag (Flag.DELETED, true)
            }
        });
    }

    public static List getMessages(String userId) throws MessagingException
    {
        return getMessages(userId, "Local");
    }
    public static int getMessageCount(String userId) throws MessagingException
    {
        return getMessageCount(userId, "Local")
    }
    public static int getMessageCount(String userId, String serverId) throws MessagingException
    {
        def receiveParams = EmailConnectionImplTestUtils.getPopConnectionParams(userId, serverId);
        return EmailConnectionImplTestUtils.getMessageCount(receiveParams.Host, Integer.parseInt(receiveParams.Port), receiveParams[EmailConnectionImpl.USERNAME], receiveParams[EmailConnectionImpl.PASSWORD], receiveParams.Store);    
    }
    public static int getMessageCount(String popHost, int popPort, String rcvUsername, String rcvPassword, String storeName) throws MessagingException
    {
        def messageCount = 0;
        getMessages(popHost, popPort, rcvUsername, rcvPassword, storeName, {messages->
            messageCount = messages.size();
        });
        return messageCount;
    }
    public static List getMessages(String userId, String serverId) throws MessagingException
    {
        def receiveParams = EmailConnectionImplTestUtils.getPopConnectionParams(userId, serverId);
        return EmailConnectionImplTestUtils.getMessages(receiveParams.Host, Integer.parseInt(receiveParams.Port), receiveParams[EmailConnectionImpl.USERNAME], receiveParams[EmailConnectionImpl.PASSWORD], receiveParams.Store);
    }
    public static void getMessages(String userId, String serverId, Closure c) throws MessagingException
    {
        def receiveParams = EmailConnectionImplTestUtils.getPopConnectionParams(userId, serverId);
        EmailConnectionImplTestUtils.getMessages(receiveParams.Host, Integer.parseInt(receiveParams.Port), receiveParams[EmailConnectionImpl.USERNAME], receiveParams[EmailConnectionImpl.PASSWORD], receiveParams.Store, c);
    }
    public static List getMessages(String popHost, int popPort, String rcvUsername, String rcvPassword, String storeName) throws MessagingException
    {
        def messageList = [];
        def messagesClosure = {messages->
            m.getContent()
            m.getSubject()
            m.getFrom();
            messageList.add(m);
        };
        getMessages (popHost, popPort, rcvUsername, rcvPassword, storeName, messagesClosure);
        return messageList;
    }
    public static void getMessages(String popHost, int popPort, String rcvUsername, String rcvPassword, String storeName, Closure c)
    {
        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore(storeName);
        store.connect(popHost, popPort, rcvUsername, rcvPassword);
        Folder folder = store.getFolder("Inbox");
        folder.open(Folder.READ_WRITE);
        def prevMessages = Arrays.asList(folder.getMessages());
        try{
            c(prevMessages)
        }finally{
            folder.close(true);
            store.close();
        }
    }
}