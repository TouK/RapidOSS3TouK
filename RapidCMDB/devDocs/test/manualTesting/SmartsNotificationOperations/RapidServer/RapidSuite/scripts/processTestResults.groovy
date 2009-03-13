/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;
import utils.StatsConverter;

def processor=new TestResultsProcessor("SmartsNotificationOperations");

processor.checkOperationLessThen("Add",["SmartsNotification","SmartsHistoricalNotification","RsEventJournal",],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Remove",["SmartsNotification"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("Search",["SmartsNotification"],"AvarageDuration",0.03,true)

def memoryLimit=processor.getFirstMemory()+20
memoryLimit+=RsEvent.countHits("alias:*")*0.002

processor.checkValueLessThen("UsedMemory",processor.getUsedMemory(),memoryLimit,true)


processor.generateResultsXml()
processor.transferResultsToHudson()


def statModels=[
    [operation:"Add",models:["SmartsNotification","RsEventJournal","SmartsHistoricalNotification"]],
    [operation:"Remove",models:["SmartsNotification"]],
    [operation:"Update",models:["SmartsNotification"]],
    [operation:"Search",models:["RsEvent","SmartsNotification","RsTopologyObject","relation.Relation","script.CmdbScript","auth.Role","auth.Group","auth.RsUser"]]
];

def converter=new StatsConverter("SmartsNotificationOperations",null);
converter.generateTabbedStats (statModels,["NumberOfOperations","AvarageDuration","AverageCount"]);

