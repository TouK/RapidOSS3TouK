package datasource;
class RapidInsightDatasourceController {

    def scaffold = RapidInsightDatasource;
    def show = {
        def rapidInsightDatasource = RapidInsightDatasource.get(params.id)
        if (!rapidInsightDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [rapidInsightDatasource: rapidInsightDatasource]}
    }

    def delete = {
        def rapidInsightDatasource = RapidInsightDatasource.get( params.id )
        if(rapidInsightDatasource) {
            try{
                rapidInsightDatasource.delete(flush:true)
                flash.message = "RapidInsightDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[RapidInsightDatasource.class.getName(), rapidInsightDatasource])]
                flash.errors = errors;
                redirect(action:show, id:rapidInsightDatasource.id)
            }

        }
        else {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
