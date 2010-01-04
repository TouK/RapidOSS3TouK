import groovy.xml.MarkupBuilder

def date = params.date;
def bringChangesSetGeraterThan = params.direction != "till";

if(date == null)
{
    date = "0"
}
date = new Date(Long.parseLong(date));
def utility = application.RapidApplication.getUtility("VersionControlUtility");
def modifications = utility.getChangeSetList();
modifications = modifications.findAll{it.isValid && (bringChangesSetGeraterThan && it.date >= date || !bringChangesSetGeraterThan && it.date <= date)};
def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
def modifiedFiles = [:]
modifications.each{changeSetDirConf->
    def changesAsMap = utility.getChangesAsMap(changeSetDirConf.file);
    changesAsMap.changes.each{changeConf->
        modifiedFiles.put(changeConf.path, changeConf);    
    }
}

mb.Files{
    def fileConfigs = modifiedFiles.values().sort {it.path}
    fileConfigs.each{conf->
        mb.File(conf);
    }
}
return sw.toString();