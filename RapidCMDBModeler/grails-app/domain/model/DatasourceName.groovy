package model;
class DatasourceName {
     static searchable = true;
     static cascaded = [modelDatasources:true]
     static hasMany = [modelDatasources: ModelDatasource];
     static mappedBy = [modelDatasources:'datasource']     
     String name;
     List modelDatasources = [];
     static constraints = {
         name(blank:false, nullable:false, key:[]);
     };

     String toString(){
         return "$name";
     }

}
