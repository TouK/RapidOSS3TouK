package com.ifountain.rcmdb.domain.generation

import model.Model
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import model.ModelProperty
import model.ModelDatasource
import model.ModelDatasourceKeyMapping
import datasource.BaseDatasource
import model.ModelRelation
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.AbstractDomainOperation
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.codehaus.groovy.grails.validation.ConstrainedPropertyBuilder
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint;

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
    def  modelRelations;
    def  reverseModelRelations;
    def  childModels;
    def findAllByModelMethod;
    def findAllByFirstModelMethod;
    def findAllBySecondModelMethod;
    protected void setUp() {
        super.setUp();
        if(System.getProperty("base.dir") == null)
        {
            System.setProperty("base.dir", "RapidCMDB");
        }
        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
        modelRelations = [:];
        reverseModelRelations = [:];
        childModels = [:];
        findAllByModelMethod = Model.metaClass.'static'.&findAllByParentModel;
        findAllByFirstModelMethod = ModelRelation.metaClass.'static'.&findAllByFirstModel;
        findAllBySecondModelMethod = ModelRelation.metaClass.'static'.&findAllBySecondModel;
        Model.metaClass.'static'.findAllByParentModel = {Model model->
            return childModels[model.name];
        }
        ModelRelation.metaClass.'static'.findAllBySecondModel = {Model model->
            return reverseModelRelations[model.name];
        }
        ModelRelation.metaClass.'static'.findAllByFirstModel = {Model model->
            return modelRelations[model.name];
        }
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        Model.metaClass.'static'.findByModelMethod = findAllByModelMethod;
        Model.metaClass.'static'.findByFirstModelMethod = findAllByFirstModelMethod;
        Model.metaClass.'static'.findBySecondModelMethod = findAllBySecondModelMethod;
    }
    private def addMasterDatasource(Model model)
    {
        def datasource1 = new BaseDatasource(name:"ds1-sample");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model);
        model.datasources += modelDatasource1;
        def keyProp = new ModelProperty(name:"keyprop", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false);
        model.modelProperties += keyProp;
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp, datasource:modelDatasource1, nameInDatasource:"keypropname");
    }
    
    public void testGenerateModel()
    {
        def model = new MockModel(name:"Class1");
        model.getControllerFile().createNewFile();
        addMasterDatasource(model);
        assertFalse (model.resourcesWillBeGenerated);

        ModelGenerator.getInstance().generateModel(model);
        assertTrue (model.resourcesWillBeGenerated);
        assertEquals (1, model.numberOfSaveCalls);
        assertTrue (model.getModelFile().exists());
        assertTrue (model.getOperationsFile().exists());
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

        Class modelOperations = compileClass(model.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertTrue (AbstractDomainOperation.isAssignableFrom(modelOperations));

        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model.getModelFile().getText ().indexOf("import $it") >= 0);    
        }
    }

    public void testGenerateModelExtendingAnotherModel()
    {
        def parentModel = new MockModel(name:"Class2");
        parentModel.getControllerFile().createNewFile();
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);
        childModel.getControllerFile().createNewFile();
        addMasterDatasource(parentModel);

        ModelGenerator.getInstance().generateModel(childModel);
        assertTrue (childModel.getModelFile().exists());
        assertTrue (childModel.getOperationsFile().exists());
        assertTrue (parentModel.getModelFile().exists());
        assertTrue (parentModel.getOperationsFile().exists());

        Class childModelClass = compileClass(childModel.name);
        def childModelInstance = childModelClass.newInstance();
        checkExistanceOfMetaDataProperties(childModelInstance);
        childModelInstance.keyprop = "keyPropValue"
        assertEquals ("Class1[keyprop:keyPropValue]", childModelInstance.toString());

        Class parentModelClass = compileClass(parentModel.name);
        def parentModelInstance = parentModelClass.newInstance();
        checkExistanceOfMetaDataProperties(parentModelInstance);

        Class childOperationClass = compileClass(childModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        Class parentOperationClass = compileClass(parentModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertTrue (AbstractDomainOperation.isAssignableFrom(parentOperationClass));
        assertEquals(parentOperationClass.getName(), childOperationClass.getSuperclass().getName());
        assertEquals(parentModelClass.getName(), childModelClass.getSuperclass().getName());
    }

    public void testGenerateModelGeneratesAllDependentModels()
    {
        def rootModel = new MockModel(name:"ClassRoot");
        def parentModel = new MockModel(name:"Class2", parentModel:rootModel);
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);
        addMasterDatasource(rootModel);

        def relatedModel = new MockModel(name:"Class3");
        def childRelatedModel = new MockModel(name:"Class4", parentModel:relatedModel);
        addMasterDatasource(relatedModel);

        relatedModel.modelProperties += new ModelProperty(name:"Prop1", type:ModelProperty.stringType, model:relatedModel);
        ModelRelation relation1 = new ModelRelation(firstName:"relation1", secondName:"reverseRelation1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE, firstModel:parentModel, secondModel:relatedModel);

        parentModel.fromRelations += relation1;
        relatedModel.toRelations += relation1;

        childModels[parentModel.name] = [childModel];
        childModels[relatedModel.name] = [childRelatedModel];
        modelRelations[parentModel.name] = [relation1];
        reverseModelRelations[relatedModel.name] = [relation1];

        ModelGenerator.getInstance().generateModel(parentModel);
        assertTrue (new File(base_directory + "${rootModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${rootModel.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue (new File(base_directory + "${childModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${childModel.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue (new File(base_directory + "${parentModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${parentModel.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue (new File(base_directory + "${relatedModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${relatedModel.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue (new File(base_directory + "${childRelatedModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${childRelatedModel.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());

        assertTrue (rootModel.resourcesWillBeGenerated);
        assertEquals (1, rootModel.numberOfSaveCalls);
        assertTrue (childModel.resourcesWillBeGenerated);
        assertEquals (1, childModel.numberOfSaveCalls);
        assertTrue (parentModel.resourcesWillBeGenerated);
        assertEquals (1, parentModel.numberOfSaveCalls);
        assertTrue (relatedModel.resourcesWillBeGenerated);
        assertEquals (1, relatedModel.numberOfSaveCalls);
        assertTrue (childRelatedModel.resourcesWillBeGenerated);
        assertEquals (1, childRelatedModel.numberOfSaveCalls);
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
            def datasource1 = new BaseDatasource(name:"ds1-sample");
            def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model);
            model.datasources += modelDatasource1;

            def date = System.currentTimeMillis();
            def keyProp1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"prop1 default value");
            def keyProp2 = new ModelProperty(name:"prop5", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:"prop5 default value");
            model.modelProperties += keyProp1;
            model.modelProperties += keyProp2;
            model.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"1");
            model.modelProperties += new ModelProperty(name:"prop3", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:converter.formater.format(new Date(date)));
            model.modelProperties += new ModelProperty(name:"prop4", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model);

            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp1, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");
            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp2, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");

            ModelGenerator.getInstance().generateModel(model);
            assertTrue (new File(base_directory + "${model.name}.groovy").exists());
            Class cls = compileClass(model.name);
            def object = cls.newInstance();
            assertTrue(object.hasMany instanceof Map);
            assertTrue(object.hasMany.isEmpty());
            assertTrue(object.belongsTo instanceof List);
            assertTrue(object.belongsTo.isEmpty());
            assertTrue(object.mappedBy instanceof Map);
            assertTrue(object.mappedBy.isEmpty());
            assertTrue(object.transients instanceof List);
            Closure searchable = object.searchable;
            ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
            searchable.setDelegate (closureGetter);
            searchable.call();
            assertTrue(closureGetter.propertiesSetByClosure["except"].isEmpty());
            assertTrue(object.transients.isEmpty());

            assertEquals("prop1 default value", object.prop1);
            assertEquals(1, object.prop2);
            assertEquals(new Date(date), object.prop3);

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
            assertFalse (prop.isNullable());
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
            assertNull ( prop.getAppliedConstraint("key"));
            if(!keyConstraint)
            {
                keyConstraint = prop.getAppliedConstraint("key");
                assertTrue (keyConstraint.getKeys().contains("prop1"));
            }

        }finally
        {
            RapidConvertUtils.getInstance().register (prevDateConf, Date.class)
        }

    }

    public void testWithSomeFederatedAndNormalProperties()
    {

        def model = new MockModel(name:"Class1");
        def datasource1 = new BaseDatasource(name:"ds1-sample");
        def datasource2 = new BaseDatasource(name:"ds2");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:false, model:model);
        def modelDatasource2 = new MockModelDatasource(datasource:datasource2, master:true, model:model);

        model.datasources += modelDatasource1;
        model.datasources += modelDatasource2;
        def prop0 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model, blank:true);
        def prop1 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, model:model);
        def prop2 = new ModelProperty(name:"dsname", type:ModelProperty.stringType, model:model);
        model.modelProperties += prop0;
        model.modelProperties += prop1;
        model.modelProperties += prop2;
        model.modelProperties += new ModelProperty(name:"Prop3", type:ModelProperty.numberType, model:model, propertyDatasource:modelDatasource1, nameInDatasource:"Prop3NameInDs", lazy:false, blank:true);
        model.modelProperties += new ModelProperty(name:"Prop4", type:ModelProperty.dateType, model:model, propertySpecifyingDatasource:prop2);

        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource1);
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop0, datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:prop1, datasource:modelDatasource1);



        ModelGenerator.getInstance().generateModel(model);
        assertTrue (new File(base_directory + "${model.name}.groovy").exists());
        Class cls = compileClass(model.name);
        def object = cls.newInstance();
        assertNull(object.prop1);
        assertEquals(object.class.getDeclaredField("prop1").getType(), String.class);
        assertNull(object.prop2);
        assertEquals(object.class.getDeclaredField("prop2").getType(), String.class);
        assertNull(object.Prop3);
        assertEquals(object.class.getDeclaredField("Prop3").getType(), Long.class);
        assertNull(object.Prop4);
        assertEquals(object.class.getDeclaredField("Prop4").getType(), Date.class);
        assertNull(object.dsname);
        assertNotNull (object.constraints);

        object.prop1 = "prop1Value";
        object.prop2 = "prop2Value";
        assertEquals ("Class1[prop1:prop1Value, prop2:prop2Value]", object.toString());

        def dsDefinition1 = object.datasources[modelDatasource1.datasource.name];
        assertFalse(dsDefinition1.master);
        assertEquals("Prop1KeyNameInDs", dsDefinition1.keys["prop1"].nameInDs);
        assertEquals("prop2", dsDefinition1.keys["prop2"].nameInDs);

        def dsDefinition2 = object.datasources[modelDatasource2.datasource.name];
        assertTrue(dsDefinition2.master);
        assertEquals("Prop1KeyNameInDs", dsDefinition2.keys["prop1"].nameInDs);
        assertEquals("prop2", dsDefinition2.keys["prop2"].nameInDs);

        def transients = object.transients;
        assertEquals (2, transients.size());
        assertTrue(transients.contains("Prop3"));
        assertTrue(transients.contains("Prop4"));

        Closure searchable = object.searchable;
        ClosurePropertyGetter closureGetter = new ClosurePropertyGetter();
        searchable.setDelegate (closureGetter);
        searchable.call();
        assertEquals(2, closureGetter.propertiesSetByClosure["except"].size());
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("Prop3"));
        assertTrue(closureGetter.propertiesSetByClosure["except"].contains("Prop4"));

        def propertyConfiguration = object.propertyConfiguration;

        assertEquals("Prop3NameInDs", propertyConfiguration["Prop3"].nameInDs);
        assertEquals(modelDatasource1.datasource.name, propertyConfiguration["Prop3"].datasource);
        assertNull(propertyConfiguration["Prop3"].datasourceProperty);
        assertFalse(propertyConfiguration["Prop3"].lazy);

        assertEquals("Prop4", propertyConfiguration["Prop4"].nameInDs);
        assertEquals("dsname", propertyConfiguration["Prop4"].datasourceProperty);
        assertNull(propertyConfiguration["Prop4"].datasource);
        assertTrue(propertyConfiguration["Prop4"].lazy);
    }

    public void testGenerateModelWithRelation()
    {
        def model1 = new MockModel(name:"Class1");
        model1.getControllerFile().createNewFile();
        addMasterDatasource(model1);

        model1.modelProperties += new ModelProperty(name:"Prop1", type:ModelProperty.stringType, model:model1);
        def model2 = new MockModel(name:"Class2");
        model2.getControllerFile().createNewFile();
        addMasterDatasource(model2);

        model2.modelProperties += new ModelProperty(name:"Prop1", type:ModelProperty.stringType, model:model2);
        ModelRelation relation1 = new ModelRelation(firstName:"relation1", secondName:"reverseRelation1", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE, firstModel:model1, secondModel:model2);
        ModelRelation relation2 = new ModelRelation(firstName:"relation2", secondName:"reverseRelation2", firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.MANY, firstModel:model2, secondModel:model1);
        ModelRelation relation3 = new ModelRelation(firstName:"relation3", secondName:"reverseRelation3", firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.MANY, firstModel:model1, secondModel:model2);
        ModelRelation relation4 = new ModelRelation(firstName:"relation4", secondName:"reverseRelation4", firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.ONE, firstModel:model1, secondModel:model2);


        modelRelations[model1.name] = [relation1,relation3,relation4];
        modelRelations[model2.name] = [relation2];
        reverseModelRelations[model1.name] = [relation2];
        reverseModelRelations[model2.name] = [relation1,relation3,relation4];

        model1.fromRelations += relation1;
        model2.toRelations += relation1;

        model2.fromRelations += relation2;
        model1.toRelations += relation2;

        model1.fromRelations += relation3;
        model2.toRelations += relation3;

        model1.fromRelations += relation4;
        model2.toRelations += relation4;



        ModelGenerator.getInstance().generateModel(model1);

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
        def modelOperationsFileContent = """import java.util.net.*;
            class ${model1.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION} extends ${AbstractDomainOperation.class.name} implements Trial{
                def method1()
                {
                    return "method1";
                }
            }""";
        def interfaceContent = "interface Trial {}";
        model1.getModelFile().getParentFile().mkdirs();
        model1.getModelFile().withWriter { w ->
            w.write(modelFileContent);
        }
        model1.getOperationsFile().withWriter { w ->
            w.write(modelOperationsFileContent);
        }

        new File(base_directory + "Trial.groovy").withWriter { w ->
            w.write(interfaceContent);
        }


        model1.parentModel = model2;
        ModelGenerator.getInstance().generateModel(model1);
        assertTrue (new File(base_directory + "${model1.name}.groovy").exists());

        Class cls = compileClass(model1.name);
        assertEquals (model2.name, cls.superclass.name);
        def object = cls.newInstance();
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("method1", object.method1());
        assertTrue (object.class.interfaces[0].getName().equals("Trial") || object.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1.getModelFile().getText (), model1.getModelFile().getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1.getModelFile().getText ().indexOf("import $it") >= 0);    
        }
        def indexOfFirstAlreadyIncludedImport = model1.getModelFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1.getModelFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);

        Class operationsClass = compileClass(model1.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertEquals (model2.name+ModelUtils.OPERATIONS_CLASS_EXTENSION, operationsClass.superclass.name);
        def operationsObject = operationsClass.newInstance();
        assertEquals ("method1", operationsObject.method1());
        assertTrue (operationsObject.class.interfaces[0].getName().equals("Trial") || operationsObject.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1.getOperationsFile().getText (), model1.getOperationsFile().getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1.getOperationsFile().getText ().indexOf("import $it") >= 0);
        }
        indexOfFirstAlreadyIncludedImport = model1.getOperationsFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1.getOperationsFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);

        ModelGenerator.getInstance().generateModel(model1);
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
        def modelOperationsFileContent = """import java.util.net.*;
            class ${model1.name}${ModelUtils.OPERATIONS_CLASS_EXTENSION} extends AnotherClass implements Trial{
                def method1()
                {
                    return "method1";
                }
            }""";
        def interfaceContent = "interface Trial {}";
        model1.getModelFile().getParentFile().mkdirs();
        model1.getModelFile().withWriter { w ->
            w.write(modelFileContent);
        }
        model1.getOperationsFile().withWriter { w ->
                    w.write(modelOperationsFileContent);
                }
        

        new File(base_directory + "Trial.groovy").withWriter { w ->
            w.write(interfaceContent);
        }

        ModelGenerator.getInstance().generateModel(model1);
        assertTrue (new File(base_directory + "${model1.name}.groovy").exists());
        Class cls = compileClass(model1.name);
        def object = cls.newInstance();
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("method1", object.method1());
        assertTrue (object.class.interfaces[0].getName().equals("Trial") || object.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1.getModelFile().getText (), model1.getModelFile().getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1.getModelFile().getText ().indexOf("import $it") >= 0);
        }
        def indexOfFirstAlreadyIncludedImport = model1.getModelFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1.getModelFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);


        Class operationsClass = compileClass(model1.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertEquals (AbstractDomainOperation.class.name, operationsClass.superclass.name);
        def operationsObject = operationsClass.newInstance();
        assertEquals ("method1", operationsObject.method1());
        assertTrue (operationsObject.class.interfaces[0].getName().equals("Trial") || operationsObject.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1.getOperationsFile().getText (), model1.getOperationsFile().getText ().indexOf("import java.util.net.*;") >= 0);
        ModelGenerator.DEFAULT_IMPORTS.each {
            assertTrue(model1.getOperationsFile().getText ().indexOf("import $it") >= 0);
        }
        indexOfFirstAlreadyIncludedImport = model1.getOperationsFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1.getOperationsFile().getText ().indexOf("${ModelGenerator.DEFAULT_IMPORTS[0]}", indexOfFirstAlreadyIncludedImport) < 0);
    }



    public void testThrowsExceptionIfModelMasterDatasourceDoesnotExist()
    {
        def model1 = new MockModel(name:"Class2");
        try
        {
            ModelGenerator.getInstance().generateModel(model1);
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
        def datasource1 = new BaseDatasource(name:"ds1-sample");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model1);
        model1.datasources += modelDatasource1;
        try
        {
            ModelGenerator.getInstance().generateModel(model1);
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
        cloader.addClasspath (base_directory);
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