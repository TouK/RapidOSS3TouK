class ModelRelation {
     Model fromModel;
     Model toModel;
     String relationName;

     String cardinality;

     static constraints = {
         cardinality(inList:["OneToOne", "OneToMany"]);
         relationName(blank:false);
     }

     String toString(){
         return "$relationName";
     }
}
