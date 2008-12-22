package com.ifountain.compass.index

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.apache.lucene.index.IndexCommitPoint
import org.apache.lucene.store.Directory
import com.ifountain.compass.CompassTestObject
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.impl.DefaultCompass
import com.ifountain.compass.CompositeDirectoryWrapperProvider

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

        Thread.sleep(300);
        WrapperIndexDeletionPolicy.getPolicies().each{WrapperIndexDeletionPolicy policy ->
            try
            {
                policy.getWrappedPolicy().release();
                fail("Should throw exception since all policies needs to be released");
            }
            catch(Exception)
            {

            }
        }

    }

    public void testTakeGlobalSnapshot()
    {
        compass = TestCompassFactory.getCompass([IndexPolicyTestObject1, IndexPolicyTestObject2]);
        saveToCompass([new IndexPolicyTestObject1(id:0), new IndexPolicyTestObject2(id:1)])
        MockIndexSnapshotAction action = new MockIndexSnapshotAction();

        int snapshotThreadState = 0;
        Thread snapshotThread = Thread.start {
            snapshotThreadState = 1;
            WrapperIndexDeletionPolicy.takeGlobalSnapshot(action);
            snapshotThreadState = 2;
        }

        Thread.sleep(500);
        assertEquals(1, snapshotThreadState);

        int addThreadState = 0;
        Thread addThread = Thread.start {
            addThreadState = 1;
            saveToCompass([new IndexPolicyTestObject1(id:2), new IndexPolicyTestObject2(id:3)])
            addThreadState = 2;
        }

        Thread.sleep(500);
        assertEquals(2, addThreadState);

        synchronized (action.blockingObject)
        {
            action.blockingObject.notifyAll();
        }
        Thread.sleep(200);
        assertEquals(1, action.numberOfCalls);
        synchronized (action.blockingObject)
        {
            action.blockingObject.notifyAll();
        }
        Thread.sleep(500);
        assertEquals(2, action.numberOfCalls);
        assertEquals(2, snapshotThreadState);

        WrapperIndexDeletionPolicy.getPolicies().each{WrapperIndexDeletionPolicy policy ->
            try
            {
                policy.getWrappedPolicy().release();
                fail("Should throw exception since all policies needs to be released");
            }
            catch(Exception)
            {

            }
        }

    }

    public void testTakeGlobalSnapshotReleasesSnapshotsIfExceptionOccurs()
    {
        compass = TestCompassFactory.getCompass([IndexPolicyTestObject1, IndexPolicyTestObject2]);
        saveToCompass([new IndexPolicyTestObject1(id:0), new IndexPolicyTestObject2(id:1)])
        MockIndexSnapshotAction action = new MockIndexSnapshotAction();
        action.exceptionWillBeThrown = new Exception("RuntimeException occurred");
        
        int snapshotThreadState = 0;
        Thread snapshotThread = Thread.start {
            snapshotThreadState = 1;
            try
            {
                WrapperIndexDeletionPolicy.takeGlobalSnapshot(action);
                snapshotThreadState = 2;
            }catch(Exception e)
            {
                snapshotThreadState = 3;
            }
        }

        Thread.sleep(500);
        assertEquals(1, snapshotThreadState);

        synchronized (action.blockingObject)
        {
            action.blockingObject.notifyAll();
        }
        Thread.sleep(200);
        assertEquals(1, action.numberOfCalls);
        synchronized (action.blockingObject)
        {
            action.blockingObject.notifyAll();
        }
        Thread.sleep(500);
        assertEquals(1, action.numberOfCalls);
        assertEquals(3, snapshotThreadState);

        WrapperIndexDeletionPolicy.getPolicies().each{WrapperIndexDeletionPolicy policy ->
            try
            {
                policy.getWrappedPolicy().release();
                fail("Should throw exception since all policies needs to be released");
            }
            catch(Exception)
            {

            }
        }

    }
}






class MockIndexSnapshotAction implements IndexSnapshotAction
{
    public Object blockingObject = new Object();
    int numberOfCalls = 0;
    Exception exceptionWillBeThrown = null;
    public void execute(IndexCommitPoint commitPoint, Directory indexDir)
    {
        synchronized (blockingObject)
        {
            blockingObject.wait();
        }
        numberOfCalls++;
        if(exceptionWillBeThrown != null)
        {
            throw exceptionWillBeThrown;           
        }

    }

}