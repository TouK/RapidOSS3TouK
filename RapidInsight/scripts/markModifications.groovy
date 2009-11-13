/************************************************************/
def excludedFiles = [
        "RapidSuite/data",
        "RapidSuite/logs",
        "RapidSuite/grails-app/conf/UIConfigurations",
        "temp"
]
/************************************************************/
application.RsApplication.getUtility("VersionControlUtility").markModifications(excludedFiles, [comment:params.comment, forceMark:params.forceMark]);