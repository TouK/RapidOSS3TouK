package auth
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 1, 2009
 * Time: 9:45:37 AM
 * To change this template use File | Settings | File Templates.
 */
class SegmentFilter {
    
    static searchable = {
        except=["errors", "__operation_class__", "__dynamic_property_storage__", "group"]
     };
    static datasources = ["RCMDB": ["keys": ["className": ["nameInDs": "className"], "groupId": ["nameInDs": "groupId"]]]]
    Long id;
    Long version;

    Date rsInsertedAt = new Date(0);

    Date rsUpdatedAt  = new Date(0);
    String rsOwner = "p"
    String className = ""
    String filter = "";
    Long groupId = 0;
    Group group;
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static relations = [
        group: [type: Group, reverseName: "filters", isMany: false]
    ]
    static constraints = {
        className(key: ["groupId"], nullable: false, blank: false)
        __operation_class__(nullable:true)
        __dynamic_property_storage__(nullable:true)
        errors(nullable:true)
        group(nullable:true)
        filter(blank:true, nullable:false);
    }
     static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "group"];

    String toString(){
        return "$className";
    }
}