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
 * Created on Feb 24, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package datasources;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ifountain.core.connection.IConnection;
import com.ifountain.core.datasource.Action;
import connections.HttpConnectionImpl;

public class DoRequestAction implements Action {

    public static final int POST = 0;
    public static final int GET = 1;
    private Logger logger;
    private int type;
    private String url;
    private Map params;
    private String response = "";
    
    public DoRequestAction(Logger logger, String url, Map params, int type) {
        this.logger = logger;
        this.url = url;
        this.params = params;
        this.type = type;
    }

    public void execute(IConnection conn) throws Exception {
    	HttpConnectionImpl httpConn = (HttpConnectionImpl)conn;
        String completeUrl = httpConn.getBaseUrl() + url;       
        logger.debug("Making the request:\n" + completeUrl);
        if(type == POST)
        {
            response =  httpConn.getHttpConnection().doPostRequest(completeUrl, params);
        }
        else
        {
            response =  httpConn.getHttpConnection().doGetRequest(completeUrl, params);
        }
        logger.debug("Response received:\n" + response);
    }

    public String getResponse() {
        return response;
    }

}
