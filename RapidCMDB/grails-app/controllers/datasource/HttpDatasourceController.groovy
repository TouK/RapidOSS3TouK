package datasource;

class HttpDatasourceController {
    def scaffold = HttpDatasource;
    def show = {
        def httpDatasource = HttpDatasource.get(params.id)
        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [httpDatasource: httpDatasource]}
    }

    def delete = {
        def httpDatasource = HttpDatasource.get( params.id )
        if(httpDatasource) {
            try{
                httpDatasource.delete(flush:true)
                flash.message = "HttpDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[HttpDatasource.class.getName(), httpDatasource])]
                flash.errors = errors;
                redirect(action:show, id:httpDatasource.id)
            }

        }
        else {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
