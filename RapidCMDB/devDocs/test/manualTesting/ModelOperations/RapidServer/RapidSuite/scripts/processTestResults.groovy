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

def checkLessThen(reports,operation,modelList,property,value)
{
	for(model in modelList){
		if(Double.valueOf(reports[operation]['modelReports'][model][property])>value)
		{
			println "${operation} Operation statistics for ${model}.${property} value is larger than ${value}"
		}
	}

}

return statsXml
