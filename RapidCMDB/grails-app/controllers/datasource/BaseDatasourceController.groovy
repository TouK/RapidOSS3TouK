package datasource;
class BaseDatasourceController {
    def scaffold = BaseDatasource;
    def show = {
        def baseDatasource = BaseDatasource.get(params.id)

        if (!baseDatasource) {
            flash.message = "BaseDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [baseDatasource: baseDatasource]}
    }
}
