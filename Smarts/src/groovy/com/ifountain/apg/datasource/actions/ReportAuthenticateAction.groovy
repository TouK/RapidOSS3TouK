package com.ifountain.apg.datasource.actions

import com.ifountain.core.datasource.Action
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportManagerService
import com.ifountain.apg.connection.ApgConnectionImpl
import com.ifountain.core.connection.IConnection
import javax.xml.namespace.QName

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
 * Time: 9:31:33 AM
 * To change this template use File | Settings | File Templates.
 */
class ReportAuthenticateAction implements Action{
    private String username;
    private String password;
    private ReportManagerService reportService;
    public ReportAuthenticateAction(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void execute(IConnection conn) {
        Authenticator.setDefault(new ApgAuthenticator(this.username, this.password));
        URL baseUrl = new URL((ApgConnectionImpl)conn.getWsdlBaseUrl());
        this.reportService =new ReportManagerService(new URL(
                    baseUrl, "wsapi/report?wsdl"), new QName(
                    "http://www.watch4net.com/APG/Remote/ReportManagerService",
                    "ReportManagerService"));
    }

    public ReportManagerService getReportService(){
        return this.reportService;
    }
}