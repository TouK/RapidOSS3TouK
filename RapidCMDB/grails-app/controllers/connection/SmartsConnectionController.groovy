package connection;

class SmartsConnectionController {

    def scaffold = SmartsConnection;

    def show = {
        def smartsConnection = SmartsConnection.get(params.id)

        if (!smartsConnection) {
            flash.message = "SmartsConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [smartsConnection: smartsConnection]}
    }

    def delete = {
        def smartsConnection = SmartsConnection.get( params.id )
        if(smartsConnection) {
            try{
                smartsConnection.delete(flush:true)
                flash.message = "SmartsConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SmartsConnection.class.getName(), smartsConnection])]
                flash.errors = errors;
                redirect(action:show, id:smartsConnection.id)
            }

        }
        else {
            flash.message = "SmartsConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
