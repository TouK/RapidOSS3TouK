package com.ifountain.rcmdb.sametime.connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import com.lotus.sametime.core.comparch.STSession
import com.lotus.sametime.community.CommunityService
import com.lotus.sametime.lookup.LookupService
import com.lotus.sametime.im.InstantMessagingService
import com.lotus.sametime.lookup.Resolver
import com.lotus.sametime.community.LoginListener
import com.lotus.sametime.community.LoginEvent
import com.lotus.sametime.core.constants.ImTypes
import org.apache.log4j.Logger
import com.lotus.sametime.im.ImServiceListener
import com.lotus.sametime.im.ImEvent
import com.lotus.sametime.lookup.ResolveListener
import com.lotus.sametime.lookup.ResolveEvent
import com.lotus.sametime.core.constants.STError
import com.lotus.sametime.im.Im
import com.lotus.sametime.core.types.STUser
import com.ifountain.core.connection.exception.ConnectionException
import com.lotus.sametime.im.ImListener
import com.lotus.sametime.core.constants.EncLevel

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 20, 2009
* Time: 3:13:34 PM
*/
class SametimeConnectionImpl extends BaseConnection implements LoginListener, ResolveListener, ImServiceListener, ImListener {
    Logger logger = Logger.getLogger(SametimeConnectionImpl.class);
    public static final String HOST = "Host"
    public static final String USERNAME = "Username"
    public static final String PASSWORD = "Password"
    public static final String COMMUNITY = "Community"
    private String host;
    private String username;
    private String password;
    private String community;
    private STSession stsession;
    private CommunityService commservice;
    private LookupService lookupService;
    private InstantMessagingService imService;
    private Resolver resolver;

    private Map<String, String> targets = new HashMap<String, String>(); //userId-target map
    private Map<String, String> userIds = new HashMap<String, String>(); //target-userId map
    private Map<String, Im> ims = new HashMap<String, Im>();
    private Map<String, STUser> resolvedUsers = new HashMap<String, STUser>();

    private static final int NOT_RECEIVED = 0;
    private static final int FAILED = 1;
    private static final int SUCCESSFUL = 2;
    private int resolveResult;
    private int imOpenResult;
    private int loginResult;
    private String targetToBeResolved;

    private Object connectionLock = new Object();
    private Object loginLock = new Object();
    private Object imLock = new Object();
    private Object userResolverLock = new Object();
    private Exception exceptionToBeThrown;

    private Closure textReceivedCallback;

    protected void connect() throws Exception {
        synchronized (connectionLock) {
            stsession = new STSession("RapidCMDB" + this);
            logger.info(getLogPrefix() + "Sametime session created.");
            stsession.loadSemanticComponents();
            logger.info(getLogPrefix() + "Sametime components loaded.");
            stsession.start();
            logger.info(getLogPrefix() + "Sametime session started.");
            login();
        }
    }

    private void login() throws InterruptedException {
        commservice = (CommunityService) stsession.getCompApi(CommunityService.COMP_NAME);
//        commservice.enableAutomaticReconnect(Integer.MAX_VALUE, 5000);
        commservice.addLoginListener(this);
        loginResult = NOT_RECEIVED;
        logger.debug(getLogPrefix() + "Trying to login to server: " + host + " with user: " + username);
        commservice.loginByPassword(host, username, password, community);
        synchronized (loginLock) {
            loginLock.wait(getTimeout());
        }
        if (loginResult == FAILED) {
            throw exceptionToBeThrown
        }
        else if (loginResult == NOT_RECEIVED) {
            throw new ConnectionException("Login request to the host ${host} timed out.");
        }

    }

