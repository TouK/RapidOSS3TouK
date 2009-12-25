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
package com.ifountain.rcmdb.domain.operation

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 5:05:27 PM
 * To change this template use File | Settings | File Templates.
 */
class DomainOperationManagerTest extends RapidCmdbTestCase{

    String operationsDirectory = "../testOutput";

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        FileUtils.deleteDirectory (new File(operationsDirectory));
        new File(operationsDirectory).mkdirs()
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testLoadOperation()
    {
        Class domainClass = createSimpleDomainClass();
        def stringWillBeReturned = "method1"
        new File("$operationsDirectory/${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX}.groovy").setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method1()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        def defaultMethods = [defaultMethod1:[method:{param1-> return param1}, isStatic:false], defaultMethod2:[method:{param1, param2-> return param1+param2}, isStatic:true]];
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, defaultMethods, this.class.classLoader);
        Class operationClass = manager.loadOperation();
        AbstractDomainOperation operInstance = operationClass.newInstance();
        assertEquals (stringWillBeReturned, operInstance.method1());
        assertSame (operationClass, manager.getOperationClass())
        assertTrue (manager.operationClassMethods.containsKey("method1"));

        String defaultMethodParam1 = "param1";
        String defaultMethodParam2 = "param2";
        assertEquals (defaultMethodParam1, operInstance.defaultMethod1(defaultMethodParam1));
        assertEquals (defaultMethodParam1+defaultMethodParam2, operInstance.defaultMethod2(defaultMethodParam1, defaultMethodParam2));
        assertEquals (defaultMethodParam1, operInstance.defaultMethod1(defaultMethodParam1));

        try{
            operationClass.defaultMethod1(defaultMethodParam1);
            fail("Should throw exception because defaultMethod1 is not static");
        }
        catch(MissingMethodException e)
        {
        }

        assertEquals (defaultMethodParam1+defaultMethodParam2, operationClass.defaultMethod2(defaultMethodParam1, defaultMethodParam2));

        new File("$operationsDirectory/${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX}.groovy").setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method2()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        Class reloadedOperationClass = manager.loadOperation();
        operInstance = reloadedOperationClass.newInstance();
        assertEquals (stringWillBeReturned, operInstance.method2());
        assertNotSame (reloadedOperationClass, operationClass)
        assertSame (reloadedOperationClass, manager.getOperationClass())
        assertFalse (manager.operationClassMethods.containsKey("method1"));
        assertTrue (manager.operationClassMethods.containsKey("method2"));

        assertEquals (defaultMethodParam1, operInstance.defaultMethod1(defaultMethodParam1));

        try{
            reloadedOperationClass.defaultMethod1(defaultMethodParam1);
            fail("Should throw exception because defaultMethod1 is not static");
        }
        catch(MissingMethodException e)
        {
        }

