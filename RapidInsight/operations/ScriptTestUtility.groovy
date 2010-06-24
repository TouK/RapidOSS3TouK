import org.apache.log4j.Logger

public class ScriptTestUtility
{
	def runTests(scriptObject, logger = Logger.getLogger("ScriptTestUtility"))
	{
        def testResults = [];
        logger.warn("-----------------------------------------------------------------------------------------");
		logger.warn("--------------------Running Tests of ${scriptObject.class.name} -------------------------");
		def runCount=0;
		def failCount=0;
		def tesMethods = scriptObject.metaClass.methods.findAll{ it.name.startsWith("test")};
		tesMethods.each { testMethod ->
            ScriptTestResult res =  runTest(scriptObject, testMethod.name, logger);
            testResults << res;
            if(!res.isPassed)
            {
                failCount++;
            }
			runCount++;
		}
		logger.warn("Runned ${runCount} tests of ${scriptObject.class.name}, ${failCount} tests Failed");
		return testResults;
	}
	def runTest(scriptObject, testName, logger = Logger.getLogger("ScriptTestUtility"))
	{
		logger.warn("-------------------------------");
		logger.warn("running test ${testName}");
        ScriptTestResult res = new ScriptTestResult(className:scriptObject.class.name, testName:testName);
		try{
			scriptObject.setUp();
		}
		catch(MissingMethodException e)
        {
            if(e.getType().name != scriptObject.class.name || e.getMethod()!="setUp")
            {
                res.exceptions.add(e);
                logger.warn("${testName} Exception occured while executing setUp ${e}");
            }
        }
		catch(Throwable e)
		{
            res.exceptions.add(e);
            logger.warn("${testName} Exception occured while executing setUp ${e}");
		}

		try{
            if(!res.hasException())
            {
                scriptObject."${testName}"();
                res.isPassed = true;
                logger.warn("${testName} runned successfully")
            }
		}
		catch(Throwable e)
		{
            res.exceptions.add(e);
            logger.warn("error occured while running test ${testName} ${e}",e);
			logger.warn("${testName} failed")
		}
		finally{
			try{
				scriptObject.tearDown();
			}
			catch(MissingMethodException e)
            {
                if(e.getType().name != scriptObject.class.name || e.getMethod()!="tearDown")
                {
                    res.isPassed = false;
                    res.exceptions.add(e);
                    logger.warn("${testName} Exception occured while executing tearDown ${e}");
                }
            }
			catch(Throwable e)
			{
                res.isPassed = false;
                res.exceptions.add(e);
                logger.warn("${testName} Exception occured while executing tearDown ${e}");
			}
		}


		return res;
	}
}

class ScriptTestResult
{
    String className;
    String testName;
    def isPassed = false;
    def exceptions = [];
    def hasException()
    {
        return !exceptions.isEmpty();
    }
}