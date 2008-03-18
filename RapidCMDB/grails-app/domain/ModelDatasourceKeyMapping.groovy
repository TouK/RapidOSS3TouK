class ModelDatasourceKeyMapping {
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = ModelDatasource;
    String nameInDatasource;

    static optionals = ["nameInDatasource"];

    String toString(){
        return property.name;
    }
}
