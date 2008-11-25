package datasource

import org.apache.log4j.Logger;
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 21, 2008
 * Time: 10:41:06 AM
 */
class OpenNmsHttpDatasourceOperations  extends BaseDatasourceOperations{
    def adapter;
    def onLoad(){
       this.adapter = new OpenNmsHttpAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
    }
}