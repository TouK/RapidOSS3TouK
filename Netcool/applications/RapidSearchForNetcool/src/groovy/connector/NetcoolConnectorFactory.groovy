package connector

import datasource.NetcoolDatasource
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 9:45:35 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConnectorFactory {
    private static Map connectorList = [:];
    public static createConnector(NetcoolDatasource datasource)
    {
        def connector = connectorList.get(datasource.name);
        if(connector == null)
        {
            Logger logger = Logger.getLogger("connector."+datasource.name);
            connector = new NetcoolConnector(datasource, logger);
            connectorList[datasource.name] = connector;
        }
        return connector;
    }
    public static clearConnectors()
    {
    	connectorList.clear();
    }
}