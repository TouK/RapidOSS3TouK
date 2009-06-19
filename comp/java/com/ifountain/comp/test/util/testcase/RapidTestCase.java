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
package com.ifountain.comp.test.util.testcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXParseException;

import com.ifountain.comp.test.util.file.FileTestUtils;
import com.ifountain.comp.utils.Timer;
import com.ifountain.comp.utils.XMLTestUtils;


/**
 * @author Fatih
 */
public class RapidTestCase extends TestCase
{
	private static ArrayList testRuntimes = new ArrayList();
	private Timer timer;
	private boolean isSetupCompleted = false;
	public RapidTestCase()
	{
		super();
        prepareSetup();
	}

    private void prepareSetup()
    {
        if(!isSetupCompleted)
        {
            isSetupCompleted = true;
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run()
                {
                    Collections.sort(testRuntimes, new Comparator() {
                        public int compare(Object o1, Object o2)
                        {
                            TestResult result1 = (TestResult) o1;
                            TestResult result2 = (TestResult) o2;
                            return (int) (result2.runtime - result1.runtime);
                        }
                    });
                    StringBuffer sb = new StringBuffer();
                    for (Iterator iter = testRuntimes.iterator(); iter.hasNext();)
                    {
                        TestResult element = (TestResult) iter.next();
                        sb.append(element.testName + ": " + element.runtime + "\n");
                    }
                    try
                    {
                        FileTestUtils.generateFile("TEST_RUNTIME_RESULTS.txt", sb.toString());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    
    /**
     * @param arg0
     */
    public RapidTestCase(String arg0)
    {
        super(arg0);
        prepareSetup();
    }

    protected void setUp() throws Exception
    {
    	super.setUp();
    	timer = new Timer();
        System.out.println("Test " + this.getName() + " started at " + new Date());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        long runtime = timer.stop();
        double usedMem = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/Math.pow(2, 20);
        double freeMem = Runtime.getRuntime().freeMemory()/Math.pow(2, 20);
        System.out.println("Test " + this.getName() + " finished in " + runtime + " milliseconds. Used Memory "+usedMem+" MB and Free Memory:" + freeMem +" MB");

        testRuntimes.add(new TestResult(this.getClass().getName() + "." + this.getName(), runtime));
    }
    
    private static class TestResult{
    	String testName;
    	long runtime;
    	public TestResult(String testName, long runtime)
    	{
    		this.testName = testName;
    		this.runtime = runtime;
    	}
    }
    
    public void assertEqualsXML(String xml1, String xml2, List ignoredTags) {
    	try {
    		XMLTestUtils.compareXml(xml1, xml2, ignoredTags);
		}
		catch (Throwable e) {
            e.printStackTrace();
            assertEquals(xml1, xml2);
		}
	}
    
    public void assertEqualsXML(String expectedXml, String actualXml) {
    	try {
    		XMLTestUtils.compareXml(expectedXml, actualXml);
    	}
    	catch(SAXParseException e)
    	{
    		throw new RuntimeException("Expected or actual xml does not seem valid since it can not be parsed by sax parser.", e);
    	}
    	catch (Throwable t) {
    		throw new ComparisonFailure("XML Comparison failed. Detailed message is: " + t.toString() + ".", expectedXml, actualXml);
    	}
    }
    
    public static TestSuite getSuiteThatRunsAllTestNTimes(Class testClass, int n)
    {
    	TestSuite suite = new TestSuite();
    	for (int i = 0; i < n; i++) {
			suite.addTestSuite(testClass);
		}
    	return suite;
    }

    public String getWorkspacePath() throws Exception
    {
        String canonicalPath = new java.io.File(".").getCanonicalPath();
        String workspacePath=null;
        //to run in developer pc
        if (canonicalPath.endsWith("RapidModules"))
        {
            workspacePath= "..";
        }
        else    //to run in hudson
        {
            workspacePath= "../../..";
        }
        return workspacePath;
    }
    public java.io.File getWorkspaceDirectory() throws Exception
    {
        return new java.io.File(getWorkspacePath());
    }
}
