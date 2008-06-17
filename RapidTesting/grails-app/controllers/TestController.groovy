import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.RegexFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.codehaus.groovy.grails.commons.GrailsApplication
import junit.framework.TestResult
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import org.apache.tools.ant.taskdefs.optional.junit.PlainJUnitResultFormatter
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.commons.lang.StringUtils
import junit.framework.TestSuite
import test.TestCase
import java.lang.reflect.Constructor

class TestController {
    def index = {  }

    def run = {  
        def testName = params.name;
        def testType = params.type;
        if(testName)
        {


            TestResult res= new TestResult();
            TestSuite suite = new TestSuite();
            if(testType == "test")
            {
                def classLoader = new GroovyClassLoader(grailsApplication.getClassLoader())
                classLoader.addClasspath ("test/integration");
                classLoader.addClasspath ("test/unit");
                def testClass = classLoader.loadClass(params.name);
                suite.addTestSuite(testClass);  // non-groovy test cases welcome, too.
            }
            else if(testType == "testcase")
            {
                String className = StringUtils.substringBeforeLast(params.name, ".");
                String methodName = StringUtils.substringAfterLast(params.name, ".");
                def classLoader = new GroovyClassLoader(grailsApplication.getClassLoader())
                classLoader.addClasspath ("test/integration");
                classLoader.addClasspath ("test/unit");
                def testClass = classLoader.loadClass(className);
                suite.addTest(TestSuite.createTest(testClass, methodName));  // non-groovy test cases welcome, too.
            }
            else
            {
                def testSuite = getTestSuite("test/${testName}", grailsApplication);
                testSuite.tests.each{test.Test test->
                    suite.addTestSuite(test.testClass);  // non-groovy test cases welcome, too.
                }
            }
            runTest(suite, "testOutput", res);

            render(text:new File("testOutput/html/junit-noframes.html").text);
        }
        else
        {
            render(text:"No test found");
        }

    }


    def getTestSuite(String dir, GrailsApplication grailsApplication)
    {
        def rootDir = new File(dir);
        def rootAbsPath = rootDir.getAbsolutePath();
        def testFiles = FileUtils.listFiles(rootDir, new RegexFileFilter(".*Test.groovy"), new TrueFileFilter());
        def classLoader = new GroovyClassLoader(grailsApplication.getClassLoader())
        classLoader.addClasspath ("test/integration");
        classLoader.addClasspath ("test/unit");
        test.TestSuite testSuite = new test.TestSuite(name:rootDir.name, tests:[]);
        testFiles.each{File testFile->
            String testPath = StringUtils.substringAfter(testFile.getAbsolutePath(), rootAbsPath);
            testPath = StringUtils.substring(testPath, 1, testPath.length());
            testPath = StringUtils.replaceChars(testPath, "/", ".")
            testPath = StringUtils.replaceChars(testPath, "\\", ".")
            Class testCaseClass = null;
            try
            {
                testCaseClass = classLoader.loadClass(StringUtils.substringBefore(testPath, ".groovy"));
                if(!GroovyTestCase.isAssignableFrom(testCaseClass))
                {
                    testCaseClass = null;    
                }
                else
                {
                    test.Test test = new test.Test(name:testCaseClass.name, testClass:testCaseClass, testCases:[]);
                    testSuite.tests.add(test);

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
        return testSuite;
    }

    def list =
    {    
        def rootDir = new File("test");
        def dirs = rootDir.listFiles();
        def testSuites = [:]
        dirs.each{File testDir->
            test.TestSuite suite = getTestSuite(testDir.getPath(), grailsApplication);
            testSuites[suite.name] = suite;
        }
        render(contentType:'text/xml') {
            Tests(){
                testSuites.each{String testSuiteName, test.TestSuite testSuite->
                     Test(name:testSuiteName, displayName:testSuiteName, type:"suite")
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

    def runTest(TestSuite suite, String testOutputDir, TestResult result)
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
}
