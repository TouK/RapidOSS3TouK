package search;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML
import auth.RsUser
import org.springframework.validation.BindException;

class SearchQueryController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    def list = {
        if(!params.max) params.max = 10
        def searchQueries = SearchQuery.list( params );
        withFormat {
			html searchQueryList:searchQueries
			xml { render searchQueries as XML }
		}
    }

    def show = {
        def searchQuery = SearchQuery.get([id:params.id])

        if(!searchQuery) {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action:list)
                }
                xml { ControllerUtils.convertErrorsToXml(errors) }
            }

        }
        else {
            withFormat {
                html {render(view:"show",model:[searchQuery:searchQuery])}
                xml { render searchQuery as XML }
            }
        }
    }

    def delete = {
        def searchQuery = SearchQuery.get( [id:params.id])
        if(searchQuery) {
            try{
                searchQuery.remove();
                withFormat {
                    html {
                        flash.message = "SearchQuery ${params.id} deleted"
                        redirect(action:list)
                    }
                    xml {render(text:ControllerUtils.convertSuccessToXml( "SearchQuery ${searchQuery.id} deleted"), contentType:"text/xml")}
                }

            }
            catch(e){
                addError("default.couldnot.delete", [SearchQuery, searchQuery])
                withFormat {
                    html {
                        flash.errors = errors;
                        redirect(action:show, id:searchQuery.id)
                    }
                    xml { render(text:ControllerUtils.convertErrorsToXml(errors), contentType:"text/xml") }
                }

            }

        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action:list)
                }
                xml { render(text:ControllerUtils.convertErrorsToXml(errors), contentType:"text/xml") }
            }

        }
    }

    def edit = {
        def searchQuery = SearchQuery.get( [id:params.id] )

        if(!searchQuery) {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            flash.errors = errors;
            redirect(action:list)
        }
        else {
            return [ searchQuery : searchQuery ]
        }
    }


    def update = {
        def searchQuery = SearchQuery.get( [id:params.id] )

        if(searchQuery) {
            if(params.group == "")
            {
                params.group = "Default";
            }
            def group = SearchQueryGroup.get(name:params.group, user:user);
            if(group  == null)
            {
                group = SearchQueryGroup.add(name:params.group, user:user);
            }
            params["group"] = ["id":group.id];
            searchQuery.update(ControllerUtils.getClassProperties(params, SearchQuery));
            if(!searchQuery.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "SearchQuery ${params.id} updated"
                        redirect(action:show,id:searchQuery.id)
                    }
                    xml {render(text:ControllerUtils.convertSuccessToXml( "SearchQuery ${searchQuery.id} updated"), contentType:"text/xml")}
                }

            }
            else {
                withFormat {
                    html {
                        render(view:'edit',model:[searchQuery:searchQuery])
                    }
                    xml { render(text:ControllerUtils.convertErrorsToXml(searchQuery.errors), contentType:"text/xml") }
                }
            }
        }
        else {
            addError("default.object.not.found", [SearchQuery.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action:edit,id:params.id)
                }
                xml { render(text:ControllerUtils.convertErrorsToXml(errors), contentType:"text/xml") }
            }

        }
    }

    def create = {
        def searchQuery = new SearchQuery()
        searchQuery.properties = params
        return ['searchQuery':searchQuery]
    }

    def save = {
        def user = RsUser.get(username:session.username);
        params["user"] = ["id":user.id]
        if(params.group == "")
        {
            params.group = "Default";
        }
        def group = SearchQueryGroup.get(name:params.group, user:user);
        if(group  == null)
        {
            group = SearchQueryGroup.add(name:params.group, user:user);
        }
        params["group"] = ["id":group.id];
        def searchQuery = SearchQuery.add(ControllerUtils.getClassProperties(params, SearchQuery))
        if(!searchQuery.hasErrors()) {
            withFormat {
                html {
                    flash.message = "SearchQuery ${searchQuery.id} created"
                    redirect(action:show,id:searchQuery.id)
                }
                xml {render(text:ControllerUtils.convertSuccessToXml( "SearchQuery ${searchQuery.id} created"), contentType:"text/xml")}
            }

        }
        else {
            withFormat {
                html {
                    render(view:'create',model:[searchQuery:searchQuery])
                }
                xml { render(text:ControllerUtils.convertErrorsToXml(searchQuery.errors), contentType:"text/xml") }
            }

        }
    }
}