package model;
class ModelDatasourceKeyMapping {
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = [datasource:ModelDatasource, property:ModelProperty];
    String nameInDatasource;

    static constraints = {
        property(unique:'datasource', validator:{val, obj ->
            if(val.blank && obj.datasource.master){
                return ['model.keymapping.masterproperty.notblank']
            }
        });
        nameInDatasource(nullable:true);
    }

    String toString(){
        return property.name;
    }
}
