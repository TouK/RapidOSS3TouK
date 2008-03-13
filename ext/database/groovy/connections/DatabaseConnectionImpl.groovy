package connections;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;

public class DatabaseConnectionImpl implements IConnection{

    public static final String DRIVER = "Driver";
    public static final String URL = "Url";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String driver;
    private String url;
    private String username;
    private String password;
    private ConnectionParam param;
    private Connection connection;

    public void connect() throws Exception {
        if(!isConnected()){
            connection = DriverManager.getConnection(url,username,password);
        }
    }

    public void disconnect() {
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }

    public ConnectionParam getParameters() {
        return param;
    }

    public void init(ConnectionParam param) throws Exception{
        this.param = param;
        this.driver = checkParam(DRIVER);
        this.url = checkParam(URL);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
        Class.forName(this.driver);
    }

    private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!param.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) param.getOtherParams().get(parameterName);
    }

    public boolean isConnected() {
        if(connection == null) return false;
        DatabaseMetaData metaData = null;
        ResultSet set = null;
        try 
        {
           metaData= connection.getMetaData();
           set = metaData.getCatalogs();
           return true;
        } 
        catch (SQLException e) 
        {
            return false;
        } 
        finally{
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static void setStatementParameters(Object[] queryParams, PreparedStatement stmt) throws SQLException {
        ParameterMetaData parameterMetaData = null;
        try
        {
            parameterMetaData = stmt.getParameterMetaData();
        }
        catch(Throwable ignored)
        {}
        
        for(int i = 0; i < queryParams.length; i++)
        {
            if(parameterMetaData != null)
            {
                if(parameterMetaData.getParameterType(i + 1) == Types.CHAR || parameterMetaData.getParameterType(i + 1) == Types.VARCHAR)
                {
                    stmt.setObject( i + 1, queryParams[ i ]);
                }
                else
                {
                    if(queryParams[i] == null || queryParams[i].toString().length() == 0)
                    {
                        stmt.setNull(i + 1, parameterMetaData.getParameterType(i + 1));
                    }
                    else
                    {
                        stmt.setObject( i + 1, queryParams[ i ]);
                    }
                }
            }
            else
            {
                stmt.setObject( i + 1, queryParams[ i ]);
            }
            
        }
    }
}
