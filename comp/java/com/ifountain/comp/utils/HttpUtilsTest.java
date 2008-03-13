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
 * Created on Jan 4, 2007
 *
 * Author Sezgin kucukkaraaslan
 */
package com.ifountain.comp.utils;

import java.util.HashMap;

import com.ifountain.comp.test.util.RCompTestCase;


public class HttpUtilsTest extends RCompTestCase {
    
    public void testDoGetRequest() throws Exception {
    	HashMap parameters = new HashMap();
    	parameters.put("q", "ifountain");
    	parameters.put("hl", "tr");
        String response = new HttpUtils().doGetRequest("http://www.google.com.tr/search", parameters);
        assertTrue(response.indexOf("RapidThoughts") > -1);
    }
    
    /**
     * DO NOT DELETE THE BELOW TEST!!
     */
//    static int count = 5000;
//    
//    public void testBatchPerformance() throws Exception {
//    	String res = HttpUtils.doGetRequest("http://localhost:9191/RapidInsight/User/login?submission=credentials&login=rsadmin&password=changeme");
//    	System.out.println(res);
//    	
//    	int COUNT = 500;
//
//    	StringBuffer rows = new StringBuffer();
//    	String rowSeperator = "!!";
//    	String columnSeperator = ";;";
//    	String propertySeperator = "::";
//
//    	for(int i = 0 ; i < COUNT ; i++)
//    	{
//    		String row = "ActionType" + propertySeperator + "AddObject" + columnSeperator + "Identifier" + propertySeperator + "switch" + i + columnSeperator + "ClassName" + propertySeperator + "Switch" + columnSeperator + "Location" + propertySeperator + "London";
//    		rows.append(row).append(rowSeperator);
//    	}
//    	
//    	//BATCH
//    	long l1 = System.currentTimeMillis();
//    	HashMap parameters = new HashMap();
//    	parameters.put("Script", "scripts/addBatch.groovy");
//    	parameters.put("RowSeperator", rowSeperator);
//    	parameters.put("ColumnSeperator", columnSeperator);
//    	parameters.put("PropertySeperator", propertySeperator);
//    	parameters.put("Data", rows.toString());
//    	String response2 = HttpUtils.doPostRequest("http://localhost:9191/RapidInsight/Action/executeScript", parameters);
//    	System.out.println(response2);
//    	long l2 = System.currentTimeMillis();
//    	long elapsed = l2 - l1;
//    	System.out.println("Elapsed for adding " + COUNT + " managed objects with batch: " + elapsed + " ms.");
//
//    	
//    	//ONE BY ONE
//    	long l3 = System.currentTimeMillis();
//    	String response = "";
//    	String urlStart = "http://localhost:9191/RapidInsight/ManagedObject/add?submission=credentials&login=rsadmin&password=changeme";
//    	for(int i = 0 ; i < COUNT ; i++)
//    	{
//    		response = HttpUtils.doPostRequest(urlStart + "&Identifier=router" + i + "&ClassName=Router&Location=Paris", new HashMap());
//    	}
//    	System.out.println(response);
//
//    	long l4 = System.currentTimeMillis();
//    	elapsed = l4 - l3;
//    	System.out.println("Elapsed for adding " + COUNT + " managed objects one by one: " + elapsed + " ms.");
//    	
//    	List requestThreads = new ArrayList();
//    	for (int i = 0; i < COUNT; i++) {
//    		String urlForThread = urlStart + "&Identifier=interface" + i + "&ClassName=Interface&Location=Rome";
//    		requestThreads.add(new RequestThread(urlForThread));
//		}
//    	
//    	//THREADED
//    	long l5 = System.currentTimeMillis();
//    	for (int i = 0; i < COUNT; i++) {
//			((RequestThread)requestThreads.get(i)).start();
//		}
//    	
//    	while(true)
//    	{
//    		if(RequestThread.finishedCount == COUNT)
//    		{
//    			break;
//    		}
//    		Thread.sleep(10);
//    	}
//    	
//    	System.out.println(RequestThread.response);
//    	
//    	long l6 = System.currentTimeMillis();
//    	elapsed = l6 - l5;
//    	System.out.println("Elapsed for adding " + COUNT + " managed objects one by one multithreaded: " + elapsed + " ms.");
//    	
//	}
//    class RequestThread extends Thread
//    {
//    	static int finishedCount; 
//    	String url;
//    	static String response;
//    	static synchronized void finished()
//    	{
//    		finishedCount++;
//    	}
//    	public RequestThread(String url)
//    	{
//    		this.url = url;
//    	}
//    	
//    	public void run() 
//    	{
//    		try {
//    			response = HttpUtils.doPostRequest(url, new HashMap());
//    			finished();
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//    	}
//    }
}


