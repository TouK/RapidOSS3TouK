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

    def delete = {
        def smartsTopologyDatasource = SmartsTopologyDatasource.get( params.id )
        if(smartsTopologyDatasource) {
            try{
                smartsTopologyDatasource.delete(flush:true)
                flash.message = "SmartsTopologyDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SmartsTopologyDatasource.class.getName(), smartsTopologyDatasource])]
                flash.errors = errors;
                redirect(action:show, id:smartsTopologyDatasource.id)
            }

        }
        else {
            flash.message = "SmartsTopologyDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
