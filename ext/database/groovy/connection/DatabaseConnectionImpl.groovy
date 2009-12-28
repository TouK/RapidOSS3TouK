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
package connection

import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import java.sql.*
import org.apache.log4j.Logger
import org.apache.commons.lang.exception.ExceptionUtils;

public class DatabaseConnectionImpl extends BaseConnection {

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
        DriverManager.setLoginTimeout((int) (getTimeout() / 1000));
        connection = DriverManager.getConnection(url, createConnectionProperties());
    }
    protected Properties createConnectionProperties()
    {
        Properties info = new Properties();
        info.put("user", username);
        info.put("password", password);
        String timeoutStringValue = ((int) getTimeout()).toString();

        if (this.driver.indexOf("mysql") >= 0)
        {
            info.put("connectTimeout", timeoutStringValue);
            info.put("socketTimeout", timeoutStringValue);
        }
        else if (this.driver.indexOf("sybase") >= 0)
        {
            info.put("SESSION_TIMEOUT", timeoutStringValue);
        }
        else if (this.driver.indexOf("oracle") >= 0)
        {
            info.put("oracle.net.CONNECT_TIMEOUT", timeoutStringValue);
            info.put("oracle.jdbc.ReadTimeout", timeoutStringValue);
        }

        return info;
    }
    protected void disconnect() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
        }
    }
    public void init(ConnectionParam param) throws Exception {
        super.init(param)
        this.driver = checkParam(DRIVER);
        this.url = checkParam(URL);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
        Class.forName(this.driver);
    }

    private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if (!params.getOtherParams().containsKey(parameterName)) {
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

    public boolean checkConnection() {
        if (connection == null) return false;
        DatabaseMetaData metaData = null;
        ResultSet set = null;
        try
        {
            metaData = connection.getMetaData();
            set = metaData.getCatalogs();
            return true;
        }
        catch (SQLException e)
        {
            Logger errorLogger = Logger.getRootLogger();
            if (errorLogger.isDebugEnabled())
            {
                errorLogger.debug("[DatabaseConnectionImpl]: Disconnect detected during checkConnection. Reason :" + e.toString());
            }

            return false;
        }
        finally {
            if (set != null) {
                try {
                    set.close();
                }
                catch (SQLException e) {
                    Logger errorLogger = Logger.getRootLogger();
                    if (errorLogger.isDebugEnabled())
                    {
                        errorLogger.debug("[DatabaseConnectionImpl]: Error during closing set used in checkConnection. Reason :" + e.toString());
                    }
                }
            }
        }
    }

    public java.sql.Connection getConnection() {
        return connection;
    }

    public boolean isConnectionException(Throwable t) {
        return ExceptionUtils.indexOfType(t, SocketException.class) > -1;
    }

    public static void setStatementParameters(Object[] queryParams, PreparedStatement stmt) throws SQLException {
        ParameterMetaData parameterMetaData = null;
        try
        {
            parameterMetaData = stmt.getParameterMetaData();
            parameterMetaData.getParameterType(1);
        }
        catch (Throwable ignored)
        {
            parameterMetaData = null;
        }

        for (int i = 0; i < queryParams.length; i++)
        {
            if (parameterMetaData != null)
            {
                if (parameterMetaData.getParameterType(i + 1) == Types.CHAR || parameterMetaData.getParameterType(i + 1) == Types.VARCHAR)
                {
                    stmt.setObject(i + 1, queryParams[i]);
                }
                else
                {
                    if (queryParams[i] == null || queryParams[i].toString().length() == 0)
                    {
                        stmt.setNull(i + 1, parameterMetaData.getParameterType(i + 1));
                    }
                    else
                    {
                        stmt.setObject(i + 1, queryParams[i]);
                    }
                }
            }
            else
            {
               stmt.setObject(i + 1, queryParams[i]); 
            }
        }

    }
}
