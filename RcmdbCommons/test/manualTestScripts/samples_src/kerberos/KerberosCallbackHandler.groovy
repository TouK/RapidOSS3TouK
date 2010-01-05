package kerberos


import java.security.*;
import javax.security.auth.*;
import javax.security.auth.callback.*;
/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Oct 2, 2009
* Time: 2:56:32 PM
* To change this template use File | Settings | File Templates.
*/
class KerberosCallbackHandler implements CallbackHandler  {

    String username = null;
    String password = null;

    public KerberosCallbackHandler(String username, String password)
    {
        this.username = username;
        this.password = password;
    }


    public void handle (Callback[] callbacks) throws  UnsupportedCallbackException, IOException
    {
        callbacks.each{ callback ->
            if (callback instanceof NameCallback) {
                callback.setName(username);
            }
            else if (callback instanceof PasswordCallback) {
              callback.setPassword(password.toCharArray());
            }
            else {
              throw new UnsupportedCallbackException(callback, "Call back not supported");
            }
        }
    }

}