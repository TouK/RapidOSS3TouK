package search;
import com.ifountain.rcmdb.domain.util.ControllerUtils
import grails.converters.XML;

class SearchQueryController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        def searchQueries = SearchQuery.list( params );
        withFormat {
			html searchQueryList:books
			xml { render searchQueries as XML }
		}
    }

    def show = {
        def searchQuery = SearchQuery.get([id:params.id])

        if(!searchQuery) {
            withFormat {
                html {
                    flash.message = "SearchQuery not found with id ${params.id}"
                    redirect(action:list)
                }
                xml { render searchQuery as XML }
            }

        }
        else {
            withFormat {
                html searchQuery : searchQuery
                xml { render searchQuery as XML }
            }
        }
    }

    def delete = {
        def searchQuery = SearchQuery.get( [id:params.id])
        if(searchQuery) {
            try{
                searchQuery.remove()
                withFormat {
                    html {
                        flash.message = "SearchQuery ${params.id} deleted"
                        redirect(action:list)
                    }
                    xml {  }
                }

            }
            catch(e){
                withFormat {
                    html {
                        def errors =[message(code:"default.couldnot.delete", args:[SearchQuery, searchQuery])]
                        flash.errors = errors;
                        redirect(action:show, id:searchQuery.id)
                    }
                    xml {  }
                }

            }

        }
        else {
            withFormat {
                html {
                    flash.message = "SearchQuery not found with id ${params.id}"
                    redirect(action:list)
                }
                xml {  }
            }

        }
    }

    def edit = {
        def searchQuery = SearchQuery.get( [id:params.id] )

        if(!searchQuery) {
            flash.message = "SearchQuery not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ searchQuery : searchQuery ]
        }
    }


    def update = {
        def searchQuery = SearchQuery.get( [id:params.id] )
        if(searchQuery) {
            searchQuery.update(ControllerUtils.getClassProperties(params, SearchQuery));
            if(!searchQuery.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "SearchQuery ${params.id} updated"
                        redirect(action:show,id:searchQuery.id)
                    }
                    xml {  }
                }

            }
            else {
                withFormat {
                    html {
                        render(view:'edit',model:[searchQuery:searchQuery])
                    }
                    xml {  }
                }

            }
        }
        else {
            withFormat {
                html {
                    flash.message = "SearchQuery not found with id ${params.id}"
                    redirect(action:edit,id:params.id)
                }
                xml {  }
            }

        }
    }

    def create = {
        def searchQuery = new SearchQuery()
        searchQuery.properties = params
        return ['searchQuery':searchQuery]
    }

    def save = {
        def searchQuery = SearchQuery.add(ControllerUtils.getClassProperties(params, SearchQuery))
        if(!searchQuery.hasErrors()) {
            withFormat {
                html {
                    flash.message = "SearchQuery ${searchQuery.id} created"
                    redirect(action:show,id:searchQuery.id)
                }
                xml { render searchQuery as XML }
            }

        }
        else {
            withFormat {
                html {
                    render(view:'create',model:[searchQuery:searchQuery])
                }
                xml { }
            }

        }
    }
}