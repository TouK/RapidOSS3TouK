package com.ifountain.compass

import org.codehaus.groovy.grails.plugins.searchable.compass.test.AbstractSearchableCompassTests
import org.codehaus.groovy.grails.plugins.searchable.test.compass.TestCompassFactory

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 25, 2008
 * Time: 3:53:16 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidcompassConcurrencyTest extends AbstractSearchableCompassTests
{
    def compass

    void setUp() {
    }

    protected void tearDown() {
        compass.close();
    }

    public void testConcurrency()
    {
//        def internalCompas = TestCompassFactory.getCompass([CompassTestObject])
//        int batchSize = 10;
//        long maxWaitTime = 10;
//        compass = new RapidCompass(internalCompas, batchSize, maxWaitTime);
//        def obj = new CompassTestObject(id:1, prop1:"prop1val");
//        saveToCompass(obj);
//        def cl1 = {
//            loadFromCompass(CompassTestObject, obj.id)
//        }
//        def threads = [];
//        for(int i=0; i < 20; i++)
//        {
//            def thr = new CompassConcurrencyThreadThread(closure:cl1);
//            threads += thr;
//            thr.start();
//        }
//
//        threads.each
//        {
//            it.join();
//            assertFalse (it.isExceptionThrown);
//        }

    }
}

class CompassConcurrencyThreadThread extends Thread{
    def closure;
    def isExceptionThrown = false;
    public void run()
    {
        try
        {
            for(int i =0; i < 1000; i++)
            {
                closure();
            }
        }catch(Throwable t)
        {
            t.printStackTrace();
            isExceptionThrown = true;
        }

    }
}