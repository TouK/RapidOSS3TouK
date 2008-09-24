import map.TopoMap
import map.MapGroup
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import map.MapGroup
import map.TopoMap

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 26, 2008
 * Time: 1:47:21 PM
 * To change this template use File | Settings | File Templates.
 */

def topoMap = TopoMap.get([id: params.mapId])
if (!topoMap) {
    web.addError("default.object.not.found", [topoMap.class.name, params.mapId]);
    web.render(text: web.errorsToXml(web.errors), contentType: "text/xml");
}
else {
    def userName = web.session.username;
    def mapGroups = MapGroup.list().findAll {
        it.username == userName
    };
    def excludedProps = ['version',
            "errors", "__operation_class__",
            ClosureEventTriggeringInterceptor.ONLOAD_EVENT,
            ClosureEventTriggeringInterceptor.BEFORE_DELETE_EVENT,
            ClosureEventTriggeringInterceptor.BEFORE_INSERT_EVENT,
            ClosureEventTriggeringInterceptor.BEFORE_UPDATE_EVENT]
    def rsSmartsObjectProp = web.grailsApplication.getDomainClass("RsSmartsObject").properties.findAll {!excludedProps.contains(it.name)}
    web.render(contentType: 'text/xml') {
        Edit {
            id(topoMap.id)
            mapName(topoMap.mapName)
            groupName {
                mapGroups.each {
                    if (it.groupName == topoMap.group.groupName) {
                        option(selected: "true", it.groupName)
                    }
                    else {
                        option(it.groupName)
                    }
                }
            }
        }
    }

}