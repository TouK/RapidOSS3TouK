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

}