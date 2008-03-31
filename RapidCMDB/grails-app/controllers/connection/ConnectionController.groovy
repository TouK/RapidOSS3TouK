package connection;
class ConnectionController {
    def scaffold = Connection;

     def show = {
        def connection = Connection.get( params.id )
        if(!connection) {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ connection : connection ] }
    }
}
