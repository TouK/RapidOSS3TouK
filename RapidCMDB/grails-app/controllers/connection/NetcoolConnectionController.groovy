package connection;
class NetcoolConnectionController {

    def scaffold = NetcoolConnection

    def show = {
        def netcoolConnection = NetcoolConnection.get(params.id)

        if (!netcoolConnection) {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [netcoolConnection: netcoolConnection]}
    }

    def delete = {
        def netcoolConnection = NetcoolConnection.get( params.id )
        if(netcoolConnection) {
            try{
                netcoolConnection.delete(flush:true)
                flash.message = "NetcoolConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolConnection.class.getName(), netcoolConnection])]
                flash.errors = errors;
                redirect(action:show, id:netcoolConnection.id)
            }

        }
        else {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
