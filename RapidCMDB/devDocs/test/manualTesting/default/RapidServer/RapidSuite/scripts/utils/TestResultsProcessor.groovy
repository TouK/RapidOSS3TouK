package utils

import com.ifountain.rcmdb.util.DataStore
import junit.framework.Test
import junit.framework.TestResult
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 18, 2008
 * Time: 4:19:04 PM
 * To change this template use File | Settings | File Templates.
 */
class TestResultsProcessor{
    def reportsMap;
    def statsXml;
    def tests;
    def testName;
    def logger;
    def logPrefix="processTestResults";
    static String firstMemoryUsedKey="ManualTestingResultsFirstUsedMemory"
    public TestResultsProcessor(testName){
       reportsMap=[:];
       tests=[];
       this.testName=testName;
       logger=Logger.getRootLogger();
       logger.warn(logPrefix+"started");
       generateCompassStatisticsMap();
    }
    public static long getFirstMemory()
    {
        def firstMemory=DataStore.get(firstMemoryUsedKey);
        if(firstMemory!=null)
        {
            firstMemory=Long.valueOf(firstMemory);
        }
        else{
           firstMemory=0; 
        }
        return firstMemory;
    }
    public static void recordFirstMemory()
    {
        DataStore.put(firstMemoryUsedKey,getUsedMemory());
    }
    public static long getUsedMemory()
    {
        def total = Runtime.getRuntime().totalMemory() / Math.pow(2,20);
        def free = Runtime.getRuntime().freeMemory() / Math.pow(2,20);
        return total-free;
    }
    private void generateCompassStatisticsMap(){
        statsXml=application.RapidApplication.getCompassStatistics()
        def parser = new XmlParser()
        def stats = parser.parseText(statsXml)

        for(report in stats.Report){

            reportsMap[report.@Operation]=[:];
            reportsMap[report.@Operation]['report']=report.attributes()

            reportsMap[report.@Operation]['modelReports']=[:]

            for(modelReport in report.ModelReport)
            {
                reportsMap[report.@Operation]['modelReports'][modelReport.@ModelName]=modelReport.attributes()
            }


        }
    }
    def generateResultsXml()
    {
        try{

            new File("${testName}-results.xml").withOutputStream {xmlOut ->

                def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                def junitTest = new JUnitTest(testName)
                xmlOutput.startTestSuite(junitTest)
                def failureCount = 0;
                def errorCount= 0;

                junitTest.setRunTime(0)
                for(test in tests)
                {
                    xmlOutput.startTest(test)
                    if(test.hasError())
                    {
                        xmlOutput.addFailure (test,test.getError());
                        failureCount++;
                    }
                    xmlOutput.endTest(test)
                }


                junitTest.setCounts(tests.size(), failureCount, errorCount);

                xmlOutput.endTestSuite(junitTest)

            }
        }
        catch(Throwable t2)
        {
            logger.warn("Error occured while generating Results XML",t2);
            new File("${testName}-results.xml").withOutputStream {xmlOut ->
                def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                def junitTest = new JUnitTest(testName)
                xmlOutput.startTestSuite(junitTest)

                xmlOutput.addError (new ManualTestUnit("${testName}ResultXmlGeneration"),t2);
                junitTest.setCounts(tests.size(), 0, 1);
                xmlOutput.endTestSuite(junitTest)

            }

        }

        try{

            def statsFile=new File("${testName}-stats.xml");
            statsFile.write(statsXml);

        }
        catch(Throwable t2)
        {
            logger.warn("Error occured while generating Stats XML",t2);
            new File("${testName}-stats.xml").withOutputStream {xmlOut ->
                def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                def junitTest = new JUnitTest(testName)
                xmlOutput.startTestSuite(junitTest)

                xmlOutput.addError (new ManualTestUnit("${testName}StatsXmlGeneration"),t2);
                junitTest.setCounts(tests.size(), 0, 1);
                xmlOutput.endTestSuite(junitTest)

            }

        }
    }
    def transferResultsToHudson()
    {
        def ant = new AntBuilder()
        ant.scp(file:"${testName}-results.xml",todir:"root@192.168.1.134:/root/.hudson/jobs/RapidCMDBTests/workspace/ManualTestResults/",password:"molkay01",trust:"true")
    }
    def checkValueLessThen(paramName,paramValue,checkValue,checkExistance)
    {
        def testUnit=new ManualTestUnit("${testName}.${paramName}");
        if(paramValue!=null)
        {
            if(paramValue>checkValue)
            {
                testUnit.setError("${paramName} value ${paramValue} is larger than ${checkValue}");
            }
        }
        else{
            if(checkExistance)
            {
                testUnit.setError("${paramName} value is null");
            }
        }
        tests.add(testUnit);
    }
    def checkValueMoreThen(paramName,paramValue,checkValue,checkExistance)
    {
        def testUnit=new ManualTestUnit("${testName}.${paramName}");
        if(paramValue!=null)
        {
            if(paramValue<checkValue)
            {
                testUnit.setError("${paramName} value ${paramValue} is smaller than ${checkValue}");
            }
        }
        else{
            if(checkExistance)
            {
                testUnit.setError("${paramName} value is null");
            }
        }
        tests.add(testUnit);
    }
    def checkOperationLessThen(operation,modelList,property,value,checkExistance)
    {
        for(model in modelList){
            def modelValue=reportsMap.get(operation)?.get("modelReports")?.get(model)?.get(property)
            def testUnit=new ManualTestUnit("${testName}.${operation}Operation.${model}.${property}");

            if(modelValue!=null)
            {
                if(Double.valueOf(modelValue)>value)
                {
                    testUnit.setError("${operation}Operation.${model}.${property} value ${modelValue} is larger than ${value}");
                }

                if(reportsMap.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")==0)
                {
                    testUnit.setError("${operation}Operation.${model}.NumberOfOperations value is equal to zero");
                }

            }
            else{
                if(checkExistance)
                {
                    testUnit.setError("Statistics value for ${operation}Operation.${model}.${property} does not exist");
                }
            }
            tests.add(testUnit);

        }

    }
    def checkOperationMoreThen(operation,modelList,property,value)
    {
        for(model in modelList){
            def modelValue=reportsMap.get(operation)?.get("modelReports")?.get(model)?.get(property)
            def testUnit=new ManualTestUnit("${testName}.${operation}Operation.${model}.${property}");

            if(modelValue!=null)
            {
                if(Double.valueOf(modelValue)<value)
                {
                    testUnit.setError("${operation}Operation.${model}.${property} value ${modelValue} is smaller than ${value}");

                }

                if(reportsMap.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")=="0")
                {
                    testUnit.setError("${operation}Operation.${model}.NumberOfOperations value is equal to zero");
                }

            }
            else{
                if(checkExistance)
                {
                    testUnit.setError("Statistics value for ${operation}Operation.${model}.${property} does not exist");
                }
            }
            tests.add(testUnit);
        }

    }
}

class ManualTestUnit implements Test {
    def name;
    def error;

    public ManualTestUnit(String name) {
        this.name = name;
        error=null;
    }

    public void setError(String errorMessage)
    {
        println "Error added to test unit : ${errorMessage}"
        error=new Exception(errorMessage);
    }

    public int countTestCases() {
        return 0;
    }
    public void run(TestResult arg0) {
    }

    public String getName() {
        return name;
    }
    public String name() {
        return name;
    }

    public boolean hasError()
    {
        return error!=null;
    }
    public Exception getError()
    {
        return error;
    }


}