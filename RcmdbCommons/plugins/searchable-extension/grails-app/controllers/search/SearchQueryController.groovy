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

class SearchQueryController {
    def index = {redirect(action: list, params: params)}
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]
    def list = {
        if (!params.max) params.max = 10
        def searchQueries = SearchQuery.search("alias:*", params).results;
        withFormat {
            html searchQueryList: searchQueries
            xml {render searchQueries as XML}
        }
    }

    def show = {
        def searchQuery = SearchQuery.get([id: params.id])

        if (!searchQuery) {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {errorsToXml(errors)}
            }

        }
        else {
            withFormat {
                html {render(view: "show", model: [searchQuery: searchQuery])}
                xml {render searchQuery as XML}
            }
        }
    }

    def delete = {
        def searchQuery = SearchQuery.get([id: params.id])
        if (searchQuery) {
            searchQuery.remove();
            withFormat {
                html {
                    flash.message = "SearchQuery ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${searchQuery.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }

        }
    }

    def edit = {
        def searchQuery = SearchQuery.get([id: params.id])
        if (!searchQuery) {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            flash.errors = errors;
            redirect(action: list)
        }
        else {
            return [searchQuery: searchQuery]
        }

    }


    def update = {
        def searchQuery = SearchQuery.get([id: params.id])
        def groupType = params.type;
        if (searchQuery) {
            if (params.group == "" || params.group.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES))
            {
                params.group = SearchQueryGroup.MY_QUERIES;
                groupType = SearchQueryGroup.DEFAULT_TYPE
            }
            def group = SearchQueryGroup.get(name: params.group, username: session.username, type: groupType);
            if (group == null)
            {
                group = SearchQueryGroup.add(name: params.group, username: session.username, type: groupType);
            }
            params["group"] = ["id": group.id];
            params["group.id"] = "${group.id}".toString();
            searchQuery.update(ControllerUtils.getClassProperties(params, SearchQuery));
            if (!searchQuery.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "SearchQuery ${params.id} updated"
                        redirect(action: show, id: searchQuery.id)
                    }
                    xml {render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${searchQuery.id} updated"), contentType: "text/xml")}
                }

            }
            else {
                withFormat {
                    html {
                        render(view: 'edit', model: [searchQuery: searchQuery])
                    }
                    xml {render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")}
                }
            }
        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: edit, id: params.id)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }

        }
    }

    def create = {
        def searchQuery = new SearchQuery()
        searchQuery.properties = params
        return ['searchQuery': searchQuery]
    }

    def save = {
        params["username"] = session.username
        def groupType = params.type;
        if (params.group == "" || params.group.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES))
        {
            params.group = SearchQueryGroup.MY_QUERIES;
            groupType = SearchQueryGroup.DEFAULT_TYPE
        }
        def group = SearchQueryGroup.get(name: params.group, username: session.username, type: groupType);
        if (group == null)
        {
            group = SearchQueryGroup.add(name: params.group, username: session.username, type: groupType);
        }
        params["group"] = ["id": group.id];
        params["group.id"] = "${group.id}".toString();
        def searchQuery = SearchQuery.add(ControllerUtils.getClassProperties(params, SearchQuery))
        if (!searchQuery.hasErrors()) {
            withFormat {
                html {
                    flash.message = "SearchQuery ${searchQuery.id} created"
                    redirect(action: show, id: searchQuery.id)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("SearchQuery ${searchQuery.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {
                    render(view: 'create', model: [searchQuery: searchQuery])
                }
                xml {render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")}
            }

        }
    }
}