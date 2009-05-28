/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;
import utils.StatsConverter;

def processor=new TestResultsProcessor("ModelOperations");

processor.checkOperationLessThen("Add",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Update",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.07,true)
processor.checkOperationLessThen("Remove",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("AddRelation",["Author","Person"],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("RemoveRelation",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.015,true)
processor.checkOperationLessThen("Search",["Book","Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.03,true)

def memoryLimit=processor.getFirstMemory()+20

processor.checkValueLessThen("UsedMemory",processor.getUsedMemory(),memoryLimit,true)

processor.generateResultsXml()



def statModels=[
    [operation:"Add",models:["Fiction","ScienceFiction","Author","Person"]],
    [operation:"Remove",models:["Fiction","ScienceFiction","Author","Person"]],
    [operation:"Update",models:["Fiction","ScienceFiction","Author","Person"]],
    [operation:"AddRelation",models:["Author","Person"]],
    [operation:"RemoveRelation",models:["Fiction","ScienceFiction","Author","Person"]],
    [operation:"CountHits",models:["Fiction","ScienceFiction","Author","Person"]],
    [operation:"Search",models:["Book","Fiction","ScienceFiction","Author","Person"]]
];

def converter=new StatsConverter("ModelOperations",null);
converter.generateTabbedStats (statModels,["NumberOfOperations","AvarageDuration","AverageCount"]);

processor.transferResultsToHudson()