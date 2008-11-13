import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import ui.map.MapGroup
import com.ifountain.rcmdb.domain.util.DomainClassUtils;

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Aug 26, 2008
* Time: 2:03:53 PM
* To change this template use File | Settings | File Templates.
*/
def userName = web.session.username;
def mapGroups = MapGroup.list().findAll {mapGroup ->
    mapGroup.username == userName
};
def netcoolEventProps = DomainClassUtils.getFilteredProperties("RsTopologyObject")
web.render(contentType: 'text/xml') {
    Create {
        groupName {
            mapGroups.each {
                option(it.groupName)
            }
        }
    }
}