    protected void disconnect() {
        synchronized (connectionLock)
        {
            logger.debug(getLogPrefix() + "Closing instant message sessions.");
            for (Iterator iter = ims.values().iterator(); iter.hasNext();) {
                Im im = (Im) iter.next();
                im.removeImListener(this);
                im.close(0);
            }
            logger.info(getLogPrefix() + "All instant message sessions closed.");
            ims.clear();
            resolvedUsers.clear();
            targets.clear();
            userIds.clear();
            if (imService != null) {
                imService.removeImServiceListener(this);
            }
            if (resolver != null) {
                resolver.removeResolveListener(this);
            }
            if (commservice != null) {
                commservice.logout();
                commservice.removeLoginListener(this);
                logger.info(getLogPrefix() + "Successfully logged out.");
            }
            if (stsession != null) {
                stsession.stop();
                logger.info(getLogPrefix() + "Sametime session stopped.");
                stsession.unloadSession();
                logger.info(getLogPrefix() + "Sametime session unloaded.");
            }
        }
    }

    public boolean checkConnection() {
        try {
            return resolveUser(username) == SUCCESSFUL
        }
        catch (e) {
            return false;
        }
    }

    public boolean isConnectionException(Throwable t) {
        return false;
    }

    public void init(ConnectionParam param) {
        super.init(param);
        host = checkParam(HOST);
        username = checkParam(USERNAME)
        password = checkParam(PASSWORD)
        community = params.getOtherParams().get(COMMUNITY);
    }

    public void loggedOut(LoginEvent event) {
        loginResult = FAILED;
        String errorMessage = getErrorMessage(event.getReason())
        logger.warn(getLogPrefix() + "Logged out. Reason: " + errorMessage);
        exceptionToBeThrown = new Exception(errorMessage);
        ims.clear();
        synchronized (loginLock) {
            loginLock.notifyAll();
        }
    }

    public void loggedIn(LoginEvent event) {
        logger.info(getLogPrefix() + "Successfully logged in.");
        loginResult = SUCCESSFUL;
        imService = (InstantMessagingService) stsession.getCompApi(InstantMessagingService.COMP_NAME);
        imService.registerImType(ImTypes.IM_TYPE_CHAT);
        imService.addImServiceListener(this);
        lookupService = (LookupService) stsession.getCompApi(LookupService.COMP_NAME);
        resolver = lookupService.createResolver(true, false, true, false);
        resolver.addResolveListener(this);
        synchronized (loginLock) {
            loginLock.notifyAll();
        }
    }

    protected synchronized int resolveUser(String target) throws InterruptedException
    {
        synchronized (connectionLock) {
            if (!isConnected()) {
                return NOT_RECEIVED
            }
        }
        resolveResult = NOT_RECEIVED;
        targetToBeResolved = target;
        logger.debug(getLogPrefix() + "Resolving user " + target);
        resolver.resolve(target);
        synchronized (userResolverLock) {
            userResolverLock.wait(getTimeout());
        }
        return resolveResult;
    }

    public void resolveConflict(ResolveEvent event) {
        logger.debug(getLogPrefix() + "Resolve conflict.");
        resolveResult = FAILED;
        synchronized (userResolverLock) {
            userResolverLock.notifyAll();
        }
    }

    public void resolveFailed(ResolveEvent event) {
        logger.debug(getLogPrefix() + "Resolve failed.");
        resolveResult = FAILED;
        synchronized (userResolverLock) {
            userResolverLock.notifyAll();
        }
    }

    public void resolved(ResolveEvent event) {
        resolveResult = SUCCESSFUL;
        STUser user = (STUser) event.getResolved();
        String userId = user.getId().getId();
        targets.put(userId, targetToBeResolved);
        userIds.put(targetToBeResolved, userId);
        resolvedUsers.put(userId, user);
        logger.debug(getLogPrefix() + "Sametime user <" + targetToBeResolved + "> is resolved.");
        synchronized (userResolverLock) {
            userResolverLock.notifyAll();
        }
    }

    public void sendImMessage(String to, String messageText) throws Exception {
        String userId = userIds.get(to);
        Im im;
        if (userId == null) {
            def resResult = resolveUser(to);
            switch (resResult) {
                case NOT_RECEIVED: throw new ConnectionException("Sametime user <" + to + "> resolve request timed out.");
                case FAILED: throw new Exception("Sametime user <" + to + "> could not be resolved.");
                case SUCCESSFUL: break;
            }
            userId = userIds.get(to);
            im = getImFromResolvedUser(userId, to);
        }
        else {
            im = ims.get(userId);
            if (im == null) {
                im = getImFromResolvedUser(userId, to);
            }
        }
        im.sendText(false, messageText);
    }

