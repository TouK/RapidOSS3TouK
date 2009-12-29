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
package com.ifountain.rcmdb.util

import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import groovy.lang.MetaClassRegistry.MetaClassCreationHandle
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 18, 2008
* Time: 6:21:25 PM
* To change this template use File | Settings | File Templates.
*/
class ModelUtilsTest extends RapidCmdbTestCase{
    def output_directory = "../testOutput";

    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        if(new File(output_directory).exists())
        {
            FileUtils.deleteDirectory (new File(output_directory));
        }
        new File(output_directory).mkdirs();

        ApplicationHolder.application = new DefaultGrailsApplication([] as Class[],new GroovyClassLoader(this.class.classLoader));
    }

    protected void tearDown() {
        ApplicationHolder.application = null;
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
    }


    public void testDeleteModelArtefacts()
    {
        def modelName = "ModelUtilsModel1";
        new File("${output_directory}/operations").mkdirs();
        new File("${output_directory}/grails-app/views/${modelName}").mkdirs();
        new File("${output_directory}/grails-app/controllers").mkdirs();
        new File("${output_directory}/grails-app/domain").mkdirs();

        new File("${output_directory}/grails-app/domain/${modelName}.groovy").createNewFile();
        new File("${output_directory}/grails-app/controllers/${modelName}Controller.groovy").createNewFile();
        new File("${output_directory}/grails-app/views/${modelName}/add.groovy").createNewFile()
        new File("${output_directory}/grails-app/views/${modelName}/edit.groovy").createNewFile()
        new File("${output_directory}/grails-app/views/${modelName}/list.groovy").createNewFile()
        new File("${output_directory}/grails-app/views/${modelName}/show.groovy").createNewFile()
        new File("${output_directory}/grails-app/views/${modelName}/addTo.groovy").createNewFile()
        new File("${output_directory}/operations/${modelName}Operations.groovy").createNewFile();

        ModelUtils.deleteModelArtefacts(output_directory, modelName);
        assertFalse (new File("${output_directory}/grails-app/domain/${modelName}.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/controllers/${modelName}Controller.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/views/${modelName}/add.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/views/${modelName}/edit.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/views/${modelName}/list.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/views/${modelName}/show.groovy").exists());
        assertFalse (new File("${output_directory}/grails-app/views/${modelName}/addTo.groovy").exists());
        assertFalse (new File("${output_directory}/operations/${modelName}Operations.groovy").exists());
    }

    public void testGenerateOperations()
    {
        generateModelFile("Model1");
        def modelClass = compileClass ("Model1");
        ModelUtils.createModelOperationsFile (modelClass, new File(output_directory), []);
        assertTrue (ModelUtils.getOperationsModelFile(new File(output_directory), modelClass.name).exists());
        Class modelOperations = compileClass(modelClass.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertTrue (AbstractDomainOperation.isAssignableFrom(modelOperations));
    }
    

    public void testGenerateOperationsWithExistingOperationFile()
    {

        def defaultImports = ["com.ifountain.core.domain.annotations.*"]
        String childModelName = "ChildModel";
        def modelOperationsFileContent = """import java.util.net.*;
            class ${childModelName}${ModelUtils.OPERATIONS_CLASS_EXTENSION} extends ${AbstractDomainOperation.class.name} implements Trial{
                def method1()
                {
                    return "method1";
                }
            }""";


        def interfaceContent = "interface Trial {}";
        new File("$output_directory/Trial.groovy").withWriter { w ->
            w.write(interfaceContent);
        }


        File model1OperationsFile = ModelUtils.getOperationsModelFile(new File(output_directory), childModelName);
        model1OperationsFile.getParentFile().mkdirs();
                model1OperationsFile.withWriter { w ->
                    w.write(modelOperationsFileContent);
                }



        generateModelHasParentModel(childModelName, "ParentModel");
        def childModel = compileClass (childModelName);
        def parentModel = compileClass ("ParentModel");
        
        ModelUtils.createModelOperationsFile (childModel, new File(output_directory), defaultImports);
        ModelUtils.createModelOperationsFile (parentModel, new File(output_directory), defaultImports);

        Class operationsClass = compileClass(childModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertEquals (parentModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION, operationsClass.superclass.name);
        def operationsObject = operationsClass.newInstance();
        assertEquals ("method1", operationsObject.method1());
        assertTrue (operationsObject.class.interfaces[0].getName().equals("Trial") || operationsObject.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1OperationsFile.getText (), model1OperationsFile.getText ().indexOf("import java.util.net.*;") >= 0);
        defaultImports.each {
            assertTrue(model1OperationsFile.getText ().indexOf("import $it") >= 0);
        }
        def indexOfFirstAlreadyIncludedImport = model1OperationsFile.getText ().indexOf("${defaultImports[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1OperationsFile.getText ().indexOf("${defaultImports[0]}", indexOfFirstAlreadyIncludedImport) < 0);
    }

    public void testGenerateOperationsWithExistingAndPreviouslyExtendingAnotherModel()
    {

        def defaultImports = ["com.ifountain.core.domain.annotations.*"]
        String modelName = "ChildModel";
        def modelOperationsFileContent = """import java.util.net.*;
            class ${modelName}${ModelUtils.OPERATIONS_CLASS_EXTENSION} extends AnotherClass implements Trial{
                def method1()
                {
                    return "method1";
                }
            }""";


        def interfaceContent = "interface Trial {}";
        new File("$output_directory/Trial.groovy").withWriter { w ->
            w.write(interfaceContent);
        }


        File model1OperationsFile = ModelUtils.getOperationsModelFile(new File(output_directory), modelName);
        model1OperationsFile.getParentFile().mkdirs();
                model1OperationsFile.withWriter { w ->
                    w.write(modelOperationsFileContent);
                }



        generateModelFile(modelName);
        def modelClass = compileClass (modelName);

        ModelUtils.createModelOperationsFile (modelClass, new File(output_directory), defaultImports);

        Class operationsClass = compileClass(modelClass.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertEquals (AbstractDomainOperation.class.name, operationsClass.superclass.name);
        def operationsObject = operationsClass.newInstance();
        assertEquals ("method1", operationsObject.method1());
        assertTrue (operationsObject.class.interfaces[0].getName().equals("Trial") || operationsObject.class.interfaces[1].getName().equals("Trial"));
        assertTrue(model1OperationsFile.getText (), model1OperationsFile.getText ().indexOf("import java.util.net.*;") >= 0);
        defaultImports.each {
            assertTrue(model1OperationsFile.getText ().indexOf("import $it") >= 0);
        }
        def indexOfFirstAlreadyIncludedImport = model1OperationsFile.getText ().indexOf("${defaultImports[0]}");
        assertTrue( indexOfFirstAlreadyIncludedImport >= 0);
        assertFalse( model1OperationsFile.getText ().indexOf("${defaultImports[0]}", indexOfFirstAlreadyIncludedImport) < 0);
    }


    public void testGenerateOperationsWithParentModel()
    {
        generateModelHasParentModel("ChildModel", "ParentModel");
        def childModel = compileClass ("ChildModel");
        def parentModel = compileClass ("ParentModel");
        ModelUtils.createModelOperationsFile (parentModel, new File(output_directory), []);
        ModelUtils.createModelOperationsFile (childModel, new File(output_directory), []);
        
        assertTrue (ModelUtils.getOperationsModelFile(new File(output_directory), childModel.name).exists());
        assertTrue (ModelUtils.getOperationsModelFile(new File(output_directory), parentModel.name).exists());

        Class childOperationClass = compileClass(childModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        Class parentOperationClass = compileClass(parentModel.name+ModelUtils.OPERATIONS_CLASS_EXTENSION);
        assertTrue (AbstractDomainOperation.isAssignableFrom(parentOperationClass));
        assertEquals(parentOperationClass.getName(), childOperationClass.getSuperclass().getName());
        assertEquals(parentOperationClass.getName(), childOperationClass.getSuperclass().getName());
    }

    public void testGenerateModelArtefacts()
    {
        MetaClassCreationHandle prevHandle = GroovySystem.getMetaClassRegistry().getMetaClassCreationHandler();

        try
        {
            def baseDir = ".";
            if(new File(System.getProperty("base.dir", ".")).getCanonicalPath().endsWith("RapidModules"))
            {
                baseDir = "RapidCMDB"
            }
            def modelName = "GenerateModelArtefactsModel1"
            def modelFileContent = """
            class ${modelName}
            {
                static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]
                static propertyConfiguration = [:]
                long id;
                long version
                String name;
            }
            """
            new File("$output_directory/${modelName}.groovy").setText (modelFileContent);
                ExpandoMetaClassCreationHandle handle = new ExpandoMetaClassCreationHandle();
            GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(handle)

            GrailsAwareClassLoader classLoader = new GrailsAwareClassLoader();
            classLoader.addClasspath (output_directory);
            classLoader.addClasspath ("${output_directory}/grails-app/controllers");
            classLoader.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);

            def modelClass = classLoader.loadClass (modelName);


            def grailsDomainClass = new DefaultGrailsDomainClass(modelClass);

            ModelUtils.generateModelArtefacts (grailsDomainClass, baseDir, output_directory);

            assertTrue (new File("${output_directory}/operations/${modelName}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy").exists());
            assertTrue (new File("${output_directory}/grails-app/controllers/${modelName}Controller.groovy").exists());
            assertTrue (new File("${output_directory}/grails-app/views/${grailsDomainClass.getLogicalPropertyName()}/create.gsp").exists());
            assertTrue (new File("${output_directory}/grails-app/views/${grailsDomainClass.getLogicalPropertyName()}/edit.gsp").exists());
            assertTrue (new File("${output_directory}/grails-app/views/${grailsDomainClass.getLogicalPropertyName()}/list.gsp").exists());
            assertTrue (new File("${output_directory}/grails-app/views/${grailsDomainClass.getLogicalPropertyName()}/show.gsp").exists());
            assertTrue (new File("${output_directory}/grails-app/views/${grailsDomainClass.getLogicalPropertyName()}/addTo.gsp").exists());

            classLoader = new GrailsAwareClassLoader();
            classLoader.addClasspath (output_directory);
            classLoader.addClasspath ("${output_directory}/grails-app/controllers");
            classLoader.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
            def cls = classLoader.loadClass("${modelName}Controller")
            assertNotNull (cls);

        }finally
        {
            GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(prevHandle)
        }
    }

    def compileClass(String name)
    {
        GrailsAwareClassLoader cloader = new GrailsAwareClassLoader();
        cloader.addClasspath ("${output_directory}");
        return cloader.loadClass(name);
    }

    def generateModelFile(String name)
    {
        new File(output_directory+"/${name}.groovy").setText ("""
        class ${name}
        {

        }
        """);
    }

    def generateModelHasParentModel(String name, String parentModelName)
    {
        generateModelFile(parentModelName);
        new File(output_directory+"/${name}.groovy").setText ("""
        class ${name} extends ${parentModelName}
        {

        }
        """);
    }
}

