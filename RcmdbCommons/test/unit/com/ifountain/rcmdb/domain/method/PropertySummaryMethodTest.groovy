package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 10, 2008
 * Time: 4:36:48 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertySummaryMethodTest extends RapidCmdbWithCompassTestCase{
    public void testPropertySummary()
    {
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value1");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value2");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value1");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value2");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value3");

        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        def res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", ["prop1"]] as Object[])
        assertEquals (1, res.size());
        assertEquals (2, res.prop1.prop1Value1);
        assertEquals (2, res.prop1.prop1Value2);
        assertEquals (1, res.prop1.prop1Value3);

        method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", "prop1"] as Object[])
        assertEquals (1, res.size());
        assertEquals (2, res.prop1.prop1Value1);
        assertEquals (2, res.prop1.prop1Value2);
        assertEquals (1, res.prop1.prop1Value3);
    }
    
    public void testPropertySummaryWithMultipleProperty()
    {
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1", prop2:"p2val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1", prop2:"");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val2", prop2:"p2val2");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val3", prop2:"p2val2", prop3:1l);

        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        def res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", ["prop1", "prop2", "prop3"]] as Object[])
        assertEquals (3, res.size());
        assertEquals (4, res.prop1.p1val1);
        assertEquals (1, res.prop1.p1val2);
        assertEquals (1, res.prop1.p1val3);

        assertEquals (1, res.prop2.p2val1);
        assertEquals (2, res.prop2.p2val2);
        assertEquals (2, res.prop2["null"]);
        assertEquals (1, res.prop2[""]);
        
        assertEquals (1, res.prop3.get(1l));
        assertEquals (1, res.prop3.get("1"));
    }

    public void testPropertySummaryWithNoProperty()
    {
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1", prop2:"p2val1");

        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        def res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", []] as Object[])
        assertEquals (0, res.size());
    }

    public void testPropertySummaryWithWrongNumberofParameters()
    {

        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        def res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*"] as Object[])
        assertEquals (0, res.size());
    }

    public void testPropertySummaryWithoutParameters()
    {
        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        def res = method.invoke(PropertySummaryMethodDomainObject1, [] as Object[])
        assertEquals (0, res.size());
    }

}

class PropertySummaryMethodDomainObject1 {
    //AUTO_GENERATED_CODE
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__"];
    };
    static cascaded = [:]
    static datasources = [:]
    Long id ;
    Long version ;
    String prop1;
    String prop2;
    Long prop3;


    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __is_federated_properties_loaded__ ;
    static constraints={
     __operation_class__(nullable:true)
     __is_federated_properties_loaded__(nullable:true)
     errors(nullable:true)
     prop1(nullable:true)
     prop2(nullable:true)
     prop3(nullable:true)
    }
    static relations = [:]
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