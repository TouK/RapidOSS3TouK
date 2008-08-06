import datasource.NetcoolDatasource
import datasource.NetcoolConversionParameter
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 29, 2008
 * Time: 3:41:29 PM
 * To change this template use File | Settings | File Templates.
 */
Logger logger = Logger.getLogger("getConversionParameters");

List netcoolDatasources = NetcoolDatasource.list();
if(netcoolDatasources.isEmpty())
{
    logger.warn("No netcool datasource is defined");
}
NetcoolDatasource netcoolDs = netcoolDatasources[0];
def conversionParams = netcoolDs.getConversionParams();
conversionParams.each{Map params->
    if(NetcoolScriptConfigurationParams.COLUMNS_WILL_BE_CONVERTED.containsKey(params.colName))
    {
        NetcoolConversionParameter.add(keyField:params.keyfield, columnName:params.colName, value:params.value, conversion:params.conversion);
    }
}
