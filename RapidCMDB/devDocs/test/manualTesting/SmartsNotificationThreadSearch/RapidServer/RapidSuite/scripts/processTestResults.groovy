/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;
import utils.StatsConverter;

def processor=new TestResultsProcessor("SmartsNotificationThreadSearch");

processor.checkOperationLessThen("Add",["SmartsNotification"],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Search",["RsEvent"],"AvarageDuration",0.03,true)

//if notificationOperationTest also runs at the same time, these methods will also be done  by notificationconnector,
// we check if they exists with parameter false
processor.checkOperationLessThen("Add",["SmartsHistoricalNotification","RsEventJournal"],"AvarageDuration",0.07,false)
processor.checkOperationLessThen("Remove",["SmartsNotification"],"AvarageDuration",0.04,false)
processor.checkOperationLessThen("Search",["SmartsNotification"],"AvarageDuration",0.03,false)

def memoryLimit=processor.getFirstMemory()+20
memoryLimit+=RsEvent.countHits("alias:*")*0.004

processor.checkValueLessThen("UsedMemory",processor.getUsedMemory(),memoryLimit,true)

processor.generateResultsXml()



def statModels=[
    [operation:"Add",models:["SmartsNotification","RsEventJournal","SmartsHistoricalNotification"]],
    [operation:"Remove",models:["SmartsNotification"]],
    [operation:"Update",models:["SmartsNotification"]],
    [operation:"Search",models:["RsEvent","SmartsNotification","RsTopologyObject","relation.Relation","script.CmdbScript","auth.Role","auth.Group","auth.RsUser"]]
];

def converter=new StatsConverter("SmartsNotificationThreadSearch",null);
converter.generateTabbedStats (statModels,["NumberOfOperations","AvarageDuration","AverageCount"]);


processor.transferResultsToHudson()