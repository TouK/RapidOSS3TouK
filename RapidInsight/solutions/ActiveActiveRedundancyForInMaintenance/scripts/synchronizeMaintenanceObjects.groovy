import script.CmdbScript;


// ---------------------------------------
// CONFIGURATION STARTS

def scriptRunParams=[];

scriptRunParams.add(["modelName":"RsInMaintenance"])
scriptRunParams.add(["modelName":"RsInMaintenanceSchedule"])

// CONFIGURATION ENDS
// ---------------------------------------

def OUTPUT=" Model Resutls : ";

scriptRunParams.each{ runParams ->

	def result=CmdbScript.runScript("synchronizeObjects", [params:runParams,"logger":logger]);

	OUTPUT+="<br> ${result}"
}

return OUTPUT;