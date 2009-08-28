package utilityTests

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.comp.test.util.logging.TestLogUtils
import junit.framework.AssertionFailedError

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Aug 28, 2009
* Time: 3:05:56 PM
* To change this template use File | Settings | File Templates.
*/
class ScriptTestUtilityTest extends RapidCmdbTestCase{
    GroovyClassLoader gcl;
    Class scriptTestUtility;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        gcl = new GroovyClassLoader(ScriptTestUtilityTest.class.classLoader)
        gcl.addClasspath ("${getWorkspaceDirectory()}/RapidInsight/operations/");
        scriptTestUtility = gcl.loadClass("ScriptTestUtility")
        DataStore.clear();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        DataStore.clear();
    }

    public void testOnlyLoadsMethodsStartingWithTest()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def scriptClass = gcl.parseClass ("""
            def method1()
            {
                ${DataStore.class.name}.get("methods").add("method1");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
            }
            def test2()
            {
                ${DataStore.class.name}.get("methods").add("test2");
            }
            def thisIsNotAtest()
            {
                ${DataStore.class.name}.get("methods").add("thisIsNotAtest");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResults = newInstance.runTests(scriptInstance, TestLogUtils.log);
        assertEquals (2, testResults.size());
        assertTrue(testResults[0].isPassed);
        assertEquals(scriptClass.name, testResults[0].className);
        assertEquals("test1", testResults[0].testName);
        assertTrue(testResults[1].isPassed);
        assertEquals(scriptClass.name, testResults[1].className);
        assertEquals("test2", testResults[1].testName);
        assertEquals (2, executedMethods.size())
        assertEquals (["test1", "test2"], executedMethods)
    }

    public void testExecutesSetUpBeforeTestAndTearDownAfterTest()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
            }
            def test2()
            {
                ${DataStore.class.name}.get("methods").add("test2");
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResults = newInstance.runTests(scriptInstance, TestLogUtils.log);
        assertEquals (2, testResults.size());
        assertTrue(testResults[0].isPassed);
        assertEquals(scriptClass.name, testResults[0].className);
        assertEquals("test1", testResults[0].testName);
        assertTrue(testResults[1].isPassed);
        assertEquals(scriptClass.name, testResults[1].className);
        assertEquals("test2", testResults[1].testName);
        assertEquals (["setUp", "test1", "tearDown","setUp", "test2", "tearDown"], executedMethods);
    }

    public void testContinueToExecuteTestEvenIfAnyOfThemFailed()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage = "this is a message"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
                throw new Exception("${exceptionMessage}")
            }
            def test2()
            {
                ${DataStore.class.name}.get("methods").add("test2");
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResults = newInstance.runTests(scriptInstance, TestLogUtils.log);
        assertEquals (2, testResults.size());
        assertFalse(testResults[0].isPassed);
        assertEquals(scriptClass.name, testResults[0].className);
        assertEquals("test1", testResults[0].testName);
        assertEquals(1, testResults[0].exceptions.size());
        assertEquals(exceptionMessage, testResults[0].exceptions[0].message);

        assertTrue(testResults[1].isPassed);
        assertEquals(scriptClass.name, testResults[1].className);
        assertEquals("test2", testResults[1].testName);
        assertEquals (["setUp", "test1", "tearDown","setUp", "test2", "tearDown"], executedMethods);
    }

    public void testIfSetupThrowsExceptionTestWillNotBeExecutedButTearDownWillBeExecuted()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage = "this is a message"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
                throw new Exception("${exceptionMessage}")
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResult = newInstance.runTest(scriptInstance, "test1", TestLogUtils.log);
        assertEquals (1, testResult.exceptions.size());
        assertFalse(testResult.isPassed);
        assertEquals (exceptionMessage, testResult.exceptions[0].getMessage());
        assertEquals (["setUp", "tearDown"], executedMethods);
    }

    public void testIfTearDownThrowsExceptionTestWillFail()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage = "this is a message"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
                throw new Exception("${exceptionMessage}")
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResult = newInstance.runTest(scriptInstance, "test1", TestLogUtils.log);
        assertEquals (1, testResult.exceptions.size());
        assertFalse (testResult.isPassed);
        assertEquals (exceptionMessage, testResult.exceptions[0].getMessage());
        assertEquals (["setUp", "test1", "tearDown"], executedMethods);
    }
    public void testIfTestThrowsExceptionTearDownWillBeExecuted()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage = "this is a message"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
                throw new Exception("${exceptionMessage}")
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResult = newInstance.runTest(scriptInstance, "test1", TestLogUtils.log);
        assertEquals (1, testResult.exceptions.size());
        assertFalse (testResult.isPassed);
        assertEquals (exceptionMessage, testResult.exceptions[0].getMessage());
        assertEquals (["setUp", "test1", "tearDown"], executedMethods);
    }

    public void testIfTestThrowsAssertionErrorTearDownWillBeExecuted()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage = "this is a message"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
                throw new ${AssertionFailedError.class.name}("${exceptionMessage}")
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResult = newInstance.runTest(scriptInstance, "test1", TestLogUtils.log);
        assertEquals (1, testResult.exceptions.size());
        assertFalse (testResult.isPassed);
        assertEquals (exceptionMessage, testResult.exceptions[0].getMessage());
        assertEquals (["setUp", "test1", "tearDown"], executedMethods);
    }

    public void testIfTestAndTearDownThrowsExceptionBothExceptionsWillBeAddedToResultExceptionList()
    {
        def executedMethods = []
        DataStore.put ("methods", executedMethods);
        def exceptionMessage1 = "this is a message1"
        def exceptionMessage2 = "this is a message2"
        def scriptClass = gcl.parseClass ("""
            def setUp()
            {
                ${DataStore.class.name}.get("methods").add("setUp");
            }
            def test1()
            {
                ${DataStore.class.name}.get("methods").add("test1");
                throw new Exception("${exceptionMessage1}")
            }
            def tearDown()
            {
                ${DataStore.class.name}.get("methods").add("tearDown");
                throw new Exception("${exceptionMessage2}")
            }
        """)
        def scriptInstance = scriptClass.newInstance();
        def newInstance = scriptTestUtility.newInstance()
        def testResult = newInstance.runTest(scriptInstance, "test1", TestLogUtils.log);
        assertEquals (2, testResult.exceptions.size());
        assertEquals (exceptionMessage1, testResult.exceptions[0].getMessage());
        assertEquals (exceptionMessage2, testResult.exceptions[1].getMessage());
        assertEquals (["setUp", "test1", "tearDown"], executedMethods);
    }
}