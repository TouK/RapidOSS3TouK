package com.ifountain.apg.datasource;

import com.ifountain.apg.datasource.actions.ReportAuthenticateAction;
import com.ifountain.apg.datasource.actions.GetReportAction;
import com.ifountain.core.datasource.BaseAdapter;
import com.watch4net.apg.v2.remote.sample.jaxws.report.*;

import javax.xml.ws.Holder;
import java.util.Map;
import java.util.List;
import java.lang.Exception;

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
 * Time: 9:13:13 AM
 * To change this template use File | Settings | File Templates.
 */
class ApgReportAdapter extends BaseAdapter {
    public ReportManagerService authenticate(String username, String password) throws Exception {
        ReportAuthenticateAction authAction = new ReportAuthenticateAction(username, password);
        executeAction(authAction);
        return authAction.getReportService();
    }

    public void getReport(String username, String password, ReportProperties properties,RealNode node,Holder<CompoundElement> compoundElement,Holder<GraphElement> graphElement,Holder<ErrorElement> errorElement,Holder<ImageElement> imageElement,Holder<TableElement> tableElement)throws Exception {
        ReportManagerService reportService = authenticate(username, password);
        GetReportAction action = new GetReportAction(reportService, properties, node,compoundElement, graphElement, errorElement, imageElement, tableElement);
        executeAction(action);
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null;
    }

}