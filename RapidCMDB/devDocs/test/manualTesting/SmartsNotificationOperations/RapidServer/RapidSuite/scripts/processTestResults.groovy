/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */


println  "**************************"

def processor=new TestResultsProcessor();


processor.checkOperationLessThen("Add",["SmartsNotification","SmartsHistoricalNotification","RsEventJournal",],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("Remove",["SmartsNotification"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("Search",["SmartsNotification"],"AvarageDuration",0.03,true)

return processor.statsXml;

public class TestResultsProcessor{
    def reportsMap;
    def statsXml;
    public TestResultsProcessor(){
       reportsMap=[:];       
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
    def checkOperationLessThen(operation,modelList,property,value,checkExistance)
    {
        for(model in modelList){
            def modelValue=reportsMap.get(operation)?.get("modelReports")?.get(model)?.get(property)

            if(modelValue!=null)
            {
                if(Double.valueOf(modelValue)>value)
                {
                    println "${operation}Operation.${model}.${property} value ${modelValue} is larger than ${value}"
                }

                if(reportsMap.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")==0)
                {
                    println "${operation}Operation.${model}.NumberOfOperations value is equal to zero"
                }

            }
            else{
                if(checkExistance)
                {
                    println "Statistics value for ${operation}Operation.${model}.${property} does not exist"
                }
            }

        }

    }
    def checkOperationMoreThen(operation,modelList,property,value)
    {
        for(model in modelList){
            def modelValue=reportsMap.get(operation)?.get("modelReports")?.get(model)?.get(property)

            if(modelValue!=null)
            {
                if(Double.valueOf(modelValue)<value)
                {
                    println "${operation}Operation.${model}.${property} value ${modelValue} is smaller than ${value}"
                }

                if(reportsMap.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")=="0")
                {
                    println "${operation}Operation.${model}.NumberOfOperations value is equal to zero"
                }

            }
            else{
                if(checkExistance)
                {
                    println "Statistics value for ${operation}Operation.${model}.${property} does not exist"
                }
            }

        }

    }
}



