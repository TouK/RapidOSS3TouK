package org.jsecurity.authc
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Nov 2, 2009
 * Time: 1:46:13 PM
 * To change this template use File | Settings | File Templates.
 */
class UsernamePasswordTokenWithParams extends UsernamePasswordToken {

    public Map params=[:];

    public UsernamePasswordTokenWithParams()
    {
        super("","");
    }
    public UsernamePasswordTokenWithParams(Map externalParams)
    {
        super("","");
        
        params.putAll(externalParams);
        if(params.login==null)
        {
            params.login="";
        }
        if(params.password==null)
        {
            params.password="";
        }

        this.setUsername(params.login);
        this.setPassword(params.password.toCharArray());

        // Support for "remember me"
        if (params.rememberMe) {
            this.setRememberMe(true) 
        }

    }
}