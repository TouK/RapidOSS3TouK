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

import java.io.File;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;




/*
* VERIFIES THAT ALL TESTS CAN BE REACHED EITHER FROM ALLTESTS OR ALLPERFORMANCETESTS
*/
public abstract class AbstractTestInclusionTestCase extends TestCase {

    private static int MAX = 30;
    private ArrayList<String> allTests = new ArrayList<String>();
    public abstract String getRootPath();
    public abstract Class[] getAllTestSuiteClasses();
    public void testFindTestsUsedInAllTests() throws Exception{
//        DbUtils.blockDatabaseOperations = true;
        findAllTestsWritten(getRootPath());
        ArrayList includedOrIntentionallyExcludedTests = new ArrayList();
        // first populate the list with intentionally excluded files.
 //       readExcludedFilesList(includedOrIntentionallyExcludedTests);
        // now add included tests to this list
        // test AllTests
        Class[] testSuiteClasses = getAllTestSuiteClasses();
        for (int i = 0; i < testSuiteClasses.length; i++)
        {
            checkTestCases(includedOrIntentionallyExcludedTests, (TestSuite)testSuiteClasses[i].getMethod("suite", new Class[0]).invoke(testSuiteClasses[i].newInstance(), new Object[0]));
        }
        
        ArrayList<String> forgottenTests = new ArrayList<String>();
        for (int i = 0; i < allTests.size(); i++) {
            if (!includedOrIntentionallyExcludedTests.contains(allTests.get(i))) {
                forgottenTests.add(allTests.get(i));
            }
        }

        StringBuffer forgottenTestsBuffer = new StringBuffer();
        
        for (int i = 0; i < forgottenTests.size(); i++){
            forgottenTestsBuffer.append("Missed test" + i + ": " + forgottenTests.get(i) + "\n");
        }

        assertTrue("At least 1 test is not reachable from AllTests.\n" + forgottenTestsBuffer.toString(), forgottenTests.size() == 0);
    }

    private void findAllTestsWritten(String path) {
        File[] f = new File[MAX];
        File temp = new File(path);
        if (temp.isDirectory()) {
            f = temp.listFiles();
            for (int i = 0; i < f.length; i++)
                findAllTestsWritten(path + "\\" + f[i].getName());
        } else {
            if (temp.getName().matches(".*Test.java")) {
                String name = temp.getName().substring(0, temp.getName().lastIndexOf("."));
                allTests.add(name);
            }
        }
    }

    private void checkTestCases(ArrayList<String> includedOrIntentionallyExcludedTests, Test test) {
        if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            String fullyQualifiedName = suite.getName();
            String fileNameOnly = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
            for (int i = 0; i < suite.testCount(); i++) {
                Test testElement = suite.testAt(i);
                if(testElement instanceof TestCase && !(testElement instanceof RapidTestCase))
                {
                	System.out.println(fullyQualifiedName);
                	if(!testElement.getClass().getName().equals(TestCase.class.getName() + "$1"))
                	{
                		System.out.println(testElement.getClass().getName() + " does not extend RapidTestCase.");
                	}
                }
                if (!includedOrIntentionallyExcludedTests.contains(fileNameOnly)) {
                    includedOrIntentionallyExcludedTests.add(fileNameOnly);
                }
                checkTestCases(includedOrIntentionallyExcludedTests, testElement);
            }
            return;
        } else
            return;
    }

    protected void tearDown() throws Exception {
      super.tearDown();  
    }
}
