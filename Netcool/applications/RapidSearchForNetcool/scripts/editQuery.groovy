import search.SearchQuery
import search.SearchQueryGroup
import ui.GridView
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 1:47:21 PM
 * To change this template use File | Settings | File Templates.
 */

def searchQuery = SearchQuery.get([id: params.queryId])
if (!searchQuery) {
    web.addError("default.object.not.found", [SearchQuery.class.name, params.queryId]);
    web.render(text: web.errorsToXml(web.errors), contentType: "text/xml");
}
else {
    def userName = web.session.username;
    def searchQueryGroups = SearchQueryGroup.list().findAll {
        it.username == userName && it.isPublic == false
    };
    def gridViews = GridView.searchEvery("username:\"${userName}\"", [sort: "name"]);
    def netcoolEventProps = DomainClassUtils.getFilteredProperties("NetcoolEvent")
    web.render(contentType: 'text/xml') {
        Edit {
            id(searchQuery.id)
            name(searchQuery.name)
            query(searchQuery.query)
            sortProperty {
                netcoolEventProps.each {
                    option(selected: it.name == searchQuery.sortProperty, it.name)
                }
            }
            sortOrder {
                option(selected: searchQuery.sortOrder == 'desc', 'desc')
                option(selected: searchQuery.sortOrder == 'asc', 'asc')
            }
            group {
                searchQueryGroups.each {
                    if (it.name == searchQuery.group.name) {
                        option(selected: "true", it.name)
                    }
                    else {
                        option(it.name)
                    }
                }
            }
            viewName {
                option(selected: searchQuery.viewName == 'default', 'default')
                gridViews.each {
                    option(selected: searchQuery.viewName == it.name, it.name)
                }
            }
        }
    }

}