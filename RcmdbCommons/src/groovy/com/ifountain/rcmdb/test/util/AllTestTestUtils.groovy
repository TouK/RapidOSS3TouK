package com.ifountain.rcmdb.test.util

import junit.framework.TestSuite
import org.apache.commons.io.FileUtils
import junit.framework.TestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 12, 2009
* Time: 1:08:43 PM
* To change this template use File | Settings | File Templates.
*/
class AllTestTestUtils {
    public static junit.framework.TestSuite loadTests(Class allTestSuiteClass, String filePathToLoadTests)
    {
        TestSuite suite = new TestSuite(allTestSuiteClass.name);
        def testsPath = new File(filePathToLoadTests);

        def possibleTestFiles = FileUtils.listFiles (testsPath, ["groovy", "java"] as String[], true);
        def testClasses = [];
        possibleTestFiles.each{File possibleTesFile->
            def gcl = new GroovyClassLoader();
            gcl.addClasspath (testsPath.getCanonicalPath());
            gcl.parseClass (possibleTesFile)
            def classes = gcl.getLoadedClasses();
            classes.each{Class cls->
                def lowerCasedName = cls.name.toLowerCase();
                if((lowerCasedName.endsWith("test") || lowerCasedName.endsWith("tests")) && TestCase.isAssignableFrom(cls) && !(lowerCasedName.endsWith("alltests") || lowerCasedName.endsWith("alltest")))
                {
                    suite.addTestSuite(allTestSuiteClass.classLoader.loadClass(cls.name));
                }
            }
        }

        return suite;
    }
}