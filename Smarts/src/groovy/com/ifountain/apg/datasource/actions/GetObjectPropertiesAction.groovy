package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.ObjectPropertyValues;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 11:58:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetObjectPropertiesAction implements Action {

    private DatabaseAccessorService dbService;
    private String filter;
    private List<String> subFilters;
    private List<String> properties;

    private List<ObjectPropertyValues> objectProperties;

    public GetObjectPropertiesAction(DatabaseAccessorService dbService, String filter, List<String> subFilters, List<String> properties) {
        this.dbService = dbService;
        this.filter = filter;
        this.subFilters = subFilters;
        this.properties = properties;
    }

    public void execute(IConnection conn) throws Exception {
        this.objectProperties = dbService.getDatabaseAccessorPort().getObjectProperties(filter, subFilters, properties);
    }

    public List<ObjectPropertyValues> getObjectProperties() {
        return objectProperties;
    }
}
