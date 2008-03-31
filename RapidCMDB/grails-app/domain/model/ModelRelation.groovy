package model;
class ModelRelation {
     public static String ONE = "One";
     public static String MANY = "Many";
     Model firstModel;
     Model secondModel;
     String firstName;
     String secondName;
     String firstCardinality;
     String secondCardinality;
     static belongsTo=[firstModel:Model, secondModel:Model];
     static constraints = {
         firstCardinality(inList:[ONE, MANY]);
         secondCardinality(inList:[ONE, MANY]);
         firstName(blank:false);
         secondName(blank:false);
     }

     String toString(){
         return "$firstName";
     }
}
