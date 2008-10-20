package ui
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 22, 2008
 * Time: 2:44:33 PM
 */
class GridColumn {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = ["RCMDB": ["master": true, "keys": ["attributeName": ["nameInDs": "attributeName"]]]]
    Long columnIndex = 0;
    String attributeName = "";
    String header = "";
    Long width = 0;
    GridView gridView;

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;

    static constraints = {
        attributeName(blank: false, nullable: false, key: ["gridView"])
        header(blank:true, nullable: true)
        columnIndex(nullable: true)
        width(nullable: true)
        __operation_class__(nullable:true)
        __is_federated_properties_loaded__(nullable:true)
        errors(nullable:true)
    };
    static relations = [
            gridView:[isMany:false, reverseName:"gridColumns", type:GridView]
    ]

    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    String toString() {
        return "$attributeName";
    }
}