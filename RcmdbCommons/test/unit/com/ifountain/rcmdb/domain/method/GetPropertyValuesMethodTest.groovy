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

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.statistics.OperationStatistics

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 14, 2008
 * Time: 9:48:15 AM
 * To change this template use File | Settings | File Templates.
 */
class GetPropertyValuesMethodTest extends RapidCmdbWithCompassTestCase {

    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    def initializeCompassWithSimpleObject()
    {
        def modelName = "Model";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop2 = [name: "prop2", type: ModelGenerator.NUMBER_TYPE, blank: false];
        def modelMetaProps = [name: modelName]
        def modelProps = [keyProp, prop1, prop2];
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, modelProps, keyPropList, [])

        this.gcl.parseClass(modelString);
        Class modelClass = this.gcl.loadClass(modelName);
        initialize([modelClass], [])
        return modelClass;
    }
    public void testGetPropertyValues()
    {
        assertTrue ( AbstractRapidDomainReadMethod.isAssignableFrom(GetPropertyValuesMethod));
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        def modelInstance2 = modelClass.'add'(keyProp: "obj2", prop1: "prop1Value1", prop2: 2);
        def modelInstance3 = modelClass.'add'(keyProp: "obj3", prop1: "prop1Value2", prop2: 1);
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());

        OperationStatistics.getInstance().reset();
        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"]);
        def stats=OperationStatistics.getInstance().getOperationStatisticsAsMap(OperationStatistics.GET_PROPERTY_VALUES_OPERATION_NAME);
        assertEquals(1,stats.global.NumberOfOperations);
        assertEquals(1,stats.Model.NumberOfOperations);
        assertEquals(1,stats.Model_1.NumberOfOperations);
        

        assertEquals(3, res.size());
        assertEquals(4, res[0].size())
        assertEquals(4, res[1].size())
        assertEquals(4, res[2].size())
        assertEquals(modelClass.name, res[0].alias);
        assertEquals(modelClass.name, res[1].alias);
        assertEquals(modelClass.name, res[2].alias);

        assertEquals(modelInstance1.id, res[0].id);
        assertEquals(modelInstance2.id, res[1].id);
        assertEquals(modelInstance3.id, res[2].id);

        assertEquals(modelInstance1.prop1, res[0].prop1);
        assertEquals(modelInstance2.prop1, res[1].prop1);
        assertEquals(modelInstance3.prop1, res[2].prop1);

        assertEquals(modelInstance1.prop2, res[0].prop2);
        assertEquals(modelInstance2.prop2, res[1].prop2);
        assertEquals(modelInstance3.prop2, res[2].prop2);

        res = modelClass.'getPropertyValues'("prop2:1", ["prop1"]);

        assertEquals(2, res.size());
        assertEquals(3, res[0].size())
        assertEquals(3, res[1].size())
        assertEquals(modelClass.name, res[0].alias);
        assertEquals(modelClass.name, res[1].alias);

        assertEquals(modelInstance1.id, res[0].id);
        assertEquals(modelInstance3.id, res[1].id);

        assertEquals(modelInstance1.prop1, res[0].prop1);
        assertEquals(modelInstance3.prop1, res[1].prop1);
    }

    public void testGetPropertyValuesWithUndefinedProperties()
    {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        assertFalse(modelInstance1.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "undefined"]);
        assertEquals(1, res.size());
        assertEquals(3, res[0].size())
        assertEquals(modelClass.name, res[0].alias);
        assertEquals(modelInstance1.id, res[0].id);
        assertEquals(modelInstance1.prop1, res[0].prop1);
    }

    public void testGetPropertyValuesWithNullAndEmptyStringProperties()
    {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp:"obj1", prop1:"", prop2:1);
        def modelInstance2 = modelClass.'add'(keyProp:"obj2", prop1:null, prop2:1);
        assertFalse (modelInstance1.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1"]);
        assertEquals (2, res.size());
        assertEquals (3, res[0].size())
        assertEquals (modelClass.name, res[0].alias);
        assertEquals (modelInstance1.id, res[0].id);
        assertEquals ("", res[0].prop1);
        assertEquals (modelClass.name, res[1].alias);
        assertEquals (modelInstance2.id, res[1].id);
        println "<"+res[1].prop1+">"
        assertEquals ("", res[1].prop1);
    }

    public void testGetPropertyValuesWithModelHasParent()
    {
        def childModelName1 = "ChildModel1";
        def childModelName2 = "ChildModel2";
        def parentModelName = "ParentModel";
        def keyProp = [name: "keyProp", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop1 = [name: "prop1", type: ModelGenerator.STRING_TYPE, blank: false];
        def prop2 = [name: "prop2", type: ModelGenerator.NUMBER_TYPE, blank: false];
        def childModel1MetaProps = [name: childModelName1, parentModel: parentModelName]
        def childModel2MetaProps = [name: childModelName2, parentModel: parentModelName]
        def parentModelMetaProps = [name: parentModelName]
        def modelProps = [keyProp, prop1, prop2];
        def keyPropList = [keyProp];
        String childModel1String = ModelGenerationTestUtils.getModelText(childModel1MetaProps, [prop1, prop2], keyPropList, [])
        String childModel2String = ModelGenerationTestUtils.getModelText(childModel2MetaProps, [prop1], keyPropList, [])
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, [keyProp], keyPropList, [])

        this.gcl.parseClass(childModel1String + childModel2String + parentModelString);
        Class childModel1Class = this.gcl.loadClass(childModelName1);
        Class childModel2Class = this.gcl.loadClass(childModelName2);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        initialize([childModel1Class, childModel2Class, parentModelClass], [])

        def childModel1Instance = childModel1Class.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        def childModel2Instance = childModel2Class.'add'(keyProp: "obj2", prop1: "prop1Value1");
        assertFalse(childModel1Instance.hasErrors());
        assertFalse(childModel2Instance.hasErrors());

        def res = parentModelClass.'getPropertyValues'("alias:*", ["prop1", "prop2", "undefined"]);

        assertEquals(2, res.size());
        assertEquals(4, res[0].size())
        assertEquals(3, res[1].size())
        assertEquals(childModel1Class.name, res[0].alias);
        assertEquals(childModel2Class.name, res[1].alias);

        assertEquals(childModel1Instance.id, res[0].id);
        assertEquals(childModel2Instance.id, res[1].id);

        assertEquals(childModel1Instance.prop1, res[0].prop1);
        assertEquals(childModel2Instance.prop1, res[1].prop1);

        assertEquals(childModel1Instance.prop2, res[0].prop2);
    }

    public void testGetPropertyValuesWithModelWithOptions()
    {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        def modelInstance2 = modelClass.'add'(keyProp: "obj2", prop1: "prop1Value1", prop2: 2);
        def modelInstance3 = modelClass.'add'(keyProp: "obj3", prop1: "prop1Value2", prop2: 3);
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"], [sort: "prop2", order: "desc"]);
        assertEquals(3, res.size());
        assertEquals(4, res[0].size())
        assertEquals(4, res[1].size())
        assertEquals(4, res[2].size())
        assertEquals(modelClass.name, res[0].alias);
        assertEquals(modelClass.name, res[1].alias);
        assertEquals(modelClass.name, res[2].alias);

        assertEquals(modelInstance3.id, res[0].id);
        assertEquals(modelInstance2.id, res[1].id);
        assertEquals(modelInstance1.id, res[2].id);

        assertEquals(modelInstance3.prop1, res[0].prop1);
        assertEquals(modelInstance2.prop1, res[1].prop1);
        assertEquals(modelInstance1.prop1, res[2].prop1);

        assertEquals(modelInstance3.prop2, res[0].prop2);
        assertEquals(modelInstance2.prop2, res[1].prop2);
        assertEquals(modelInstance1.prop2, res[2].prop2);

        res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"], [sort: "id", max:1]);
        assertEquals(1, res.size());
        assertEquals(4, res[0].size())
        assertEquals(modelInstance1.id, res[0].id);

        res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"], [sort: "id", offset:1, max:1]);
        assertEquals(1, res.size());
        assertEquals(4, res[0].size())
        assertEquals(modelInstance2.id, res[0].id);
    }

    public void testGetPropertyValuesWithQueryOptions() {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        def modelInstance2 = modelClass.'add'(keyProp: "obj2", prop1: "prop1Value2", prop2: 2);
        def modelInstance3 = modelClass.'add'(keyProp: "obj3", prop1: "prop1Value3", prop2: 3);
        def modelInstance4 = modelClass.'add'(keyProp: "obj4", prop1: "prop1Value4", prop2: 4);
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());
        assertFalse(modelInstance4.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop2"], [sort: "prop1", order: "desc", max: 2, offset: 1]);
        assertEquals(2, res.size());

        assertEquals(modelClass.name, res[0].alias);
        assertEquals(modelClass.name, res[1].alias);
        assertEquals(modelInstance3.prop2, res[0].prop2);
        assertEquals(modelInstance2.prop2, res[1].prop2);
    }

    //search every method is used instead of search when max property is not given
    public void testGetPropertyValuesWithHitsGreaterThan10() {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp: "obj1", prop1: "prop1Value1", prop2: 1);
        def modelInstance2 = modelClass.'add'(keyProp: "obj2", prop1: "prop1Value2", prop2: 2);
        def modelInstance3 = modelClass.'add'(keyProp: "obj3", prop1: "prop1Value3", prop2: 3);
        def modelInstance4 = modelClass.'add'(keyProp: "obj4", prop1: "prop1Value4", prop2: 4);
        def modelInstance5 = modelClass.'add'(keyProp: "obj5", prop1: "prop1Value4", prop2: 5);
        def modelInstance6 = modelClass.'add'(keyProp: "obj6", prop1: "prop1Value4", prop2: 6);
        def modelInstance7 = modelClass.'add'(keyProp: "obj7", prop1: "prop1Value4", prop2: 7);
        def modelInstance8 = modelClass.'add'(keyProp: "obj8", prop1: "prop1Value4", prop2: 8);
        def modelInstance9 = modelClass.'add'(keyProp: "obj9", prop1: "prop1Value4", prop2: 9);
        def modelInstance10 = modelClass.'add'(keyProp: "obj10", prop1: "prop1Value4", prop2: 10);
        def modelInstance11 = modelClass.'add'(keyProp: "obj11", prop1: "prop1Value4", prop2: 11);
        assertFalse(modelInstance1.hasErrors());
        assertFalse(modelInstance2.hasErrors());
        assertFalse(modelInstance3.hasErrors());
        assertFalse(modelInstance4.hasErrors());
        assertFalse(modelInstance5.hasErrors());
        assertFalse(modelInstance6.hasErrors());
        assertFalse(modelInstance7.hasErrors());
        assertFalse(modelInstance8.hasErrors());
        assertFalse(modelInstance9.hasErrors());
        assertFalse(modelInstance10.hasErrors());
        assertFalse(modelInstance11.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop2"]);
        assertEquals(11, res.size());
    }
}