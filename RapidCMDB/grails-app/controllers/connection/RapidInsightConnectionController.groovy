package connection;
class RapidInsightConnectionController {
    def scaffold = RapidInsightConnection;

    def show = {
        def rapidInsightConnection = RapidInsightConnection.get(params.id)

        if (!rapidInsightConnection) {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [rapidInsightConnection: rapidInsightConnection]}
    }

     def delete = {
        def rapidInsightConnection = RapidInsightConnection.get( params.id )
        if(rapidInsightConnection) {
            try{
                rapidInsightConnection.delete(flush:true)
                flash.message = "RapidInsightConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[RapidInsightConnection.class.getName(), rapidInsightConnection])]
                flash.errors = errors;
                redirect(action:show, id:rapidInsightConnection.id)
            }

        }
        else {
            flash.message = "RapidInsightConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
