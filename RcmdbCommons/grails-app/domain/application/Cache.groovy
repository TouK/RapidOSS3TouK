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
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static datasources = ["RCMDB":["keys":["id":["nameInDs":"id"]]]]
    Long id;
    Long version;
    String rsOwner = "p"
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    static constraints = {
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        errors(nullable:true)
    };

    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
}