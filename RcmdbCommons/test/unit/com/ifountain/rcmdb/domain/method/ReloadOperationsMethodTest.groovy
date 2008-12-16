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
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.apache.log4j.Logger
import com.ifountain.rcmdb.test.util.TestDatastore

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 6:12:27 PM
 * To change this template use File | Settings | File Templates.
 */
class ReloadOperationsMethodTest extends RapidCmdbTestCase{
    def baseDir = "../testOutput";
    public static dsKey = ReloadOperationsMethodTest.name;
    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        TestDatastore.put (dsKey, [])
    }


    public void testReloadOperations()
    {

        def domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, baseDir);
        manager.getOperationFile().setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                    }
                """
        )

        Class classBeforeReload = manager.loadOperation();


        ReloadOperationsMethod method = new ReloadOperationsMethod(domainClass.metaClass, [], manager, Logger.getRootLogger());
        method.invoke(domainClass, null);

        assertNotSame (classBeforeReload, manager.getOperationClass());
    }

    public void testReloadOperationsWithSubClasses()
    {
        String reloadOpMessage = "reloadOperations called"
        GroovyClassLoader classLoader = new GroovyClassLoader(this.class.classLoader);
        def subClass1 = classLoader.parseClass("""
            class SubDomainClass1ReloadOperationsMethodTest
            {
                def static reloadOperations(boolean reloadSubClasses)
                {
                    ${this.class.name}.addOperationMessage("${reloadOpMessage}SubDomainClass1"+reloadSubClasses)
                }
            }
        """);
        def subClass2 = classLoader.parseClass("""
            class SubDomainClass2ReloadOperationsMethodTest
            {
                def static reloadOperations(boolean reloadSubClasses)
                {
                    ${this.class.name}.addOperationMessage("${reloadOpMessage}SubDomainClass2"+reloadSubClasses)
                }
            }
        """);
        def domainClass = createSimpleDomainClass();
        DomainOperationManager manager = new DomainOperationManager(domainClass, baseDir);
        manager.getOperationFile().setText (
                """
                    class  ${domainClass.name}${DomainOperationManager.OPERATION_SUFFIX} extends ${AbstractDomainOperation.class.name}
                    {
                    }
                """
        )

        Class classBeforeReload = manager.loadOperation();


        ReloadOperationsMethod method = new ReloadOperationsMethod(domainClass.metaClass, [subClass1, subClass2], manager, Logger.getRootLogger());

        method.invoke(domainClass, [false] as Object[]);

        Class classAfterFirstReload = manager.getOperationClass()
        assertNotSame (classBeforeReload, classAfterFirstReload);
        assertEquals(0, TestDatastore.get(dsKey).size());

        method.invoke(domainClass, [true] as Object[]);

        assertNotSame (classAfterFirstReload, manager.getOperationClass());
        assertEquals(2, TestDatastore.get(dsKey).size());
        assertTrue (TestDatastore.get(dsKey).contains(reloadOpMessage+"SubDomainClass1"+"false"));
        assertTrue (TestDatastore.get(dsKey).contains(reloadOpMessage+"SubDomainClass2"+"false"));
    }
    

    def static addOperationMessage(message)
    {
        TestDatastore.get(dsKey).add(message);
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