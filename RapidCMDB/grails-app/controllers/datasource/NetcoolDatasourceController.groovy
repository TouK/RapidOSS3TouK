package datasource;
class NetcoolDatasourceController {

    def scaffold = NetcoolDatasource
    def show = {
        def netcoolDatasource = NetcoolDatasource.get(params.id)
        if (!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [netcoolDatasource: netcoolDatasource]}
    }
}
