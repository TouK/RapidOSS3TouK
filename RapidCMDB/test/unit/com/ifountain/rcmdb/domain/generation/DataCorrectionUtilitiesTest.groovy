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

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import model.*
import com.ifountain.rcmdb.util.ModelUtils;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 16, 2008
 * Time: 11:58:02 AM
 * To change this template use File | Settings | File Templates.
 */
class DataCorrectionUtilitiesTest extends RapidCmdbWithCompassTestCase
{
    def static base_directory = "../testoutput/base/";
    def static temp_directory = "../testoutput/temp/";
    public void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        def templatesDir = "src/templates"
        if(new File(".").getCanonicalPath().endsWith("RapidModules"))
        {
            ModelGenerator.getInstance().initialize (base_directory, temp_directory, "RcmdbCommons");
            templatesDir = "RapidCMDB/"+templatesDir
        }
        else
        {
            ModelGenerator.getInstance().initialize (base_directory, temp_directory, ".");
        }

        FileUtils.deleteDirectory (new File(base_directory).parentFile);
        new File(base_directory).mkdirs();
        FileUtils.copyDirectoryToDirectory(new File(templatesDir), new File(base_directory+"/src"));
    }

    public void testBeforeReloadWithModelDeleteAllInstances()
    {
        String model1Name = "Class1";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def propList = [prop1];
        def keyPropList = [prop1];
        def model1 = createModel(model1Name, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        FileUtils.copyDirectoryToDirectory(new File(temp_directory+"/grails-app"), new File(base_directory));

        def oldClass1 = loadGrailsDomainClass(model1Name, base_directory);

        initialize([oldClass1, ModelAction, PropertyAction], [])
        def oldDomainClasses = [:];
        oldDomainClasses[model1Name] = this.ga.getDomainClass(model1Name);

        oldClass1.'add'(prop1:"obj1");
        assertNotNull (oldClass1.'get'(prop1:"obj1"));

        model1 = createModel (model1Name, propList, [], []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);

        FileUtils.deleteDirectory (new File(base_directory+"operations"))
        def newClass1 = loadGrailsDomainClass(model1Name, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newClass1])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses );
        assertNull (oldClass1.'get'(prop1:"obj1"));
        assertTrue (new File(base_directory+"operations/${model1Name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
    }

    public void testBeforeReloadWithNewModel()
    {
        String model1Name = "Class1";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def propList = [prop1];
        def keyPropList = [prop1];

        initialize([ModelAction, PropertyAction], [])

        String model1 = createModel (model1Name, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);

        FileUtils.deleteDirectory (new File(base_directory+"operations"))
        def newClass1 = loadGrailsDomainClass(model1Name, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newClass1])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), [:], new ArrayList(newDomainClasses.values()), newDomainClasses );
        assertTrue (new File(base_directory+"operations/${model1Name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
    }


    public void testModelStartedToBeAParent()
    {
        String modelChild1 = "Class1";
        String modelParent = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1];
        def keyPropList = [prop1];
        def model1 = createModel(modelParent, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        def parentClass1 = loadGrailsDomainClass(modelParent, temp_directory);
        initialize([parentClass1, ModelAction, PropertyAction], [], true)
        def oldDomainClasses = [:];
        oldDomainClasses[modelParent] = this.ga.getDomainClass(modelParent);

        def parentClassInstance1 = parentClass1.'add'(prop1:"parentInstance1");
        assertFalse (parentClassInstance1.hasErrors());



        def model2 = createModel (modelChild1, modelParent, [], [], []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);
        def newChildClass1 = loadGrailsDomainClass(modelChild1, temp_directory);
        def newDomainClasses = generateDomainClasses([parentClass1, newChildClass1])
        
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses);
        destroy();
        gcl = new GroovyClassLoader();
        newChildClass1 = loadGrailsDomainClass(modelChild1, temp_directory);
        parentClass1 = loadGrailsDomainClass(modelParent, temp_directory);
        initialize([parentClass1, newChildClass1, ModelAction, PropertyAction], [], true)
        DataCorrectionUtilities.dataCorrectionAfterReloadStep ();

        def parentClassInstance1AfterReload = parentClass1.'get'(prop1:"parentInstance1");
        assertEquals (parentClassInstance1.id, parentClassInstance1AfterReload.id);
    }


    public void testBeforeReloadWithDeletedModel()
    {
        String model1Name = "Class1";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def propList = [prop1];
        def keyPropList = [prop1];
        def model1 = createModel(model1Name, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        FileUtils.copyDirectoryToDirectory(new File(temp_directory+"/grails-app"), new File(base_directory));

        def oldClass1 = loadGrailsDomainClass(model1Name, base_directory);

        initialize([oldClass1, ModelAction, PropertyAction], [])
        def oldDomainClasses = [:];
        oldDomainClasses[model1Name] = this.ga.getDomainClass(model1Name);

        oldClass1.'add'(prop1:"obj1");
        assertNotNull (oldClass1.'get'(prop1:"obj1"));

        new File(base_directory+"grails-app/conrollers").mkdirs();
        new File(base_directory+"grails-app/views").mkdirs();
        new File(base_directory+"operations").mkdirs();
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(), [:]);
        assertNull (oldClass1.'get'(prop1:"obj1"));
        assertFalse (new File(base_directory+"grails-app/controllers/${model1Name}Controller.groovy").exists());
        assertFalse (new File(base_directory+"grails-app/views/${model1Name.substring(0,1).toLowerCase()+model1Name.substring(1)}/list.gsp").exists());
        assertFalse (new File(base_directory+"operations/${model1Name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
    }

    public void testBeforeReloadWithPropertyAction()
    {
        String model1Name = "Class1";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        def model1 = createModel(model1Name, propList, keyPropList, []);
        println model1;
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        FileUtils.copyDirectoryToDirectory(new File(temp_directory+"/grails-app"), new File(base_directory));

        def oldClass1 = loadGrailsDomainClass(model1Name, base_directory);
        initialize([oldClass1, ModelAction, PropertyAction], [])
        def oldDomainClasses = [:];
        oldDomainClasses[model1Name] = this.ga.getDomainClass(model1Name);

        prop2.type = ModelGenerator.NUMBER_TYPE;
        propList = [prop1, prop2];
        keyPropList = [prop1];
        model1 = createModel (model1Name, propList, keyPropList, []);
        println model1;
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);

        FileUtils.deleteDirectory (new File(base_directory+"operations"))
        def newClass1 = loadGrailsDomainClass(model1Name, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newClass1])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses );
        assertTrue (new File(base_directory+"operations/${model1Name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
        assertEquals(1, PropertyAction.count());
        assertEquals(PropertyAction.SET_DEFAULT_VALUE, PropertyAction.list()[0].action);
        assertEquals(prop2.name, PropertyAction.list()[0].propName);
    }

    public void testAfterReloadWithPropertyAction()
    {
        String model1Name = "Class1";
        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        def model1 = createModel(model1Name, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        FileUtils.copyDirectoryToDirectory(new File(temp_directory+"/grails-app"), new File(base_directory));

        def oldClass1 = loadGrailsDomainClass(model1Name, base_directory);
        this.initialize([oldClass1, ModelAction, PropertyAction], [], true)
        def oldDomainClasses = [:];
        oldDomainClasses[model1Name] = this.ga.getDomainClass(model1Name);

        println oldClass1.'add'(prop1:"prop1Value1", prop2:"prop2Value1").errors;
        oldClass1.'add'(prop1:"prop1Value2", prop2:"prop2Value2");
        oldClass1.'add'(prop1:"prop1Value3", prop2:"prop2Value3");


        prop2.type = ModelGenerator.NUMBER_TYPE;
        propList = [prop1, prop2];
        keyPropList = [prop1];
        model1 = createModel (model1Name, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);

        def newClass1 = loadGrailsDomainClass(model1Name, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newClass1])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses );
        this.destroy();
        gcl = new GroovyClassLoader(this.class.classLoader);
        newClass1 = loadGrailsDomainClass(model1Name, temp_directory);
        this.initialize([newClass1, ModelAction, PropertyAction], [], true)
        DataCorrectionUtilities.dataCorrectionAfterReloadStep ();
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
        assertEquals(0, PropertyAction.count());
        def instance = newClass1.'get'(prop1:"prop1Value1");
        assertEquals (new Long(1), instance.prop2);
        instance = newClass1.'get'(prop1:"prop1Value2");
        assertEquals (new Long(1), instance.prop2);
        instance = newClass1.'get'(prop1:"prop1Value3");
        assertEquals (new Long(1), instance.prop2);
    }

    public void testAfterReloadWithPropertyActionRelatedOfParentModel()
    {

        String modelChild1 = "Class1";
        String modelParent = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop3 = [name:"prop3", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];

        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        def model1 = createModel(modelParent, propList, keyPropList, []);
        def model2 = createModel(modelChild1, modelParent, [prop3], [], []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);
        def parentClass1 = loadGrailsDomainClass(modelParent, temp_directory);
        def childClass1 = loadGrailsDomainClass(modelChild1, temp_directory);
        initialize([parentClass1, childClass1, ModelAction, PropertyAction], [], true)
        def oldDomainClasses = [:];
        oldDomainClasses[modelParent] = this.ga.getDomainClass(modelParent);
        oldDomainClasses[modelChild1] = this.ga.getDomainClass(modelChild1);


        childClass1.'add'(prop1:"prop1Value1", prop2:"prop2Value1", prop3:"prop3Value1");
        childClass1.'add'(prop1:"prop1Value2", prop2:"prop2Value2", prop3:"prop3Value2");
        childClass1.'add'(prop1:"prop1Value3", prop2:"prop2Value3", prop3:"prop3Value3");
        assertEquals (3, childClass1.'list'().size());

        prop2.type = ModelGenerator.NUMBER_TYPE;
        propList = [prop1, prop2];
        keyPropList = [prop1];
        model1 = createModel (modelParent, propList, keyPropList, []);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);

        def newParentClass1 = loadGrailsDomainClass(modelParent, temp_directory, true);
        def newChildClass1 = loadGrailsDomainClass(modelChild1, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newParentClass1, newChildClass1])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses );
        this.destroy();
        gcl = new GroovyClassLoader(this.class.classLoader);
        newParentClass1 = loadGrailsDomainClass(modelParent, temp_directory);
        newChildClass1 = loadGrailsDomainClass(modelChild1, temp_directory);
        this.initialize([newParentClass1, newChildClass1, ModelAction, PropertyAction], [], true)
        DataCorrectionUtilities.dataCorrectionAfterReloadStep ();
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
        assertEquals(0, PropertyAction.count());
        assertEquals (3, newChildClass1.'list'().size());
        def instance = newChildClass1.'get'(prop1:"prop1Value1");
        assertEquals (new Long(1), instance.prop2);
        assertEquals ("prop3Value1", instance.prop3);
        instance = newChildClass1.'get'(prop1:"prop1Value2");
        assertEquals (new Long(1), instance.prop2);
        assertEquals ("prop3Value2", instance.prop3);
        instance = newChildClass1.'get'(prop1:"prop1Value3");
        assertEquals (new Long(1), instance.prop2);
        assertEquals ("prop3Value3", instance.prop3);
    }


    public void testAfterReloadWithPropertyRelationAction()
    {
        String modelName1 = "Class1";
        String modelName2 = "Class2";

        def prop1 = [name:"prop1", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];
        def prop2 = [name:"prop2", type:ModelGenerator.STRING_TYPE, blank:false, defaultValue:"1"];


        def propList = [prop1, prop2];
        def keyPropList = [prop1];
        def rel1 = [name:"rel1",  reverseName:"revrel1", toModel:modelName2, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_MANY, isOwner:true];
        def revrel1 = [name:"revrel1",  reverseName:"rel1", toModel:modelName1, cardinality:ModelGenerator.RELATION_TYPE_ONE, reverseCardinality:ModelGenerator.RELATION_TYPE_ONE, isOwner:false];
        def model1 = createModel(modelName1, propList, keyPropList, [rel1]);
        def model2 = createModel (modelName2, propList, keyPropList, [revrel1]);

        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);
        FileUtils.copyDirectoryToDirectory(new File(temp_directory+"/grails-app"), new File(base_directory));

        def oldClass1 = loadGrailsDomainClass(modelName1, base_directory);
        def oldClass2 = loadGrailsDomainClass(modelName2, base_directory);
        this.initialize([oldClass1, oldClass2,ModelAction, PropertyAction], [], true)
        def oldDomainClasses = [:];
        oldDomainClasses[modelName1] = this.ga.getDomainClass(modelName1);
        oldDomainClasses[modelName2] = this.ga.getDomainClass(modelName2);

        def class1Instance1 = oldClass1.'add'(prop1:"class1Instance1", prop2:"prop2Value2");
        def class1Instance2 = oldClass1.'add'(prop1:"class1Instance2", prop2:"prop2Value3");
        def class2Instance1 = oldClass2.'add'(prop1:"class2Instance1", prop2:"prop2Value3", revrel1:class1Instance1);
        def class2Instance2 = oldClass2.'add'(prop1:"class2Instance2", prop2:"prop2Value3");
        assertFalse (class1Instance1.hasErrors())
        assertFalse (class2Instance1.hasErrors())
        assertEquals(class1Instance1.id, class2Instance1.revrel1.id);


        rel1.reverseCardinality = ModelGenerator.RELATION_TYPE_ONE;
        revrel1.cardinality= ModelGenerator.RELATION_TYPE_ONE;
        model1 = createModel (modelName1, propList, keyPropList, [rel1]);
        model2 = createModel (modelName2, propList, keyPropList, [revrel1]);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model1);
        ModelGenerator.getInstance().generateSingleModelFileWithoutValidation(model2);

        def newClass1 = loadGrailsDomainClass(modelName1, temp_directory, true);
        def newClass2 = loadGrailsDomainClass(modelName2, temp_directory, true);
        def newDomainClasses = generateDomainClasses([newClass1, newClass2])
        DataCorrectionUtilities.dataCorrectionBeforeReloadStep (new File(base_directory).getPath(), new File(temp_directory).getPath(), oldDomainClasses, new ArrayList(newDomainClasses.values()), newDomainClasses );
        this.destroy();
        gcl = new GroovyClassLoader(this.class.classLoader);
        newClass1 = loadGrailsDomainClass(modelName1, temp_directory);
        newClass2 = loadGrailsDomainClass(modelName2, temp_directory);
        this.initialize([newClass1, newClass2, ModelAction, PropertyAction], [], true)
        DataCorrectionUtilities.dataCorrectionAfterReloadStep ();
        assertTrue(ModelAction.search("alias:*").results.isEmpty());
        assertEquals(0, PropertyAction.count());
        class1Instance1 = newClass1.'get'(prop1:"class1Instance1");
        assertNotNull (class1Instance1);
        class2Instance1 = newClass2.'get'(prop1:"class2Instance1");
        assertNotNull (class2Instance1);
        class1Instance2 = newClass1.'get'(prop1:"class1Instance2");
        class2Instance2 = newClass2.'get'(prop1:"class2Instance2");
        assertNull(class1Instance1.rel1);
        assertNull(class2Instance1.revrel1);
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

    def createModel(String name,  List modelProperties, List keyProperties, List relations)
    {
        return createModel(name, null, modelProperties, keyProperties, relations);
    }

    def createModel(String name, String parentName, List modelProperties, List keyProperties, List relations)
    {
        def model = new StringWriter();
        def modelbuilder = new MarkupBuilder(model);
        def props = [name:name];
        if(parentName != null)
        {
            props["parentModel"] = parentName;
        }
        modelbuilder.Model(props){
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

    def loadGrailsDomainClass(String className, String directory, boolean loadFromNewClassLoader = false)
    {
        def cloader = gcl;
        if(loadFromNewClassLoader)
        {
            cloader = new GroovyClassLoader();
        }
        cloader.addClasspath(directory +ModelGenerator.MODEL_FILE_DIR);
        return cloader.loadClass (className);
    }
}