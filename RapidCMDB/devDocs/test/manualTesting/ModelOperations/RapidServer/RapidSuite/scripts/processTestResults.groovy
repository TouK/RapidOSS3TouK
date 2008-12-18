/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

import utils.TestResultsProcessor;

def processor=new TestResultsProcessor("ModelOperations");

processor.checkOperationLessThen("Add",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("Update",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("Remove",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.04,true)
processor.checkOperationLessThen("AddRelation",["Author","Person"],"AvarageDuration",0.06,true)
processor.checkOperationLessThen("RemoveRelation",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.015,true)
processor.checkOperationLessThen("Search",["Book","Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.03,true)




processor.generateResultsXml()
processor.transferResultsToHudson()

