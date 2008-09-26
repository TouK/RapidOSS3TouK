package auth
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 26, 2008
 * Time: 2:01:25 PM
 */

class Group
{
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "users", "role"];
    };
    static datasources = ["RCMDB": ["keys": ["name": ["nameInDs": "name"]]]]
    String name = "";
    String segmentFilter = "";
    String rsOwner = "p"
    Long id;
    Long version;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    List users = [];
    Role role;
    static relations = [
            users: [type: RsUser, reverseName: "groups", isMany: true],
            role: [type: Role, reverseName: "groups", isMany: false]
    ]
    static constraints = {
        name(blank: true, nullable: true)
        segmentFilter(blank: true, nullable: true)
        __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
        role(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "users", "role"];

    public String toString()
    {
        return name;
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
}