package datasource;
class SmartsNotificationDatasourceController {
    def scaffold = SmartsNotificationDatasource;
    def show = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get(params.id)
        if (!smartsNotificationDatasource) {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [smartsNotificationDatasource: smartsNotificationDatasource]}
    }

    def delete = {
        def smartsNotificationDatasource = SmartsNotificationDatasource.get( params.id )
        if(smartsNotificationDatasource) {
            try{
                smartsNotificationDatasource.delete(flush:true)
                flash.message = "SmartsNotificationDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[SmartsNotificationDatasource.class.getName(), smartsNotificationDatasource])]
                flash.errors = errors;
                redirect(action:show, id:smartsNotificationDatasource.id)
            }

        }
        else {
            flash.message = "SmartsNotificationDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
