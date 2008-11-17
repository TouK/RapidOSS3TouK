package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 1:36:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetObjectCountAction implements Action {
    private DatabaseAccessorService dbService;
    private String filter;
    private int startTimestamp;
    private int endTimestamp;
    private String timeFilter;
    private int limit;

    private int objectCount;

    public GetObjectCountAction(DatabaseAccessorService dbService, String filter, int startTimestamp, int endTimestamp, String timeFilter, int limit) {
        this.dbService = dbService;
        this.filter = filter;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timeFilter = timeFilter;
        this.limit = limit;
    }

    public int getObjectCount() {
        return objectCount;
    }

    public void execute(IConnection conn) throws Exception {
        objectCount = dbService.getDatabaseAccessorPort().getObjectCount(filter, startTimestamp, endTimestamp, timeFilter, limit);
    }
}
