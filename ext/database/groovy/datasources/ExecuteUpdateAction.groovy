package datasources;

import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import connections.DatabaseConnectionImpl;

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
            stmt = ((DatabaseConnectionImpl)conn).getConnection().prepareStatement( sql );
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
