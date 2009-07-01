package com.ifountain.demo

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.core.connection.ConnectionParam
import connection.HttpConnectionImpl
import datasource.DoRequestAction
import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jun 12, 2009
* Time: 10:23:19 AM
* To change this template use File | Settings | File Templates.
*/
class RapidInsightDemoTests extends RapidCoreTestCase {

    String BASE_URL="http://206.251.255.44/RapidSuite/";
    String USERNAME="rsadmin";
    String PASSWORD="molkay01";
    int MIN_TIMEOUT=30000;
    int MAX_TIMEOUT=30000;
    def logger=Logger.getRootLogger();
    
    public void testDemoRunsNormally()
    {
        def con=getConnection();

        def nonLoginResponse=getUrlResponse(con,'',[:]);
        assertTrue("already signed in",nonLoginResponse.indexOf('auth/signIn')>0);
        assertFalse("base url have exception",nonLoginResponse.indexOf('Exception')>0);

        def loginResponse=getUrlResponseWithCredentials(con,'auth/signIn',[:]);
        assertFalse("not signed in",loginResponse.indexOf('auth/signIn')>0);
        assertFalse("singin url have exception",loginResponse.indexOf('Exception')>0);

        //since we are logged in we no longer get request with credentials
        def urlsToCheck=[:];
        urlsToCheck["index/events.gsp"]='<li class="selected"><a href="/RapidSuite/index/events.gsp">';
        urlsToCheck["index/inventory.gsp"]='<li class="selected"><a href="/RapidSuite/index/inventory.gsp">';
        
        urlsToCheck["getEventDetails.gsp?name=testevent"]='does not exist';
        urlsToCheck["getObjectDetails.gsp?name=testobject"]='does not exist';
        urlsToCheck["search?offset=0&sort=id&order=asc&max=100&searchIn=RsEvent"]='<Objects total=';
        urlsToCheck["search?offset=0&sort=id&order=asc&max=100&searchIn=RsTopologyObject"]='<Objects total=';
        
        urlsToCheck["smartsAdmin.gsp"]='adminLayout';
        urlsToCheck["script/list"]='adminLayout';

        urlsToCheck.each{ url , keyToCheck ->
              def urlResponse=getUrlResponse(con,url,[:]);
              //println urlResponse

              assertFalse("${url} not signed in",urlResponse.indexOf('auth/signIn')>0);
              assertFalse("${url} have exception",urlResponse.indexOf('Exception')>0);
              assertTrue("${url} does not contain ${keyToCheck}",urlResponse.indexOf(keyToCheck)>=0);
        }

    }
    protected def getUrlResponseWithCredentials(con,url,params)
    {
       params["login"]=USERNAME;
       params["password"]=PASSWORD;
       return getUrlResponse(con,url,params);
    }
    protected def getUrlResponse(con,url,params)
    {
        println '--------------------------------------------------------'
        println "requesting url ${url} with params ${params}"
        DoRequestAction action=new DoRequestAction(logger,url,params,DoRequestAction.GET);
        action.execute (con);
        return action.getResponse();
    }

    protected HttpConnectionImpl getConnection()
    {
        HttpConnectionImpl con=new HttpConnectionImpl();
        Map otherParams = [:];
        otherParams.put(HttpConnectionImpl.BASE_URL, BASE_URL);

        ConnectionParam param = new ConnectionParam("http", "ds", HttpConnectionImpl.class.getName(), otherParams,10,MIN_TIMEOUT,MAX_TIMEOUT);
        con.init(param);
        con.connect();
        return con;
    }
}