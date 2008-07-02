package model

import com.ifountain.rcmdb.util.RapidCMDBConstants;
class ModelDatasourceKeyMapping {
    static searchable = true;
    ModelProperty property;
    ModelDatasource datasource;
    static belongsTo = [datasource:ModelDatasource, property:ModelProperty];
    String nameInDatasource;
    static mappedBy = [datasource:'keyMappings', property:"mappedKeys"]
    static constraints = {
        property(key:['datasource'], nullable:true, validator:{val, obj ->
            if(val.propertyDatasource && val.propertyDatasource.datasource.name != RapidCMDBConstants.RCMDB){
                return ['model.keymapping.cannot.be.federated']
            }
        });
        nameInDatasource(nullable:true);
    }

    String toString(){
        return property.name;
    }
}
