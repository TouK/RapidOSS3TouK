import script.CmdbScript;



def scriptRunParams=[];

scriptRunParams.add(["modelName":"search.SearchQuery","withRelations":"true"])
scriptRunParams.add(["modelName":"search.SearchQueryGroup","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.GridColumn","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.GridView","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.map.TopoMap","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.map.MapGroup","withRelations":"true"])
scriptRunParams.add(["modelName":"message.RsMessageRule"])
scriptRunParams.add(["modelName":"auth.RsUserInformation","withRelations":"true"])

//message.RsMessageRule have searchQueryId and userid
//ChannelUserInformation have userid

def OUTPUT=" Model Resutls : ";

scriptRunParams.each{ runParams ->

	def result=CmdbScript.runScript("synchronizeObjects", [params:runParams,"logger":logger]);
	
	OUTPUT+="<br> ${result}"
}

return OUTPUT;