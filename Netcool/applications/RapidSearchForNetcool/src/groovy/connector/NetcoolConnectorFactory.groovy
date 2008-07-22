package connector

import datasource.NetcoolDatasource
import org.apache.log4j.Logger
import datasource.NetcoolConversionParameter
import com.ifountain.comp.utils.CaseInsensitiveMap

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 4, 2008
 * Time: 9:45:35 AM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConnectorFactory {
    private static Map connectorList = [:];
    private static Map conversionParams = new CaseInsensitiveMap();
    public static createConnector(NetcoolDatasource datasource)
    {
        if(conversionParams.isEmpty())
        {
            NetcoolConversionParameter.list().each{NetcoolConversionParameter convParam->
                def convParamMap = conversionParams[convParam.columnName];
                if(convParamMap == null)
                {
                    convParamMap = new CaseInsensitiveMap();
                    conversionParams[convParam.columnName] = convParamMap;
                }
                convParamMap[convParam.value]=convParam.conversion;
            }
        }
        def connector = connectorList.get(datasource.name);
        if(connector == null)
        {
            Logger logger = Logger.getLogger("connector."+datasource.name);
            connector = new NetcoolConnector(datasource, logger, conversionParams);
            connectorList[datasource.name] = connector;
        }
        return connector;
    }
    
    public static clearConnectors()
    {
    	connectorList.clear();
    }
}