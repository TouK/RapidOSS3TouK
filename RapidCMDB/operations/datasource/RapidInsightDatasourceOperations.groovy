package datasource
import datasource.RapidInsightAdapter
import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:48:47 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidInsightDatasourceOperations extends BaseDatasourceOperations{
    def adapter;
    def onLoad = {
       this.adapter = new RapidInsightAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
    }
}