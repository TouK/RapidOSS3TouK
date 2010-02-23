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
package search

import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML
import auth.RsUser
import auth.Role;
class SearchQueryGroupController {
    def index = {redirect(action: list, params: params)}
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]
    def list = {
        if (!params.max) params.max = 10
        def searchQueryGroups = SearchQueryGroup.search("alias:*", params).results;
        render searchQueryGroups as XML
    }
    def delete = {
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])
        if (searchQueryGroup) {
            if (searchQueryGroup.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("searchgroup.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            try {
                searchQueryGroup.remove()
                render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} deleted"), contentType: "text/xml")
            }
            catch (e) {
                addError("default.custom.error", [e.getMessage()])
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }
    def update = {
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])
        if (searchQueryGroup) {
            if (searchQueryGroup.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("searchgroup.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            if (params.name && params.name.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES)) {
                params.type = SearchQueryGroup.DEFAULT_TYPE;
            }
            def queryGroupParams = ControllerUtils.getClassProperties(params, SearchQueryGroup)
            queryGroupParams["username"] = queryGroupParams.isPublic ? RsUser.RSADMIN : session.username;
            searchQueryGroup.update(queryGroupParams);
            if (!searchQueryGroup.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(searchQueryGroup.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def save = {
        if (params.isPublic == 'true' && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
            addError("searchgroup.not.authorized", []);
            render(text: errorsToXml(this.errors), contentType: "text/xml")
            return;
        }
        if (params.name && params.name.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES)) {
            params.type = SearchQueryGroup.DEFAULT_TYPE;
        }
        def queryGroupParams = ControllerUtils.getClassProperties(params, SearchQueryGroup)
        queryGroupParams["username"] = queryGroupParams.isPublic ? RsUser.RSADMIN : session.username;
        def searchQueryGroup = SearchQueryGroup.add(queryGroupParams)
        if (!searchQueryGroup.hasErrors()) {
            render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} created"), contentType: "text/xml")
        }
        else {
            render(text: errorsToXml(searchQueryGroup.errors), contentType: "text/xml")
        }
    }
}