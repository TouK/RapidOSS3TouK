package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.compass.core.CompassSession
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 24, 2008
 * Time: 2:39:49 PM
 * To change this template use File | Settings | File Templates.
 */
class MemoryDirectoryWrapperTest  extends AbstractSearchableCompassTests{
    Compass compass;

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown();
        if(compass)
        {
            compass.close();
        }
    }

    public void testWaitsProcessingSpecifiedNumberOfbytes()
    {
        System.setProperty("maxNumberOfUnProcessedBytes", "20")
        System.setProperty("minNumberOfUnProcessedBytes", "10")
        compass = TestCompassFactory.getCompass([CompassTestObject,CompassTestObject2], null, true)
        println compass.openSession().beginTransaction().getSession().queryBuilder().queryString("alias:*").toQuery().hits().length();
        long t = System.currentTimeMillis();
        for(int i=0; i < 10000; i++)
        {

            CompassSession session = compass.openSession();
            def tr = session.beginTransaction();
            session.save (new CompassTestObject(id:4001+i));
            tr.commit();
            session.close();
        }
        println System.currentTimeMillis()-t;    
    }


}