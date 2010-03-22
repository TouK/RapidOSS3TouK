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

import com.ifountain.core.datasource.BaseAdapter
import java.awt.image.BufferedImage
import org.apache.commons.httpclient.ConnectTimeoutException
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.log4j.Logger

public class HttpAdapter extends BaseAdapter {

    public HttpAdapter() {
        super();
    }

    public HttpAdapter(connectionName, logger) {
        super(connectionName, 0, logger);
    }

    public HttpAdapter(connectionName, reconnectInterval, logger) {
        super(connectionName, reconnectInterval, logger);
    }

    public static getInstance() {
        def adapter = new HttpAdapter();
        adapter.setLogger(Logger.getRootLogger());
        return adapter;
    }
    public static getInstance(connectionName) {
        return new HttpAdapter(connectionName, Logger.getRootLogger());
    }

    public String doRequest(String url, Map params, int type) throws Exception {
        DoRequestAction action = new DoRequestAction(logger, url, params, type, null);
        executeAction(action);
        return action.getResponse();
    }

    public String doRequest(String url, String requestBody) throws Exception {
        DoRequestAction action = new DoRequestAction(logger, url, params, DoRequestAction.POST, requestBody);
        executeAction(action);
        return action.getResponse();
    }
    public String doRequestWithBasicAuth(String url, Map params, int type, String username, String password, String requestBody) throws Exception {
        DoRequestAction action = new DoRequestAction(logger, url, params, type, username, password, requestBody);
        executeAction(action);
        return action.getResponse();
    }
    public BufferedImage getImage(String url, Map params) throws Exception {
        GetImageAction action = new GetImageAction(url, params);
        executeAction(action);
        return action.getImage();
    }
    def getBytes(String url, Map params) throws Exception {
        GetBytesAction action = new GetBytesAction(url, params);
        executeAction(action);
        return action.getBytes();
    }

    public String doRequest(String url, Map params) throws Exception {
        return doGetRequest(url, params);
    }

    public String doGetRequest(String url, Map params) throws Exception {
        return doRequest(url, params, DoRequestAction.GET);
    }

    public String doGetRequestWithBasicAuth(String url, Map params, String username, String password) throws Exception {
        return doRequestWithBasicAuth(url, params, DoRequestAction.GET, username, password, null);
    }

    public String doPostRequestWithBasicAuth(String url, Map params, String username, String password) throws Exception {
        return doRequestWithBasicAuth(url, params, DoRequestAction.POST, username, password, null);
    }
    public String doPostRequestWithBasicAuth(String url, String username, String password, String requestBody) throws Exception {
        return doRequestWithBasicAuth(url, params, DoRequestAction.POST, username, password, requestBody);
    }

    public void uploadFile(String url, String fieldName, File fileTobeUploaded, String fileName, Map params = [:]) throws Exception {
        UploadFileAction action = new UploadFileAction(logger, url, fieldName, fileTobeUploaded, fileName);
        action.setParams(params);
        executeAction(action);
    }

    public String doPostRequest(String url, Map params) throws Exception {
        return doRequest(url, params, DoRequestAction.POST);
    }
    public String doPostRequest(String url, String requestBody) throws Exception {
        return doRequest(url, requestBody);
    }

    public Map<String, Object> getObject(Map<String, String> ids, List<String> fieldsToBeRetrieved) {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
}
