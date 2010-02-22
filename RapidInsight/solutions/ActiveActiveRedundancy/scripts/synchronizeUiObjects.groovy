import script.CmdbScript;


// ---------------------------------------
// CONFIGURATION STARTS

def scriptRunParams=[];

scriptRunParams.add(["modelName":"search.SearchQuery","withRelations":"true"])
scriptRunParams.add(["modelName":"search.SearchQueryGroup","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.GridColumn","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.GridView","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.map.TopoMap","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.map.MapGroup","withRelations":"true"])
scriptRunParams.add(["modelName":"ui.ComponentConfig"])
scriptRunParams.add(["modelName":"message.RsMessageRule"])
scriptRunParams.add(["modelName":"auth.RsUserInformation","withRelations":"true"])

// CONFIGURATION ENDS
// ---------------------------------------


logger.warn("synchronizeUiObjects starts");
def OUTPUT=" Model Results : ";

scriptRunParams.each{ runParams ->

	def result=CmdbScript.runScript("synchronizeObjects", [params:runParams,"logger":logger]);
	
	OUTPUT+="<br> ${result}"
}

logger.warn("synchronizeUiObjects ends");

return OUTPUT;