package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import datasource.BaseDatasource
import model.ModelProperty
import model.ModelDatasourceKeyMapping
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import model.PropertyAction
import model.ModelAction
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import model.ModelRelation
import model.DatasourceName

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 3, 2008
* Time: 5:09:31 PM
* To change this template use File | Settings | File Templates.
*/
class ExistingDataAnalyzerTest extends RapidCmdbTestCase{

    def static base_directory = "../testoutput/";
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        if(new File(System.getProperty("base.dir")?System.getProperty("base.dir"):".").getAbsolutePath().endsWith("RapidCMDB"))
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, System.getProperty("base.dir"));
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, base_directory, "RapidCMDB");
        }
        
        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }
    

    public void testPropertyTypeChange()
    {
        String modelName = "Class1";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");

        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        prop2.type = ModelProperty.numberType;
        generateModel (modelName, propList, keyPropList);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        PropertyAction action = actions[0];
        assertEquals (prop2.name, action.propName);
        assertEquals (PropertyAction.SET_DEFAULT_VALUE, action.action);
        assertEquals (modelName, action.modelName);
    }

    public void testKeyPropertyTypeChange()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        String modelName = "Class1";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");

        def propList = [prop1];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        prop1.type = ModelProperty.numberType;
        generateModel (modelName, propList, keyPropList);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        ModelAction action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);
    }

    public void testIfPropertyDoesnotExistInOldModel()
    {
        String modelName = "Class1";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");


        def propList = [prop1];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        propList += prop2;
        generateModel (modelName, propList, keyPropList);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        PropertyAction action = actions[0];
        assertEquals (prop2.name, action.propName);
        assertEquals (PropertyAction.SET_DEFAULT_VALUE, action.action);
        assertEquals (modelName, action.modelName);
    }


    public void testIfPropertyNullSettingIsChanged()
    {
        String modelName = "Class1";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:true, defaultValue:"1");


        def propList = [prop1,prop2];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        prop2.blank = false
        generateModel (modelName, propList, keyPropList);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        PropertyAction action = actions[0];
        assertEquals (prop2.name, action.propName);
        assertEquals (PropertyAction.SET_DEFAULT_VALUE, action.action);
        assertEquals (modelName, action.modelName);
    }


    public void testIfKeyIsRemoved()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        String modelName = "Class1";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");

        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        ModelAction action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);

        keyPropList = [prop1];
        propList = [prop1];
        generateModel (modelName, propList, keyPropList);

        newDomainClass = loadGrailsDomainClass(modelName);
        newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);


        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (1, actions.size());
        action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);

        keyPropList = [prop1];
        propList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList);
        oldDomainClass = loadGrailsDomainClass(modelName);
        oldGrailsDomainClass = new DefaultGrailsDomainClass(oldDomainClass);

        keyPropList = [prop1, prop2];
        propList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList);

        newDomainClass = loadGrailsDomainClass(modelName);  
        newGrailsDomainClass = new DefaultGrailsDomainClass(newDomainClass);


        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass, newGrailsDomainClass);
        assertEquals (0, actions.size());
    }

    public void testIfRelationTypeIsChangedFromOneToOneToOneToMany()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        MockModel model1 = createModel(modelName1, propList, keyPropList);
        MockModel model2 = createModel (modelName2, propList, keyPropList);
        def rel1 = new ModelRelation(firstName:"rel1",  secondName:"revrel1", firstModel:model1, secondModel:model2, firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.ONE);
        model1.fromRelations += rel1;
        model2.toRelations += rel1;

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldGrailsDomainClass1 = new DefaultGrailsDomainClass(oldDomainClass1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClass2 = new DefaultGrailsDomainClass(oldDomainClass2);

        rel1.firstCardinality = ModelRelation.MANY;

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newGrailsDomainClass1 = new DefaultGrailsDomainClass(newDomainClass1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClass2 = new DefaultGrailsDomainClass(newDomainClass2);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass1, newGrailsDomainClass1);
        assertEquals (1, actions.size());
        PropertyAction action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName1, action.modelName);

        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass2, newGrailsDomainClass2);
        assertEquals (1, actions.size());
        action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName2, action.modelName);
    }

    public void testIfRelationTypeIsChangedFromOneToManyToOneToOne()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        MockModel model1 = createModel(modelName1, propList, keyPropList);
        MockModel model2 = createModel (modelName2, propList, keyPropList);
        def rel1 = new ModelRelation(firstName:"rel1",  secondName:"revrel1", firstModel:model1, secondModel:model2, firstCardinality:ModelRelation.MANY, secondCardinality:ModelRelation.ONE);
        model1.fromRelations += rel1;
        model2.toRelations += rel1;

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldGrailsDomainClass1 = new DefaultGrailsDomainClass(oldDomainClass1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClass2 = new DefaultGrailsDomainClass(oldDomainClass2);

        rel1.firstCardinality = ModelRelation.ONE;

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newGrailsDomainClass1 = new DefaultGrailsDomainClass(newDomainClass1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClass2 = new DefaultGrailsDomainClass(newDomainClass2);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass1, newGrailsDomainClass1);
        assertEquals (1, actions.size());
        PropertyAction action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName1, action.modelName);

        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass2, newGrailsDomainClass2);
        assertEquals (1, actions.size());
        action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName2, action.modelName);
    }

    public void testWithNewRelation()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = new ModelProperty(name:"prop1", type:ModelProperty.stringType, blank:false, defaultValue:"1");
        def prop2 = new ModelProperty(name:"prop2", type:ModelProperty.stringType, blank:false, defaultValue:"1");


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        MockModel model1 = createModel(modelName1, propList, keyPropList);
        MockModel model2 = createModel (modelName2, propList, keyPropList);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldGrailsDomainClass1 = new DefaultGrailsDomainClass(oldDomainClass1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClass2 = new DefaultGrailsDomainClass(oldDomainClass2);

        def rel1 = new ModelRelation(firstName:"rel1",  secondName:"revrel1", firstModel:model1, secondModel:model2, firstCardinality:ModelRelation.ONE, secondCardinality:ModelRelation.MANY);
        model1.fromRelations += rel1;
        model2.toRelations += rel1;
        rel1.firstCardinality = ModelRelation.MANY;

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);
        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newGrailsDomainClass1 = new DefaultGrailsDomainClass(newDomainClass1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClass2 = new DefaultGrailsDomainClass(newDomainClass2);

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass1, newGrailsDomainClass1);
        assertEquals (0, actions.size());
        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClass2, newGrailsDomainClass2);
        assertEquals (0, actions.size());
    }

    


    def generateModel(String name, List modelProperties, List keyProperties)
    {
        def model = createModel(name, modelProperties, keyProperties);
        
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model);
    }

    def createModel(String name, List modelProperties, List keyProperties)
    {
        def model = new MockModel(name:name);
        def datasource1 = new DatasourceName(name:"ds1-sample");
        def modelDatasource1 = new MockModelDatasource(datasource:datasource1, master:true, model:model);
        model.datasources += modelDatasource1;
        model.modelProperties += new ModelProperty(name:"id", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"1");
        model.modelProperties += new ModelProperty(name:"version", type:ModelProperty.numberType, propertyDatasource:modelDatasource1, model:model,blank:false,defaultValue:"1");
        modelProperties.each{ModelProperty prop->
            prop.propertyDatasource = modelDatasource1;
            prop.model = model;
            model.modelProperties += prop;
        }
        keyProperties.each{ModelProperty prop->
            modelDatasource1.keyMappings += new ModelDatasourceKeyMapping(property:prop, datasource:modelDatasource1, nameInDatasource:"KeyPropNameInDs");;
        }

        return model;
    }

    def loadGrailsDomainClass(String className)
    {
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader());
        gcl.setShouldRecompile(true);
        gcl.addClasspath(base_directory + ModelGenerator.MODEL_FILE_DIR);
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        return gcl.loadClass (className);
    }
}