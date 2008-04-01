package model

import datasource.BaseDatasource;
class ModelDatasource {
    BaseDatasource datasource;
    boolean master = false;
    Model model;
    static belongsTo = Model;

    static hasMany = [keyMappings: ModelDatasourceKeyMapping];

    static constraints = {
        datasource(unique:'model');
        master(validator: {val, obj ->
            if (val){
                ModelDatasource.findAllByModelAndMaster(obj.model, true).each
                {
                    if(it.datasource.name != obj.datasource.name)
                    {
                        return ['model.invalid.master'];
                    }
                    else{
                        it.keyMappings?.each{keyMapping ->
                            if(keyMapping.property.blank){
                                return ['model.keymapping.masterproperty.notblank'];
                            }
                        }
                    }
                }
            }
        })
    }
    String toString(){
        return datasource.name;  
    }

}
