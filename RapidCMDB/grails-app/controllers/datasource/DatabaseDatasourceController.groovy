package datasource;
class DatabaseDatasourceController {
    def scaffold = DatabaseDatasource;
    def show = {
        def databaseDatasource = DatabaseDatasource.get(params.id)

        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [databaseDatasource: databaseDatasource]}
    }

    def delete = {
        def databaseDatasource = DatabaseDatasource.get( params.id )
        if(databaseDatasource) {
            try{
                databaseDatasource.delete(flush:true)
                flash.message = "DatabaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[DatabaseDatasource.class.getName(), databaseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:databaseDatasource.id)
            }

        }
        else {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
