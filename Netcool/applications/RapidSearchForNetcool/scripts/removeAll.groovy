import script.CmdbScript
import connection.NetcoolConnection
import connector.NetcoolLastRecordIdentifier

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 4, 2008
 * Time: 6:34:13 PM
 * To change this template use File | Settings | File Templates.
 */
NetcoolEvent.removeAll();
NetcoolJournal.removeAll();
NetcoolLastRecordIdentifier.removeAll();
NetcoolConnection.list().each{
    def connectorScriptName = it.name+"Connector";
    if(CmdbScript.get(name:connectorScriptName) != null)
    {
        CmdbScript.deleteScript(connectorScriptName);
    }
    it.remove();
}
CmdbScript.get(name:"getConversionParameters")?.remove();
