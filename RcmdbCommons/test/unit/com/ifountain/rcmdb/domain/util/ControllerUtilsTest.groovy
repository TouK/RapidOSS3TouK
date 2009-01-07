package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 7, 2009
* Time: 9:00:20 AM
* To change this template use File | Settings | File Templates.
*/
class ControllerUtilsTest extends RapidCmdbWithCompassTestCase{
    public void testGetClassProperties()
    {
        def modelName = "ChildModel";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.BOOLEAN_TYPE, blank:false, defaultValue:"false"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"stringDefault"];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def modelMetaProps = [name:modelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, [keyProp, prop1,prop2], keyPropList, [rel1])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, [keyProp], keyPropList, [revrel1])
        this.gcl.parseClass(modelString+relatedModelString);
        Class modelClass = this.gcl.loadClass(modelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([modelClass, relatedModelClass], [])

        //Test with true boolean and string
        def relatedModelInstance1 = modelClass.'add'(keyProp:"relatedModelInstance1");
        def params = [_prop1:"", prop1:"on", prop2:"stringValue"]
        def classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (true, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test with false boolean
        params = [_prop1:"", prop2:"stringValue"]
        classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test ignores id
        params = [_prop1:"", prop2:"stringValue", id:"100", _id:"100"]
        classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test with relation
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"${relatedModelInstance1.id}".toString()]
        classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertEquals (relatedModelInstance1.id, classProperties.rel1.id);

        //Test with null relation
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"null"]
        classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertNull(classProperties.rel1);

        //Test with undefined property
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"${relatedModelInstance1.id}".toString(), undefinedProp:"as"]
        classProperties = ControllerUtils.getClassProperties (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertEquals (relatedModelInstance1.id, classProperties.rel1.id);
    }

    public void testConvertToSuccessfulXML()
    {
        String message = "message1";
        def xml = ControllerUtils.convertSuccessToXml(message);
        def xmlNode = new XmlParser().parseText(xml);
        assertEquals(message, xmlNode.text());
        assertEquals("Successful", xmlNode.name());
    }
}