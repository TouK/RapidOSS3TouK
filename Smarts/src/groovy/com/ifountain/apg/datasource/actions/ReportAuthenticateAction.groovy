package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.watch4net.apg.v2.remote.sample.jaxws.report.ReportManagerService;
import com.ifountain.apg.connection.ApgConnectionImpl;
import com.ifountain.core.connection.IConnection;
import javax.xml.namespace.QName;
import java.net.Authenticator;
import java.net.URL


/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 9:31:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportAuthenticateAction implements Action{
    private String username;
    private String password;
    private ReportManagerService reportService;
    public ReportAuthenticateAction(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void execute(IConnection conn) throws Exception{
        Authenticator.setDefault(new ApgAuthenticator(this.username, this.password));
        URL baseUrl = new URL(((ApgConnectionImpl)conn).getWsdlBaseUrl());
        this.reportService =new ReportManagerService(new URL(
                    baseUrl, "wsapi/report?wsdl"), new QName(
                    "http://www.watch4net.com/APG/Remote/ReportManagerService",
                    "ReportManagerService"));
    }

    public ReportManagerService getReportService(){
        return this.reportService;
    }
}