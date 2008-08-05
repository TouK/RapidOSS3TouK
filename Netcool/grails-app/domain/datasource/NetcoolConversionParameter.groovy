package datasource
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 8, 2008
 * Time: 1:13:43 PM
 * To change this template use File | Settings | File Templates.
 */
class NetcoolConversionParameter {
    static searchable = true;
    String keyField;
    String columnName;
    int value;
    String conversion;
    static constraints = {
        keyField(key:[], nullable:false, blank:false);
        columnName(nullable:false, blank:false);
        value(nullable:false);
        conversion(nullable:false, blank:true);
    }

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