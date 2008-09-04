package datasource
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 25, 2008
 * Time: 11:24:18 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsModel {
     static searchable = {
        except = ["columns"];
    };
    static cascaded = ["columns":true]
    static datasources = [:]
    Long id;
    Long version;
    String name;
    String parentName;
    List columns = [];
    static constraints={
        name(nullable:false, key:[])
        columns(nullable:true)
    }
    static relations = [
            columns:[type:SmartsModelColumn, reverseName:"model", isMany:true]
    ]
    static transients =  []
}