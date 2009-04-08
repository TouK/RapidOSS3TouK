package scripting

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.apache.log4j.Logger
import com.ifountain.rcmdb.scripting.ScriptObjectWrapper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 9:41:06 AM
* To change this template use File | Settings | File Templates.
*/
class ScriptObjectWrapperTest extends RapidCmdbTestCase {
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testScriptObjectWrapper()
    {
        Closure executionContextLoggerStatementClosure = {String datastoreEntryName->
            return """ ${DataStore.class.name}.put("${datastoreEntryName}", ${ExecutionContextManager.class.name}.getInstance().getExecutionContext()) """
        };
        def bindingParamNameToBePassed = "bindingParam1";
        def method1ReturnObjectValue = "method1"
        def scriptClassLoader = new GroovyClassLoader();
        Class scriptClass = scriptClassLoader.parseClass ("""
            ${executionContextLoggerStatementClosure("runContext")}
            return ${bindingParamNameToBePassed};
            def method1()
            {
                ${executionContextLoggerStatementClosure("method1Context")}
                return "${method1ReturnObjectValue}"
            }
        """);
        Script script = new ScriptObjectWrapper(scriptClass.newInstance());
        try
        {
            script.run ();
            fail("Should throw exception since parameter is not assigned");
        }catch(MissingPropertyException e)
        {
            assertEquals (bindingParamNameToBePassed, e.getProperty());
        }
        assertEquals (method1ReturnObjectValue, script.method1());

        //test with logger    
        def scriptLogger = Logger.getLogger("logger1");
        def bindingParams = [:]
        bindingParams[bindingParamNameToBePassed] = "paramValue"
        bindingParams[RapidCMDBConstants.LOGGER] = scriptLogger
        Binding binding = new Binding(bindingParams)
        script.setBinding (binding);
        assertEquals (bindingParams[bindingParamNameToBePassed], script.run ());
        assertEquals (method1ReturnObjectValue, script.method1());
        assertSame(scriptLogger, DataStore.get("method1Context")[RapidCMDBConstants.LOGGER]);
        assertSame(scriptLogger, DataStore.get("runContext")[RapidCMDBConstants.LOGGER]);
    }

    public void testScriptObjectWrapperWithBindingPassedWithConstructor()
    {
        def bindingParamNameToBePassed = "bindingParam1";
        def method1ReturnObjectValue = "method1"
        def scriptClassLoader = new GroovyClassLoader();
        Class scriptClass = scriptClassLoader.parseClass ("""
            return ${bindingParamNameToBePassed};
            def method1()
            {
                return "${method1ReturnObjectValue}"
            }
        """);
        def bindingParams = [:]
        bindingParams[bindingParamNameToBePassed] = "paramValue"
        Binding bindings = new Binding(bindingParams);
        Script script = new ScriptObjectWrapper(scriptClass.newInstance(), bindings);

        assertEquals (bindingParams[bindingParamNameToBePassed], script.run ());
        assertEquals (method1ReturnObjectValue, script.method1());
    }

    public void testScriptObjectWrapperWithSetAndgetProperty()
    {
        def scriptClassLoader = new GroovyClassLoader();
        Class scriptClass = scriptClassLoader.parseClass ("""
            return prop1;
        """);
        def prop1Value = "prop1Value";
        Script script = new ScriptObjectWrapper(scriptClass.newInstance());
        script.setProperty ("prop1", prop1Value);
        assertEquals (prop1Value, script.getProperty("prop1"))
    }

    public void testScriptObjectWrapperDoesNotOverwriteScriptBindsIfNotSpecified()
    {
        def scriptClassLoader = new GroovyClassLoader();
        Class scriptClass = scriptClassLoader.parseClass ("""
            return prop1;
        """);
        def prop1Value = "prop1Value";
        def script = scriptClass.newInstance();
        script.setProperty ("prop1", prop1Value);
        Script scriptWrapper = new ScriptObjectWrapper(script);
        assertEquals (prop1Value, scriptWrapper.getProperty("prop1"))
    }
}