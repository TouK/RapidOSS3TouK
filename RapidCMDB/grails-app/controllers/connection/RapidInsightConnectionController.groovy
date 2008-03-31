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
}
