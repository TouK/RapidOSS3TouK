/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;

def processor=new TestResultsProcessor("SmartsNotificationOperations");

processor.checkOperationLessThen("Add",["SmartsNotification"],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Search",["RsEvent"],"AvarageDuration",0.03,true)

processor.checkValueLessThen("UsedMemory",processor.getFirstMemory(),processor.getFirstMemory()+20,true)

processor.generateResultsXml()
processor.transferResultsToHudson()

