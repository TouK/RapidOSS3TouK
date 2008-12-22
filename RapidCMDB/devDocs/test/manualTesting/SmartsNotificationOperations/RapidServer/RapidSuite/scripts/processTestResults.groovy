/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;

def processor=new TestResultsProcessor("SmartsNotificationOperations");

processor.checkOperationLessThen("Add",["SmartsNotification","SmartsHistoricalNotification","RsEventJournal",],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Remove",["SmartsNotification"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("Search",["SmartsNotification"],"AvarageDuration",0.03,true)

def memoryLimit=processor.getFirstMemory()+20
memoryLimit+=RsEvent.countHits("alias:*")*0.002

processor.checkValueLessThen("UsedMemory",processor.getUsedMemory(),memoryLimit,true)


processor.generateResultsXml()
processor.transferResultsToHudson()

