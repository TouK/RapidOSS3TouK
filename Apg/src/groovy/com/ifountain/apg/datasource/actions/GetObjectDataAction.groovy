package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.TimeSeries;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessor;
import com.watch4net.apg.v2.remote.sample.jaxws.db.Aggregation;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 10:35:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetObjectDataAction implements Action{
    private DatabaseAccessorService dbService;
    private List<TimeSeries> objectData;
    private String filter;
    private List<String> subFilters;
    private int startTimestamp;
    private int endTimestamp;
    private String timeFilter;
    private int period;
    private List<Aggregation> fields;
    private List<String> selectedVariables;
    private int limit;
    public GetObjectDataAction(DatabaseAccessorService dbService, String filter, List<String> subFilters, int startTimestamp,int endTimestamp,
               String timeFilter, int period, List<Aggregation> fields, List<String> selectedVariables, int limit){
        this.dbService = dbService;
        this.filter = filter;
        this.subFilters = subFilters;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timeFilter = timeFilter;
        this.period = period;
        this.fields = fields;
        this.selectedVariables = selectedVariables;
        this.limit = limit;
    }
    public void execute(IConnection conn) throws Exception{
        DatabaseAccessor dbPort = dbService.getDatabaseAccessorPort();
        this.objectData = dbPort.getObjectData(filter, subFilters, startTimestamp, endTimestamp, timeFilter, period, fields, selectedVariables, limit);
    }

    public List<TimeSeries> getObjectData(){
        return this.objectData;
    }

}