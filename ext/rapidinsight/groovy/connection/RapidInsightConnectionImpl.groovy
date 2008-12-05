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

import com.ifountain.core.connection.ConnectionParam;

public class RapidInsightConnectionImpl extends HttpConnectionImpl {

    public static final String USERNAME = "Username";
    public static final String PASSWORD = "Password";
    private String username;
    private String password;

    public void connect() throws Exception {
    	if(!checkConnection()){
	    	super.connect();
	    	def completeUrl = getBaseUrl() + "/User/login";
	    	def params = ["submission":"credentials", "login":username, "password":password];
	    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
	    	if(response.indexOf("<Successful>") == -1){
	    		throw new Exception("Could not login using URL: " + completeUrl)
	    	}
    	}
    }

    public void disconnect() {
    	def completeUrl = getBaseUrl() + "/User/logout";
    	def params = [:];
    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
    	if(response.indexOf("<Successful>") == -1){
    		throw new Exception("Could not logout");
    	}
    }

    public void _init(ConnectionParam param) throws Exception {
        super.init(param);
        this.username = checkParam(USERNAME);
        this.password = checkParam(PASSWORD);
    }

    public boolean checkConnection() {
    	def completeUrl = getBaseUrl() + "/User/login";
    	def params = [:];
    	def response =  getHttpConnection().doGetRequest(completeUrl, params);
    	if(response.indexOf("<Successful>") == -1){
    		return false;
    	}
    	return true;
    }

}
