package com.ifountain.domain

import model.Model
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import model.ModelProperty
import model.ModelDatasource
import model.ModelDatasourceKeyMapping
import datasource.BaseDatasource
import model.ModelRelation
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsClassUtils;

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
class ModelGeneratorTest extends GroovyTestCase{
    def static base_directory = "../testoutput/";

    protected void setUp() {
        super.setUp();
        System.setProperty("base.dir", "RapidCMDB");
        FileUtils.deleteDirectory (new File(base_directory));
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
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
        addMasterDatasource(model);

        ModelGenerator.getInstance().generateModel(model);
        assertTrue (new File(base_directory + "${model.name}.groovy").exists());
        Class cls = compileModel(model);
        def object = cls.newInstance();
        object.keyprop = "keypropvalue";
        checkExistanceOfMetaDataProperties(object);
        assertTrue (model.generateAll);
        assertEquals (1, model.numberOfSaveCalls);
        assertEquals ("Class1[keyprop:keypropvalue]", object.toString());
    }

    public void testGenerateModelExtendingAnotherModel()
    {
        def parentModel = new MockModel(name:"Class2");  
        def childModel = new MockModel(name:"Class1", parentModel:parentModel);
        addMasterDatasource(parentModel);

        ModelGenerator.getInstance().generateModel(childModel);
        assertTrue (new File(base_directory + "${childModel.name}.groovy").exists());
        assertTrue (new File(base_directory + "${parentModel.name}.groovy").exists());
        assertTrue (childModel.generateAll);
        assertEquals (1, childModel.numberOfSaveCalls);
        assertTrue (parentModel.generateAll);
        assertEquals (1, parentModel.numberOfSaveCalls);

        Class childModelClass = compileModel(childModel);
        def childModelInstance = childModelClass.newInstance();
        checkExistanceOfMetaDataProperties(childModelInstance);
        childModelInstance.keyprop = "keyPropValue"
        assertEquals ("Class1[keyprop:keyPropValue]", childModelInstance.toString());

        Class parentModelClass = compileModel(parentModel);
        def parentModelInstance = parentModelClass.newInstance();
        checkExistanceOfMetaDataProperties(parentModelInstance);

        assertEquals(parentModelClass.getName(), childModelClass.getSuperclass().getName());
    }

    public void testWithSomeProperties()
    {
        def model = new MockModel(name:"Class1");
        def datasource1 = new BaseDatasource(name:"ds1-sample");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model);
        model.datasources += modelDatasource1;

