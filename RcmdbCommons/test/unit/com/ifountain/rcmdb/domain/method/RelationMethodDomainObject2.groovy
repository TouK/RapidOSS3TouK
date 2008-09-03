package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 2:47:56 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationMethodDomainObject2 {
     //AUTO_GENERATED_CODE
    static searchable = {
        except = ["revRel1", "revRel2", "revRel3", "revRel4", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static datasources = [:]
    String prop1 ="1";
    Long id ;
    Long version ;
    RelationMethodDomainObject1 revRel1;
    RelationMethodDomainObject1 revRel2;
    List revRel4 = [];
    List revRel3 = [];
    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static hasMany = [revRel4:RelationMethodDomainObject1, revRel3:RelationMethodDomainObject1]
    static constraints={
    prop1(blank:true,nullable:true)
     __operation_class__(nullable:true)
     __is_federated_properties_loaded__(nullable:true)
     errors(nullable:true)
     revRel1(nullable:true)
     revRel2(nullable:true)
     revRel3(nullable:true)
     revRel4(nullable:true)
    }
    static mappedBy=[revRel1:"rel1", revRel2:"rel2", revRel3:"rel3", revRel4:"rel4"]
    static belongsTo = [RelationMethodDomainObject1]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];

    //AUTO_GENERATED_CODE

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject2)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}