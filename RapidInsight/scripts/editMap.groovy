/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
    def mapGroups = MapGroup.searchEvery("username:\"${userName.toQuery()}\" AND isPublic:false")
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