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
def mapGroups = MapGroup.searchEvery("username:\"${userName.toQuery()}\" AND isPublic:false")
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
