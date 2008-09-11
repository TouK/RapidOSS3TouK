package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DistinctPropertyValues;

import java.util.List;

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
