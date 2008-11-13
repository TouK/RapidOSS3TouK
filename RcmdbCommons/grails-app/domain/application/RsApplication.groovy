package application
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 4:08:09 PM
 * To change this template use File | Settings | File Templates.
 */

class RsApplication {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]
    String rsOwner = "p"
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
