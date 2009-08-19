/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.testing

import org.apache.commons.lang.StringUtils
import test.TestSuite
import test.Test
import com.ifountain.comp.file.DirListener

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 20, 2008
 * Time: 3:53:35 PM
 * To change this template use File | Settings | File Templates.
 */
class TestingManager extends DirListener{
    private static TestingManager manager;
    public Map testClasses;
    public List testSuites;
    def config;
    ClassLoader parentClassLoader;

    public static TestingManager getInstance()
    {
        return manager;
    }

    public static TestingManager initializeManager(config, parentClassLoader)
    {
        manager = new TestingManager(config, parentClassLoader);
    }


    private TestingManager(config, parentClassLoader)
    {
        println "props set"
        this.parentClassLoader = parentClassLoader;
        testClasses = new HashMap();
        testSuites = [];
        this.config = config;
        config.testDirectories.each{
            String testSuiteName = it.getName();
            def testSuite = new TestSuite(name:testSuiteName, tests:[]);
            testClasses[testSuiteName] = testSuite;
            testSuites += testSuite;
        }
        println "CONF:"+this.config.testDirectories;
        initialize(this.config.testDirectories, [".svn":".svn", "CVS":"CVS"]);
    }

    public void fileDeleted(File testFile)
    {

    }
    public void fileChanged(File testFile)
    {
        println "Found test :${testFile}"
        def classLoader = new GroovyClassLoader(parentClassLoader)
        config.testDirectories.each{File rootDir->
            classLoader.addClasspath (rootDir.getAbsolutePath());
        }
        config.testDirectories.each{ File rootTestFile->
            if(testFile.getAbsolutePath().startsWith(rootTestFile.getAbsolutePath()))
            {
                String testSuiteName = rootTestFile.getName();
                def testSuite = testClasses.get(testSuiteName);
                String testPath = StringUtils.substringAfter(testFile.getAbsolutePath(), rootTestFile.getAbsolutePath());
                testPath = StringUtils.substring(testPath, 1, testPath.length());
                testPath = StringUtils.replaceChars(testPath, "/", ".")
                testPath = StringUtils.replaceChars(testPath, "\\", ".")

                String testName = StringUtils.substringBefore(testPath, ".groovy");
                try
                {
                    Class testCaseClass = classLoader.loadClass(testName);
                    if(junit.framework.TestCase.isAssignableFrom(testCaseClass))
                    {
                        Test test = testClasses.get(testName);
                        if(test == null)
                        {
                            test = new Test(name:testName, testClass:testCaseClass, testCases:[]);
                            testClasses[testName] = test;
                            testSuite.tests.add(test);
                        }
                        else
                        {
                            test.testCases.clear();
                        }
                        test.testClass = testCaseClass;

                        testCaseClass.methods.each{
                            if(it.name.startsWith("test"))
                            {
                                test.testCases.add(new test.TestCase(name:it.name, test:test));
                            }
                        }
                    }
                }
                catch(Throwable t)
                {
                }
            }
        }
    }


}