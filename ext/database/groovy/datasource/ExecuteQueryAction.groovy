package datasource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import connection.DatabaseConnectionImpl;

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
