/*
 * Created on Jan 21, 2008
 *
 */
package com.ifountain.core.datasource.mocks;

import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.ConnectionParameterSupplier;

public class MockConnectionParameterSupplierImpl implements ConnectionParameterSupplier
{
    String passedConnConfigName;
    ConnectionParam param;
    
    @Override
    public ConnectionParam getConnectionParam(String connName)
    {
        return param;
    }
    
    public String getPassedConnConfigName()
    {
        return passedConnConfigName;
    }
    public void setParam(ConnectionParam param)
    {
        this.param = param;
    }
}