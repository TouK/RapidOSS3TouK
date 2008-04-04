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
         firstName(blank:false, unique:'firstModel', validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 'a' && firstChar <= 'z')){
                return ['modelrelation.name.uppercased'];
            }
        });
        secondName(blank:false, unique:'secondModel', validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 'a' && firstChar <= 'z')){
                return ['modelrelation.name.uppercased'];
            }
        });
     }

     String toString(){
         return "$firstName";
     }
}
