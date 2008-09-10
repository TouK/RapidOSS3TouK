package com.ifountain.apg.datasource.actions

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService
import javax.xml.namespace.QName
import com.ifountain.apg.connection.ApgConnectionImpl

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 9:14:34 AM
 * To change this template use File | Settings | File Templates.
 */
class DatabaseAuthenticateAction implements Action{
    private String username;
    private String password;
    private DatabaseAccessorService dbService;
    public DatabaseAuthenticateAction(String username, String password) {
        this.username = username;
        this.password = password;   
    }
    public void execute(IConnection conn) {
        Authenticator.setDefault(new ApgAuthenticator(this.username, this.password));
        URL baseUrl = new URL((ApgConnectionImpl)conn.getWsdlBaseUrl());
        this.dbService = new DatabaseAccessorService(
                    new URL(baseUrl, "wsapi/db?wsdl"),
                    new QName("http://www.watch4net.com/APG/Remote/DatabaseAccessorService","DatabaseAccessorService"));
    }

    public DatabaseAccessorService getDbService(){
        return this.dbService;
    }

}