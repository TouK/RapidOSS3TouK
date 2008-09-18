package com.ifountain.apg.datasource

import com.ifountain.apg.datasource.actions.GetReportAction
import com.ifountain.apg.datasource.actions.ReportAuthenticateAction
import com.ifountain.core.datasource.BaseAdapter
import com.watch4net.apg.v2.remote.sample.jaxws.report.*
import javax.xml.ws.Holder
import org.apache.log4j.Logger;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 9:13:13 AM
 * To change this template use File | Settings | File Templates.
 */
class ApgReportAdapter extends BaseAdapter {

    public ApgReportAdapter(String connConfigName, long reconnectInterval, Logger logger)
    {
        super(connConfigName, reconnectInterval, logger);
    }
    public ReportManagerService authenticate(String username, String password){
        ReportAuthenticateAction authAction = new ReportAuthenticateAction(username, password);
        executeAction(authAction);
        return authAction.getReportService();
    }

    public void getReport(String username, String password, ReportProperties properties, RealNode node, Holder<CompoundElement> compoundElement, Holder<GraphElement> graphElement, Holder<ErrorElement> errorElement, Holder<ImageElement> imageElement, Holder<TableElement> tableElement){
        ReportManagerService reportService = authenticate(username, password);
        GetReportAction action = new GetReportAction(reportService, properties, node, compoundElement, graphElement, errorElement, imageElement, tableElement);
        executeAction(action);
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null;
    }

}