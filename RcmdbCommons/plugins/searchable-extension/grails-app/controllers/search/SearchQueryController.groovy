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
import auth.Role

class SearchQueryController {
    def index = {redirect(action: list, params: params)}
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]
    def list = {
        if (!params.max) params.max = 10
        def searchQueries = SearchQuery.search("alias:*", params).results;
        render searchQueries as XML
    }

    def delete = {
        def searchQuery = SearchQuery.get([id: params.id])
        if (searchQuery) {
            if (searchQuery.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("searchquery.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            try {
                searchQuery.remove()
                render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${params.id} deleted"), contentType: "text/xml")
            }
            catch (e) {
                addError("default.custom.error", [e.getMessage()])
                render(text: errorsToXml(errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def update = {
        def searchQuery = SearchQuery.get([id: params.id])
        def groupType = params.type;
        if (searchQuery) {
            if (searchQuery.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
                addError("searchquery.not.authorized", []);
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }
            if (params.group == "" || params.group.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES))
            {
                params.group = SearchQueryGroup.MY_QUERIES;
                groupType = SearchQueryGroup.DEFAULT_TYPE
            }
            def group = SearchQueryGroup.get(name: params.group, username: session.username, type: groupType);
            if (group == null)
            {
                group = SearchQueryGroup.get(name: params.group, type: groupType, username: RsUser.RSADMIN);
                if (group == null || !group.isPublic) {
                    group = SearchQueryGroup.add(name: params.group, username: session.username, type: groupType);
                }
            }
            def groupChanged = false;
            if(group.id != searchQuery.group?.id) groupChanged = true
            params["group"] = ["id": group.id];
            params["group.id"] = "${group.id}".toString();
            def queryParams = ControllerUtils.getClassProperties(params, SearchQuery)
            queryParams["username"] = queryParams.isPublic ? RsUser.RSADMIN : session.username;
            searchQuery.update(queryParams);
            if (!searchQuery.hasErrors()) {
                if(groupChanged){
                     updateGroupsOfSubQueries(searchQuery, group)
                }
                render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${searchQuery.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }

    def updateGroupsOfSubQueries(searchQuery, group){
        SearchQuery.searchEvery("parentQueryId:${searchQuery.id}").each{
            it.update(group:group)
            updateGroupsOfSubQueries(it, group);
        }
    }

    def save = {
        def queryParams = ControllerUtils.getClassProperties(params, SearchQuery)
        if (queryParams.isPublic && !RsUser.hasRole(session.username, Role.ADMINISTRATOR)) {
            addError("searchquery.not.authorized", []);
            render(text: errorsToXml(this.errors), contentType: "text/xml")
            return;
        }
        def groupType = queryParams.type;
        def searchGroup = params.group;
        if (searchGroup == "" || searchGroup.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES))
        {
            searchGroup = SearchQueryGroup.MY_QUERIES;
            groupType = SearchQueryGroup.DEFAULT_TYPE
        }
        def group = SearchQueryGroup.get(name: searchGroup, type: groupType, username: session.username);
        if (group == null)
        {
            group = SearchQueryGroup.get(name: searchGroup, type: groupType, username: RsUser.RSADMIN);
            if (group == null || !group.isPublic) {
                group = SearchQueryGroup.add(name: searchGroup, username: session.username, type: groupType);
            }
        }
        queryParams["group"] = group;
        queryParams["username"] = queryParams.isPublic ? RsUser.RSADMIN : session.username;
        def searchQuery = SearchQuery.add(queryParams)
        if (!searchQuery.hasErrors()) {
            render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${searchQuery.id} created"), contentType: "text/xml")
        }
        else {
            render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")
        }
    }
}