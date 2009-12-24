package com.ifountain.rcmdb.test.util

import junit.framework.TestSuite
import org.apache.commons.io.FileUtils
import junit.framework.TestCase
import org.apache.commons.lang.StringUtils
import com.ifountain.comp.utils.HttpUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 1:08:43 PM
* To change this template use File | Settings | File Templates.
*/
class AllTestTestUtils {
    public static junit.framework.TestSuite loadTests(Class allTestSuiteClass, List packagePaths)
    {
        TestSuite suite = new TestSuite(allTestSuiteClass);
        packagePaths.each{String path->
            suite.addTest (loadTests(allTestSuiteClass, path));
        }
        return suite;
    }
    public static junit.framework.TestSuite loadTests(Class allTestSuiteClass, String packagePath)
    {
        TestSuite suite = new TestSuite(packagePath);
        def testsPath = new File(packagePath);

        def possibleTestFiles = FileUtils.listFiles (testsPath, ["groovy", "java"] as String[], true);

        def testClasses = [];
        possibleTestFiles.each{File possibleTesFile->
            if(possibleTesFile.name.endsWith("Test.java") || possibleTesFile.name.endsWith("Test.groovy") || possibleTesFile.name.endsWith("Tests.groovy"))
            {
                def fileName =  possibleTesFile.canonicalPath.substring(testsPath.canonicalPath.length()+1);
                def className = StringUtils.substringBeforeLast(fileName, ".").replaceAll("/", ".").replaceAll("\\\\", ".");
                suite.addTestSuite(allTestSuiteClass.classLoader.loadClass (className));
            }
        }

        return suite;
    }
    //Sample url : "http://192.168.1.134:8080/job/RapidCMDBTests/2261/console"
    //should be the output of console url
    public static junit.framework.TestSuite loadTestsInHudsonOrder(Class allTestSuiteClass,hudsonUrl)
    {
        def testClassNames=[];
        //find testnames in hudson
        def testStartPrefix="Running test";
        def testEndPrefix="...";

        HttpUtils httpUtil=new HttpUtils();

        def res=httpUtil.doGetRequest (hudsonUrl,[:]);

        def startIndex=res.indexOf(testStartPrefix);
        while(startIndex>=0)
        {
            def  endIndex=res.indexOf(testEndPrefix,startIndex+1);
            if(endIndex>0)
            {
                def testName=res.substring(startIndex+testStartPrefix.length(),endIndex)?.trim();
                testClassNames.add(testName);
                startIndex=res.indexOf(testStartPrefix,endIndex+1);
            }
            else
            {
                startIndex=-1;
            }

        }
        //run them
        TestSuite suite = new TestSuite();
        testClassNames.each{ className ->
             println "loading test : ${className}"
             try{
                suite.addTestSuite(allTestSuiteClass.classLoader.loadClass (className));
             }
             catch(ClassNotFoundException e)
             {
                 println "! WARNING : test : ${className} not found";
             }
        }
        return suite;
    }
}