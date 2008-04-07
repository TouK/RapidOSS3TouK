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

     def delete = {
        def netcoolDatasource = NetcoolDatasource.get( params.id )
        if(netcoolDatasource) {
            try{
                netcoolDatasource.delete(flush:true)
                flash.message = "NetcoolDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolDatasource.class.getName(), netcoolDatasource])]
                flash.errors = errors;
                redirect(action:show, id:netcoolDatasource.id)
            }

        }
        else {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
