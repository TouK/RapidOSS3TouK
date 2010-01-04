package utils
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 8:57:43 AM
 * To change this template use File | Settings | File Templates.
 */
class StatsConverter {
    def reportsMap;
    def statsXml;
    def testName;
    def logger;
    def logPrefix="statsConverter";
    def statsFile;
     public StatsConverter(testName,statsFilePath){
       reportsMap=[:];       
       this.testName=testName;
       if(statsFilePath!=null)
       {
            statsFile=new File(statsFilePath);
       }
       this.statsFile=statsFile;
       logger=Logger.getRootLogger();
       logger.warn(logPrefix+"started");
       generateCompassStatisticsMap();
    }
    private void generateCompassStatisticsMap(){
        try {
            
            if(statsFile==null)
            {
                statsXml=application.RapidApplication.getCompassStatistics();
            }
            else
            {
                statsXml=statsFile.getText();

            }
            
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
        catch(e)
        {
            logger.warn(logPrefix+"Exception occured while generating statistics map. Reason : ${e}",e);
            e.printStackTrace();
        }
    }
    public void generateTabbedStats(modelsList,propertyList)
    {
        try{
            def basePath="";
            if(statsFile!=null)
            {                   
                basePath=statsFile.getParent()+"/";
            }
            
            File file=new File(basePath+testName+"_tabbed_stats.txt");
            file.write("Operation\tModel\t");
            propertyList.each{ property ->
                file.append("${property}\t");
            }
            file.append("\r\n");

             modelsList.each{ modelsMap ->
                 def modelReports=reportsMap.get(modelsMap.operation)?.get("modelReports");
                 if(modelReports)
                 {
                    modelsMap.models.each{  model ->
                        def modelStats=modelReports.get(model);
                        if(modelStats)
                        {
                             file.append("${modelsMap.operation}\t${model}\t");
                            propertyList.each{ property ->
                                def value="";
                                if(property=="AverageCount")
                                {
                                    def avgDuration=modelStats.get("AvarageDuration");
                                    if(avgDuration && avgDuration!="0" )
                                    {
                                    	avgDuration=avgDuration.toDouble();
                                        value = 1 / avgDuration;
                                    }

                                }
                                else
                                {
                                	value=modelStats.get(property);
                                }
                                file.append("${value}\t");
                            }
                            file.append("\r\n");
                        }
                    }
                 }
             }
         }
         catch(e)
         {
             logger.warn(logPrefix+"Error while generating tabbed stats file. Reason ${e}",e);
             e.printStackTrace();
         }
    }
}