        assertEquals (defaultMethodParam1+defaultMethodParam2, reloadedOperationClass.defaultMethod2(defaultMethodParam1, defaultMethodParam2));

    }


    public void testGetOperationClassReturnsParentOperationClassIfItsOperationDoesNotExist()
    {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        Class parentDomainClass = classLoader.parseClass("""
            class ParentDomainClass
            {

            }
        """);
        def stringWillBeReturned = "method1"
        new File("$operationsDirectory/${parentDomainClass.name}${DomainOperationManager.OPERATION_SUFFIX}.groovy").setText (
                """
                    class  ${parentDomainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method1()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        DomainOperationManager parentManager = new DomainOperationManager(parentDomainClass, operationsDirectory, null, [:], this.class.classLoader);
        Class parentOperationClass = parentManager.loadOperation();
        Map methods = parentManager.getOperationClassMethods();

        Class childDomainClass = classLoader.parseClass("""
            class ChildDomainClass
            {

            }
        """);
        DomainOperationManager childManager = new DomainOperationManager(childDomainClass, operationsDirectory, parentManager, [:], this.class.classLoader);

        assertSame(parentOperationClass, childManager.getOperationClass());
        assertSame(methods, childManager.getOperationClassMethods());
    }

    public void testLoadOperationWithAClassWithPackagename()
    {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        Class domainClass = classLoader.parseClass("""
            package packagename1.packagename2.packagename3;
            class TrialDomainClass
            {

            }
        """);
        def stringWillBeReturned = "method1"
        def oprFile = new File("$operationsDirectory/packagename1/packagename2/packagename3/${domainClass.simpleName}${DomainOperationManager.OPERATION_SUFFIX}.groovy");
        oprFile.parentFile.mkdirs();
        oprFile.setText (
                """
                    package packagename1.packagename2.packagename3;
                    class  ${domainClass.simpleName}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method1()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        Class operationClass = manager.loadOperation();
        AbstractDomainOperation operInstance = operationClass.newInstance();
        assertEquals (stringWillBeReturned, operInstance.method1());
        assertSame (operationClass, manager.getOperationClass())
        assertTrue (manager.operationClassMethods.containsKey("method1"));
    }


    public void testLoadOperationThrowsExceptionIfOperationLoadDisabled()
    {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        Class domainClass = classLoader.parseClass("""
            package packagename1.packagename2.packagename3;
            class TrialDomainClass
            {

            }
        """);
        def stringWillBeReturned = "method1"
        def oprFile = new File("$operationsDirectory/packagename1/packagename2/packagename3/${domainClass.simpleName}${DomainOperationManager.OPERATION_SUFFIX}.groovy");
        oprFile.parentFile.mkdirs();
        oprFile.setText (
                """
                    package packagename1.packagename2.packagename3;
                    class  ${domainClass.simpleName}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method1()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        
        try
        {
            DomainOperationManager.disableLoadOperation();
            manager.loadOperation();
            fail("Should throw exception");
        }
        catch(DomainOperationLoadException ex)
        {
            assertEquals("Operation Loading is Disabled.",ex.getMessage());
            assertNull(ex.getCause());
        }
        finally{
            DomainOperationManager.enableLoadOperation();
        }
        assertEquals ("if no operation class exist ${AbstractDomainOperation.name} should be returned", AbstractDomainOperation.class.name, manager.getOperationClass().name);
    }

    public void testLoadOperationThrowsExceptionIfOperationFileDoesnotExist()
    {
        Class domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        try
        {
            manager.loadOperation();
            fail("Should throw exception");
        }
        catch(DomainOperationLoadException ex)
        {
            FileNotFoundException fex = ex.getCause();
            assertEquals(manager.getOperationFile().path, fex.getMessage());
        }
        assertEquals ("if no operation class exist ${AbstractDomainOperation.name} should be returned", AbstractDomainOperation.class.name, manager.getOperationClass().name);
    }



    public void testLoadOperationThrowsExceptionIfOperationClassIsSmaeWithPrevious()
    {
        Class domainClass = createSimpleDomainClass();
        def stringWillBeReturned = "method1"
        def operationName = "${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX}".toString()
        new File("$operationsDirectory/${operationName}.groovy").setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                        def method1()
                        {
                            return "${stringWillBeReturned}";
                        }
                    }
                """
        )
        def defaultMethods = [defaultMethod1:[method:{param1-> return param1}, isStatic:false], defaultMethod2:[method:{param1, param2-> return param1+param2}, isStatic:true]];
        def parentClassLoader = new GroovyClassLoader(this.class.classLoader);
        parentClassLoader.addClasspath (operationsDirectory);
        Class classLoadedByParent = parentClassLoader.loadClass (operationName);
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, defaultMethods, parentClassLoader);
        Class loadedOprClassForFirstTime = manager.loadOperation();
        assertSame(classLoadedByParent, loadedOprClassForFirstTime);
        try{
            manager.loadOperation()
            fail("Should throw exception since same operation class file is loaded.");
        }
        catch(DomainOperationLoadException ex)
        {
            assertEquals (DomainOperationLoadException.sameOperationClassIsLoaded().getMessage(), ex.getMessage());
        }

        assertSame (loadedOprClassForFirstTime, manager.getOperationClass());


    }

    public void testLoadOperationThrowsExceptionIfOperationFileDoesnotExistButLoadsParentOperationFile()
    {
        Class domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        try
        {
            manager.loadOperation();
            fail("Should throw exception");
        }
        catch(DomainOperationLoadException ex)
        {
            FileNotFoundException fex = ex.getCause();
            assertEquals(manager.getOperationFile().path, fex.getMessage());
        }
    }

    public void testLoadOperationThrowsExceptionIfOperationIsNotInstanceOfAbstractOperation()
    {
        Class domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        manager.getOperationFile().setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX}
                    {
                    }
                """
        )
        try
        {
            manager.loadOperation();
            fail("Should throw exception");
        }
        catch(DomainOperationLoadException ex)
        {
            assertEquals(DomainOperationLoadException.shouldInheritAbstractDomainOperation().getMessage(), ex.getMessage());
        }
    }

    public void testLoadOperationThrowsExceptionIfOperationCannotBeCompiled()
    {
        Class domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory, null, [:], this.class.classLoader);
        manager.getOperationFile().setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                """
        )
        try
        {
            manager.loadOperation();
            fail("Should throw exception");
        }
        catch(DomainOperationLoadException ex)
        {
        }
    }

    def createSimpleDomainClass()
    {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        return classLoader.parseClass("""
            class TrialDomainClass
            {

            }
        """);
    }
}