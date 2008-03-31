package datasource;

class HttpDatasourceController {
    def scaffold = HttpDatasource;
    def show = {
        def httpDatasource = HttpDatasource.get(params.id)
        if (!httpDatasource) {
            flash.message = "HttpDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [httpDatasource: httpDatasource]}
    }
}
