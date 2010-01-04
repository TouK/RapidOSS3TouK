import application.RapidApplication
import groovy.xml.MarkupBuilder
import grails.util.GrailsUtil
import org.apache.commons.lang.exception.ExceptionUtils
import junit.framework.Assert

/*
    User should write the name of the script class in testScripts list. The methods starting with
    test prefix will be executed. Result of the test will be sent to client. Also, details about
    execution will be printed into script log file
    !!!!Important!!!! You don't need to define test scripts as CmdbScript. Just write the name of the script in
    the following list
*/
//=====================================CONFIGURATION==========================================================
def testScripts = []
//===============================================================================================



def scriptClasses = []
def gcl = new GroovyClassLoader();
gcl.addClasspath ("scripts")
testScripts.each{testScriptName->
    def scriptClass = gcl.parseClass(new File("scripts/${testScriptName}.groovy"));
    scriptClass.metaClass.methodMissing = {String methodName, args->
        if(methodName.startsWith("assert"))
        {
            try{
                return Assert.invokeMethod (methodName, args)
            }catch(groovy.lang.MissingMethodException ex)
            {
                if(ex.getMethod() == methodName)
                {
                    throw new MissingMethodException(methodName, this.class, args);
                }
            }
        }
    }
    scriptClasses << [scriptName:testScriptName, scriptClass:scriptClass];
}
def allTestResults = [:]
def total = 0;
def failure = 0
def passed = 0
def testUtility = RapidApplication.getUtility("ScriptTestUtility");
scriptClasses.each{Map classConfig->
    String scriptName = classConfig.scriptName;
    Class scriptClass = classConfig.scriptClass
    def scriptInstance = scriptClass.newInstance();
    scriptInstance.setProperty ("logger", logger);
    def testResults = testUtility.runTests(scriptInstance, logger);
    total += testResults.size();
    def failureCount = testResults.findAll{!it.isPassed}.size();
    failure += failureCount;
    allTestResults[scriptName] = [testResults:testResults, total:testResults.size(), failure:failureCount];
}


def sw = new StringWriter();
def mb = new MarkupBuilder(sw)
mb.AllTestResults(Total:total, Failure:failure, Passed:(total-failure)){
    allTestResults.each{scriptName, Map testResults->
        mb.TestResults(ScriptName:scriptName, Total:testResults.total, Failure:testResults.failure, Passed:testResults.total - testResults.failure)
        {
            testResults.testResults.each{testResult->

                mb.TestResult([TestName:testResult.testName,  IsPassed:testResult.isPassed])
                {
                    testResult.exceptions.each{Throwable e->
                        mb.Exception(ExceptionUtils.getFullStackTrace(GrailsUtil.deepSanitize(e)))
                    }
                }
            }
        }

    }
}

return sw.toString();

