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
 * Time: 1:44:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetStaticObjectCountAction implements Action {
    private DatabaseAccessorService dbService;
    private String filter;

    private int staticObjectCount;

    public GetStaticObjectCountAction(DatabaseAccessorService dbService, String filter) {
        this.dbService = dbService;
        this.filter = filter;
    }

    public void execute(IConnection conn) throws Exception {
        staticObjectCount = dbService.getDatabaseAccessorPort().getStaticObjectCount(filter);
    }

    public int getStaticObjectCount() {
        return staticObjectCount;
    }
}
