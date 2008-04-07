package datasource;
class SingleTableDatabaseDatasourceController {
    def scaffold = SingleTableDatabaseDatasource;
    def show = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get(params.id)
        if (!singleTableDatabaseDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [singleTableDatabaseDatasource: singleTableDatabaseDatasource]}
    }

    def delete = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get( params.id )
        if(singleTableDatabaseDatasource) {
            try{
                singleTableDatabaseDatasource.delete(flush:true)
                flash.message = "SingleTableDatabaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SingleTableDatabaseDatasource.class.getName(), singleTableDatabaseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:singleTableDatabaseDatasource.id)
            }

        }
        else {
            flash.message = "SingleTableDatabaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
