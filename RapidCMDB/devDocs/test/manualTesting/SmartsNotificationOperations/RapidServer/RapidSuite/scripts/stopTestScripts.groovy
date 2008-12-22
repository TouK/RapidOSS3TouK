/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 19, 2008
 * Time: 5:07:59 PM
 * To change this template use File | Settings | File Templates.
 */
import script.CmdbScript

def scriptsToStop=["notificationAdder","notificationDeleter","notificationSearcher","notificationWebSearcher"]

scriptsToStop.each{ scriptName ->
    def script=CmdbScript.get(name:scriptName);
    CmdbScript.updateScript(script,[enabled:false],false);
}