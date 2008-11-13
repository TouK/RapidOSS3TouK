import script.CmdbScript
import connection.NetcoolConnection
import connector.NetcoolLastRecordIdentifier
import connector.NetcoolConnector
import datasource.NetcoolDatasource
import datasource.NetcoolConversionParameter

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 4, 2008
 * Time: 6:34:13 PM
 * To change this template use File | Settings | File Templates.
 */
NetcoolEvent.removeAll();
NetcoolJournal.removeAll();
NetcoolHistoricalJournal.removeAll();
NetcoolLastRecordIdentifier.removeAll();
NetcoolConversionParameter.removeAll();
NetcoolConnector.list().each{
    def scriptName = NetcoolConnector.getScriptName(it.name);
    def connName = NetcoolConnector.getConnectionName(it.name);
    def dsName = NetcoolConnector.getDatasourceName(it.name);
    if(CmdbScript.get(name:scriptName) != null)
    {
        CmdbScript.deleteScript(scriptName);
    }
    NetcoolDatasource.get(name:dsName)?.remove();
    NetcoolConnection.get(name:connName)?.remove();
    it.remove();
}
if(CmdbScript.get(name:"getConversionParameters") != null)
{
    CmdbScript.deleteScript("getConversionParameters");
}
web.flash.message = "Removed successfully."
web.redirect(uri:'/script/list');