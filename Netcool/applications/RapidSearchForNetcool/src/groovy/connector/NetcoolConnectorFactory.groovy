package connector

import datasource.NetcoolDatasource
import org.apache.log4j.Logger
import datasource.NetcoolConversionParameter
import com.ifountain.comp.utils.CaseInsensitiveMap
import org.apache.log4j.RollingFileAppender
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Level

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
    public static createConnector(NetcoolDatasource datasource, String logLevel)
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
        NetcoolConnector connector = connectorList.get(datasource.name);
        if(connector == null)
        {
            Logger logger = Logger.getLogger("connector."+datasource.name);
            logger.removeAllAppenders();
            def layout = new org.apache.log4j.PatternLayout("%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
            def appender = new DailyRollingFileAppender(layout, "logs/${datasource.name}Connector.log",  "'.'yyyy-MM-dd-HH-mm");
            logger.addAppender (appender);
            connector = new NetcoolConnector(datasource, logger, conversionParams);
            connectorList[datasource.name] = connector;
        }
        connector.logger.setLevel (Level.toLevel(logLevel))
        return connector;
    }
    
    public static clearConnectors()
    {
    	connectorList.clear();
    }

    public static removeConnector(connectorName)
    {
    	connectorList.remove(connectorName);
    }
}