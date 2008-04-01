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
                def isValid = true;
                ModelDatasource.findAllByModelAndMaster(obj.model, true).each
                {
                    if(it.datasource.name != obj.datasource.name)
                    {
                        isValid = false;
                    }
                }
                if(!isValid){
                    return ['model.invalid.master'];
                }
            }
        })
    }
    String toString(){
        return datasource.name;  
    }

}
