package search

import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events
import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML

class SearchQueryController {
    def final static PROPS_TO_BE_EXCLUDED = ["id": "id", "_action_Update": "_action_Update", "controller": "controller", "action": "action"]
    def index = {redirect(action: list, params: params)}
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]
    def list = {
        if (!params.max) params.max = 10
        def searchQueries = SearchQuery.list(params);
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
                html {
                    return [searchQuery: searchQuery]
                }
                xml {
                    def userName = session.username;
                    def searchQueryGroups = SearchQueryGroup.list().findAll {
                        it.username == userName && it.isPublic == false
                    };
                    render(contentType: 'text/xml') {
                        Edit {
                            id(searchQuery.id)
                            name(searchQuery.name)
                            query(searchQuery.query)
                            sortProperty(searchQuery.sortProperty);
                            sortOrder {
                                option(selected: searchQuery.sortOrder == 'desc', 'desc')
                                option(selected: searchQuery.sortOrder == 'asc', 'asc')
                            }
                            group {
                                searchQueryGroups.each {
                                    if (it.name == searchQuery.group.name) {
                                        option(selected: "true", it.name)
                                    }
                                    else {
                                        option(it.name)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

    }


    def update = {
        def searchQuery = SearchQuery.get([id: params.id])
        if (searchQuery) {
            if (params.group == "")
            {
                params.group = "My Queries";
            }
            def group = SearchQueryGroup.get(name: params.group, username: session.username, type:"default");
            if (group == null)
            {
                group = SearchQueryGroup.add(name: params.group, username: session.username, type:"default");
            }
            params["group"] = ["id": group.id];
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
        withFormat {
            html {
                def searchQuery = new SearchQuery()
                searchQuery.properties = params
                return ['searchQuery': searchQuery]
            }
            xml {
                def userName = session.username;
                def searchQueryGroups = SearchQueryGroup.list().findAll {
                    it.username == userName && it.isPublic == false
                };
                render(contentType: 'text/xml') {
                    Create {
                        group {
                            searchQueryGroups.each {
                                option(it.name)
                            }
                        }
                    }
                }
            }
        }

    }

    def save = {
        params["username"] = session.username
        if (params.group == "")
        {
            params.group = "My Queries";
        }
        def group = SearchQueryGroup.get(name: params.group, username: session.username, type:"default");
        if (group == null)
        {
            group = SearchQueryGroup.add(name: params.group, username: session.username, type:"default");
        }
        params["group"] = ["id": group.id];
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