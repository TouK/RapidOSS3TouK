package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.apache.log4j.Logger

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 6:12:27 PM
 * To change this template use File | Settings | File Templates.
 */
class ReloadOperationsMethodTest extends RapidCmdbTestCase{
    def static reloadOperationsMessages = [];
    def baseDir = "../testOutput";

    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        reloadOperationsMessages.clear();
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
        GroovyClassLoader classLoader = new GroovyClassLoader();
        def subClass1 = classLoader.parseClass("""
            class SubDomainClass1
            {
                def static reloadOperations(boolean reloadSubClasses)
                {
                    ${this.class.name}.addOperationMessage("${reloadOpMessage}SubDomainClass1"+reloadSubClasses)
                }
            }
        """);
        def subClass2 = classLoader.parseClass("""
            class SubDomainClass2
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
        assertEquals(0, reloadOperationsMessages.size());

        method.invoke(domainClass, [true] as Object[]);

        assertNotSame (classAfterFirstReload, manager.getOperationClass());
        assertEquals(2, reloadOperationsMessages.size());
        assertTrue (reloadOperationsMessages.contains(reloadOpMessage+"SubDomainClass1"+"false"));
        assertTrue (reloadOperationsMessages.contains(reloadOpMessage+"SubDomainClass2"+"false"));
    }
    

    def static addOperationMessage(message)
    {
        reloadOperationsMessages += message;        
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