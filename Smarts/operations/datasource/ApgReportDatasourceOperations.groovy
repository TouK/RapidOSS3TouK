package datasource

import com.ifountain.apg.datasource.ApgReportAdapter
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:07:30 PM
 * To change this template use File | Settings | File Templates.
 */
class ApgReportDatasourceOperations extends BaseDatasourceOperations{
    def adapter;
    def onLoad (){
        this.adapter = new ApgReportAdapter(getProperty("connection").name, reconnectInterval * 1000, Logger.getRootLogger());
    }
}