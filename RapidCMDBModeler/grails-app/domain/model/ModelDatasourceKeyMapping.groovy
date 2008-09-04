package model

import com.ifountain.rcmdb.util.RapidCMDBConstants;
class ModelDatasourceKeyMapping {
    static searchable = {
        except:["datasource", "property"]
    };
    ModelProperty property;
    ModelDatasource datasource;
    String nameInDatasource;
    static relations = [
            datasource:[type:ModelDatasource, reverseName:"keyMappings", isMany:false],
            property:[type:ModelProperty, reverseName:"mappedKeys", isMany:false]
    ]
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
