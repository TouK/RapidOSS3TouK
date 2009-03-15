/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 19, 2008
 * Time: 5:07:59 PM
 * To change this template use File | Settings | File Templates.
 */
import script.CmdbScript

try {
    CmdbScript.stopListening(connector.SmartsConnector.get(name:"smnot").ds.listeningScript);
}
catch(e)
{
    logger.warn("Exception occured while stopping listening script smnot .Reason : ${e}");
}

def scriptsToStop=["notificationAdder","notificationDeleter","notificationSearcher","notificationWebSearcher"]

scriptsToStop.each{ scriptName ->
    def script=CmdbScript.get(name:scriptName);
    CmdbScript.updateScript(script,[enabled:false],false);
}

