package com.ifountain.rcmdb.test.util

import junit.framework.TestSuite
import org.apache.commons.io.FileUtils
import junit.framework.TestCase
import org.apache.commons.lang.StringUtils

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
        TestSuite suite = new TestSuite(allTestSuiteClass.name);
        def testsPath = new File(packagePath);

        def possibleTestFiles = FileUtils.listFiles (testsPath, ["groovy", "java"] as String[], true);

        def testClasses = [];
        possibleTestFiles.each{File possibleTesFile->
            if(possibleTesFile.name.endsWith("Test.java") || possibleTesFile.name.endsWith("Test.groovy"))
            {
                def fileName =  possibleTesFile.canonicalPath.substring(testsPath.canonicalPath.length()+1);
                def className = StringUtils.substringBeforeLast(fileName, ".").replaceAll("/", ".").replaceAll("\\\\", ".");
                suite.addTestSuite(allTestSuiteClass.classLoader.loadClass (className));
            }
        }

        return suite;
    }
}