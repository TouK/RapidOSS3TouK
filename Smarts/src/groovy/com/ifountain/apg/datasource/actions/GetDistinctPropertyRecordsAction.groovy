package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.PropertyRecord;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 1:16:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetDistinctPropertyRecordsAction implements Action{
    private DatabaseAccessorService dbService;
    private String filter;
    private List<String> properties;
    List<PropertyRecord> propertyRecords;

    public GetDistinctPropertyRecordsAction(DatabaseAccessorService dbService, String filter, List<String> properties) {
        this.dbService = dbService;
        this.filter = filter;
        this.properties = properties;
    }

    public void execute(IConnection conn) throws Exception {
        propertyRecords = dbService.getDatabaseAccessorPort().getDistinctPropertyRecords(filter, properties);
    }

    public List<PropertyRecord> getPropertyRecords() {
        return this.propertyRecords;
    }
}
