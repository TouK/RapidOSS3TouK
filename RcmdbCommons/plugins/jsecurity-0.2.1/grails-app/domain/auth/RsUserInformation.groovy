package auth
/**
 * Created by IntelliJ IDEA.
 * User: deneme
 * Date: Aug 25, 2008
 * Time: 4:28:04 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserInformation {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    static constraints = {
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    };

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];


}