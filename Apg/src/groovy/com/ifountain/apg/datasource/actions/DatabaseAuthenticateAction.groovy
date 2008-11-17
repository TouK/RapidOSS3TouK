package com.ifountain.apg.datasource.actions

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService
import javax.xml.namespace.QName
import com.ifountain.apg.connection.ApgConnectionImpl

import java.net.Authenticator;
import java.net.URL;
import java.net.MalformedURLException;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 9:14:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseAuthenticateAction implements Action{
    private String username;
    private String password;
    private DatabaseAccessorService dbService;
    public DatabaseAuthenticateAction(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void execute(IConnection conn) throws MalformedURLException {
        Authenticator.setDefault(new ApgAuthenticator(this.username, this.password));
        URL baseUrl = new URL(((ApgConnectionImpl)conn).getWsdlBaseUrl());
        this.dbService = new DatabaseAccessorService(
                    new URL(baseUrl, "wsapi/db?wsdl"),
                    new QName("http://www.watch4net.com/APG/Remote/DatabaseAccessorService","DatabaseAccessorService"));
    }

    public DatabaseAccessorService getDbService(){
        return this.dbService;
    }

}