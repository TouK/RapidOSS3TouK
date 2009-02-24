/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 24, 2009
 * Time: 10:33:44 AM
 * To change this template use File | Settings | File Templates.
 */
import utils.StatsConverter;

File f = new File("d:/works/auto manual tests/");
f.eachDir(){ dir ->
    dir.eachFile(){ File statsFile ->        
        def testName=statsFile.getName();
        if(testName.indexOf(".xml")>0 && testName.indexOf("stat")>=0)
        {
             testName=testName.replace(".xml","");
             println testName;
             if(testName.indexOf("odel")>0)
             {
                 def statModels=[
                     [operation:"Add",models:["Fiction","ScienceFiction","Author","Person"]],
                     [operation:"Remove",models:["Fiction","ScienceFiction","Author","Person"]],
                     [operation:"Update",models:["Fiction","ScienceFiction","Author","Person"]],
                     [operation:"AddRelation",models:["Author","Person"]],
                     [operation:"RemoveRelation",models:["Fiction","ScienceFiction","Author","Person"]],
                     [operation:"CountHits",models:["Fiction","ScienceFiction","Author","Person"]],
                     [operation:"Search",models:["Book","Fiction","ScienceFiction","Author","Person"]]
                 ];

                 def converter=new StatsConverter(testName,statsFile.getPath());
                 converter.generateTabbedStats (statModels,["NumberOfOperations","AvarageDuration","AverageCount"]);

             }
             else
             {
                def statModels=[
                    [operation:"Add",models:["SmartsNotification","RsEventJournal","SmartsHistoricalNotification"]],
                    [operation:"Remove",models:["SmartsNotification"]],
                    [operation:"Update",models:["SmartsNotification"]],
                    [operation:"Search",models:["RsEvent","SmartsNotification","RsTopologyObject","relation.Relation","script.CmdbScript","auth.Role","auth.Group","auth.RsUser"]]
                ];

                def converter=new StatsConverter(testName,statsFile.getPath());
                converter.generateTabbedStats (statModels,["NumberOfOperations","AvarageDuration","AverageCount"]);
             }
        }

    }
}