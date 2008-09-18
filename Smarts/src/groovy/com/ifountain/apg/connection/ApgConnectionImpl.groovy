package com.ifountain.apg.connection;

import com.ifountain.core.connection.BaseConnection;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 10, 2008
 * Time: 8:58:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class ApgConnectionImpl extends BaseConnection{

    public static final String WSDL_BASE_URL = "WsdlBaseUrl";
    private String wsdlBaseUrl;
    protected void connect() {}

    protected void disconnect() {}

    public void init(ConnectionParam param) throws Exception {
        this.params = param;
	    this.wsdlBaseUrl= checkParam(WSDL_BASE_URL);
    }

    public boolean isConnected() {
        return true;
    }
    private String checkParam(String parameterName) throws UndefinedConnectionParameterException {
        if(!params.getOtherParams().containsKey(parameterName)){
            throw new UndefinedConnectionParameterException(parameterName);
        }
        return (String) params.getOtherParams().get(parameterName);
    }

    public String getWsdlBaseUrl(){
        return this.wsdlBaseUrl;
    }

}