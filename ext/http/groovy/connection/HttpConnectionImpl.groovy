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

import com.ifountain.comp.utils.HttpUtils
import com.ifountain.core.connection.BaseConnection
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.core.connection.exception.UndefinedConnectionParameterException
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.httpclient.ConnectTimeoutException

public class HttpConnectionImpl extends BaseConnection{

    public static final String BASE_URL = "BaseUrl";
    private String baseUrl;
    private HttpUtils httpUtils;

    protected void connect() throws Exception {
    }

    protected void disconnect() {
    }

   
    public void init(ConnectionParam param) throws Exception {
        super.init (param);
        this.baseUrl = checkParam(BASE_URL);
        setHttpConnection();
    }

    public boolean checkConnection() {
        return true;
    }
    //should be used driectly only in test methods
	protected void setHttpConnection(){
		setHttpConnection (new HttpUtils());
	}
	//should be used driectly only in test methods
	protected void setHttpConnection(HttpUtils httpUtils){
		this.httpUtils = httpUtils;
		this.httpUtils.setTimeout ((int)getTimeout());
	}	
	
	public HttpUtils getHttpConnection(){
		return httpUtils;
	}

    public String getBaseUrl() {
        return baseUrl;
    }

    public boolean isConnectionException(Throwable t) {
        return ExceptionUtils.indexOfType(t, SocketException.class) > -1 || ExceptionUtils.indexOfType(t, ConnectTimeoutException.class) > -1;
    }

}