    private void openIm(Im im, String to) throws Exception
    {
        logger.debug(getLogPrefix() + "Opening im.");
        imOpenResult = NOT_RECEIVED;
        im.open();
        synchronized (imLock) {
            imLock.wait(getTimeout());
        }
        if (imOpenResult == NOT_RECEIVED)
        {
            throw new ConnectionException("Sametime instant message session request with user <" + to + "> timed out.");
        }
        if (imOpenResult == FAILED)
        {
            throw new Exception("Sametime instant message session with user <" + to + "> cannot be created.");
        }
    }

    private Im getImFromResolvedUser(String userId, String target) throws Exception {
        STUser user = resolvedUsers.get(userId);
        logger.debug(getLogPrefix() + "Creating instant message session with: " + user.getName());
        Im im = imService.createIm(user, EncLevel.ENC_LEVEL_NONE, ImTypes.IM_TYPE_CHAT);
        im.addImListener(this);
        openIm(im, target);
        im = ims.get(userId);
        return im;
    }


    public void imReceived(ImEvent event) {
        Im im = event.getIm();
        STUser user = im.getPartner();
        String userId = user.getId().getId();
        if (targets.containsKey(userId)) {
            logger.debug(getLogPrefix() + "Im received from " + targets.get(userId));
            if (!resolvedUsers.containsKey(userId)) {
                resolvedUsers.put(userId, user);
            }
        }
        else {
            logger.debug(getLogPrefix() + "Im received from " + userId);
        }
        if (!ims.containsKey(userId)) {
            im.addImListener(this);
            ims.put(userId, im);
        }
    }

    public void dataReceived(ImEvent event) {
    }

    public void textReceived(ImEvent event) {
        if (textReceivedCallback != null) {
            Im im = event.getIm();
            STUser user = im.getPartner();
            String userId = user.getId().getId();
            String target;
            if (targets.containsKey(userId)) {
                target = targets.get(userId);
            }
            else {
                target = userId;
            }
            String eventText = event.getText();
            textReceivedCallback(target, eventText);
        }
    }

    public void imClosed(ImEvent event) {
        Im im = event.getIm();
        String userId = im.getPartner().getId().getId();
        im.removeImListener(this);
        ims.remove(userId);
        logger.info(getLogPrefix() + "IM closed");
    }

    public void imOpened(ImEvent event) {
        imOpenResult = SUCCESSFUL;
        Im im = event.getIm();
        String userId = im.getPartner().getId().getId();
        ims.put(userId, im);
        logger.debug(getLogPrefix() + "Sametime instant message session with <" + targets.get(userId) + "> created.");
        synchronized (imLock) {
            imLock.notifyAll();
        }
    }

    public void openImFailed(ImEvent event) {
        logger.debug(getLogPrefix() + "Instant message session creation failed.");
        imOpenResult = FAILED;
        synchronized (imLock) {
            imLock.notifyAll();
        }
    }


    private String getLogPrefix() {
        return "[SametimeConnection]: "
    }
    public String getErrorMessage(int reason) {
        if (reason == STError.ST_CONNECT_HOST_UNREACHABLE) {
            return "Host " + host + " is unreachable";
        }
        else if (reason == STError.ST_CONNECT_BAD_LOGIN) {
            return "Bad login for user " + username + ".";
        }
        else {
            return STError.getMessageString(reason);
        }
    }
    public CommunityService getCommservice() {
        return commservice;
    }

    public InstantMessagingService getImService() {
        return imService;
    }


    public LookupService getLookupService() {
        return lookupService;
    }

    public STSession getStsession() {
        return stsession;
    }

    protected Map getTargets() {
        return targets;
    }
    protected Map getUserIds() {
        return userIds;
    }

    public Map<String, STUser> getResolvedUsers() {
        return resolvedUsers;
    }

    public setTextReceivedCallback(Closure c) {
        textReceivedCallback = c;
    }
}