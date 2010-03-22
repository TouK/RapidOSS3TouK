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
package datasource
import datasource.HttpAdapter
import org.apache.log4j.Logger
import com.ifountain.annotations.HideProperty;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:47:41 PM
 * To change this template use File | Settings | File Templates.
 */
class HttpDatasourceOperations extends BaseDatasourceOperations{


    HttpAdapter adapter;
    def onLoad(){
       def ownConnection=getProperty("connection")
       if(ownConnection != null)
       {
            this.adapter = new HttpAdapter(ownConnection.name, reconnectInterval*1000, getLogger());
       }
    }

    @HideProperty def getAdapters()
    {
        return [adapter];
    }

    def doRequest(String url, Map params, int type){
        return adapter.doRequest(url, params, type);
    }

    def doRequestWithBasicAuth(String url, Map params, String username, String password, int type){
        return adapter.doRequestWithBasicAuth(url, params, type, username, password, null);
    }
    def doRequestWithBasicAuth(String url, Map params, String username, String password){
        return doRequestWithBasicAuth(url, params, username, password, DoRequestAction.GET);
    }

    def doRequest(String url, Map params){
        return adapter.doRequest(url, params);
    }

    def doGetRequest(String url, Map params){
        return adapter.doGetRequest(url, params);
    }
    def doGetRequestWithBasicAuth(String url, Map params, String username, String password){
        return doRequestWithBasicAuth(url, params, username, password, DoRequestAction.GET);
    }

    def doPostRequest(String url, Map params){
        return adapter.doPostRequest(url, params);
    }
    def doPostRequest(String url, String requestBody){
        return adapter.doPostRequest(url, requestBody);
    }

    def doPostRequestWithBasicAuth(String url, Map params, String username, String password){
        return doRequestWithBasicAuth(url, params, username, password, DoRequestAction.POST);
    }
    def doPostRequestWithBasicAuth(String url, String username, String password, String requestBody){
        return adapter.doRequestWithBasicAuth(url, [:], type, username, password, requestBody);
    }

    def uploadFile(String url, String fieldName, String file, String fileName, Map params=[:])
    {
        adapter.uploadFile (url, fieldName, new File(file), fileName, params);        
    }
}