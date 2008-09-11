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
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static constraints = {
        keyField(key:[], nullable:false, blank:false);
        columnName(nullable:false, blank:false);
        conversion(nullable:false, blank:true);
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    }
}