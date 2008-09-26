package search

import auth.RsUser

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 10, 2008
 * Time: 5:26:22 PM
 * To change this template use File | Settings | File Templates.
 */
class SearchQueryGroup {
    static searchable = {
        except:["queries"]
     };
    String username;
    String rsOwner = "p"
    String name;
    List queries = [];
    boolean isPublic = false;
    String type = "";
    static relations = [
            queries:[type:SearchQuery, reverseName:"group", isMany:true]
    ]
    static constraints = {
        name(key:["username"]);
    }

}