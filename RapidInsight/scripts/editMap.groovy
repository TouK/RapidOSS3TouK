import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import ui.map.*
import com.ifountain.rcmdb.domain.util.DomainClassUtils;

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
    def rsTopologyObjectProp = DomainClassUtils.getFilteredProperties("RsTopologyObject")
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