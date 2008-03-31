package datasource;
class SingleTableDatabaseDatasourceController {
    def scaffold = SingleTableDatabaseDatasource;
    def show = {
        def singleTableDatabaseDatasource = SingleTableDatabaseDatasource.get(params.id)
        if (!singleTableDatabaseDatasource) {
            flash.message = "RapidInsightDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [singleTableDatabaseDatasource: singleTableDatabaseDatasource]}
    }
}
