package model;
class ModelDatasourceKeyMapping {
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = [datasource:ModelDatasource, property:ModelProperty];
    String nameInDatasource;

    static optionals = ["nameInDatasource"];

    String toString(){
        return property.name;
    }
}
