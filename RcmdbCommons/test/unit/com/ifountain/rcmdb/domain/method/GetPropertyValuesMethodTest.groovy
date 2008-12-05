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
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.domain.generation.ModelGenerator

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 14, 2008
 * Time: 9:48:15 AM
 * To change this template use File | Settings | File Templates.
 */
class GetPropertyValuesMethodTest extends RapidCmdbWithCompassTestCase{

    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    def initializeCompassWithSimpleObject()
    {
        def modelName = "Model";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop2 = [name:"prop2", type:ModelGenerator.NUMBER_TYPE, blank:false];
        def modelMetaProps = [name:modelName]
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
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp:"obj1", prop1:"prop1Value1", prop2:1);
        def modelInstance2 = modelClass.'add'(keyProp:"obj2", prop1:"prop1Value1", prop2:2);
        def modelInstance3 = modelClass.'add'(keyProp:"obj3", prop1:"prop1Value2", prop2:1);
        assertFalse (modelInstance1.hasErrors());
        assertFalse (modelInstance2.hasErrors());
        assertFalse (modelInstance3.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"]);
        assertEquals (3, res.size());
        assertEquals (4, res[0].size())
        assertEquals (4, res[1].size())
        assertEquals (4, res[2].size())
        assertEquals (modelClass.name, res[0].alias);
        assertEquals (modelClass.name, res[1].alias);
        assertEquals (modelClass.name, res[2].alias);

        assertEquals (modelInstance1.id, res[0].id);
        assertEquals (modelInstance2.id, res[1].id);
        assertEquals (modelInstance3.id, res[2].id);

        assertEquals (modelInstance1.prop1, res[0].prop1);
        assertEquals (modelInstance2.prop1, res[1].prop1);
        assertEquals (modelInstance3.prop1, res[2].prop1);

        assertEquals (modelInstance1.prop2, res[0].prop2);
        assertEquals (modelInstance2.prop2, res[1].prop2);
        assertEquals (modelInstance3.prop2, res[2].prop2);

        res = modelClass.'getPropertyValues'("prop2:1", ["prop1"]);

        assertEquals (2, res.size());
        assertEquals (3, res[0].size())
        assertEquals (3, res[1].size())
        assertEquals (modelClass.name, res[0].alias);
        assertEquals (modelClass.name, res[1].alias);

        assertEquals (modelInstance1.id, res[0].id);
        assertEquals (modelInstance3.id, res[1].id);

        assertEquals (modelInstance1.prop1, res[0].prop1);
        assertEquals (modelInstance3.prop1, res[1].prop1);
    }

