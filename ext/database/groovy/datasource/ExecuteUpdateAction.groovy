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
import org.apache.log4j.Logger;

public class ExecuteUpdateAction implements Action {

    private Logger logger;
    private Object[] queryParams;
    private String sql;
    private int affectedRowCount = -1;
    
    public ExecuteUpdateAction(Logger logger, String sql, Object[] queryParams) {
        this.logger = logger;
        this.queryParams = queryParams;
        this.sql = sql;
    }

    public void execute(IConnection conn) throws Exception {
        if(queryParams == null)
        {
            throw new Exception("QueryParameters cannot be null.");
        }
        PreparedStatement stmt = null;
        try 
        {
            logger.debug("Preparing statement.");
            stmt = conn.getConnection().prepareStatement( sql );
            DatabaseConnectionImpl.setStatementParameters(queryParams, stmt);
            logger.debug("Executing statement.");
            affectedRowCount = stmt.executeUpdate();
            logger.debug("" + affectedRowCount + " row(s) effected."); 
        }
        finally
        {
            if(stmt != null)
                stmt.close();
        }
    }

    public int getAffectedRowCount() {
        return affectedRowCount;
    }
}
