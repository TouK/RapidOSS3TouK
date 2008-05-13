package connection;

class SnmpConnectionController {

    def scaffold = SnmpConnection;

    def show = {
        def snmpConnection = SnmpConnection.get(params.id)

        if (!snmpConnection) {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [snmpConnection: snmpConnection]}
    }

    def delete = {
        def snmpConnection = SnmpConnection.get( params.id )
        if(snmpConnection) {
            try{
                snmpConnection.delete(flush:true)
                flash.message = "SnmpConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SnmpConnection.class.getName(), snmpConnection])]
                flash.errors = errors;
                redirect(action:show, id:snmpConnection.id)
            }

        }
        else {
            flash.message = "SnmpConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}