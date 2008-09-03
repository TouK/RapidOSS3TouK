import MapGroup
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 2:03:53 PM
 * To change this template use File | Settings | File Templates.
 */
def userName = web.session.username;
def mapGroups = MapGroup.list().findAll {mapGroup->
    mapGroup.username == userName
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
        groupName {
            mapGroups.each {
                option(it.groupName)
            }
        }      
    }
}
