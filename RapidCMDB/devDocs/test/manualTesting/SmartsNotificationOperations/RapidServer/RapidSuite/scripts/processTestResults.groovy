/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */


println  "**************************"

import junit.framework.Test
import junit.framework.TestResult
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter

def processor=new TestResultsProcessor("SmartsNotificationOperations");


processor.checkOperationLessThen("Add",["SmartsNotification","SmartsHistoricalNotification","RsEventJournal",],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("Remove",["SmartsNotification"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("Search",["SmartsNotification"],"AvarageDuration",0.03,true)

for(test in processor.tests)
{
    println "${test.getName()} . ${test.error}"
}
processor.generateResultsXml()

return processor.statsXml;

class TestResultsProcessor{
    def reportsMap;
    def statsXml;
    def tests;
    def testName;
    public TestResultsProcessor(testName){
       reportsMap=[:];
       tests=[];
       this.testName=testName;
       generateCompassStatisticsMap();

    }
    private void generateCompassStatisticsMap(){
        statsXml=application.RsApplication.getCompassStatistics()
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
            new File("${testName}-results.xml").withOutputStream {xmlOut ->
                def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                def junitTest = new JUnitTest(testName)
                xmlOutput.startTestSuite(junitTest)

                xmlOutput.addError (new ManualTestUnit("${testName}ResultXmlGeneration"),t2);
                junitTest.setCounts(tests.size(), 0, 1);
                xmlOutput.endTestSuite(junitTest)
                
            }
        }    
    }
    def transferResultsToHudson()
    {
        def ant = new AntBuilder()
        ant.scp(file:"${testName}-results.xml",todir:"root@192.168.1.130:/root/.hudson/jobs/RapidCMDBTests/workspace/ManualTestResults/",password:"molkay01",trust:"true")
    }
    def checkOperationLessThen(operation,modelList,property,value,checkExistance)
    {
        for(model in modelList){
            def modelValue=reportsMap.get(operation)?.get("modelReports")?.get(model)?.get(property)
            def testUnit=new ManualTestUnit("${operation}Operation.${model}.${property}");
            
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
            def testUnit=new ManualTestUnit("${operation}Operation.${model}.${property}");
            
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



