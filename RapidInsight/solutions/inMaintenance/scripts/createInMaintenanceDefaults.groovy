import script.CmdbScript




def scriptsToAdd = []

//in maintenance
scriptsToAdd.add([name: "putInMaintenance", enabledForAllGroups: true, logFileOwn: true, logLevel: "INFO"]);
scriptsToAdd.add([name: "getMaintenanceData", enabledForAllGroups: true]);
scriptsToAdd.add([name: "removeExpiredMaintenances", type: CmdbScript.SCHEDULED, scheduleType: CmdbScript.PERIODIC, enabled: true, period: 60, logFileOwn: true, logLevel: "INFO"]);




scriptsToAdd.each {scriptParams ->

    try {
        CmdbScript.addScript(scriptParams)
    }
    catch (e)
    {
        logger.warn("createDefaults: Could not add script with params : ${scriptParams}.Reason:${e}", e)
    }
}

RsInMaintenanceSchedule.removeExpiredItems();
RsInMaintenanceSchedule.list().each {
    try {
        it.scheduleMaintenance();
    }
    catch (e) {
        logger.warn("Could not activate maintenance schedule for ${it.objectName} with id ${it.id}. Reason: ${e.getMessage()}")
    }
}



