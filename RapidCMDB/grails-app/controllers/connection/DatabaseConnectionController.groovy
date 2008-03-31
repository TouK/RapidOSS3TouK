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
}
