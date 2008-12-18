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



checkLessThen(reportsMap,"Add",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06,true)
checkLessThen(reportsMap,"Update",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.06,true)
checkLessThen(reportsMap,"Remove",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.04,true)
checkLessThen(reportsMap,"AddRelation",["Author","Person"],"AvarageDuration",0.06,true)
checkLessThen(reportsMap,"RemoveRelation",["Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.015,true)
checkLessThen(reportsMap,"Search",["Book","Fiction","ScienceFiction","Author","Person"],"AvarageDuration",0.03,true)



def checkLessThen(reports,operation,modelList,property,value,checkExistance)
{
	for(model in modelList){
		def modelValue=reports.get(operation)?.get("modelReports")?.get(model)?.get(property)

		if(modelValue!=null)
		{
			if(Double.valueOf(modelValue)>value)
			{
				println "${operation}Operation.${model}.${property} value ${modelValue} is larger than ${value}"
			}

			if(reports.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")=="0")
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
def checkMoreThen(reports,operation,modelList,property,value)
{
	for(model in modelList){
		def modelValue=reports.get(operation)?.get("modelReports")?.get(model)?.get(property)

		if(modelValue!=null)
		{
			if(Double.valueOf(modelValue)<value)
			{
				println "${operation}Operation.${model}.${property} value ${modelValue} is smaller than ${value}"
			}

			if(reports.get(operation)?.get("modelReports")?.get(model)?.get("NumberOfOperations")=="0")
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

return statsXml
