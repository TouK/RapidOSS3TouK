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
import junit.framework.TestResult
import junit.framework.TestSuite
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.lang.StringUtils
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.testing.TestingManager
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.servlet.mvc.ParameterCreationListener
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import grails.util.GrailsWebUtil

class TestController implements ApplicationContextAware{
    ApplicationContext applicationContext
    def index = {  }

    def run = {
        def testName = params.name;
        def testType = params.type;
        if(testName)
        {

            com.ifountain.testing.TestLock.isTestRunning = true;
            try
            {
                def prevAttributes = RequestContextHolder.getRequestAttributes();
                
                TestResult res= new TestResult();
                TestSuite suite = new TestSuite();
                def classLoader = new GroovyClassLoader(grailsApplication.getClassLoader())
                new RapidTestingConfiguration().testDirectories.each{File testDir->
                    classLoader.addClasspath (testDir.getAbsolutePath());   
                }
                if(testType == "test")
                {
                    def testClass = classLoader.loadClass(params.name);
                    suite.addTestSuite(testClass);  // non-groovy test cases welcome, too.
                }
                else if(testType == "testcase")
                {
                    String className = StringUtils.substringBeforeLast(params.name, ".");
                    String methodName = StringUtils.substringAfterLast(params.name, ".");
                    def testClass = classLoader.loadClass(className);
                    suite.addTest(TestSuite.createTest(testClass, methodName));  // non-groovy test cases welcome, too.
                }
                else
                {
                    def testSuite = getTestingManager(grailsApplication).testClasses[testName];
                    testSuite.tests.each{test.Test test->
                        suite.addTestSuite(test.testClass);
                    }
                }

                runTest(suite, "testOutput", res, applicationContext);
                RequestContextHolder.setRequestAttributes (prevAttributes);

                render(text:new File("testOutput/html/junit-noframes.html").text);
            }finally
            {
                com.ifountain.testing.TestLock.isTestRunning = false;    
            }
        }
        else
        {
            render(text:"No test found");
        }

    }



    def list =
    {    
        render(contentType:'text/xml') {
            Tests(){
                getTestingManager(grailsApplication).testSuites.each{test.TestSuite testSuite->
                     Test(name:testSuite.name, displayName:testSuite.name, type:"suite")
                     {
                         testSuite.tests.each{test.Test test->
                            Test(name:test.name, displayName:test.name, type:"test"){
                                test.testCases.each{ test.TestCase testCase->
                                    Test(name:testCase.test.name + "."+testCase.name, displayName:testCase.name, type:"testcase");        
                                }
                            }
                         }
                     }
                }
            }
        }
    }

    def runTest(TestSuite suite, String testOutputDir, TestResult result, appCtx)
    {
        def ANT = new AntBuilder();
        ANT.delete(dir:testOutputDir);
        ANT.mkdir(dir:testOutputDir+"/plain");
        ANT.mkdir(dir:testOutputDir+"/html");
        ANT.mkdir(dir:testOutputDir+"/xml");
        for (TestSuite test in suite.tests()) {
            new File("${testOutputDir}/TEST-${test.name}.xml").withOutputStream {xmlOut ->
                new File("${testOutputDir}/plain/TEST-${test.name}.txt").withOutputStream {plainOut ->

                    def savedOut = System.out
                    def savedErr = System.err

                    try {
                        def outBytes = new ByteArrayOutputStream()
                        def errBytes = new ByteArrayOutputStream()
                        System.out = new PrintStream(outBytes)
                        System.err = new PrintStream(errBytes)
                        def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                        def plainOutput = new PlainJUnitResultFormatter(output: plainOut)
                        def junitTest = new JUnitTest(test.name)
                        plainOutput.startTestSuite(junitTest)
                        xmlOutput.startTestSuite(junitTest)
                        savedOut.println "Running test ${test.name}..."
                        def start = System.currentTimeMillis()
                        def runCount = 0
                        def failureCount = 0
                        def errorCount = 0
                        def tests = [];
                        if(test instanceof TestSuite)
                        {
                            for (i in 0..<test.testCount()) {
                                tests.add(test.testAt(i));
                            }
                        }
                        else
                        {
                            tests.add(test);    
                        }
                        for (t in tests) {
                            GrailsWebUtil.bindMockWebRequest(appCtx);
                            def thisTest = new TestResult()
                            thisTest.addListener(xmlOutput)
                            thisTest.addListener(plainOutput)
                            System.out.println "--Output from ${t.name}--"
                            System.err.println "--Output from ${t.name}--"

                            println "TestStart ${[test, t, thisTest]}"
                            if(test instanceof TestSuite)
                            {
                                test.runTest (t, thisTest)
                            }
                            else
                            {
                                suite.runTest (t, thisTest);
                            }
                            println "TestStart ${[test, t, thisTest]}"
                            runCount += thisTest.runCount()
                            failureCount += thisTest.failureCount()
                            errorCount += thisTest.errorCount()

                            if (thisTest.errorCount() > 0 || thisTest.failureCount() > 0) {
                                savedOut.println "FAILURE"
                                thisTest.errors().each {result.addError(t, it.thrownException())}
                                thisTest.failures().each {result.addFailure(t, it.thrownException())}
                            }
                            else {savedOut.println "SUCCESS"}
                        }
                        junitTest.setCounts(runCount, failureCount, errorCount);
                        junitTest.setRunTime(System.currentTimeMillis() - start)

                        def outString = outBytes.toString()
                        def errString = errBytes.toString()
                        new File("${testOutputDir}/TEST-${test.name}-out.txt").write(outString)
                        new File("${testOutputDir}/TEST-${test.name}-err.txt").write(errString)
                        plainOutput.setSystemOutput(outString)
                        plainOutput.setSystemError(errString)
                        plainOutput.endTestSuite(junitTest)
                        xmlOutput.setSystemOutput(outString)
                        xmlOutput.setSystemError(errString)
                        xmlOutput.endTestSuite(junitTest)
                    } finally {
                        System.out = savedOut
                        System.err = savedErr
                    }
                }
            }
        }
        ANT.junitreport(toDir:testOutputDir+"/xml"){
            ANT.fileset(dir:testOutputDir);
            ANT.report(format:"noframes", todir:testOutputDir+"/html");
        }
    }

    def getTestingManager(GrailsApplication grailsApplication)
    {
        if(TestingManager.getInstance() == null)
        {
            TestingManager.initializeManager(new RapidTestingConfiguration(), grailsApplication.classLoader);
        }
        return TestingManager.getInstance();
    }
}
