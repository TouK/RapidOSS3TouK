package connector

import connector.NetcoolConnector
import org.apache.log4j.Logger
import datasource.NetcoolConversionParameter
import com.ifountain.comp.utils.CaseInsensitiveMap
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
    public static synchronized createConnector(NetcoolConnector connector)
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
        NetcoolConnectorImpl connectorImpl = connectorList.get(connector.name);
        if(connectorImpl == null)
        {
            Logger logger = Logger.getLogger("connector."+connector.name);
            logger.removeAllAppenders();
            def layout = new org.apache.log4j.PatternLayout("%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
            def appender = new DailyRollingFileAppender(layout, "logs/${connector.name}Connector.log",  "'.'yyyy-MM-dd");
            logger.addAppender (appender);
            logger.setAdditivity(false);
            connectorImpl = new NetcoolConnectorImpl(connector, logger, conversionParams);
            connectorList[connector.name] = connectorImpl;
        }
        connectorImpl.logger.setLevel (Level.toLevel(connector.logLevel))
        return connectorImpl;
    }
    
    public static synchronized clearConnectors()
    {
    	connectorList.clear();
    }

    public static synchronized removeConnector(connectorName)
    {
    	connectorList.remove(connectorName);
    }

    public static synchronized clearConversionParams(){
         conversionParams.clear();
    }

    public static setLogLevel(connectorName, logLevel){
        NetcoolConnectorImpl connectorImpl = connectorList.get(connectorName);
        if(connectorImpl != null){
            connectorImpl.logger.setLevel (Level.toLevel(logLevel))
        }
    }
}