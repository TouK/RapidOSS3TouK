/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

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
        assertTrue ( AbstractRapidDomainReadMethod.isAssignableFrom(PropertySummaryMethod));
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.list()*.remove();
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value1");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value2");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value1");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value2");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value3");
        PropertySummaryMethodDomainObject1.add(prop1:"prop1Value2 prop1Value3 prop1Value4part1 prop1Value4PArt2  prop1Value4PArt3");




        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        OperationStatistics.getInstance().reset();
        Map res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", ["prop1"]] as Object[])
        def stats=OperationStatistics.getInstance().getOperationStatisticsAsMap(OperationStatistics.PROPERTY_SUMMARY_OPERATION_NAME);
        assertEquals(1,stats.global.NumberOfOperations);

        assertEquals (1, res.size());
        assertEquals (2, res.prop1.prop1Value1);
        assertEquals (2, res.prop1.prop1Value2);
        assertEquals (1, res.prop1.prop1Value3);
        assertEquals (1, res.prop1.get("prop1Value2 prop1Value3 prop1Value4part1 prop1Value4PArt2  prop1Value4PArt3"));

        method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", "prop1"] as Object[])
        assertEquals (1, res.size());
        assertEquals (2, res.prop1.prop1Value1);
        assertEquals (2, res.prop1.prop1Value2);
        assertEquals (1, res.prop1.prop1Value3);
        assertEquals (1, res.prop1.get("prop1Value2 prop1Value3 prop1Value4part1 prop1Value4PArt2  prop1Value4PArt3"));
    }
    
    public void testPropertySummaryWithMultipleProperty()
    {
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.list()*.remove();
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1", prop2:"p2val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val1", prop2:"");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val2", prop2:"p2val2");
        PropertySummaryMethodDomainObject1.add(prop1:"p1val3", prop2:"p2val2", prop3:1l);

        PropertySummaryMethod method = new PropertySummaryMethod(PropertySummaryMethodDomainObject1.metaClass);
        OperationStatistics.getInstance().reset();
        def res = method.invoke(PropertySummaryMethodDomainObject1, ["alias:*", ["prop1", "prop2", "prop3"]] as Object[])
        def stats=OperationStatistics.getInstance().getOperationStatisticsAsMap(OperationStatistics.PROPERTY_SUMMARY_OPERATION_NAME);
        assertEquals(1,stats.global.NumberOfOperations);

        assertEquals (3, res.size());
        assertEquals (4, res.prop1.p1val1);
        assertEquals (1, res.prop1.p1val2);
        assertEquals (1, res.prop1.p1val3);

        assertEquals (1, res.prop2.p2val1);
        assertEquals (2, res.prop2.p2val2);
        assertEquals (3, res.prop2[""]);

        assertEquals (1, res.prop3[1l]);
        assertEquals (1, res.prop3["1"]);
        assertEquals ("all null properties will be saved as default property specified in compass configuration of converters",5, res.prop3["0"]);

        res.prop3.each{key, value->
            if(key != null)
            {
                assertTrue (key instanceof Long)
            }
        }
    }

    public void testPropertySummaryWithNoProperty()
    {
        initialize([PropertySummaryMethodDomainObject1], []);
        PropertySummaryMethodDomainObject1.list()*.remove();
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
        except = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    };
    static cascaded = [:]
    static datasources = [:]
    Long id ;
    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;
    String prop1;
    String prop2;
    Long prop3;


    org.springframework.validation.Errors errors ;
    Object __operation_class__ ;
    Object __dynamic_property_storage__ ;
    static constraints={
     __operation_class__(nullable:true)
     __dynamic_property_storage__(nullable:true)
     errors(nullable:true)
     prop1(nullable:true)
     prop2(nullable:true)
     prop3(nullable:true)
    }
    static relations = [:]
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__"];
    //AUTO_GENERATED_CODE

    public boolean equals(Object obj) {
        if(obj instanceof RelationMethodDomainObject1)
        {
            return obj.id == id;
        }
        return super.equals(obj);
    }
}