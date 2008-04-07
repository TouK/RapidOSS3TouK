package connection;
class HttpConnectionController {

    def scaffold = HttpConnection;
    def show = {
        def httpConnection = HttpConnection.get( params.id )

        if(!httpConnection) {
            flash.message = "HttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ httpConnection : httpConnection ] }
    }

    def delete = {
        def httpConnection = HttpConnection.get( params.id )
        if(httpConnection) {
            try{
                httpConnection.delete(flush:true)
                flash.message = "HttpConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[HttpConnection.class.getName(), httpConnection])]
                flash.errors = errors;
                redirect(action:show, id:httpConnection.id)
            }

        }
        else {
            flash.message = "HttpConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
