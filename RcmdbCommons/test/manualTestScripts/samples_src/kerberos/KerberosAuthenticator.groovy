package kerberos

import javax.security.auth.login.LoginContext
import javax.security.auth.login.Configuration

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 2, 2009
* Time: 3:00:52 PM
* To change this template use File | Settings | File Templates.
*/
class KerberosAuthenticator {

    def configurationName="KerberosAuthenticator";

    public KerberosAuthenticator(kerberosRealm,kdcAddress,confFile)
    {
        System.setProperty("java.security.krb5.realm", kerberosRealm);
        System.setProperty("java.security.krb5.kdc", kdcAddress);
        System.setProperty("java.security.auth.login.config", confFile);
    }

    public boolean authenticate(username,password)
    {
          println Configuration.getConfiguration().class
          println Configuration.getConfiguration()
          println Configuration.getConfiguration().getAppConfigurationEntry(configurationName)

          println "in auth"
          def callbackHandler=new KerberosCallbackHandler(username,password);
          def loginContext=new LoginContext(configurationName, callbackHandler);
          loginContext.login();
          println "**********************************************************************"
          println loginContext.getSubject().getPrincipals();
          //println "**********************************************************************"
          //println loginContext.getSubject().getPrivateCredentials();
          println "**********************************************************************"
          println "login done"
          return false;
    }



}