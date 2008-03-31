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
}
