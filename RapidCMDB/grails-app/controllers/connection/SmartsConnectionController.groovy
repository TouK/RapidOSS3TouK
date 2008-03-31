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
}
