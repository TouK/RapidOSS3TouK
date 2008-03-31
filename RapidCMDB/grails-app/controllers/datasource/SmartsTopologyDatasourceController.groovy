package datasource;
class SmartsTopologyDatasourceController {
    def scaffold = SmartsTopologyDatasource;
    def show = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get(params.id)
        if (!smartsTopologyDatasource) {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [smartsTopologyDatasource: smartsTopologyDatasource]}
    }
}
