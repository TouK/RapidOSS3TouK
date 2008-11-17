package com.ifountain.apg.datasource.actions;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 9:34:39 AM
 * To change this template use File | Settings | File Templates.
 */
class ApgAuthenticator extends Authenticator {
    private String username;
    private String password;
    public ApgAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password.toCharArray());
    }

}