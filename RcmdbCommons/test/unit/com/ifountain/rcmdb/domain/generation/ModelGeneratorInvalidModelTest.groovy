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

    public void testThrowsExceptionIfInvalidStorageTypeSpecified()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        String modelName = "ChildModel";
        String invalidStorageType = "invalidStorageType"
        def modelXml = createModel (modelName, null, [], [prop1], [], [], invalidStorageType);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since storageType is not valid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidStorageType(modelName, invalidStorageType).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfOneOfRelatedClassesDoesnotExist()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]

        String modelName = "ChildModel";
        String modelName2 = "ParentModel";
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def modelXml = createModel (modelName, null, [prop1, prop2], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since related model does not exist");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.undefinedRelatedModel(modelName, rel1.name, modelName2).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfDuplicateRelationsDefined()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]

        String parentModelName = "ParentModel";
        String childModelName = "ChildModel";
        String relatedModelName = "RelatedModel";
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:relatedModelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:childModelName, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        def modelXml1 = createModel (parentModelName, null, [prop1], [prop1], [rel1]);
        def modelXml2 = createModel (childModelName, parentModelName, [], [], [rel1]);
        def modelXml3 = createModel (relatedModelName, null, [prop1], [prop1], [revrel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1, modelXml2, modelXml3])
            fail("Should throw exception since duplicate relations defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateRelation(childModelName, rel1.name).getMessage(), e.getMessage());
        }

        String subChildModelName = "SubChild"
        modelXml1 = createModel (parentModelName, null, [prop1], [prop1], [rel1]);
        modelXml2 = createModel (childModelName, parentModelName, [], [], []);
        modelXml3 = createModel (relatedModelName, null, [prop1], [prop1], [revrel1]);
        def modelXml4 = createModel (subChildModelName, childModelName, [], [], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1, modelXml2, modelXml3, modelXml4])
            fail("Should throw exception since duplicate relations defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateRelation(subChildModelName, rel1.name).getMessage(), e.getMessage());
        }


        modelXml1 = createModel (childModelName, null, [prop1], [prop1], [rel1, rel1]);
        modelXml2 = createModel (relatedModelName, null, [prop1], [prop1], [revrel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1, modelXml2])
            fail("Should throw exception since duplicate relations defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateRelation(childModelName, rel1.name).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfDuplicatePropertyDefined()
    {
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]

        String parentModelName = "ParentModel";
        String childModelName = "ChildModel";
        def modelXml1 = createModel (parentModelName, null, [prop1], [prop1], []);
        def modelXml2 = createModel (childModelName, parentModelName, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1, modelXml2])
            fail("Should throw exception since duplicate property defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateProperty(childModelName, prop1.name).getMessage(), e.getMessage());
        }

        String subChildModelName = "SubChild"
        modelXml1 = createModel (parentModelName, null, [prop1], [prop1], []);
        modelXml2 = createModel (childModelName, parentModelName, [], [], []);
        def modelXml3 = createModel (subChildModelName, childModelName, [prop1], [], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1, modelXml2, modelXml3])
            fail("Should throw exception since duplicate properties defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateProperty(subChildModelName, prop1.name).getMessage(), e.getMessage());
        }

//
        modelXml1 = createModel (childModelName, null, [prop1, prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml1])
            fail("Should throw exception since duplicate properties defined");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateProperty(childModelName, prop1.name).getMessage(), e.getMessage());
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
        def modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelName(modelName).getMessage(), e.getMessage());
        }

        modelName = "AABCD";
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelName(modelName).getMessage(), e.getMessage());
        }

        modelName = "A_bB90CCC";
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
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
        def modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model property name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelPropertyName(modelName, prop1.name).getMessage(), e.getMessage());
        }

        prop1 = [name:"aBBBCC", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model property  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelPropertyName(modelName, prop1.name).getMessage(), e.getMessage());
        }
        prop1 = [name:"_bBBBB", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }
        prop1 = [name:"b_BBBB99", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        prop1 = [name:"bbBBBB99", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        ModelGenerator.getInstance().invalidNames = ["invalidName"]

        prop1 = [name:"invalidName", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        modelXml = createModel (modelName, null, [prop1], [prop1], []);
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
        def modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        def modelXml2 = createModel (modelName2, null, [prop1], [prop1], [revrel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }
        rel1.name = "aBBBCC"
        modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }

        rel1.name = "_bBBBB"
        modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }
        rel1.name = "b_BBBB99"
        modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }

        rel1.name = "bbBBBB99"
        modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])

        }catch(ModelGenerationException e)
        {
            fail("Should not throw exception");
        }


        ModelGenerator.getInstance().invalidNames = ["invalidName"]

        rel1.name = "invalidName"
        modelXml = createModel (modelName, null, [prop1], [prop1], [rel1]);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml, modelXml2])
            fail("Should throw exception since  model relation  name is invalid");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.invalidModelRelationName(modelName, rel1.name).getMessage(), e.getMessage());
        }
    }


    public void testThrowsExceptionIfFederatedPropertyDatasourceIsNotDefined()
    {

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def ds1 = [name:"ds1",keyMappings:[[propertyName:prop1.name, nameInDatasource:prop1.name]]]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasource:ds1.name, lazy:false]
        String modelName = "Model1";
        def modelXml = createModel (modelName, null, [], [prop1, prop2], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model datasource doesnot exist");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.datasourceDoesnotExists(modelName, ds1.name, prop2.name).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfFederatedPropertyDatasourcePropertyIsNotDefined()
    {

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1", datasourceProperty:"prop3", lazy:false]
        String modelName = "Model1";
        def modelXml = createModel (modelName, null, [], [prop1, prop2], [prop1], []);
        try
        {
            ModelGenerator.getInstance().generateModels([modelXml])
            fail("Should throw exception since  model datasource property doesnot exist");
        }catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.datasourcePropertyDoesnotExists(modelName, "prop3", prop2.name).getMessage(), e.getMessage());
        }
    }
    def createModel(String name, String parentModel,  List modelProperties, List keyProperties, List relations)
    {
        return createModel(name, parentModel, [], modelProperties, keyProperties, relations);   
    }

    def createModel(String name, String parentModel, List modelDatasources,  List modelProperties, List keyProperties, List relations, String storageType = null)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        def modelProps = [name:name];
        if(parentModel)
        modelProps["parentModel"] = parentModel;
        if(storageType)
        modelProps["storageType"] = storageType;
        modelbuilder.Model(modelProps){
            modelbuilder.Datasources(){
                if(!keyProperties.isEmpty())
                {
                    modelbuilder.Datasource(name:"RCMDB"){
                        keyProperties.each{Map keyPropConfig->
                            modelbuilder.Key(propertyName:keyPropConfig.name)
                        }
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

            modelbuilder.Datasources(){
                boolean isIdAdded = false;
                boolean isVersionAdded = false;
                modelDatasources.each{Map dsConfig->
                    def keyMaps = dsConfig.remove("keyMappings");
                    modelbuilder.Datasource(dsConfig){
                        keyMaps.each{key->
                            modelbuilder.Key(key);
                        }
                    }
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