package datasource
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 5:58:25 PM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConversionParameterOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    def static getRealValue(String colName, String val)
    {
        NetcoolConversionParameter conversion = NetcoolConversionParameter.search("columnName:$colName AND conversion:$val").results[0];
        if(conversion)
        {
            return conversion.value;
        }
        return val;
    }

    def static getConvertedValue(String colName, Object val)
    {
        NetcoolConversionParameter conversion = NetcoolConversionParameter.get(keyField:"$colName$val".toString());
        if(conversion)
        {
            return conversion.conversion;
        }
        return val;
    }
}