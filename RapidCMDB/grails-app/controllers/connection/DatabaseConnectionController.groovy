package connection;
class DatabaseConnectionController {

   def scaffold = DatabaseConnection;

   def show = {
        def databaseConnection = DatabaseConnection.get( params.id )

        if(!databaseConnection) {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ databaseConnection : databaseConnection ] }
    }

    def delete = {
        def databaseConnection = DatabaseConnection.get( params.id )
        if(databaseConnection) {
            try{
                databaseConnection.delete(flush:true)
                flash.message = "DatabaseConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[DatabaseConnection.class.getName(), databaseConnection])]
                flash.errors = errors;
                redirect(action:show, id:databaseConnection.id)
            }

        }
        else {
            flash.message = "DatabaseConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
