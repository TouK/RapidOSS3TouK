package search;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML
import auth.RsUser;

class SearchQueryGroupController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

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
            withFormat {
                html {
                    flash.message = "SearchQueryGroup not found with id ${params.id}"
                    redirect(action:list)
                }
                xml { render searchQueryGroup as XML }
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
                    xml {  }
                }

            }
            catch(e){
                withFormat {
                    html {
                        def errors =[message(code:"default.couldnot.delete", args:[SearchQueryGroup, searchQueryGroup])]
                        flash.errors = errors;
                        redirect(action:show, id:searchQueryGroup.id)
                    }
                    xml {  }
                }

            }

        }
        else {
            withFormat {
                html {
                    flash.message = "SearchQueryGroup not found with id ${params.id}"
                    redirect(action:list)
                }
                xml {  }
            }

        }
    }

    def edit = {
        def searchQueryGroup = SearchQueryGroup.get( [id:params.id] )

        if(!searchQueryGroup) {
            flash.message = "SearchQueryGroup not found with id ${params.id}"
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
                    xml {  }
                }

            }
            else {
                withFormat {
                    html {
                        render(view:'edit',model:[searchQueryGroup:searchQueryGroup])
                    }
                    xml {  }
                }

            }
        }
        else {
            withFormat {
                html {
                    flash.message = "SearchQueryGroup not found with id ${params.id}"
                    redirect(action:edit,id:params.id)
                }
                xml {  }
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
                xml { render searchQueryGroup as XML }
            }

        }
        else {
            withFormat {
                html {
                    render(view:'create',model:[searchQueryGroup:searchQueryGroup])
                }
                xml { }
            }

        }
    }
}