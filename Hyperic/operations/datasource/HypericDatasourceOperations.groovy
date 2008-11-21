package datasource
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Nov 21, 2008
 * Time: 10:41:06 AM
 */
class HypericDatasourceOperations  extends BaseDatasourceOperations{
    def adapter;
    def onLoad(){
       this.adapter = new HypericAdapter(getProperty("connection").name, reconnectInterval*1000, Logger.getRootLogger());
    }
}