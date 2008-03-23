class ModelRelation {
     public static String ONE_TO_ONE = "OneToOne";
     public static String ONE_TO_MANY = "OneToMany";
     public static String MANY_TO_MANY = "ManyToMany";
     static belongsTo = [fromModel:Model]
     Model toModel;
     String name;
     String cardinality;
     static constraints = {
         cardinality(inList:[ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY]);
         name(blank:false);
     }

     def setName(String nameP)
     {
        def firstChar = nameP.substring (0,1)
        def remaining = nameP.substring (1);
        name = firstChar.toLowerCase()+remaining;
     }

     String toString(){
         return "$name";
     }
}
