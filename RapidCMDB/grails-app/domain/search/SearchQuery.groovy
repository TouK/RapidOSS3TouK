package search;
import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Jun 1, 2008
* Time: 3:42:38 PM
* To change this template use File | Settings | File Templates.
*/
class SearchQuery {
    RsUser user;
    String query;
    static belongsTo = ["RsUser"];
}