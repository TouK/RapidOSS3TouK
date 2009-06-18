package com.ifountain.rcmdb.domain.generation

import com.ifountain.compass.CompositeDirectoryWrapperProvider
import com.ifountain.rcmdb.converter.DateConverter
import com.ifountain.rcmdb.converter.RapidConvertUtils
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.util.RapidCMDBConstants
import model.*
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import org.springframework.validation.Errors

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 24, 2008
 * Time: 4:28:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelGeneratorTest extends RapidCmdbTestCase{
    def static base_directory = "../testoutput/";
    protected void setUp()
    {
        super.setUp();
        if(ApplicationHolder.application == null)
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "RcmdbCommons");
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, ".");
            System.setProperty("basedir", "RapidCMDBModeler")
        }
        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    private def addMasterDatasource(Model model)
    {
        def datasource1 = new DatasourceName(name:RapidCMDBConstants.RCMDB);
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model);
        model.datasources += modelDatasource1;
        def keyProp = new ModelProperty(name:"keyprop", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false);
        model.modelProperties += keyProp;
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp, datasource:modelDatasource1, nameInDatasource:"keypropname");
    }
    
    public void testGenerateModel()
    {
        def model = new MockModel(name:"Class1", storageType:CompositeDirectoryWrapperProvider.RAM_DIR_TYPE);
        addMasterDatasource(model);

        ModelGeneratorAdapter.generateModels([model]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model.name).exists());

        Class cls = compileClass(model.name);
        def object = cls.newInstance();
        object.keyprop = "keypropvalue";
        assertNull(object.id);
        assertNull(object.version);
        checkExistanceOfMetaDataProperties(object);
        assertNull (object.errors);
        assertEquals (Errors.class, object.metaClass.getMetaProperty("errors").type);
        assertNull (object[com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME]);
        assertNull (object[com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED]);
        assertEquals (Object.class, object.metaClass.getMetaProperty(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME).type);
        assertEquals ("Class1[keyprop:keypropvalue]", object.toString());
        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals(3, closureGetter.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("errors"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));
        assertEquals(CompositeDirectoryWrapperProvider.RAM_DIR_TYPE, closureGetter.propertiesSetByClosure["storageType"]);

        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(ModelGenerator.getInstance().getGeneratedModelFile(model.name).getText ().indexOf("import $it") >= 0);
        }
    }

    public void testGenerateModelExtendingAnotherModel()
    {
        def parentModel = new MockModel(name:"Class2");
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);
        addMasterDatasource(parentModel);
        childModel.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.numberType, model:childModel,blank:false,defaultValue:"1");

        ModelGeneratorAdapter.generateModels([childModel, parentModel]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(childModel.name).exists());
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(parentModel.name).exists());


        Class childModelClass = compileClass(childModel.name);
        def childModelInstance = childModelClass.newInstance();
        checkExistanceOfMetaDataProperties(childModelInstance);
        childModelInstance.keyprop = "keyPropValue"
        assertEquals ("Class1[keyprop:keyPropValue]", childModelInstance.toString());

        Class parentModelClass = compileClass(parentModel.name);
        def parentModelInstance = parentModelClass.newInstance();
        checkExistanceOfMetaDataProperties(parentModelInstance);
    }

    public void testGenerateModelExtendingAnotherModelWithIndexProperty()
    {
        def parentModel = new MockModel(name:"Class2", indexName:"index1");
        def childModel = new MockModel(name:"Class1", parentModel:parentModel, indexName:"index2");
        addMasterDatasource(parentModel);
        childModel.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.numberType, model:childModel,blank:false,defaultValue:"1");

        ModelGeneratorAdapter.generateModels([childModel, parentModel]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(childModel.name).exists());
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(parentModel.name).exists());


        Class childModelClass = compileClass(childModel.name);
        def childModelInstance = childModelClass.newInstance();
        Class parentModelClass = compileClass(parentModel.name);
        def parentModelInstance = parentModelClass.newInstance();

        Closure searchable = childModelInstance.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals("index2", closureGetter.propertiesSetByClosure["subIndex"]);

        searchable = parentModelInstance.searchable;
        closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals("index1", closureGetter.propertiesSetByClosure["subIndex"]);
    }

    public void testWithSomeProperties()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        def prevDateConf = RapidConvertUtils.getInstance().lookup (Date);
        try
        {
            String dateFormatString = "yyyy-dd-MM HH:mm:ss.SSS";
            def converter = new DateConverter(dateFormatString);
            RapidConvertUtils.getInstance().register (converter, Date.class)
            def model = new MockModel(name:"Class1");
            def datasource1 = new DatasourceName(name:"RCMDB");
            def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model);
            model.datasources += modelDatasource1;

            def date = System.currentTimeMillis();
            def keyProp1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"prop1 default value");
            def keyProp2 = new ModelProperty(name:"prop5", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:"prop5 default value");
            model.modelProperties += keyProp1;
            model.modelProperties += keyProp2;
            model.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"1");
            model.modelProperties += new ModelProperty(name:"prop3", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:converter.formater.format(new Date(date)));
            model.modelProperties += new ModelProperty(name:"prop4", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model);
            model.modelProperties += new ModelProperty(name:"prop6", type:ModelProperty.floatType, propertyDatasource:modelDatasource1, model:model, blank:false,defaultValue:"1.0");
            model.modelProperties += new ModelProperty(name:"prop7", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model);
            model.modelProperties += new ModelProperty(name:"prop8", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model);
            model.modelProperties += new ModelProperty(name:"prop9", type:ModelProperty.floatType, propertyDatasource:modelDatasource1, model:model);
            model.modelProperties += new ModelProperty(name:"prop10", type:ModelProperty.booleanType, propertyDatasource:modelDatasource1, model:model);
            model.modelProperties += new ModelProperty(name:"prop11", type:ModelProperty.booleanType, propertyDatasource:modelDatasource1, model:model, defaultValue:"True");

            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp1, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");
            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp2, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");

            ModelGeneratorAdapter.generateModels([model]);
            assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model.name).exists());
            Class cls = compileClass(model.name);
            def object = cls.newInstance();
            assertTrue(object.relations instanceof Map);
            assertTrue(object.relations.isEmpty());
            assertTrue(object.transients instanceof List);

            assertEquals(String, object.class.getDeclaredField("prop1").type);
            assertEquals(Long, object.class.getDeclaredField("prop2").type);
            assertEquals(Date, object.class.getDeclaredField("prop3").type);
            assertEquals(Date, object.class.getDeclaredField("prop4").type);
            assertEquals(String, object.class.getDeclaredField("prop5").type);
            assertEquals(Double, object.class.getDeclaredField("prop6").type);
            assertEquals(String, object.class.getDeclaredField("prop7").type);
            assertEquals(Long, object.class.getDeclaredField("prop8").type);
            assertEquals(Double, object.class.getDeclaredField("prop9").type);
            assertEquals(Boolean, object.class.getDeclaredField("prop10").type);
            assertEquals(Boolean, object.class.getDeclaredField("prop11").type);

            Closure searchable = object.searchable;
            ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
            searchable.setDelegate (closureGetter);
            searchable.call();
            assertNull(closureGetter.propertiesSetByClosure["subIndex"]);
            assertEquals(3, closureGetter.propertiesSetByClosure["except"].size());
            assertTrue(closureGetter.propertiesSetByClosure["except"].contains("errors"));
            assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
            assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));
            assertEquals(3, object.transients.size());
            assertTrue(object.transients.contains("errors"));
            assertTrue(object.transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
            assertTrue(object.transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));

            assertEquals("prop1 default value", object.prop1);
            assertEquals(1, object.prop2);
            assertEquals((int)1.0, (int)object.prop6);
            assertEquals(1, object.prop2);
            assertEquals(new Date(date), object.prop3);
            assertEquals(new Date(0), object.prop4);
            assertEquals("", object.prop7);
            assertEquals(new Long(0), object.prop8);
            assertEquals(new Double(0), object.prop9);
            assertEquals(new Boolean(false), object.prop10);
            assertEquals(new Boolean(true), object.prop11);

            Closure contraintsClosure = object.constraints;
            ConstrainedPropertyBuilder contraintsClosurePropertyBuilder = new ConstrainedPropertyBuilder(object);
            contraintsClosure.setDelegate (contraintsClosurePropertyBuilder);
            contraintsClosure.call();
            ConstrainedProperty prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop1"];
            assertFalse (prop.isNullable());
            assertFalse (prop.isBlank());
            KeyConstraint  keyConstraint = prop.getAppliedConstraint("key");
            if(keyConstraint)
            {
                assertTrue (keyConstraint.getKeys().contains("prop5"));
            }
            
            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop2"];
            assertTrue (prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));
            
            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop3"];
            assertTrue (prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop4"];
            assertTrue(prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop5"];
            assertFalse (prop.isNullable());
            assertFalse (prop.isBlank());
            if(!keyConstraint)
            {
                keyConstraint = prop.getAppliedConstraint("key");
                assertTrue (keyConstraint.getKeys().contains("prop1"));
            }


            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop6"];
            assertTrue (prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop7"];
            assertTrue (prop.isNullable());
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop8"];
            assertTrue (prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop9"];
            assertTrue (prop.isNullable());
            assertNull(prop.getAppliedConstraint(ConstrainedProperty.BLANK_CONSTRAINT));
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop10"];
            assertTrue (prop.isNullable());
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop11"];
            assertTrue (prop.isNullable());
            assertNull ( prop.getAppliedConstraint("key"));


        }finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
        }

    }

    public void testWithSomeFederatedAndNormalProperties()
    {

        def model = new MockModel(name:"Class1");
        def datasource1 = new DatasourceName(name:"ds1-sample");
        def datasource2 = new DatasourceName(name:RapidCMDBConstants.RCMDB);
        def datasource3 = new DatasourceName(name:"ds3-sample", mappedName:"ds3-sample-mapped-name");
        def datasource4 = new DatasourceName(name:"ds4-sample", mappedNameProperty:"prop1");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model);
        def modelDatasource2 = new MockModelDatasource(datasource:datasource2, model:model);
        def modelDatasource3 = new MockModelDatasource(datasource:datasource3, model:model);
        def modelDatasource4 = new MockModelDatasource(datasource:datasource4, model:model);

        model.datasources += modelDatasource1;
        model.datasources += modelDatasource2;
        model.datasources += modelDatasource3;
        model.datasources += modelDatasource4;
        def prop0 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model, blank:true);
        def prop1 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, model:model);
        def prop2 = new ModelProperty(name:"dsname", type:ModelProperty.stringType, model:model);
        model.modelProperties += prop0;
        model.modelProperties += prop1;
        model.modelProperties += prop2;
        model.modelProperties += new ModelProperty(name:"prop3", type:ModelProperty.numberType, model:model, propertyDatasource:modelDatasource1, nameInDatasource:"Prop3NameInDs", lazy:false, blank:true);
        model.modelProperties += new ModelProperty(name:"prop4", type:ModelProperty.dateType, model:model, propertySpecifyingDatasource:prop2);
        model.modelProperties += new ModelProperty(name:"prop5", type:ModelProperty.dateType, model:model, propertyDatasource:modelDatasource3);
        model.modelProperties += new ModelProperty(name:"prop6", type:ModelProperty.dateType, model:model, propertyDatasource:modelDatasource4);

        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource1);
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource2, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource2);
        modelDatasource3.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource3, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource3.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource3);
        modelDatasource4.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource4, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource4.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource4);



        ModelGeneratorAdapter.generateModels([model]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model.name).exists());
        Class cls = compileClass(model.name);
        def object = cls.newInstance();
        assertEquals("", object.prop1);
        assertEquals(object.class.getDeclaredField("prop1").getType(), String.class);
        assertEquals("", object.prop2);
        assertEquals(object.class.getDeclaredField("prop2").getType(), String.class);
        assertEquals(new Long(0), object.prop3);
        assertEquals(object.class.getDeclaredField("prop3").getType(), Long.class);
        assertEquals(new Date(0), object.prop4);
        assertEquals(object.class.getDeclaredField("prop4").getType(), Date.class);
        assertEquals(new Date(0), object.prop5);
        assertEquals(object.class.getDeclaredField("prop5").getType(), Date.class);
        assertEquals(new Date(0), object.prop6);
        assertEquals(object.class.getDeclaredField("prop6").getType(), Date.class);
        assertEquals("", object.dsname);
        assertNotNull (object.constraints);

        object.prop1 = "prop1Value";
        object.prop2 = "prop2Value";
        assertEquals ("Class1[prop1:prop1Value, prop2:prop2Value]", object.toString());

        def dsDefinition1 = object.datasources[modelDatasource1.datasource.name];
        assertEquals("Prop1KeyNameInDs", dsDefinition1.keys["prop1"].nameInDs);
        assertEquals("prop2", dsDefinition1.keys["prop2"].nameInDs);

        def dsDefinition2 = object.datasources[modelDatasource2.datasource.name];
        assertEquals("Prop1KeyNameInDs", dsDefinition2.keys["prop1"].nameInDs);
        assertEquals("prop2", dsDefinition2.keys["prop2"].nameInDs);

        def transients = object.transients;
        assertEquals (7, transients.size());
        assertTrue(transients.contains("prop3"));
        assertTrue(transients.contains("prop4"));
        assertTrue(transients.contains("prop5"));
        assertTrue(transients.contains("prop6"));
        assertTrue(transients.contains("errors"));
        assertTrue(transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
        assertTrue(transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));

        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals(7, closureGetter.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop3"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop4"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop5"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop6"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("errors"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));

        def propertyConfiguration = object.propertyConfiguration;

        assertEquals("Prop3NameInDs", propertyConfiguration["prop3"].nameInDs);
        assertEquals(modelDatasource1.datasource.name, propertyConfiguration["prop3"].datasource);
        assertNull(propertyConfiguration["prop3"].datasourceProperty);
        assertFalse(propertyConfiguration["prop3"].lazy);

        assertEquals("prop4", propertyConfiguration["prop4"].nameInDs);
        assertEquals("dsname", propertyConfiguration["prop4"].datasourceProperty);
        assertNull(propertyConfiguration["prop4"].datasource);
        assertTrue(propertyConfiguration["prop4"].lazy);

        assertEquals("prop5", propertyConfiguration["prop5"].nameInDs);
        assertEquals(modelDatasource3.datasource.name, propertyConfiguration["prop5"].datasource);
        assertTrue(propertyConfiguration["prop5"].lazy);

        assertEquals("prop6", propertyConfiguration["prop6"].nameInDs);
        assertEquals(modelDatasource4.datasource.name, propertyConfiguration["prop6"].datasource);
        assertTrue(propertyConfiguration["prop6"].lazy);

        assertEquals(modelDatasource1.datasource.name, object.datasources[modelDatasource1.datasource.name].mappedName);
        assertNull(object.datasources[modelDatasource1.datasource.name].mappedNameProperty);
        assertEquals(modelDatasource2.datasource.name, object.datasources[modelDatasource2.datasource.name].mappedName);
        assertNull(object.datasources[modelDatasource2.datasource.name].mappedNameProperty);
        assertEquals(modelDatasource3.datasource.mappedName, object.datasources[modelDatasource3.datasource.name].mappedName);
        assertNull(object.datasources[modelDatasource3.datasource.name].mappedNameProperty);
        assertEquals(modelDatasource4.datasource.mappedNameProperty, object.datasources[modelDatasource4.datasource.name].mappedNameProperty);
        assertNull(object.datasources[modelDatasource4.datasource.name].mappedName);
    }

    public void testGenerateModelWithRelation()
    {
        def model1 = new MockModel(name:"Class1");
        addMasterDatasource(model1);

        model1.modelProperties += new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model1);
        def model2 = new MockModel(name:"Class2");
        addMasterDatasource(model2);

        model2.modelProperties += new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model2);
        ModelRelation relation1 = new ModelRelation(firstName:"relation1", secondName:"reverseRelation1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE, firstModel:model1, secondModel:model2);
        ModelRelation relation2 = new ModelRelation(firstName:"relation2", secondName:"reverseRelation2", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.MANY, firstModel:model2, secondModel:model1);
        ModelRelation relation3 = new ModelRelation(firstName:"relation3", secondName:"reverseRelation3", firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.MANY, firstModel:model1, secondModel:model2);
        ModelRelation relation4 = new ModelRelation(firstName:"relation4", secondName:"reverseRelation4", firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.ONE, firstModel:model1, secondModel:model2);

        model1.fromRelations += relation1;
        model2.toRelations += relation1;

        model2.fromRelations += relation2;
        model1.toRelations += relation2;

        model1.fromRelations += relation3;
        model2.toRelations += relation3;

        model1.fromRelations += relation4;
        model2.toRelations += relation4;



        ModelGeneratorAdapter.generateModels([model1, model2]);

        Class cls = compileClass(model1.name);
        def object = cls.newInstance();
        assertEquals (0, object.relation3.size());
        assertEquals(model2.getName(), object.class.getDeclaredField("relation1").getType().getName());
        assertEquals(model2.getName(), object.class.getDeclaredField("reverseRelation2").getType().getName())
        assertEquals(model2.getName(), object.relations.relation3.type.name)
        assertEquals(model2.getName(), object.class.getDeclaredField("relation4").getType().getName())

        assertFalse (object.relations.relation1.isMany);
        assertFalse (object.relations.reverseRelation2.isMany);
        assertTrue (object.relations.relation3.isMany);
        assertFalse (object.relations.relation4.isMany);

        assertEquals (model2.name, object.relations.relation1.type.name);
        assertEquals (model2.name, object.relations.reverseRelation2.type.name);
        assertEquals (model2.name, object.relations.relation3.type.name);
        assertEquals (model2.name, object.relations.relation4.type.name);
        
        assertEquals("reverseRelation1", object.relations.relation1.reverseName)
        assertEquals("relation2", object.relations.reverseRelation2.reverseName)
        assertEquals("reverseRelation3", object.relations.relation3.reverseName)
        assertEquals("reverseRelation4", object.relations.relation4.reverseName)

        Closure contraintsClosure = object.constraints;
        ConstrainedPropertyBuilder contraintsClosurePropertyBuilder = new ConstrainedPropertyBuilder(object);
        contraintsClosure.setDelegate (contraintsClosurePropertyBuilder);
        contraintsClosure.call();
        ConstrainedProperty relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["relation1"];
        assertTrue (relProp.isNullable());
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["reverseRelation2"];
        assertTrue (relProp.isNullable());
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["relation3"];
        assertNull(relProp);
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["relation4"];
        assertTrue (relProp.isNullable());



        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals(7, closureGetter.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("errors"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(relation1.firstName));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(relation2.secondName));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(relation3.firstName));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains(relation4.firstName));


        Class cls2 = compileClass(model2.name);
        def object2 = cls2.newInstance();
        assertEquals (0, object2.reverseRelation3.size());
        assertEquals (0, object2.relation2.size());
        assertEquals (0, object2.reverseRelation4.size());
        assertEquals(model1.getName(), object2.class.getDeclaredField("reverseRelation1").getType().getName());
        assertEquals(model1.getName(), object2.relations.relation2.type.name)
        assertEquals(model1.getName(), object2.relations.reverseRelation3.type.name)
        assertEquals(model1.getName(), object2.relations.reverseRelation4.type.name)


        assertFalse (object2.relations.reverseRelation1.isMany);
        assertTrue (object2.relations.relation2.isMany);
        assertTrue (object2.relations.reverseRelation3.isMany);
        assertTrue (object2.relations.reverseRelation4.isMany);

        assertEquals (model1.name, object2.relations.reverseRelation1.type.name);
        assertEquals (model1.name, object2.relations.relation2.type.name);
        assertEquals (model1.name, object2.relations.reverseRelation3.type.name);
        assertEquals (model1.name, object2.relations.reverseRelation4.type.name);

        assertEquals("relation1", object2.relations.reverseRelation1.reverseName)
        assertEquals("reverseRelation2", object2.relations.relation2.reverseName)
        assertEquals("relation3", object2.relations.reverseRelation3.reverseName)
        assertEquals("relation4", object2.relations.reverseRelation4.reverseName)

        contraintsClosure = object2.constraints;
        contraintsClosurePropertyBuilder = new ConstrainedPropertyBuilder(object2);
        contraintsClosure.setDelegate (contraintsClosurePropertyBuilder);
        contraintsClosure.call();
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["reverseRelation1"];
        assertTrue (relProp.isNullable());
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["relation2"];
        assertNull (relProp);
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["reverseRelation3"];
        assertNull(relProp);
        relProp = contraintsClosurePropertyBuilder.getConstrainedProperties()["reverseRelation4"];
        assertNull (relProp);


        Closure searchable2 = object2.searchable;
        ClosurePropertyGetter closureGetter2 = new ClosurePropertyGetter();
        searchable2.setDelegate (closureGetter2);
        searchable2.call();
        assertEquals(7, closureGetter2.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains("errors"));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(relation1.secondName));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(relation2.firstName));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(relation3.secondName));
        assertTrue(closureGetter2.propertiesSetByClosure["except"].contains(relation4.secondName));
    }

    public void testIfModelAlreadyExistsChangesParentKeepsOtherCode()
    {
        def model1 = new MockModel(name:"Class1");
        def model2 = new MockModel(name:"Class2");
        addMasterDatasource(model2);

        def modelFileContent = """import java.util.net.*;
            class ${model1.name} extends AnotherClass implements Trial{
                def method1()
                {
                    return "method1";
                }              
            }""";
        
        def interfaceContent = "interface Trial {}";
        File model1File = ModelGenerator.getInstance().getGeneratedModelFile(model1.name);
        model1File.getParentFile().mkdirs();
        model1File.withWriter { w ->
            w.write(modelFileContent);
        }
        ModelGenerator.getInstance().getGeneratedModelFile("Trial").withWriter { w ->
            w.write(interfaceContent);
        }


        model1.parentModel = model2;
        model1.datasources = [];
        ModelGeneratorAdapter.generateModels([model1, model2]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model1.name).exists());

        Class cls1 = compileClass(model1.name);
        Class cls2 = compileClass(model2.name);
        assertEquals (model2.name, cls1.superclass.name);
        def object = cls1.newInstance();
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("method1", object.method1());
        assertTrue (object.class.interfaces[0].getName().equals("Trial") || object.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1File.getText (), model1File.getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1File.getText ().indexOf("import $it") >= 0);
        }
        def indexOfFirstAlreadyIncludedImport = model1File.getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1File.getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);


        
