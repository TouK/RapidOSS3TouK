import search.SearchQueryGroup
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 2:03:53 PM
 * To change this template use File | Settings | File Templates.
 */
def userName = web.session.username;
def searchQueryGroups = SearchQueryGroup.list().findAll {queryGroup->
    queryGroup.username == userName && queryGroup.isPublic == false
};
def excludedProps = ['version',
        "errors", "__operation_class__",
        ClosureEventTriggeringInterceptor.ONLOAD_EVENT,
        ClosureEventTriggeringInterceptor.BEFORE_DELETE_EVENT,
        ClosureEventTriggeringInterceptor.BEFORE_INSERT_EVENT,
        ClosureEventTriggeringInterceptor.BEFORE_UPDATE_EVENT]
def netcoolEventProps = web.grailsApplication.getDomainClass("RsSmartsObject").properties.findAll {!excludedProps.contains(it.name)}
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
    }
}
