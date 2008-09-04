package datasource
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 25, 2008
 * Time: 11:24:44 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsModelColumn {
    static searchable = {
        except = ["model"];
    };
    static datasources = [:]
    String smartsName;
    String localName
    String type;
    boolean isDeleteMarker;
    SmartsModel model;
    static constraints={
        smartsName(nullable:false, key:["model"])
        model(nullable:true)
    }
    static relations = [
            model:[type:SmartsModel, reverseName:"columns", isMany:false]
    ]
    static transients =  []
}