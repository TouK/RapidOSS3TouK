/************************************************************/
def excludedFiles = [
        "RapidSuite/data",
        "RapidSuite/logs",
        "RapidSuite/grails-app/conf/UIConfigurations",
        "RapidSuite/rrdFiles",
        "temp"
]
/************************************************************/
application.RapidApplication.getUtility("VersionControlUtility").markModifications(excludedFiles, [comment:params.comment, forceMark:params.forceMark]);