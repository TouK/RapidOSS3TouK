package datasource
import com.ifountain.snmp.datasource.SnmpListeningAdapter
import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:50:05 PM
 * To change this template use File | Settings | File Templates.
 */
class SnmpDatasourceOperations extends BaseListeningDatasourceOperations{
    def getListeningAdapter(Map params){
         return new SnmpListeningAdapter(getProperty("connection").name, 0, Logger.getRootLogger());
    }
}