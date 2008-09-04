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
    static searchable = true;
    String username;
    SearchQueryGroup group;
    String name;
    String query;
    String sortProperty;
    String type="";
    boolean isPublic = false;
    String sortOrder = "asc";
    static mappedBy = [group:"queries"]
    static constraints = {
        name(key:["username"]);
        sortOrder(inList:["asc","desc"]);
    }
}