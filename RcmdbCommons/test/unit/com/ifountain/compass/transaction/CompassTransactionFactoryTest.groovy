package com.ifountain.compass.transaction

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.codehaus.groovy.grails.commons.GrailsApplication
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import com.ifountain.compass.CompassTestObject
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.compass.DefaultCompassConfiguration

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 3, 2009
* Time: 9:10:36 AM
* To change this template use File | Settings | File Templates.
*/
class CompassTransactionFactoryTest extends AbstractSearchableCompassTests {
    Compass compass;

    public void setUp() {
        super.setUp()
    }

    public void tearDown() {
        super.tearDown();
        if (compass)
        {
            compass.close();
        }
    }

    public void testCreateTransaction()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        CompassTransactionFactory factory = new CompassTransactionFactory(compass);
        CompassTransaction tr = factory.createTransaction();

        //save an object
        CompassTestObject objToBeSaved = new CompassTestObject(id:0);
        tr.getSession().save (objToBeSaved);
        tr.commit();
        assertTrue (tr.getSession().isClosed());

        tr = factory.createTransaction();
        assertEquals("Tr is committed object should be added to repo",1, tr.getSession().queryBuilder().queryString ("id:0").toQuery().hits().length());
        tr.commit();

        //test rollback
        tr = factory.createTransaction();
        tr.getSession().delete (objToBeSaved);
        tr.rollback();
        assertTrue (tr.getSession().isClosed());
        try{
            tr.commit()
            fail("Should throw exception since transaction already closed");
        }
        catch(Exception e)
        {
        }
        tr = factory.createTransaction();
        assertEquals("Tr is rollbacked object should not be deleted from repo",1, tr.getSession().queryBuilder().queryString ("id:0").toQuery().hits().length());
        tr.commit();

    }

    public void testCreateGlobalTransaction()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([CompassTestObject])
        ApplicationHolder.application = application;
        compass = TestCompassFactory.getCompass(application, null, false, DefaultCompassConfiguration.getDefaultSettings(null));
        CompassTransactionFactory factory = new CompassTransactionFactory(compass);
        CompassGlobalTransaction tr = factory.createGlobalTransaction();

        //save an object
        CompassTestObject objToBeSaved = new CompassTestObject(id:0);
        tr.getSession().save (objToBeSaved);
        tr.commit();

        CompassTransaction searchTransaction = factory.createTransaction();
        assertEquals("Global transaction commit method will not do anything",0, searchTransaction.getSession().queryBuilder().queryString ("id:0").toQuery().hits().length());
        searchTransaction.commit();

        tr.commitGlobalTransaction();
        assertTrue (tr.getSession().isClosed());
        searchTransaction = factory.createTransaction();
        assertEquals("Global transaction commitGlobalTransaction method will commit transaction",1, searchTransaction.getSession().queryBuilder().queryString ("id:0").toQuery().hits().length());
        searchTransaction.commit();

        //test rollback
        tr = factory.createGlobalTransaction();
        tr.getSession().delete (objToBeSaved);
        tr.rollbackGlobalTransaction();
        assertTrue (tr.getSession().isClosed());
        try{
            tr.commitGlobalTransaction()
            fail("Should throw exception since transaction already closed");
        }
        catch(Exception e)
        {
        }
        searchTransaction = factory.createTransaction();
        assertEquals("Global transaction rollbackGlobalTransaction method will not rollback transaction",0, searchTransaction.getSession().queryBuilder().queryString ("id:0").toQuery().hits().length());
        searchTransaction.commit();

        tr = factory.createGlobalTransaction();
        tr.rollback();
        try
        {
            tr.rollbackGlobalTransaction();
        }catch(Exception e)
        {
            fail("Should not throw exception since rollback method do nothing");
        }

    }

}