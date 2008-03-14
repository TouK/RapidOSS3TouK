class SingleTableDatabaseDatasource extends BaseDatasource{
    DatabaseConnection connection;
    String table;
    String keys;
    String adapterClass = "datasources.SingleTableDatabaseAdapter";

    static constraints = {
        table(blank:false, nullable:false);
        keys(blank:false, nullable:false)
    };
}
