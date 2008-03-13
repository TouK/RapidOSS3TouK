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
 * Created on Apr 12, 2006
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.comp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.EncodingUtil;

public class HttpUtils {

	private static MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
    private HttpClient httpClient = new HttpClient(manager);
    private HttpClient clientForBasicAuth = new HttpClient(manager);
    private static int INITIAL_BUFFER_SIZE = 4*1024;//4K
    
    public String doPostRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException
    {
    	PostMethod post = preparePostMethod(urlStr, params);
    	return executeHttpMethod(post, httpClient);
    }

    public PostMethod preparePostMethod(String urlStr, Map params) {
        PostMethod post = new PostMethod(urlStr);
    	post.setRequestBody(getNameValuePairsFromMap(params));
    	post.getParams().setContentCharset("UTF-8");
        return post;
    }
    
    public String doGetRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException{
        GetMethod get = prepareGetMethod(urlStr, params);
        return executeGetMethod(get, httpClient);
    }
    public String doGetWithBasicAuth(String urlStr, String userName, String password, Map params) throws HttpException, HttpStatusException, IOException{
        URL url = new URL(urlStr);
        GetMethod get = prepareGetForBasicAuth(urlStr, params);
        clientForBasicAuth.getState().setCredentials(new AuthScope(url.getHost(), -1), new UsernamePasswordCredentials(userName, password));
        return executeGetMethod(get, clientForBasicAuth);
    }

	public String executeGetMethod(GetMethod get, HttpClient client) throws HttpStatusException, HttpException, IOException {
		return executeHttpMethod(get, client);
	}
	
	public String executeGetMethod(GetMethod get) throws HttpStatusException, HttpException, IOException {
	    return executeHttpMethod(get, httpClient);
	}

    public String executePostMethod(PostMethod post, HttpClient client) throws HttpStatusException, HttpException, IOException {
        return executeHttpMethod(post, client);
    }
    public String executePostMethod(PostMethod post) throws HttpStatusException, HttpException, IOException {
        return executeHttpMethod(post, httpClient);
    }
	public GetMethod prepareGetMethod(String urlStr, Map params) {
		GetMethod get = new GetMethod(urlStr);
        get.setFollowRedirects(true);
        get.setQueryString(getNameValuePairsFromMap(params));
        get.getParams().setContentCharset("UTF-8");
		return get;
	}
    
    public GetMethod prepareGetForBasicAuth(String urlString, Map params){
        GetMethod get = prepareGetMethod(urlString, params);
        get.setDoAuthentication(true);
        return get;
    }

	private NameValuePair[] getNameValuePairsFromMap(Map params) {
		List nameValuePairs = new ArrayList();
    	Iterator iterator = params.keySet().iterator();
    	while(iterator.hasNext())
    	{
    		String key = (String) iterator.next();
    		String value = (String) params.get(key);
    		nameValuePairs.add(new NameValuePair(key, value));
    	}
    	NameValuePair[] pairs = (NameValuePair[]) nameValuePairs.toArray(new NameValuePair[0]);
    	
		return pairs;
	}
    
    private String executeHttpMethod(HttpMethodBase method, HttpClient client) throws HttpStatusException, HttpException, IOException
    {
    	String response = "";
        
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode > 399) {//2xx status codes are success, 3xx are redirections etc., 4xx are errors.
                throw new HttpStatusException("ERROR: HttpServer returned the following status code <" + statusCode + "> for the url:\n " + method.getURI());
            }
            
            InputStream instream = method.getResponseBodyAsStream();
            if (instream != null) {
                long contentLength = method.getResponseContentLength();
                if (contentLength > Integer.MAX_VALUE) { //guard below cast from overflow
                    throw new IOException("Content too large to be buffered: "+ contentLength +" bytes");
                }
                
                
                ByteArrayOutputStream outstream = new ByteArrayOutputStream( contentLength > 0 ? (int) contentLength : INITIAL_BUFFER_SIZE);
                byte[] buffer = new byte[4096];
                int len;
                while ((len = instream.read(buffer)) > 0) {
                    outstream.write(buffer, 0, len);
                }
                outstream.close();
                byte[] rawData = outstream.toByteArray();
                if (rawData != null) {
                    return EncodingUtil.getString(rawData, method.getResponseCharSet());
                } else {
                    return null;
                }
            }
        } 
        
        finally {
            // Release the connection. When keepalive is true(which is true by default), connection 
            //must be released to reuse it. 
        	method.releaseConnection();
        }
        return response;
    }
}
