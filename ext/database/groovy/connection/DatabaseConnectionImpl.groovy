package connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import java.sql.*

public class DatabaseConnectionImpl extends BaseConnection{

    public static final String DRIVER = "Driver";
    public static final String URL = "Url";
    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String driver;
    private String url;
    private String username;
    private String password;
    private java.sql.Connection connection;

    protected void connect() throws Exception {
        DriverManager.setLoginTimeout ((int)(getTimeout()/1000));
        connection = DriverManager.getConnection(url,username,password);
    }

    protected void disconnect() {
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
    public void init(ConnectionParam param) throws Exception{
        super.init(param)
        this.driver = checkParam(DRIVER);
        this.url = checkParam(URL);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
        Class.forName(this.driver);
    }

    private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

    public boolean checkConnection() {
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

    public java.sql.Connection getConnection() {
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
