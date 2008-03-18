class ModelRelation {
     Model fromModel;
     Model toModel;

     String cardinality;

     static constraints = {
         cardinality(inList(["OneToOne, OneToMany, ManyToMany"]));
     }
}