//
//        ModelGeneratorAdapter.generateModels([model1, model2]);
//        assertNotNull(compileClass(model1.name));
    }


    public void testIfModelAlreadyExistsAndHasNoParentModelsAndAlreadyHasDefaultImports()
    {
        def model1 = new MockModel(name:"Class1");

        addMasterDatasource(model1);

        def modelFileContent = """import java.util.net.*;
            import ${ModelGenerator.DEFAULT_IMPORTS[0]};
            class ${model1.name} extends AnotherClass implements Trial{
                def method1()
                {
                    return "method1";
                }
            }""";
        
        def interfaceContent = "interface Trial {}";

        File model1File = ModelGenerator.getInstance().getGeneratedModelFile(model1.name);

        model1File.getParentFile().mkdirs();
        

        model1File.withWriter { w ->
            w.write(modelFileContent);
        }
        
        

        ModelGenerator.getInstance().getGeneratedModelFile("Trial").withWriter { w ->
            w.write(interfaceContent);
        }

        ModelGeneratorAdapter.generateModels([model1]);
        assertTrue (model1File.exists());
        Class cls = compileClass(model1.name);
        def object = cls.newInstance();
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("method1", object.method1());
        assertTrue (object.class.interfaces[0].getName().equals("Trial") || object.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1File.getText (), model1File.getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1File.getText ().indexOf("import $it") >= 0);
        }
        def indexOfFirstAlreadyIncludedImport = model1File.getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1File.getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);

        
    }



    public void testThrowsExceptionIfModelMasterDatasourceDoesnotExist()
    {
        def model1 = new MockModel(name:"Class2");
        try
        {
            ModelGeneratorAdapter.generateModels([model1]);
            fail("Should throw exception since no master records specified");
        }
        catch(ModelGenerationException exception)
        {
            assertEquals (ModelGenerationException.masterDatasourceDoesnotExists(model1.name).getMessage(), exception.getMessage());
        }
    }

    public void testThrowsExceptionIfKeymappingsDoesnotExistsForADatasource()
    {
        def model1 = new MockModel(name:"Class1");
        addMasterDatasource(model1);

        def datasource2 = new DatasourceName(name:"anotherDs");
        def modelDatasource2 = new MockModelDatasource(datasource:datasource2, model:model1);
        model1.datasources += modelDatasource2;
        try
        {
            ModelGeneratorAdapter.generateModels([model1]);
            fail("Should throw exception since no key mappings specified for datasource");
        }
        catch(ModelGenerationException exception)
        {
            assertEquals (ModelGenerationException.noKeySpecifiedForDatasource(modelDatasource2.datasource.name, model1.name).getMessage(), exception.getMessage());
        }
    }

    public void testIfKeymappingsDoesnotExistsForMasterDatasourceAddAIdAsMasterDatasourceKey()
    {
        def model1 = new MockModel(name:"Class1");
        def datasource1 = new DatasourceName(name:"RCMDB");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model1);
        model1.datasources += modelDatasource1;
        ModelGeneratorAdapter.generateModels([model1]);
        Class cls = compileClass(model1.name);
        assertTrue(cls.newInstance().datasources.RCMDB.keys.containsKey("id"));


        def parentModel = new MockModel(name:"Parent");
        def childModel = new MockModel(name:"Child", parentModel:parentModel);
        def datasourceParent = new DatasourceName(name:"RCMDB");
        def modelDatasourceParent = new MockModelDatasource(datasource:datasourceParent, model:parentModel);
        parentModel.datasources += modelDatasourceParent;

        ModelGeneratorAdapter.generateModels([parentModel, childModel]);

        Class childClass = compileClass(childModel.name);

        assertTrue(GrailsClassUtils.getStaticPropertyValue(childClass, "datasources").isEmpty());

    }


    public void testThrowsExceptionIfSameDatasourceDefinedForParentAndChild()
    {


        def parentModel = new MockModel(name:"Parent");
        def subParentModel = new MockModel(name:"SubParent",parentModel:parentModel);
        def childModel = new MockModel(name:"Child", parentModel:subParentModel);
        def datasourceParent = new DatasourceName(name:"RCMDB");
        def datasourceChild = new DatasourceName(name:"RCMDB");
        def modelDatasourceParent = new MockModelDatasource(datasource:datasourceParent, model:parentModel);
        def modelDatasourceChild = new MockModelDatasource(datasource:datasourceChild, model:childModel);
        parentModel.datasources += modelDatasourceParent;
        childModel.datasources += modelDatasourceChild;

        try
        {
            ModelGeneratorAdapter.generateModels([parentModel, childModel, subParentModel]);
            fail("Should throw exception");
        }
        catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateParentDatasource(datasourceChild.name, childModel.name).getMessage(), e.getMessage());
        }
    }

    public void testThrowsExceptionIfSameDatasourceDefined()
    {
        def childModel = new MockModel(name:"Child");
        def datasourceChild1 = new DatasourceName(name:"RCMDB");
        def datasourceChild2 = new DatasourceName(name:"RCMDB");
        def modelDatasourceChild1 = new MockModelDatasource(datasource:datasourceChild1, model:childModel);
        def modelDatasourceChild2 = new MockModelDatasource(datasource:datasourceChild2, model:childModel);
        childModel.datasources += modelDatasourceChild1;
        childModel.datasources += modelDatasourceChild2;

        try
        {
            ModelGeneratorAdapter.generateModels([childModel]);
            fail("Should throw exception");
        }
        catch(ModelGenerationException e)
        {
            assertEquals (ModelGenerationException.duplicateDatasource(datasourceChild1.name, childModel.name).getMessage(), e.getMessage());
        }
    }



    private void checkExistanceOfMetaDataProperties(object)
    {
        assertTrue(object.datasources instanceof Map);
        assertTrue(object.propertyConfiguration instanceof Map);
        assertTrue(object.propertyConfiguration.isEmpty());
        assertTrue(object.transients instanceof List);
        assertTrue(object.transients.contains("errors"));
        if(object.class.superclass.name == Object.class.name)
        {
          assertTrue(object.transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.OPERATION_PROPERTY_NAME));
          assertTrue(object.transients.contains(com.ifountain.rcmdb.util.RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED));
        }
        assertTrue(object.relations instanceof Map);
        assertTrue(object.relations.isEmpty());
    }

    def compileClass(String name)
    {
        GrailsAwareClassLoader cloader = new GrailsAwareClassLoader();
        cloader.addClasspath ("${base_directory}/${ModelGenerator.MODEL_FILE_DIR}");
        return cloader.loadClass(name);
    }
}

class ClosurePropertyGetter
{
    def propertiesSetByClosure = [:]
    public void setProperty(String propName, Object propValue)
    {
        propertiesSetByClosure[propName] = propValue;
    }

    public Object invokeMethod(String s, Object o) {
        propertiesSetByClosure[s] = o[0];    
    }


}