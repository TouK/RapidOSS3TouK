package com.ifountain.apg.datasource.actions

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import com.watch4net.apg.v2.remote.sample.jaxws.report.*
import javax.xml.ws.Holder

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 2:13:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetReportAction implements Action {

    private ReportManagerService reportService;
    private ReportProperties properties;
    private RealNode node;
    private Holder<CompoundElement> compoundElement;
    private Holder<GraphElement> graphElement;
    private Holder<ErrorElement> errorElement;
    private Holder<ImageElement> imageElement;
    private Holder<TableElement> tableElement;

    public GetReportAction(ReportManagerService reportService, ReportProperties properties, RealNode node, Holder<CompoundElement> compoundElement,
                           Holder<GraphElement> graphElement, Holder<ErrorElement> errorElement, Holder<ImageElement> imageElement, Holder<TableElement> tableElement) {
        this.reportService = reportService;
        this.properties = properties;
        this.node = node;
        this.compoundElement = compoundElement;
        this.graphElement = graphElement;
        this.errorElement = errorElement;
        this.imageElement = imageElement;
        this.tableElement = tableElement;
    }

    public void execute(IConnection conn){
        reportService.getReportManagerPort().getReport(properties, node, compoundElement, graphElement, errorElement, imageElement, tableElement);
    }
}
