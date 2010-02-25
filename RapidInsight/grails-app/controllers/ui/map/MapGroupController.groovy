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
package ui.map

import grails.converters.XML
import auth.RsUser
import auth.Role
import com.ifountain.rcmdb.domain.util.ControllerUtils

class MapGroupController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]

    def list = {
        if (!params.max) params.max = 10
        def mapGroups = MapGroup.search("alias:*", params).results;
        render mapGroups as XML
    }

    def delete = {
        def mapGroup = MapGroup.get([id: params.id])
        if (mapGroup) {
            if (mapGroup.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("default.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            try {
                mapGroup.remove();
                render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.id} deleted"), contentType: "text/xml")
            }
            catch (e) {
                addError("default.custom.error", [e.getMessage()])
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }
    def update = {
        def mapGroup = MapGroup.get([id: params.id])
        if (mapGroup) {
            if (mapGroup.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("default.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            def mapGroupParams = ControllerUtils.getClassProperties(params, MapGroup)
            mapGroupParams["username"] = mapGroupParams.isPublic ? RsUser.RSADMIN : session.username;
            mapGroup.update(mapGroupParams);
            if (!mapGroup.hasErrors()) {
                render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(mapGroup.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [MapGroup.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def save = {
        def mapGroupParams = ControllerUtils.getClassProperties(params, MapGroup)
        if (mapGroupParams.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
            addError("default.not.authorized", []);
            render(text: errorsToXml(this.errors), contentType: "text/xml")
            return;
        }
        mapGroupParams["username"] = mapGroupParams.isPublic ? RsUser.RSADMIN : session.username;
        def mapGroup = MapGroup.add(mapGroupParams)
        if (!mapGroup.hasErrors()) {
            render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("MapGroup ${params.name} created"), contentType: "text/xml")
        }
        else {
            render(text: errorsToXml(mapGroup.errors), contentType: "text/xml")
        }
    }

}