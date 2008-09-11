package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;

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
 * Time: 1:31:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetObjectFiltersAction implements Action {
    private DatabaseAccessorService dbService;
    private String filter;
    private int startTimestamp;
    private int endTimestamp;
    private String timeFilter;
    private int limit;

    private List<String> objectFilters;

    public GetObjectFiltersAction(DatabaseAccessorService dbService, String filter, int startTimestamp, int endTimestamp, String timeFilter, int limit) {
        this.dbService = dbService;
        this.filter = filter;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timeFilter = timeFilter;
        this.limit = limit;
    }

    public void execute(IConnection conn) throws Exception {
        objectFilters = dbService.getDatabaseAccessorPort().getObjectFilters(filter, startTimestamp, endTimestamp, timeFilter, limit);
    }

    public List<String> getObjectFilters() {
        return objectFilters;
    }
}
