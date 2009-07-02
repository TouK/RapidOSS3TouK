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
package datasource

import com.ifountain.core.connection.IConnection
import com.ifountain.core.datasource.Action
import org.apache.log4j.Logger;

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

        String completeUrl = getCompleteUrl(conn.getBaseUrl());
        logger.debug("Making the request:\n" + completeUrl);
        if(type == POST)
        {
            response =  conn.getHttpConnection().doPostRequest(completeUrl, params);
        }
        else
        {
            response =  conn.getHttpConnection().doGetRequest(completeUrl, params);
        }
        logger.debug("Response received:\n"+completeUrl);
    }

    protected String getCompleteUrl(String baseUrl){
        if(baseUrl.length() > 0 && baseUrl.charAt(baseUrl.length() -1) == '/'){
            baseUrl = baseUrl.substring(0, baseUrl.length() -1)
        }
        if(url.length() > 0 && url.charAt(0) == '/'){
            url = url.substring(1);
        }
        return baseUrl + "/" + url;
    }

    public String getResponse() {
        return response;
    }

}
