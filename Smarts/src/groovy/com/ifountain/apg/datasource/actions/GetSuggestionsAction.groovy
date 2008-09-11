package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;
import com.watch4net.apg.v2.remote.sample.jaxws.db.Suggestion;

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
 * Time: 2:02:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetSuggestionsAction implements Action {

    private DatabaseAccessorService dbService;
    private String filter;
    private String pattern;
    private List<String> properties;
    private int limit;

    private List<Suggestion> suggesstions;

    public GetSuggestionsAction(DatabaseAccessorService dbService, String filter, String pattern, List<String> properties, int limit) {
        this.dbService = dbService;
        this.filter = filter;
        this.pattern = pattern;
        this.properties = properties;
        this.limit = limit;
    }

    public void execute(IConnection conn) throws Exception {
        suggesstions = dbService.getDatabaseAccessorPort().getSuggestions(filter, pattern, properties, limit);
    }

    public List<Suggestion> getSuggesstions() {
        return suggesstions;
    }
}
