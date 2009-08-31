package application
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Dec 31, 2008
 * Time: 10:50:14 AM
 * To change this template use File | Settings | File Templates.
 */
class Cache {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]
    String rsOwner = "p"
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);

    static constraints = {
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    };

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
}