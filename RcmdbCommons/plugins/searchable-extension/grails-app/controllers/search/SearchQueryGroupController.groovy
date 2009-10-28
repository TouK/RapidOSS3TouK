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
import grails.converters.XML;
class SearchQueryGroupController {
    def index = {redirect(action: list, params: params)}
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]
    def list = {
        if (!params.max) params.max = 10
        def searchQueryGroups = SearchQueryGroup.search("alias:*", params).results;
        withFormat {
            html searchQueryGroupList: searchQueryGroups
            xml {render searchQueryGroups as XML}
        }
    }

    def show = {
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])
        if (!searchQueryGroup) {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {render(view: "show", model: [searchQueryGroup: searchQueryGroup])}
                xml {render searchQueryGroup as XML}
            }
        }
    }

    def delete = {
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])
        if (searchQueryGroup) {
            try {
                searchQueryGroup.remove()
                withFormat {
                    html {
                        flash.message = "SearchQueryGroup ${params.id} deleted"
                        redirect(action: list)
                    }
                    xml {render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} deleted"), contentType: "text/xml")}
                }

            }
            catch (e) {
                addError("default.custom.error", [e.getMessage()])

                withFormat {
                    html {
                        flash.errors = errors;
                        redirect(action: show, id: searchQueryGroup.id)
                    }
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }

            }

        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
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
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])

        if (!searchQueryGroup) {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            flash.errors = errors;
            redirect(action: list)
        }
        else {
            return [searchQueryGroup: searchQueryGroup]
        }
    }


    def update = {
        def searchQueryGroup = SearchQueryGroup.get([id: params.id])
        if (searchQueryGroup) {
            if (params.name && params.name.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES)) {
                params.type = SearchQueryGroup.DEFAULT_TYPE;
            }
            searchQueryGroup.update(ControllerUtils.getClassProperties(params, SearchQueryGroup));
            if (!searchQueryGroup.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "SearchQueryGroup ${params.id} updated"
                        redirect(action: show, id: searchQueryGroup.id)
                    }
                    xml {render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} updated"), contentType: "text/xml")}
                }

            }
            else {
                withFormat {
                    html {
                        render(view: 'edit', model: [searchQueryGroup: searchQueryGroup])
                    }
                    xml {render(text: errorsToXml(searchQueryGroup.errors), contentType: "text/xml")}
                }

            }
        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
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
        def searchQueryGroup = new SearchQueryGroup()
        searchQueryGroup.properties = params
        return ['searchQueryGroup': searchQueryGroup]
    }

    def save = {
        params["username"] = session.username;
        if (params.name && params.name.equalsIgnoreCase(SearchQueryGroup.MY_QUERIES)) {
            params.type = SearchQueryGroup.DEFAULT_TYPE;
        }
        def searchQueryGroup = SearchQueryGroup.add(ControllerUtils.getClassProperties(params, SearchQueryGroup))
        if (!searchQueryGroup.hasErrors()) {
            withFormat {
                html {
                    flash.message = "SearchQueryGroup ${searchQueryGroup.id} created"
                    redirect(action: show, id: searchQueryGroup.id)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {
                    render(view: 'create', model: [searchQueryGroup: searchQueryGroup])
                }
                xml {render(text: errorsToXml(searchQueryGroup.errors), contentType: "text/xml")}
            }

        }
    }
}