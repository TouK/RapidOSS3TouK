package com.ifountain.rcmdb.domain.util
//
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.test.util.ModelGenerationTestUtils
import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import org.springframework.mock.web.MockHttpServletResponse

//
///**
//* Created by IntelliJ IDEA.
//* User: admin
//* Date: Jan 7, 2009
//* Time: 9:00:20 AM
//* To change this template use File | Settings | File Templates.
//*/
class ControllerUtilsTest extends RapidCmdbWithCompassTestCase
{

    def controllerUtilsClass;
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGetClassProperties()
    {
        def modelName = "ChildModel";
        def relatedModelName = "RelatedModel";
        def keyProp = [name:"keyProp", type:ModelGenerator.STRING_TYPE, blank:false];
        def prop1 = [name:"prop1", type:ModelGenerator.BOOLEAN_TYPE, blank:false, defaultValue:"false"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"stringDefault"];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def rel2 = [name:"rel2",  reverseName:"revrel2", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def revrel2 = [name:"revrel2",  reverseName:"rel2", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];

        def modelMetaProps = [name:modelName]
        def relatedModelMetaProps = [name:relatedModelName]
        def keyPropList = [keyProp];
        String modelString = ModelGenerationTestUtils.getModelText(modelMetaProps, [keyProp, prop1,prop2], keyPropList, [rel1, rel2])
        String relatedModelString = ModelGenerationTestUtils.getModelText(relatedModelMetaProps, [keyProp], keyPropList, [revrel1, revrel2])
        this.gcl.parseClass(modelString+relatedModelString);
        Class modelClass = this.gcl.loadClass(modelName);
        Class relatedModelClass = this.gcl.loadClass(relatedModelName);
        initialize([modelClass, relatedModelClass], [])
        controllerUtilsClass = ApplicationHolder.application.classLoader.loadClass("com.ifountain.rcmdb.domain.util.ControllerUtils");

        //Test with true boolean and string
        def relatedModelInstance1 = relatedModelClass.'add'(keyProp:"relatedModelInstance1");
        def relatedModelInstance2 = relatedModelClass.'add'(keyProp:"relatedModelInstance2");
        assertFalse (relatedModelInstance1.hasErrors());
        assertFalse (relatedModelInstance2.hasErrors());
        def params = [_prop1:"", prop1:"on", prop2:"stringValue"]
        def classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (true, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test with false boolean
        params = [_prop1:"", prop2:"stringValue"]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test ignores id
        params = [_prop1:"", prop2:"stringValue", id:"100", _id:"100"]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);

        //Test with relation
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"${relatedModelInstance1.id}".toString()]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertEquals (relatedModelInstance1.id, classProperties.rel1.id);
        assertEquals ("relatedModelInstance1", classProperties.rel1.keyProp);

        //Test with undefined relation
        params = [_prop1:"", prop2:"stringValue", "rel3.id":"${relatedModelInstance1.id}".toString()]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(2, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertFalse (classProperties.containsKey("rel3"));
        assertFalse (classProperties.containsKey("rel3.id"));

        //Test with multiple relations
        params = [_prop1:"", prop2:"stringValue", "rel2.id":"${relatedModelInstance1.id},${relatedModelInstance2.id}".toString(), rel2:[id:"${relatedModelInstance1.id},${relatedModelInstance2.id}".toString()]]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        def relations = classProperties.rel2.sort{it.keyProp};
        assertEquals (2, relations.size());
        assertEquals (relatedModelInstance1.keyProp, relations[0].keyProp);
        assertEquals (relatedModelInstance2.keyProp, relations[1].keyProp);

        //Test with many type relations and one instance
        params = [_prop1:"", prop2:"stringValue", "rel2.id":"${relatedModelInstance1.id}".toString(), rel2:[id:"${relatedModelInstance1.id}".toString()]]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertEquals (1, classProperties.rel2.size());
        assertEquals (relatedModelInstance1.keyProp, classProperties.rel2[0].keyProp);

        //Test with null relation
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"null"]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
        assertEquals(3, classProperties.size());
        assertEquals (false, classProperties.prop1);
        assertEquals (params.prop2, classProperties.prop2);
        assertNull(classProperties.rel1);

        //Test with undefined property
        params = [_prop1:"", prop2:"stringValue", "rel1.id":"${relatedModelInstance1.id}".toString(), undefinedProp:"as"]
        classProperties = controllerUtilsClass.'getClassProperties' (params, modelClass);
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
    public void testConvertErrorToXml()
    {
        String message = "message1";
        def xml = ControllerUtils.convertErrorToXml(message);
        println xml;
        def xmlNode = new XmlParser().parseText(xml);
        assertEquals("Errors", xmlNode.name());
        println xmlNode.childNodes;
        def errorNode=xmlNode.Error[0];
        assertEquals(message, errorNode.@error);
        assertEquals("Error", errorNode.name());
    }

    public void testGetWebResponse()
    {
        //test with no execution context
        assertNull(ControllerUtils.getWebResponse());
                
        ExecutionContextManagerUtils.executeInContext ([:])
        {
            //test with execution context but with no response added
            assertNull(ControllerUtils.getWebResponse());

            //test with execution context and response added
            def mockWebResponse=new MockHttpServletResponse();
            ExecutionContextManagerUtils.addWebResponseToCurrentContext (mockWebResponse);
            assertSame(mockWebResponse,ControllerUtils.getWebResponse());

        }
    }
}