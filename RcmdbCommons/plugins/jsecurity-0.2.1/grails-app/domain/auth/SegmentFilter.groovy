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
        except:["errors", "__operation_class__", "__is_federated_properties_loaded__", "groups"]
     };
    static datasources = ["RCMDB": ["keys": ["className": ["nameInDs": "className"], "groupId": ["nameInDs": "groupId"]]]]
    Long id;
    Long version;
    String rsOwner = "p"
    String className = ""
    String filter = "";
    Long groupId = 0;
    List groups = [];
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static relations = [
        groups: [type: Group, reverseName: "filters", isMany: true]
    ]
    static constraints = {
        className(key: ["groupId"], nullable: false, blank: false)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
        filter(blank:true, nullable:false);
    }
     static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "groups"];

    String toString(){
        return "$className";
    }
}