    public void testGetPropertyValuesWithUndefinedProperties()
    {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp:"obj1", prop1:"prop1Value1", prop2:1);
        assertFalse (modelInstance1.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "undefined"]);
        assertEquals (1, res.size());
        assertEquals (3, res[0].size())
        assertEquals (modelClass.name, res[0].alias);
        assertEquals (modelInstance1.id, res[0].id);
        assertEquals (modelInstance1.prop1, res[0].prop1);
    }

    public void testGetPropertyValuesWithModelHasParent()
    {
        def childModelName1 = "ChildModel1";
        def childModelName2 = "ChildModel2";
        def parentModelName = "ParentModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop2 = [name:"prop2", type:ModelGenerator.NUMBER_TYPE, blank:false];
        def childModel1MetaProps = [name:childModelName1, parentModel:parentModelName]
        def childModel2MetaProps = [name:childModelName2, parentModel:parentModelName]
        def parentModelMetaProps = [name:parentModelName]
        def modelProps = [keyProp, prop1, prop2];
        def keyPropList = [keyProp];
        String childModel1String = ModelGenerationTestUtils.getModelText(childModel1MetaProps, [prop1, prop2], keyPropList, [])
        String childModel2String = ModelGenerationTestUtils.getModelText(childModel2MetaProps, [prop1], keyPropList, [])
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelMetaProps, [keyProp], keyPropList, [])

        this.gcl.parseClass(childModel1String+childModel2String+parentModelString);
        Class childModel1Class = this.gcl.loadClass(childModelName1);
        Class childModel2Class = this.gcl.loadClass(childModelName2);
        Class parentModelClass = this.gcl.loadClass(parentModelName);
        initialize([childModel1Class, childModel2Class, parentModelClass], [])

        def childModel1Instance = childModel1Class.'add'(keyProp:"obj1", prop1:"prop1Value1", prop2:1);
        def childModel2Instance = childModel2Class.'add'(keyProp:"obj2", prop1:"prop1Value1");
        assertFalse (childModel1Instance.hasErrors());
        assertFalse (childModel2Instance.hasErrors());

        def res = parentModelClass.'getPropertyValues'("alias:*", ["prop1", "prop2", "undefined"]);

        assertEquals (2, res.size());
        assertEquals (4, res[0].size())
        assertEquals (3, res[1].size())
        assertEquals (childModel1Class.name, res[0].alias);
        assertEquals (childModel2Class.name, res[1].alias);

        assertEquals (childModel1Instance.id, res[0].id);
        assertEquals (childModel2Instance.id, res[1].id);

        assertEquals (childModel1Instance.prop1, res[0].prop1);
        assertEquals (childModel2Instance.prop1, res[1].prop1);

        assertEquals (childModel1Instance.prop2, res[0].prop2);
    }

    public void testGetPropertyValuesWithModelWithOptions()
    {
        def modelClass = initializeCompassWithSimpleObject();
        def modelInstance1 = modelClass.'add'(keyProp:"obj1", prop1:"prop1Value1", prop2:1);
        def modelInstance2 = modelClass.'add'(keyProp:"obj2", prop1:"prop1Value1", prop2:2);
        def modelInstance3 = modelClass.'add'(keyProp:"obj3", prop1:"prop1Value2", prop2:3);
        assertFalse (modelInstance1.hasErrors());
        assertFalse (modelInstance2.hasErrors());
        assertFalse (modelInstance3.hasErrors());

        def res = modelClass.'getPropertyValues'("alias:*", ["prop1", "prop2"], [sort:"prop2", order:"desc"]);
        assertEquals (3, res.size());
        assertEquals (4, res[0].size())
        assertEquals (4, res[1].size())
        assertEquals (4, res[2].size())
        assertEquals (modelClass.name, res[0].alias);
        assertEquals (modelClass.name, res[1].alias);
        assertEquals (modelClass.name, res[2].alias);

        assertEquals (modelInstance3.id, res[0].id);
        assertEquals (modelInstance2.id, res[1].id);
        assertEquals (modelInstance1.id, res[2].id);

        assertEquals (modelInstance3.prop1, res[0].prop1);
        assertEquals (modelInstance2.prop1, res[1].prop1);
        assertEquals (modelInstance1.prop1, res[2].prop1);

        assertEquals (modelInstance3.prop2, res[0].prop2);
        assertEquals (modelInstance2.prop2, res[1].prop2);
        assertEquals (modelInstance1.prop2, res[2].prop2);
    }

     public void testGetPropertyValuesWithRelations()
    {
        fail("Will be implemented later");
        def parentModelName = "ParentModel";
        def model1Name = "Model1";
        def model2Name = "Model2";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false];
        def model1MetaProps = [name:model1Name, parentModel:parentModelName]
        def model2MetaProps = [name:model2Name, parentModel:parentModelName]
        def parentModelProps = [name:parentModelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def modelProps = [keyProp, prop1];
        def keyPropList = [keyProp];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:model1Name, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        String model1String = ModelGenerationTestUtils.getModelText(model1MetaProps, [prop1], keyPropList, [rel1])
        String model2String = ModelGenerationTestUtils.getModelText(model2MetaProps, [prop1], keyPropList, [])
        String parentModelString = ModelGenerationTestUtils.getModelText(parentModelProps, [prop1], keyPropList, [])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, [prop1], keyPropList, [revrel1])

        this.gcl.parseClass(model1String+model2String+relatedModelString+parentModelString);
        Class model1Class = this.gcl.loadClass(model1Name);
        Class model2Class = this.gcl.loadClass(model2Name);
        Class parentClass = this.gcl.loadClass(parentModelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([model1Class, model2Class, parentClass, relatedModelClass], [])
        def model1Instance1 = model1Class.'add'(keyProp:"obj1", prop1:"prop1Value1");
        def model1Instance2 = model1Class.'add'(keyProp:"obj1", prop1:"prop1Value1");
        def model2Instance1 = model2Class.'add'(keyProp:"obj2", prop1:"prop1Value2");
        def relatedModelInstance = relatedModelClass.'add'(keyProp:"obj3", prop1:"prop1Value1", revrel1:[model1Instance1]);
        assertFalse (model1Instance1.hasErrors());
        assertFalse (model1Instance2.hasErrors());
        assertFalse (model2Instance1.hasErrors());
        assertFalse (relatedModelInstance.hasErrors());

        def res = parentClass.'getPropertyValues'("alias:*", ["prop1", "undefined", "rel1"]);
        assertEquals (3, res.size());
        assertEquals (4, res[0].size())
        assertEquals (4, res[0].size())
        assertEquals (3, res[1].size())
        assertEquals (model1Class.name, res[0].alias);
        assertEquals (model1Class.name, res[0].alias);
        assertEquals (model2Class.name, res[1].alias);

        assertEquals (model1Instance1.id, res[0].id);
        assertEquals (model1Instance2.id, res[1].id);
        assertEquals (model2Instance1.id, res[2].id);

        assertEquals (model1Instance1.prop1, res[0].prop1);
        assertEquals (model1Instance2.prop1, res[1].prop1);
        assertEquals (model2Instance1.prop1, res[2].prop1);

        assertEquals (1, res[0].rel1.size());
        assertEquals (relatedModelInstance.id, res[0].rel1[0].id);
        assertEquals (0, res[1].rel1.size());

    }

}