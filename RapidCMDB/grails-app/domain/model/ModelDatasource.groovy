package model

import datasource.BaseDatasource;
class ModelDatasource {
    BaseDatasource datasource;
    boolean master = false;
    Model model;
    static belongsTo = Model;

    static hasMany = [keyMappings: ModelDatasourceKeyMapping];

    String toString(){
        return datasource.name;
    }
}
