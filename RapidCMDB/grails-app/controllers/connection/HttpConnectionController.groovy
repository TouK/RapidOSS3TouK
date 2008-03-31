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
}
