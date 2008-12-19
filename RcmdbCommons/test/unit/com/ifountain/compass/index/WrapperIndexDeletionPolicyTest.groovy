package com.ifountain.compass.index

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.apache.lucene.index.IndexCommitPoint
import org.apache.lucene.store.Directory
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.impl.DefaultCompass

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 19, 2008
 * Time: 12:21:29 PM
 * To change this template use File | Settings | File Templates.
 */
class WrapperIndexDeletionPolicyTest extends AbstractSearchableCompassTests{
    DefaultCompass compass;

    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
        if(compass)
        {
            compass.close();
        }
    }
    public void testSanpshotDoesNotBlockWriteOperations()
    {
        compass = TestCompassFactory.getCompass([CompassTestObject]);
        MockIndexSnapshotAction action = new MockIndexSnapshotAction();
        def savedObjects = [];
        for(int i=0; i < 100; i++)
        {
            CompassTestObject savedCompassObject = new CompassTestObject(id:i);
            savedObjects.add(savedCompassObject);
        }
        saveToCompass(savedObjects);

        WrapperIndexDeletionPolicy propertyDelitionPolicy = WrapperIndexDeletionPolicy.getPolicies()[0];
        Thread t = Thread.start {
            propertyDelitionPolicy.snapshot (action);
        }

        for(int i=0 ; i < 100; i++)
        {
            CompassTestObject obj = loadFromCompass(CompassTestObject, i);
            assertNotNull (obj);
        }

        savedObjects = [];
        for(int i=0; i < 100; i++)
        {
            CompassTestObject savedCompassObject = new CompassTestObject(id:i+100);
            savedObjects.add(savedCompassObject);
        }
        saveToCompass(savedObjects);

        for(int i=0 ; i < 200; i++)
        {
            CompassTestObject obj = loadFromCompass(CompassTestObject, i);
            assertNotNull (obj);
        }

        synchronized (action.blockingObject)
        {
            action.blockingObject.notifyAll();
        }

    }
}


class MockIndexSnapshotAction implements IndexSnapshotAction
{
    public Object blockingObject = new Object();
    public void execute(IndexCommitPoint commitPoint, Directory indexDir)
    {
        synchronized (blockingObject)
        {
            blockingObject.wait();
        }
    }

}