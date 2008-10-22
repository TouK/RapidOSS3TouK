package ui
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 22, 2008
 * Time: 2:44:33 PM
 */
class ComponentConfig {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["name": ["nameInDs": "name"]]]]
    String name = "";
    String username = "";
    Long pollingInterval = 0;

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    static constraints = {
        name(blank: false, nullable: false, key: ["username"])
        username(blank:false, nullable: false)
        pollingInterval(nullable: true)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    };
    static relations = [
    ]

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    String toString() {
        return "$name";
    }
}