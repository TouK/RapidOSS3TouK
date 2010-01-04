package auth;

import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
import org.jsecurity.authc.AccountException


/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 4:04:14 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserLdapAuthenticator {
    def logger=application.RapidApplication.getLogger();
    static def authLogPrefix="User Authentication (Ldap) : ";

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



        //do ldap authentication
        def ldapInformation = user.retrieveLdapInformation();
        if (ldapInformation == null)
        {
            getLogger().warn(authLogPrefix+"Ldap Information could not be found for '${username}'");
            throw new UnknownAccountException("Ldap Information could not be found for '${username}'");
        }
        authenticateWithLdap(ldapInformation.ldapConnection,ldapInformation.userdn,password,username);

        getLogger().info(authLogPrefix+"Authentication successfully done for user '${username}' ");

        return user;
    }

    private void authenticateWithLdap(ldapConnection,String ldapUserdn,String ldapPassword,String username)
    {
        if (ldapConnection == null)
        {
            getLogger().warn(authLogPrefix+"LdapInformation is not bound with an LdapConnection for user '${username}'");
            throw new UnknownAccountException("LdapInformation is not bound with an LdapConnection for user '${username}'");
        }

        getLogger().info(authLogPrefix+"Authenticating User '${username}' against Ldap");
        if (!ldapConnection.checkAuthentication(ldapUserdn, ldapPassword))
        {
            getLogger().warn(authLogPrefix+"Ldap Authentication failed for user '${username}'");
            throw new IncorrectCredentialsException("Invalid Ldap password for user '${username}'");
        }
    }
}