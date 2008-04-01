package model;
class ModelDatasourceKeyMapping {
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = [datasource:ModelDatasource, property:ModelProperty];
    String nameInDatasource;

    static optionals = ["nameInDatasource"];

    static constraints = {
        property(unique:'datasource', validator:{val, obj ->
            if(val.blank && obj.datasource.master){
                return ['model.keymapping.masterproperty.notblank']
            }
        });
    }

    String toString(){
        return property.name;
    }
}