        def date = System.currentTimeMillis();
        def keyProp = new ModelProperty(name:"prop1", type:ModelProperty.stringType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:"prop2 default value");
        model.modelProperties += keyProp;
        model.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"1");
        model.modelProperties += new ModelProperty(name:"prop3", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model,blank:true,defaultValue:date);
        model.modelProperties += new ModelProperty(name:"prop4", type:ModelProperty.dateType, propertyDatasource:modelDatasource1, model:model);

        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:keyProp, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");

        ModelGenerator.getInstance().generateModel(model);
        assertTrue (new File(base_directory + "${model.name}.groovy").exists());
        Class cls = compileModel(model);
        def object = cls.newInstance();
        assertTrue(object.hasMany instanceof Map);
        assertTrue(object.hasMany.isEmpty());
        assertTrue(object.belongsTo instanceof List);
        assertTrue(object.belongsTo.isEmpty());
        assertTrue(object.mappedBy instanceof Map);
        assertTrue(object.mappedBy.isEmpty());
        assertTrue(object.transients instanceof List);
        assertTrue(object.transients.isEmpty());

        assertEquals("prop2 default value", object.prop1);
        assertEquals(1, object.prop2);
        assertEquals(new Date(date), object.prop3);
        assertNotNull (object.constraints);

    }

    public void testConstraints()
    {
        fail("Constraint tests should be implemented");
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
        model.modelProperties += new ModelProperty(name:"prop1", type:ModelProperty.stringType, model:model, blank:true);
        model.modelProperties += new ModelProperty(name:"prop2", type:ModelProperty.stringType, model:model);
        model.modelProperties += new ModelProperty(name:"dsname", type:ModelProperty.stringType, model:model);
        model.modelProperties += new ModelProperty(name:"Prop3", type:ModelProperty.numberType, model:model, propertyDatasource:modelDatasource1, nameInDatasource:"Prop3NameInDs", lazy:false, blank:true);
        model.modelProperties += new ModelProperty(name:"Prop4", type:ModelProperty.dateType, model:model, propertySpecifyingDatasource:model.modelProperties[2]);

        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:model.modelProperties[0], datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:model.modelProperties[1], datasource:modelDatasource1);
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:model.modelProperties[0], datasource:modelDatasource1, nameInDatasource:"Prop1KeyNameInDs");
        modelDatasource2.keyMappings += new ModelDatasourceKeyMapping(property:model.modelProperties[1], datasource:modelDatasource1);



        ModelGenerator.getInstance().generateModel(model);
        assertTrue (new File(base_directory + "${model.name}.groovy").exists());
        Class cls = compileModel(model);
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
        assertTrue(transients.contains("Prop3"));
        assertTrue(transients.contains("Prop4"));

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
        addMasterDatasource(model1);

        model1.modelProperties += new ModelProperty(name:"Prop1", type:ModelProperty.stringType, model:model1);
        def model2 = new MockModel(name:"Class2");
        addMasterDatasource(model2);

        model2.modelProperties += new ModelProperty(name:"Prop1", type:ModelProperty.stringType, model:model2);
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
        ModelGenerator.getInstance().generateModel(model1);
        assertTrue (model1.generateAll);
        assertEquals (1, model1.numberOfSaveCalls);
        assertTrue (model2.generateAll);
        assertEquals (1, model2.numberOfSaveCalls);

        Class cls = compileModel(model1);
        def object = cls.newInstance();
        
        assertEquals(model2.getName(), object.class.getDeclaredField("relation1").getType().getName());
        assertEquals(model2.getName(), object.class.getDeclaredField("reverseRelation2").getType().getName())
        assertEquals(model2.getName(), object.hasMany.relation3.getName())
        assertEquals(model2.getName(), object.class.getDeclaredField("relation4").getType().getName())

        assertEquals("reverseRelation1", object.mappedBy.relation1)
        assertEquals("relation2", object.mappedBy.reverseRelation2)
        assertEquals("reverseRelation3", object.mappedBy.relation3)
        assertEquals("reverseRelation4", object.mappedBy.relation4)


        Class cls2 = compileModel(model2);
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

        def modelFileContent = """import java.util.net.*;\n
                        class ${model1.name} extends AnotherClass implements Trial{\n
                            def method1()\n
                            {            \n
                                return "method1";
                            }              \n
                        }""";
        def controllerFileContent = """
                        class ${model1.name}Controller{\n
                            def method1()\n
                            {            \n
                                return "method1";
                            }              \n
                        }""";
        def interfaceContent = "interface Trial {}";
        model1.getModelFile().getParentFile().mkdirs();
        model1.getModelFile().withWriter { w ->
            w.write(modelFileContent);
        }

        new File(base_directory + "Trial.groovy").withWriter { w ->
            w.write(interfaceContent);
        }

        model1.getControllerFile().withWriter { w ->
            w.write(controllerFileContent);
        }

        model1.parentModel = model2;
        ModelGenerator.getInstance().generateModel(model1);
        assertTrue (new File(base_directory + "${model1.name}.groovy").exists());
        Class cls = compileModel(model1);
        def object = cls.newInstance();
        checkExistanceOfMetaDataProperties(object);
        assertEquals ("method1", object.method1());
        assertEquals ("Trial", object.class.interfaces[0].getName());
        assertTrue(model1.getModelFile().getText ().indexOf("import java.util.net.*;\n") == 0);

        GrailsAwareClassLoader cloader = new GrailsAwareClassLoader();
        cloader.addClasspath (base_directory);
        Class controllerCls = cloader.loadClass(model1.name+"Controller");
        def controllerObj = controllerCls.newInstance();
        assertEquals ("method1",controllerObj.method1());
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
            assertEquals (ModelGenerationException.noKeySpecifiedForDatasource(modelDatasource1.datasource.name).getMessage(), exception.getMessage());
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

    def compileModel(Model model)
    {
        GrailsAwareClassLoader cloader = new GrailsAwareClassLoader();
        cloader.addClasspath (base_directory);
        return cloader.loadClass(model.name);
    }
}



class MockModel extends Model
{
    def numberOfSaveCalls = 0;
    def datasources = [];
    def modelProperties = []
    def fromRelations = [];
    def toRelations = [];
    def getModelFile()
    {
        return new File("../testoutput/${name}.groovy");
    }

    def getControllerFile()
    {
        return new File("../testoutput/${name}Controller.groovy");
    }

    def save()
    {
        numberOfSaveCalls++;
    }
}
class MockModelDatasource extends ModelDatasource
{
    def keyMappings = [];
    def getModelFile()
    {
        return new File("../testoutput/${name}.groovy");
    }
}