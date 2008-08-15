package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import model.PropertyAction
import model.ModelAction
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 3, 2008
* Time: 5:09:31 PM
* To change this template use File | Settings | File Templates.
*/
class ExistingDataAnalyzerTest extends RapidCmdbTestCase{

    def static base_directory = "../testoutput/";
    StringWriter model;
    def modelbuilder;
    def metaClassCreationHandler;
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

        FileUtils.deleteDirectory (new File(base_directory));
        new File(base_directory).mkdirs();
        metaClassCreationHandler = GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler();
        GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle (new ExpandoMetaClassCreationHandle());
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(metaClassCreationHandler);
    }

    public void testPropertyTypeChange()
    {
        String modelName = "Class1";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"]

        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList, []);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])

        prop2.type = ModelGenerator.NUMBER_TYPE;
        generateModel (modelName, propList, keyPropList, []);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (2, actions.size());
        PropertyAction action = actions[0];
        assertEquals (prop2.name, action.propName);
        assertEquals (PropertyAction.SET_DEFAULT_VALUE, action.action);
        assertEquals (modelName, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);
    }

    public void testKeyPropertyTypeChange()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        String modelName = "Class1";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];

        def propList = [prop1];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList, []);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])

        prop1.type = ModelGenerator.NUMBER_TYPE;
        generateModel (modelName, propList, keyPropList, []);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (2, actions.size());
        ModelAction action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);
    }

    public void testIfPropertyDoesnotExistInOldModel()
    {
        String modelName = "Class1";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList, []);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])

        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        propList += prop2;
        generateModel (modelName, propList, keyPropList, []);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (2, actions.size());
        PropertyAction action = actions[0];
        assertEquals (prop2.name, action.propName);
        assertEquals (PropertyAction.SET_DEFAULT_VALUE, action.action);
        assertEquals (modelName, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);
    }


    public void testIfPropertyDoesnotExistInNewModel()
    {
        String modelName = "Class1";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];

        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList, []);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])


        propList = [prop1];
        generateModel (modelName, propList, keyPropList, []);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (1, actions.size());
        ModelAction modelAction = actions[0];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);
    }



    public void testIfKeyIsRemoved()
    {
        ConstrainedProperty.registerNewConstraint (KeyConstraint.KEY_CONSTRAINT, KeyConstraint);
        String modelName = "Class1";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];

        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList, []);
        def oldDomainClass = loadGrailsDomainClass(modelName);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])

        prop2.type = ModelGenerator.NUMBER_TYPE;
        keyPropList = [prop1];
        generateModel (modelName, propList, keyPropList, []);

        def newDomainClass = loadGrailsDomainClass(modelName);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (2, actions.size());
        ModelAction action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);


        keyPropList = [prop1];
        propList = [prop1];
        generateModel (modelName, propList, keyPropList, []);

        newDomainClass = loadGrailsDomainClass(modelName);
        newGrailsDomainClasses = generateDomainClasses([newDomainClass])


        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (2, actions.size());
        action = actions[0];
        assertEquals (ModelAction.DELETE_ALL_INSTANCES, action.action);
        assertEquals (modelName, action.modelName);

        modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName, modelAction.modelName);

        keyPropList = [prop1];
        propList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList, []);
        oldDomainClass = loadGrailsDomainClass(modelName);
        oldGrailsDomainClasses = generateDomainClasses([oldDomainClass])

        keyPropList = [prop1, prop2];
        propList = [prop1, prop2];
        generateModel (modelName, propList, keyPropList, []);

        newDomainClass = loadGrailsDomainClass(modelName);
        newGrailsDomainClasses = generateDomainClasses([newDomainClass])


        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName], newGrailsDomainClasses[modelName]);
        assertEquals (0, actions.size());
    }

    public void testIfRelationTypeIsChangedFromOneToOneToOneToMany()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        def model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        def model2 = createModel (modelName2, propList, keyPropList, [revrel1]);

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass1, oldDomainClass2])

        rel1.cardinality = ModelGenerator.RELATION_TYPE_MANY;
        revrel1.reverseCardinality= ModelGenerator.RELATION_TYPE_MANY;

        model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        model2 = createModel (modelName2, propList, keyPropList, [revrel1]);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass1, newDomainClass2])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName1], newGrailsDomainClasses[modelName1]);
        assertEquals (2, actions.size());
        PropertyAction action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName1, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName1, modelAction.modelName);

        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName2], newGrailsDomainClasses[modelName2]);
        assertEquals (2, actions.size());
        action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName2, action.modelName);

        modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName2, modelAction.modelName);
    }

    public void testIfRelationTypeIsChangedFromOneToManyToOneToOne()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        def model2 = createModel (modelName2, propList, keyPropList, [revrel1]);
                

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass1, oldDomainClass2])

        rel1.cardinality = ModelGenerator.RELATION_TYPE_ONE;
        revrel1.reverseCardinality= ModelGenerator.RELATION_TYPE_ONE;

        model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        model2 = createModel (modelName2, propList, keyPropList, [revrel1]);

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass1, newDomainClass2])


        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName1], newGrailsDomainClasses[modelName1]);
        assertEquals (2, actions.size());
        PropertyAction action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName1, action.modelName);

        ModelAction modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName1, modelAction.modelName);

        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName2], newGrailsDomainClasses[modelName2]);
        assertEquals (2, actions.size());
        action = actions[0];
        assertEquals (PropertyAction.CLEAR_RELATION, action.action);
        assertEquals (modelName2, action.modelName);

        modelAction = actions[1];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName2, modelAction.modelName);
    }

     public void testIfRelationIsDeleted()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:false];
        def model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        def model2 = createModel (modelName2, propList, keyPropList, [revrel1]);

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass1, oldDomainClass2])

        model1 = createModel(modelName1, propList, keyPropList, []);
        model2 = createModel (modelName2, propList, keyPropList, []);

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass1, newDomainClass2])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName1], newGrailsDomainClasses[modelName1]);
        assertEquals (1, actions.size());
        ModelAction modelAction = actions[0];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName1, modelAction.modelName);

        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName2], newGrailsDomainClasses[modelName2]);
        assertEquals (1, actions.size());
        modelAction = actions[0];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName2, modelAction.modelName);
    }


    public void testWithNewRelation()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1, prop2];
        def keyPropList = [prop1, prop2];
        def model1 = createModel(modelName1, propList, keyPropList, []);
        def model2 = createModel (modelName2, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def oldDomainClass1 = loadGrailsDomainClass(modelName1);
        def oldDomainClass2 = loadGrailsDomainClass(modelName2);
        def oldGrailsDomainClasses = generateDomainClasses([oldDomainClass1, oldDomainClass2])

        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_MANY, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        model2 = createModel (modelName2, propList, keyPropList, [revrel1]);
        
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);
        def newDomainClass1 = loadGrailsDomainClass(modelName1);
        def newDomainClass2 = loadGrailsDomainClass(modelName2);
        def newGrailsDomainClasses = generateDomainClasses([newDomainClass1, newDomainClass2])

        def actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName1], newGrailsDomainClasses[modelName1]);
        assertEquals (1, actions.size());
        ModelAction modelAction = actions[0];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName1, modelAction.modelName);
        actions = ExistingDataAnalyzer.createActions (oldGrailsDomainClasses[modelName2], newGrailsDomainClasses[modelName2]);
        assertEquals (1, actions.size());
        modelAction = actions[0];
        assertEquals (ModelAction.GENERATE_RESOURCES, modelAction.action);
        assertEquals (modelName2, modelAction.modelName)
    }

    def generateDomainClasses(List classes)
    {
        def domainClassesMap = [:];
        classes.each{
            domainClassesMap[it.name] = new DefaultGrailsDomainClass(it);    
        }
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesMap.values() as GrailsDomainClass[], domainClassesMap);
        return domainClassesMap;
    }



    def generateModel(String name, List modelProperties, List keyProperties, List relations)
    {
        
        def model = createModel(name, modelProperties, keyProperties, relations);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model);
    }

    def createModel(String name, List modelProperties, List keyProperties, List relations)
    {
        model = new StringWriter();
        modelbuilder = new MarkupBuilder(model);
        modelbuilder.Model(name:name){
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

    def loadGrailsDomainClass(String className)
    {
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader());
        gcl.setShouldRecompile(true);
        gcl.addClasspath(base_directory + ModelGenerator.MODEL_FILE_DIR);
        return gcl.loadClass (className);
    }
}