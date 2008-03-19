class HelloController {

    def index = {
        def datasources = BaseDatasource.list();
        render(contentType:'text/xml') {
            Datasources{
                datasources.each{
                    Datasource(name: it.name);
                }
            }
        }
    }
}
