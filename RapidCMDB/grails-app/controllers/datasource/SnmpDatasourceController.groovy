package datasource;
class SnmpDatasourceController {

    def scaffold = SnmpDatasource
    def show = {
        def snmpDatasource = SnmpDatasource.get(params.id)
        if (!snmpDatasource) {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [snmpDatasource: snmpDatasource]}
    }

     def delete = {
        def snmpDatasource = SnmpDatasource.get( params.id )
        if(snmpDatasource) {
            try{
                snmpDatasource.delete(flush:true)
                flash.message = "SnmpDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SnmpDatasource.class.getName(), snmpDatasource])]
                flash.errors = errors;
                redirect(action:show, id:snmpDatasource.id)
            }

        }
        else {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def start = {
        def snmpDatasource = SnmpDatasource.get( params.id )
        if(snmpDatasource) {
            try{
                snmpDatasource.open()
                flash.message = "SnmpDatasource ${params.id} started"
                redirect(action:show, id:snmpDatasource.id)
            }
            catch(e){
                def errors =[e.getMessage()]
                flash.errors = errors;
                redirect(action:show, id:snmpDatasource.id)
            }

        }
        else {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def stop = {
         def snmpDatasource = SnmpDatasource.get( params.id )
        if(snmpDatasource) {
            snmpDatasource.close()
            flash.message = "SnmpDatasource ${params.id} stopped"
            redirect(action:show, id:snmpDatasource.id)
        }
        else {
            flash.message = "SnmpDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
