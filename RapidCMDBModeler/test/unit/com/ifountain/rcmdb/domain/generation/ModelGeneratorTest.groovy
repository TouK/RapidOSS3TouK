package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import model.*
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import com.ifountain.rcmdb.util.RapidCMDBConstants

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
        if(new File(System.getProperty("base.dir")?System.getProperty("base.dir"):".").getAbsolutePath().endsWith("RapidCMDBModeler"))
        {
            System.setProperty("basedir", ".")
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "${System.getProperty("base.dir")}/../RcmdbCommons");
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
        def model = new MockModel(name:"Class1");
        addMasterDatasource(model);

        ModelGeneratorAdapter.generateModels([model]);
        assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model.name).exists());

        Class cls = compileClass(model.name);
        def object = cls.newInstance();
        object.keyprop = "keypropvalue";
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("Class1[keyprop:keypropvalue]", object.toString());
        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertTrue(closureGetter.propertiesSetByClosure["except"].isEmpty());

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

            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp1, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");
            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp2, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");

            ModelGeneratorAdapter.generateModels([model]);
            assertTrue (ModelGenerator.getInstance().getGeneratedModelFile(model.name).exists());
            Class cls = compileClass(model.name);
            def object = cls.newInstance();
            assertTrue(object.hasMany instanceof Map);
            assertTrue(object.hasMany.isEmpty());
            assertTrue(object.belongsTo instanceof List);
            assertTrue(object.belongsTo.isEmpty());
            assertTrue(object.mappedBy instanceof Map);
            assertTrue(object.mappedBy.isEmpty());
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

            Closure searchable = object.searchable;
            ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
            searchable.setDelegate (closureGetter);
            searchable.call();
            assertTrue(closureGetter.propertiesSetByClosure["except"].isEmpty());
            assertTrue(object.transients.isEmpty());

            assertEquals("prop1 default value", object.prop1);
            assertEquals(1, object.prop2);
            assertEquals((int)1.0, (int)object.prop6);
            assertEquals(1, object.prop2);
            assertEquals(new Date(date), object.prop3);
            assertEquals(new Date(0), object.prop4);
            assertEquals("", object.prop7);
            assertEquals(new Long(0), object.prop8);
            assertEquals(new Double(0), object.prop9);

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
            assertNull ( prop.getAppliedConstraint("key"));
            
            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop3"];
            assertTrue (prop.isNullable());
            assertTrue (prop.isBlank());
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop4"];
            assertTrue(prop.isNullable());
            assertTrue (prop.isBlank());
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
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop7"];
            assertTrue (prop.isNullable());
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop8"];
            assertTrue (prop.isNullable());
            assertNull ( prop.getAppliedConstraint("key"));

            prop = contraintsClosurePropertyBuilder.getConstrainedProperties()["prop9"];
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
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model);
        def modelDatasource2 = new MockModelDatasource(datasource:datasource2, model:model);

        model.datasources += modelDatasource1;
        model.datasources += modelDatasource2;
        def prop0 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model, blank:true);
        def prop1 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, model:model);
        def prop2 = new ModelProperty(name:"dsname", type:ModelProperty.stringType, model:model);
        model.modelProperties += prop0;
        model.modelProperties += prop1;
        model.modelProperties += prop2;
        model.modelProperties += new ModelProperty(name:"prop3", type:ModelProperty.numberType, model:model, propertyDatasource:modelDatasource1, nameInDatasource:"Prop3NameInDs", lazy:false, blank:true);
        model.modelProperties += new ModelProperty(name:"prop4", type:ModelProperty.dateType, model:model, propertySpecifyingDatasource:prop2);

        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource1);
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource1);



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
        assertEquals (2, transients.size());
        assertTrue(transients.contains("prop3"));
        assertTrue(transients.contains("prop4"));

        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals(2, closureGetter.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop3"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("prop4"));

        def propertyConfiguration = object.propertyConfiguration;

        assertEquals("Prop3NameInDs", propertyConfiguration["prop3"].nameInDs);
        assertEquals(modelDatasource1.datasource.name, propertyConfiguration["prop3"].datasource);
        assertNull(propertyConfiguration["prop3"].datasourceProperty);
        assertFalse(propertyConfiguration["prop3"].lazy);

        assertEquals("prop4", propertyConfiguration["prop4"].nameInDs);
        assertEquals("dsname", propertyConfiguration["prop4"].datasourceProperty);
        assertNull(propertyConfiguration["prop4"].datasource);
        assertTrue(propertyConfiguration["prop4"].lazy);
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

        assertEquals(model2.getName(), object.class.getDeclaredField("relation1").getType().getName());
        assertEquals(model2.getName(), object.class.getDeclaredField("reverseRelation2").getType().getName())
        assertEquals(model2.getName(), object.hasMany.relation3.getName())
        assertEquals(model2.getName(), object.class.getDeclaredField("relation4").getType().getName())

        assertEquals("reverseRelation1", object.mappedBy.relation1)
        assertEquals("relation2", object.mappedBy.reverseRelation2)
        assertEquals("reverseRelation3", object.mappedBy.relation3)
        assertEquals("reverseRelation4", object.mappedBy.relation4)

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


        Class cls2 = compileClass(model2.name);
        def object2 = cls2.newInstance();

        assertEquals(model1.getName(), object2.class.getDeclaredField("reverseRelation1").getType().getName());
        assertEquals(model1.getName(), object2.hasMany.relation2.getName())
        assertEquals(model1.getName(), object2.hasMany.reverseRelation3.getName())
        assertEquals(model1.getName(), object2.hasMany.reverseRelation4.getName())

        assertEquals("relation1", object2.mappedBy.reverseRelation1)
        assertEquals("reverseRelation2", object2.mappedBy.relation2)
        assertEquals("relation3", object2.mappedBy.reverseRelation3)
        assertEquals("relation4", object2.mappedBy.reverseRelation4)

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

        assertEquals(1, object2.belongsTo.size())
        assertEquals(model1.getName(), object2.belongsTo[0].getName())
    }

    public void testIfModelAlreadyExistsChangesParentKeepsOtherCode()
    {
        def model1 = new MockModel(name:"Class1");
        def model2 = new MockModel(name:"Class2");

        addMasterDatasource(model1);
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


        

        ModelGeneratorAdapter.generateModels([model1]);
        assertNotNull(compileClass(model1.name));
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
        def datasource1 = new DatasourceName(name:"RCMDB");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, model:model1);
        model1.datasources += modelDatasource1;
        try
        {
            ModelGeneratorAdapter.generateModels([model1]);
            fail("Should throw exception since no key mappings specified for datasource");
        }
        catch(ModelGenerationException exception)
        {
            assertEquals (ModelGenerationException.noKeySpecifiedForDatasource(modelDatasource1.datasource.name, model1.name).getMessage(), exception.getMessage());
        }
    }


    private void checkExistanceOfMetaDataProperties(object)
    {
        assertTrue(object.datasources instanceof Map);
        assertTrue(object.propertyConfiguration instanceof Map);
        assertTrue(object.propertyConfiguration.isEmpty());
        assertTrue(object.transients instanceof List);
        assertTrue(object.transients.isEmpty());
        assertTrue(object.hasMany instanceof Map);
        assertTrue(object.hasMany.isEmpty());
        assertTrue(object.belongsTo instanceof List);
        assertTrue(object.belongsTo.isEmpty());
        assertTrue(object.mappedBy instanceof Map);
        assertTrue(object.mappedBy.isEmpty());
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

}