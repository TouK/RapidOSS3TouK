package search;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML
import auth.RsUser;

class SearchQueryGroupController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    def list = {
        if(!params.max) params.max = 10
        def searchQueryGroups = SearchQueryGroup.list( params );
        withFormat {
			html searchQueryGroupList:searchQueryGroups
			xml { render searchQueryGroups as XML }
		}
    }

    def show = {
        def searchQueryGroup = SearchQueryGroup.get([id:params.id])
        if(!searchQueryGroup) {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action:list)
                }
                xml { render(text:ControllerUtils.convertErrorsToXml(errors), contentType:"text/xml") }
            }

        }
        else {
            withFormat {
                html {render(view:"show",model:[searchQueryGroup:searchQueryGroup])}
                xml { render searchQueryGroup as XML }
            }
        }
    }

    def delete = {
        def searchQueryGroup = SearchQueryGroup.get( [id:params.id])
        if(searchQueryGroup) {
            try{
                searchQueryGroup.remove()
                withFormat {
                    html {
                        flash.message = "SearchQueryGroup ${params.id} deleted"
                        redirect(action:list)
                    }
                    xml { render(text:ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} deleted"), contentType:"text/xml") }
                }

            }
            catch(e){
                addError("default.couldnot.delete", [SearchQueryGroup, searchQueryGroup])
                withFormat {
                    html {
                        flash.errors = errors;
                        redirect(action:show, id:searchQueryGroup.id)
                    }
                    xml { render(text:ControllerUtils.convertErrorsToXml(errors), contentType:"text/xml") }
                }

            }

        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
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
        def searchQueryGroup = SearchQueryGroup.get( [id:params.id] )

        if(!searchQueryGroup) {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
            flash.errors = errors;
            redirect(action:list)
        }
        else {
            return [ searchQueryGroup : searchQueryGroup ]
        }
    }


    def update = {
        def searchQueryGroup = SearchQueryGroup.get( [id:params.id] )
        if(searchQueryGroup) {
            searchQueryGroup.update(ControllerUtils.getClassProperties(params, SearchQueryGroup));
            if(!searchQueryGroup.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "SearchQueryGroup ${params.id} updated"
                        redirect(action:show,id:searchQueryGroup.id)
                    }
                    xml { render(text:ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} updated"), contentType:"text/xml") }
                }

            }
            else {
                withFormat {
                    html {
                        render(view:'edit',model:[searchQueryGroup:searchQueryGroup])
                    }
                    xml { render(text:ControllerUtils.convertErrorsToXml(searchQueryGroup.errors), contentType:"text/xml") }
                }

            }
        }
        else {
            addError("default.object.not.found", [SearchQueryGroup.class.name, params.id]);
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
        def searchQueryGroup = new SearchQueryGroup()
        searchQueryGroup.properties = params
        return ['searchQueryGroup':searchQueryGroup]
    }

    def save = {
        def user = RsUser.get(username:session.username);
        params["user"] = ["id":user.id]
        def searchQueryGroup = SearchQueryGroup.add(ControllerUtils.getClassProperties(params, SearchQueryGroup))
        if(!searchQueryGroup.hasErrors()) {
            withFormat {
                html {
                    flash.message = "SearchQueryGroup ${searchQueryGroup.id} created"
                    redirect(action:show,id:searchQueryGroup.id)
                }
                xml { render(text:ControllerUtils.convertSuccessToXml("SearchQueryGroup ${params.id} created"), contentType:"text/xml") }
            }

        }
        else {
            withFormat {
                html {
                    render(view:'create',model:[searchQueryGroup:searchQueryGroup])
                }
                xml { render(text:ControllerUtils.convertErrorsToXml(searchQueryGroup.errors), contentType:"text/xml") }
            }

        }
    }
}