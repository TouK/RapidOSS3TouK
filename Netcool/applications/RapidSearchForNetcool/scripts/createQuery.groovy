import search.SearchQueryGroup
import ui.GridView
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 2:03:53 PM
 * To change this template use File | Settings | File Templates.
 */
def userName = web.session.username;
def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup ->
    queryGroup.username == userName && queryGroup.isPublic == false
};
def gridViews = GridView.searchEvery("username:\"${userName}\"", [sort: "name"]);
def netcoolEventProps = DomainClassUtils.getFilteredProperties("NetcoolEvent");
web.render(contentType: 'text/xml') {
    Create {
        group {
            searchQueryGroups.each {
                option(it.name)
            }
        }
        sortProperty {
            netcoolEventProps.each {
                option(it.name)
            }
        }
        viewName {
            option('default');
            gridViews.each {
                option(it.name)
            }
        }
    }
}
