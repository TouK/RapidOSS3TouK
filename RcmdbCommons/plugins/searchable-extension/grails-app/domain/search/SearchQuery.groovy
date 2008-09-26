package search

import auth.RsUser;
/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Jun 1, 2008
* Time: 3:42:38 PM
* To change this template use File | Settings | File Templates.
*/
class SearchQuery {
    static searchable = {
        except:["group"]
     };
    String username;
    SearchQueryGroup group;
    String name;
    String rsOwner = "p"
    String query;
    String sortProperty;
    String viewName = "default";
    String type="";
    boolean isPublic = false;
    String sortOrder = "asc";

    static relations = [
            group:[type:SearchQueryGroup, reverseName:"queries", isMany:false]
    ]
    static constraints = {
        name(key:["username"]);
        sortOrder(inList:["asc","desc"]);
        viewName(blank:true, nullable:true);
        sortProperty(blank:true, nullable:true);
    }
}