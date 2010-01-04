package datasource

import com.ifountain.core.datasource.Action
import com.ifountain.core.connection.IConnection
import org.apache.log4j.Logger
import java.sql.CallableStatement
import connection.DatabaseConnectionImpl

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 29, 2009
* Time: 1:55:19 PM
*/
class CallPreparedStatementAction implements Action {
    private Logger logger;
    private Object[] sqlParams;
    private String sql;

    public CallPreparedStatementAction(Logger logger, String sql, Object[] sqlParams) {
        this.logger = logger;
        this.sqlParams = sqlParams;
        this.sql = sql;
    }
    public void execute(IConnection conn) {
        logger.debug("Will call prepared statement.");
        if (sqlParams == null)
            throw new Exception("SqlParameters can not be null.");
        CallableStatement callableStatement = null;
        try
        {
            logger.debug("Prepared statement sql is : " + sql);
            callableStatement = ((DatabaseConnectionImpl) conn).getConnection().prepareCall(sql);
            DatabaseConnectionImpl.setStatementParameters(sqlParams, callableStatement);
            logger.debug("Executing prepared statement.");
            callableStatement.execute();
            logger.debug("Prepared statement executed succesfully.");
        }
        finally
        {
            if (callableStatement != null)
                callableStatement.close();
        }
    }

}