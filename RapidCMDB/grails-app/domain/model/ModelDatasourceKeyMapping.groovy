package model;
class ModelDatasourceKeyMapping {
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = [datasource:ModelDatasource, property:ModelProperty];
    String nameInDatasource;

    static optionals = ["nameInDatasource"];

    static constraints = {
        property(unique:'datasource');
    }

    String toString(){
        return property.name;
    }
}
