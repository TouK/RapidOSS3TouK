package auth;

import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
import org.jsecurity.authc.AccountException
import application.Cache;
import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.comp.utils.CaseInsensitiveMap

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 4:08:55 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserTokenAuthenticator {
    def logger=application.RsApplication.getLogger();
    static def authLogPrefix="User Authentication (Token) : ";

    static def CACHE_ENTRY_KEY="RsUserLoginTokens";
    private static Object CACHE_ENTRY_LOCK = new Object();

    public RsUser authenticateUser(params)
    {
        def loginToken=params.loginToken;
        if(!loginToken)
        {
            getLogger().warn(authLogPrefix+"LoginToken should be specified");
            throw new AccountException("LoginToken should be specified");
        }

        def username=null;

        //access to cache entry is syncronized and userEntry is duplicated
        synchronized (CACHE_ENTRY_LOCK)
        {
            def entry=getCacheEntry();
            def userEntry=entry.tokens[params.loginToken];

            if(userEntry==null)
            {
                getLogger().warn(authLogPrefix+"No userEntry found for loginToken: ${loginToken}");
                throw new IncorrectCredentialsException("No userEntry found for loginToken")
            }
            else
            {
                username=userEntry.username;
                if(userEntry.expireAt<System.currentTimeMillis())
                {

                    entry.users.remove(username);
                    entry.tokens.remove(loginToken);

                    getLogger().warn(authLogPrefix+"LoginToken expired for user ${username} , loginToken : ${loginToken}");
                    throw new IncorrectCredentialsException("LoginToken expired for user ${username}");
                }
            }
        }
        
        //above syncronized code succeeds only if token is valid
        //User retrieval does not need to be syncronized it is not related with the cache entry
        def user=RsUser.get(username:username);
        if(user==null)
        {
            getLogger().warn(authLogPrefix+"User ${username} does not exist");
            throw new UnknownAccountException("User ${username} does not exist");
        }
        return user;
    }

    //expireAfter in seconds
    public def generateTokenForUser(String username,Long expireAfter)
    {
        if(RsUser.countHits("username:${username.exactQuery()}")==0)
        {
            getLogger().warn(authLogPrefix+"User ${username} does not exist");
            throw new UnknownAccountException("User ${username} does not exist");
        }
        synchronized (CACHE_ENTRY_LOCK)
        {
            def entry=getCacheEntry();
            def userEntry=entry.users[username];
            if(userEntry==null)
            {
                userEntry=[:];
            }
            else
            {
                entry.tokens.remove(userEntry.loginToken);
            }

            //entry have username, loginToken and expireAt information
            //the entry have reference in both entry.users and entry.token
            userEntry.username=username;
            userEntry.loginToken=createToken(username);
            userEntry.expireAt=System.currentTimeMillis()+(expireAfter*1000);

            entry.users[userEntry.username]=userEntry;
            entry.tokens[userEntry.loginToken]=userEntry;

            return userEntry.loginToken;
        }
    }
    private String createToken(String username)
    {
           def newToken="${username}${System.nanoTime()}".toString();
           return new Sha1Hash(newToken).toHex();
    }
    public def getCacheEntry()
    {
        synchronized (CACHE_ENTRY_LOCK)
        {
            def entry=Cache.retrieve(CACHE_ENTRY_KEY);
            if(entry==null)
            {
                entry=[:];
                entry.users=new CaseInsensitiveMap();                
                entry.tokens=[:];
                Cache.store(CACHE_ENTRY_KEY,entry);
            }
            return entry;
        }
    }
    public def clearCacheEntry()
    {
        synchronized (CACHE_ENTRY_LOCK)
        {
            def entry=getCacheEntry();
            entry.users.clear();
            entry.tokens.clear();
        }
    }
}

