/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import connection.DatabaseConnectionImpl
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.apache.log4j.Logger;

public class ExecuteQueryAction implements Action {

    private Logger logger;
    private Object[] queryParams;
    private String sql;
    private int fetchSize = 0;
    private ResultSet resultSet;
    public ExecuteQueryAction(Logger logger, String sql, Object[] queryParams, int fetchSize) {
        this.logger = logger;
        this.queryParams = queryParams;
        this.sql = sql;
        this.fetchSize = fetchSize;
    }
    public ExecuteQueryAction(Logger logger, String sql, Object[] queryParams) {
        this(logger, sql, queryParams, 0);
    }

    public void execute(IConnection conn) throws Exception {
        if(queryParams == null) {
            throw new Exception("QueryParameters cannot be null.");
        }
        logger.debug("Preparing statement.");
        PreparedStatement stmt = conn.getConnection().prepareStatement(sql);
        DatabaseConnectionImpl.setStatementParameters(queryParams, stmt);
        if(fetchSize > 0)
            stmt.setFetchSize(fetchSize);
        logger.debug("Executing query.");
        resultSet = stmt.executeQuery();
    }
    public ResultSet getResultSet() {
        return resultSet;
    }

}
