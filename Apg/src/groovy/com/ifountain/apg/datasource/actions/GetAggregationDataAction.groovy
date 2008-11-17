package com.ifountain.apg.datasource.actions;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import com.watch4net.apg.v2.remote.sample.jaxws.db.Aggregations;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.TimeSerie;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 11:25:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetAggregationDataAction implements Action{

    private DatabaseAccessorService dbService;
    private String filter;
    private List<String> subFilter;
    private int startTimestamp;
    private int endTimestamp;
    private String timeFilter;
    private int period;
    private Aggregations aggregations;

    private List<TimeSerie> aggregateData;

    public GetAggregationDataAction(DatabaseAccessorService dbService, String filter, List<String> subFilter, int startTimestamp, int endTimestamp,
                                             String timeFilter, int period, Aggregations aggregations){
        this.dbService = dbService;
        this.filter = filter;
        this.subFilter = subFilter;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timeFilter = timeFilter;
        this.period = period;
        this.aggregations = aggregations;
    }

    public void execute(IConnection conn) throws Exception{
        this.aggregateData = dbService.getDatabaseAccessorPort().getAggregatedData(filter, subFilter, startTimestamp, endTimestamp, timeFilter, period, aggregations);
    }

    public List<TimeSerie> getAggregateData(){
        return this.aggregateData;
    }

}