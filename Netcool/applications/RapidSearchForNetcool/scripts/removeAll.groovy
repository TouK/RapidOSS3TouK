import script.CmdbScript
import connection.NetcoolConnection

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
    CmdbScript.deleteScript("Connector");
    it.remove();
}
CmdbScript.get(name:"getConversionParameters")?.remove();
