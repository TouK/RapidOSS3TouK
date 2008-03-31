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
}
