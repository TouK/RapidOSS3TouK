package datasource

import com.ifountain.apg.datasource.ApgDatabaseAdapter
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:08:19 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgDatabaseDatasourceOperations extends BaseDatasourceOperations{
    def adapter;
    def onLoad = {
        this.adapter = new ApgDatabaseAdapter(getProperty("connection").name, reconnectInterval * 1000, Logger.getRootLogger());
    }
}