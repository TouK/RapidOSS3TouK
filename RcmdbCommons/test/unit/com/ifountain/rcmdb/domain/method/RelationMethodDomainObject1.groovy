package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 1, 2008
 * Time: 2:47:12 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationMethodDomainObject1 {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["rel1", "rel2", "rel3", "rel4", "errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static cascaded = ["rel2":true]
    static datasources = [:]
    Long id ;
    Long version ;
    RelationMethodDomainObject2 rel1;
    List rel2 = [];
    RelationMethodDomainObject2 rel3;
    List rel4 = [];

    RelationMethodDomainObject2 noOtherSideRel1;
    List noOtherSideRel2 = [];
    RelationMethodDomainObject2 noOtherSideRel3;
    List noOtherSideRel4 = [];

    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static hasMany = [rel2:RelationMethodDomainObject2, rel4:RelationMethodDomainObject2, noOtherSideRel2:RelationMethodDomainObject2,noOtherSideRel4:RelationMethodDomainObject2]
    static constraints={
     __operation_class__(nullable:true)
     __is_federated_properties_loaded__(nullable:true)
     errors(nullable:true)
     rel1(nullable:true)
     rel2(nullable:true)
     rel3(nullable:true)
     rel4(nullable:true)   
     noOtherSideRel1(nullable:true)
     noOtherSideRel2(nullable:true)
     noOtherSideRel3(nullable:true)
     noOtherSideRel4(nullable:true)   
    }
    static mappedBy=[rel1:"revRel1", rel2:"revRel2", rel3:"revRel3", rel4:"revRel4"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    //AUTO_GENERATED_CODE

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject1)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}