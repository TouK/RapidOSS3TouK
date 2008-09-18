package com.ifountain.apg.datasource.actions;

import com.ifountain.core.datasource.Action;
import com.ifountain.core.connection.IConnection;
import com.watch4net.apg.v2.remote.sample.jaxws.db.DatabaseAccessorService;

import java.util.List;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 11, 2008
 * Time: 1:49:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAvailableAccessorsAction implements Action {
    private DatabaseAccessorService dbService;
    private String filter;

    private List<String> availableAccessors;

    public GetAvailableAccessorsAction(DatabaseAccessorService dbService, String filter) {
        this.dbService = dbService;
        this.filter = filter;
    }

    public void execute(IConnection conn) throws Exception {
        availableAccessors = dbService.getDatabaseAccessorPort().getAvailableAccessors(filter);
    }

    public List<String> getAvailableAccessors() {
        return availableAccessors;
    }
}
