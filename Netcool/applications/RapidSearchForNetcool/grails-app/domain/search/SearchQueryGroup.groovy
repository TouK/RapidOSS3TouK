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
    static searchable = true;
    RsUser user;
    String name;
    static hasMany = [queries:SearchQuery]
    static mappedBy = ["queries":"group"]

}