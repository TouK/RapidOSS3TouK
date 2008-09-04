package model;
class DatasourceName {
     static searchable = {
         except:["modelDatasources"]
     };
     static cascaded = [modelDatasources:true]
     static relations = [modelDatasources:[type:ModelDatasource, reverseName:"datasource", isMany:true]]
     String name;
     List modelDatasources = [];
     static constraints = {
         name(blank:false, nullable:false, key:[]);
     };

     String toString(){
         return "$name";
     }

}
