package model;
class ModelRelation {
     public static String ONE_TO_ONE = "OneToOne";
     public static String ONE_TO_MANY = "OneToMany";
     public static String MANY_TO_MANY = "ManyToMany";
     Model fromModel;
     Model toModel;
     String fromName;
     String toName;
     String cardinality;
     static belongsTo=[toModel:Model, fromModel:Model];
     static constraints = {
         cardinality(inList:[ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY]);
         fromName(blank:false);
         toName(blank:false);
     }

     String toString(){
         return "$fromName";
     }
}
