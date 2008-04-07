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

    def delete = {
        def connection = Connection.get( params.id )
        if(connection) {
            try{
                connection.delete(flush:true)
                flash.message = "Connection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[Connection.class.getName(), connection])]
                flash.errors = errors;
                redirect(action:show, id:connection.id) 
            }

        }
        else {
            flash.message = "Connection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
