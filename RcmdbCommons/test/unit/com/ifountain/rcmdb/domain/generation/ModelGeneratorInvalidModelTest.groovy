package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import groovy.xml.MarkupBuilder

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 10, 2008
 * Time: 1:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelGeneratorInvalidModelTest extends RapidCmdbTestCase{
     def static base_directory = "../testoutput/";   
     protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        if(new File(".").getCanonicalPath().endsWith("RapidModules"))
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "RcmdbCommons");
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, ".");
        }
         ModelGenerator.getInstance().invalidNames = [];
    }
    
    public void testIfParentModelDoesNotExist()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "ChildModel";
        String parentModelName = "ParentModel";
        def modelXml = createModel (modelName, parentModelName, [prop1, prop2], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since parent model does not exist");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.undefinedParentModel(modelName, parentModelName).getMessage(), e.getMessage());   
        }
    }

    public void testThrowsExceptionIfOneOfRelatedClassesDoesnotExist()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]

        String modelName = "ChildModel";
        String modelName2 = "ParentModel";
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def modelXml = createModel (modelName, null, [prop1, prop2], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since related model does not exist");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.undefinedRelatedModel(modelName, rel1.name, modelName2).getMessage(), e.getMessage());
        }
    }


    public void testThrowsExceptionIfModelNameIsInInvalidList()
    {
        ModelGenerator.getInstance().invalidNames = ["InvalidName"]
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "InvalidName";
        def modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelName(modelName).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfModelNameIsInInvalid()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "aBCD";
        def modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelName(modelName).getMessage(), e.getMessage());
        }

        modelName = "AABCD";
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelName(modelName).getMessage(), e.getMessage());
        }

        modelName = "A_bB90CCC";
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }
    }

    public void testThrowsExceptionIfModelPropertyNameIsInInvalid()
    {
        def prop1 = [name:"Abaassadas", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "Model1";
        def modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model property name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelPropertyName(modelName, prop1.name).getMessage(), e.getMessage());
        }

        prop1 = [name:"aBBBCC", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model property  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelPropertyName(modelName, prop1.name).getMessage(), e.getMessage());
        }
        prop1 = [name:"_bBBBB", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }
        prop1 = [name:"b_BBBB99", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        prop1 = [name:"bbBBBB99", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        ModelGenerator.getInstance().invalidNames = ["invalidName"]

        prop1 = [name:"invalidName", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model property  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelPropertyName(modelName, prop1.name).getMessage(), e.getMessage());
        }
    }


    public void testThrowsExceptionIfModelRelationNameIsInInvalid()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "Model1";
        String modelName2 = "Model2";
        def rel1 = [name:"Abaassadas",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        def modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        def modelXml2 = createModel (modelName2, null, [prop1], [], [revrel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }
        rel1.name = "aBBBCC"
        modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }

        rel1.name = "_bBBBB"
        modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }
        rel1.name = "b_BBBB99"
        modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        rel1.name = "bbBBBB99"
        modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }


        ModelGenerator.getInstance().invalidNames = ["invalidName"]

        rel1.name = "invalidName"
        modelXml = createModel (modelName, null, [prop1], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }
    }


    def createModel(String name, String parentModel, List modelProperties, List keyProperties, List relations)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        def modelProps = [name:name];
        if(parentModel)
        modelProps["parentModel"] = parentModel;
        modelbuilder.Model(modelProps){
            modelbuilder.Datasources(){
                modelbuilder.Datasource(name:"RCMDB"){
                    keyProperties.each{Map keyPropConfig->
                        modelbuilder.Key(propertyName:keyPropConfig.name)
                    }
                }
            }

            modelbuilder.Properties(){
                boolean isIdAdded = false;
                boolean isVersionAdded = false;
                modelProperties.each{Map propConfig->
                    modelbuilder.Property(propConfig)
                }
            }

            modelbuilder.Relations(){
                relations.each{
                    modelbuilder.Relation(it);
                }
            }

        }
        return model.toString();
    }

}