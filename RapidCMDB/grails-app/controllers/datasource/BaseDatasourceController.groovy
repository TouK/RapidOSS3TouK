package datasource;
class BaseDatasourceController {
    def scaffold = BaseDatasource;
    def show = {
        def baseDatasource = BaseDatasource.get(params.id)

        if (!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [baseDatasource: baseDatasource]}
    }
    def delete = {
        def baseDatasource = BaseDatasource.get( params.id )
        if(baseDatasource) {
            try{
                baseDatasource.delete(flush:true)
                flash.message = "BaseDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[BaseDatasource.class.getName(), baseDatasource])]
                flash.errors = errors;
                redirect(action:show, id:baseDatasource.id)
            }

        }
        else {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
