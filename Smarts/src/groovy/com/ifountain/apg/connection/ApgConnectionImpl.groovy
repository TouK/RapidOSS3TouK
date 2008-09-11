package com.ifountain.apg.connection;

import com.ifountain.core.connection.BaseConnection;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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