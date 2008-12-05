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
package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
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
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory);
        Class operationClass = manager.loadOperation();
        AbstractDomainOperation operInstance = operationClass.newInstance();
        assertEquals (stringWillBeReturned, operInstance.method1());
        assertSame (operationClass, manager.getOperationClass())
        assertTrue (manager.operationClassMethods.containsKey("method1"));

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
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory);
        Class operationClass = manager.loadOperation();
        AbstractDomainOperation operInstance = operationClass.newInstance();
        assertEquals (stringWillBeReturned, operInstance.method1());
        assertSame (operationClass, manager.getOperationClass())
        assertTrue (manager.operationClassMethods.containsKey("method1"));
    }

    public void testLoadOperationThrowsExceptionIfOperationFileDoesnotExist()
    {
        Class domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory);
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
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory);
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
        DomainOperationManager manager = new DomainOperationManager(domainClass, operationsDirectory);
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