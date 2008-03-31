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
}
