package model;
class DatasourceName {
     String name;
     static constraints = {
         name(blank:false, nullable:false, unique:true);
     };

     String toString(){
         return "$name";
     }

}
