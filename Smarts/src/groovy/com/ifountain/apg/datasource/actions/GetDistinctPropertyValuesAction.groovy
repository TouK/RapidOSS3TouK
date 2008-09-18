package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DistinctPropertyValues;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 12:08:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetDistinctPropertyValuesAction implements Action {
    private DatabaseAccessorService dbService;
    private String filter;
    private List<String> subFilters;
    private List<String> properties;
    private int limit;

    private List<DistinctPropertyValues> propertyValues;

    public GetDistinctPropertyValuesAction(DatabaseAccessorService dbService, String filter, List<String> subFilters,
                                           List<String> properties, int limit) {
        this.dbService = dbService;
        this.filter = filter;
        this.subFilters = subFilters;
        this.properties = properties;
        this.limit = limit;
    }

    public List<DistinctPropertyValues> getPropertyValues() {
        return propertyValues;
    }

    public void execute(IConnection conn) throws Exception {
        propertyValues = dbService.getDatabaseAccessorPort().getDistinctPropertyValues(filter, subFilters, properties, limit);
    }
}
