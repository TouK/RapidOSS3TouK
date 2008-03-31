package datasource;
class DatabaseDatasourceController {
    def scaffold = DatabaseDatasource;
    def show = {
        def databaseDatasource = DatabaseDatasource.get(params.id)

        if (!databaseDatasource) {
            flash.message = "DatabaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [databaseDatasource: databaseDatasource]}
    }
}
