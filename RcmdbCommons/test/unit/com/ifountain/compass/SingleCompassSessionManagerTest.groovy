package com.ifountain.compass

import org.codehaus.groovy.grails.plugins.searchable.compass.test.AbstractSearchableCompassTests
import org.codehaus.groovy.grails.plugins.searchable.test.compass.TestCompassFactory
import org.compass.core.CompassSession

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 5:48:34 PM
 * To change this template use File | Settings | File Templates.
 */
class SingleCompassSessionManagerTest extends AbstractSearchableCompassTests{
    def compass;
    void setUp() {
        compass = TestCompassFactory.getCompass([CompassTestObject]);

    }

    protected void tearDown() {
        compass.close();
        SingleCompassSessionManager.destroy();
    }
    public void testBeginTransaction()
    {
        SingleCompassSessionManager.initialize(compass);
        RapidCompassTransaction tr = SingleCompassSessionManager.beginTransaction();
        Thread.sleep (1000);
        CompassSession session = tr.getSession();
        assertNotNull (tr);
        assertFalse (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        assertFalse (tr.getSession().isClosed());
        tr.commit();
        assertTrue (tr.getSession().isClosed());
        assertFalse (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        tr = SingleCompassSessionManager.beginTransaction();
        assertFalse (tr.getSession().isClosed());
        assertNotSame(session, tr.getSession());
    }

    public void testBeginTransactionWithMaxTransactionCount()
    {
        int maxNumberOfTransactions = 2;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, 0);
        RapidCompassTransaction tr = SingleCompassSessionManager.beginTransaction();
        CompassSession session = tr.getSession();
        assertNotNull (tr);
        assertTrue (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        assertFalse (tr.getSession().isClosed());
        tr.commit();
        assertFalse (SingleCompassSessionManager.getUncommittedTransactions().contains(tr));
        assertFalse (tr.getSession().isClosed());
        tr = SingleCompassSessionManager.beginTransaction();
        assertFalse (tr.getSession().isClosed());
        assertSame(session, tr.getSession());
        tr.commit();
        assertTrue (tr.getSession().isClosed());
    }

    public void testBeginTransactionWithMaxTime()
    {
        int maxNumberOfTransactions = 0;
        int maxWaitTime = 1000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr3 = SingleCompassSessionManager.beginTransaction();
        tr1.commit();
        tr2.commit();
        Thread.sleep (1100);
        assertFalse (tr1.getSession().isClosed());
        assertFalse (tr2.getSession().isClosed());
        assertFalse (tr3.getSession().isClosed());
        tr3.commit();
        Thread.sleep (1100);
        assertTrue (tr1.getSession().isClosed());
        assertTrue (tr2.getSession().isClosed());
        assertTrue (tr3.getSession().isClosed());
        RapidCompassTransaction tr4 = SingleCompassSessionManager.beginTransaction();
        assertNotSame(tr1.getSession(), tr4.getSession());
    }


    public void testBeginTransactionWithMaxTransactionCountAndIfThereExistUncommittedTransactionsDoesnotCloseSession()
    {
        int maxNumberOfTransactions = 2;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, 0);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr3 = SingleCompassSessionManager.beginTransaction();
        tr1.commit();
        RapidCompassTransaction tr4 = SingleCompassSessionManager.beginTransaction();
        assertSame (tr1.getSession(), tr4.getSession());
        assertSame (tr1.getSession(), tr3.getSession());
        assertSame (tr1.getSession(), tr2.getSession());
        assertFalse (tr1.getSession().isClosed());

        tr2.commit();
        tr3.commit();
        tr4.commit();
        assertTrue (tr1.getSession().isClosed());

        RapidCompassTransaction tr5 = SingleCompassSessionManager.beginTransaction();
        assertNotSame(tr1.getSession(), tr5.getSession());
    }

    public void testBeginTransactionWithMaxTransactionCountAndTimer()
    {
        int maxNumberOfTransactions = 200;
        int maxWaitTime = 1000;
        SingleCompassSessionManager.initialize(compass, maxNumberOfTransactions, maxWaitTime);
        RapidCompassTransaction tr1 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr2 = SingleCompassSessionManager.beginTransaction();
        RapidCompassTransaction tr3 = SingleCompassSessionManager.beginTransaction();
        tr1.commit();
        tr2.commit();
        tr3.commit();
        Thread.sleep (1100);
        RapidCompassTransaction tr4 = SingleCompassSessionManager.beginTransaction();
        assertTrue (tr1.getSession().isClosed());
        assertFalse (tr4.getSession().isClosed());
        assertNotSame(tr1.getSession(), tr4.getSession());
        Thread.sleep (1100);
        assertFalse (tr4.getSession().isClosed());
        tr4.commit();
        Thread.sleep (1100);
        assertTrue (tr4.getSession().isClosed());


    }
}