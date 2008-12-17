/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 17, 2008
 * Time: 1:08:14 PM
 * To change this template use File | Settings | File Templates.
 */

def statsXml=application.RsApplication.getCompassStatistics()

def parser = new XmlParser()
def stats = parser.parseText(statsXml)

println  "**************************"

def reportsMap=[:]
for(report in stats.Report){

	reportsMap[report.@Operation]=[:];
	reportsMap[report.@Operation]['report']=report.attributes()

	reportsMap[report.@Operation]['modelReports']=[:]

	for(modelReport in report.ModelReport)
	{
		reportsMap[report.@Operation]['modelReports'][modelReport.@ModelName]=modelReport.attributes()
	}


}

/*
reportsMap.each{  operation, report ->
	println "report ${report}"

	report.modelReports.each{ model, modelReport ->
		println "inreport ${modelReport}"
	}

}
*/

checkLessThen(reportsMap,"Add",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06)
checkMoreThen(reportsMap,"Add",["Fiction","ScienceFiction","Author","Person"],"NumberOfOperations",0)

checkLessThen(reportsMap,"Update",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06)
checkMoreThen(reportsMap,"Update",["Fiction","ScienceFiction","Author","Person"],"NumberOfOperations",0)

checkLessThen(reportsMap,"Remove",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.04)
checkMoreThen(reportsMap,"Remove",["Fiction","ScienceFiction","Author","Person"],"NumberOfOperations",0)

checkLessThen(reportsMap,"AddRelation",["Author","Person"],"AvarageDuration",0.06)
checkMoreThen(reportsMap,"AddRelation",["Author","Person"],"NumberOfOperations",0)

checkLessThen(reportsMap,"RemoveRelation",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.015)
checkMoreThen(reportsMap,"RemoveRelation",["Fiction","ScienceFiction","Author","Person"],"NumberOfOperations",0)

checkLessThen(reportsMap,"Search",["Book","Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.03)
checkMoreThen(reportsMap,"Search",["Book","Fiction","ScienceFiction","Author","Person"],"NumberOfOperations",0)

def checkLessThen(reports,operation,modelList,property,value)
{
	for(model in modelList){
		def modelValue=reports.get(operation)?.get("modelReports")?.get(model)?.get(property)

		if(modelValue!=null)
		{
			if(Double.valueOf(reports[operation]['modelReports'][model][property])>value)
			{
				println "${operation}Operation.${model}.${property} value ${modelValue} is larger than ${value}"
			}

		}

	}

}
def checkMoreThen(reports,operation,modelList,property,value)
{
	for(model in modelList){
		def modelValue=reports.get(operation)?.get("modelReports")?.get(model)?.get(property)

		if(modelValue!=null)
		{
			if(Double.valueOf(reports[operation]['modelReports'][model][property])<value)
			{
				println "${operation}Operation.${model}.${property} value ${modelValue} is smaller than ${value}"
			}

		}

	}

}

return statsXml
