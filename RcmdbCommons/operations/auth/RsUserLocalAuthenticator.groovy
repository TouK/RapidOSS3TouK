package auth;

import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
import org.jsecurity.authc.AccountException


/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 4:11:24 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserLocalAuthenticator {
    def logger=application.RapidApplication.getLogger();
    static def authLogPrefix="User Authentication (Local) : ";

    public RsUser authenticateUser(params)
    {
        String username=params.login;
        String password=params.password;

        getLogger().info(authLogPrefix+"Authenticating User '${username}'");

        if(username==null)
        {
            throw new AccountException('Null usernames are not allowed.')
        }

        // Get the user with the given username. If the user is not found than exception is thrown
        def user = RsUser.get(username:username)
        if (!user) {
            getLogger().warn(authLogPrefix+"No account found for user '${username}'");
            throw new UnknownAccountException("No account found for user ${username}");
        }

        username = user.username;
        getLogger().info(authLogPrefix+"Found user '${user.username}' in Repository");


        // Now check the user's password against the hashed value stored in the database.
        if (!user.isPasswordSame(password)) {
            getLogger().warn(authLogPrefix+"Invalid password for user '${username}'");
            throw new IncorrectCredentialsException("Invalid password for user '${username}'");
        }
        getLogger().info(authLogPrefix+"Authentication successfully done for user '${username}' ");

        return user;
    }

}