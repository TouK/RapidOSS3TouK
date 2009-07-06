package com.ifountain.rcmdb.domain.backup

import com.ifountain.compass.CompassTestObject
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import com.ifountain.rcmdb.domain.BackupAction
import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.apache.commons.io.FileUtils
import org.compass.core.spi.InternalCompass


/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 19, 2008
 * Time: 11:19:56 AM
 * To change this template use File | Settings | File Templates.
 */
class BackupActionTest extends AbstractSearchableCompassTests
{
    InternalCompass compass;

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

    public void testBackup()
    {

        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));

        def destDir = new File("../tesoutput");
        if(destDir.exists())
        {
            FileUtils.deleteDirectory (destDir);
        }
        destDir.mkdirs();
        compass = TestCompassFactory.getCompass([CompassTestObject]);
        BackupAction action = new BackupAction(compass, [CompassTestObject], destDir.getPath());
        CompassTestObject savedCompassObject1 = new CompassTestObject(id:0, prop1:"prop1ValueForInstance1");
        saveToCompass([savedCompassObject1]);
        assertEquals(1, WrapperIndexDeletionPolicy.getPolicies().size());
        WrapperIndexDeletionPolicy policyBeforeSecondAdd = WrapperIndexDeletionPolicy.getPolicies()[0];
        CompassTestObject savedCompassObject2 = new CompassTestObject(id:1, prop1:"prop1ValueForInstance2");
        saveToCompass([savedCompassObject2]);
        assertEquals(1, WrapperIndexDeletionPolicy.getPolicies().size());
        WrapperIndexDeletionPolicy policyAfterSecondAdd = WrapperIndexDeletionPolicy.getPolicies()[0];
        assertSame (policyBeforeSecondAdd.getWrappedPolicy(), policyAfterSecondAdd.getWrappedPolicy());
        
        policyAfterSecondAdd.snapshot (action);

        def compassObjectIndexDir = new File("${destDir.getPath()}/${CompassTestObject.simpleName.toLowerCase()}");
        assertTrue (compassObjectIndexDir.exists())

        //This object does not exist in backup
        CompassTestObject savedCompassObject3 = new CompassTestObject(id:2);
        saveToCompass([savedCompassObject3]);
        compass.close();

        //We close dcompass and copy backup to index directory and start compass on this directory
        //to see if backup is correct
        FileUtils.copyDirectory (destDir, new File(TestCompassFactory.indexDirectory+"/index"));

        compass = TestCompassFactory.getCompass([CompassTestObject], null, true);
        CompassTestObject obj1 = loadFromCompass(CompassTestObject, savedCompassObject1.id);
        CompassTestObject obj2 = loadFromCompass(CompassTestObject, savedCompassObject2.id);
        CompassTestObject obj3 = null
        try
        {
            obj3 = loadFromCompass(CompassTestObject, savedCompassObject3.id);
        }
        catch(Throwable t)
        {
        }
        assertNotNull (obj1);
        assertNotNull (obj2);
        assertNull (obj3);
        assertEquals (savedCompassObject1.prop1, obj1.prop1);
        assertEquals (savedCompassObject2.prop1, obj2.prop1);

    }

    public void testBackupWithRestartedCompass()
    {

        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));

        def destDir = new File("../backupDir");
        if(destDir.exists())
        {
            FileUtils.deleteDirectory (destDir);
        }
        destDir.mkdirs();
        compass = TestCompassFactory.getCompass([CompassTestObject], [], true);

        CompassTestObject savedCompassObject1 = new CompassTestObject(id:0, prop1:"prop1ValueForInstance1");
        saveToCompass([savedCompassObject1]);

        compass.close();
        WrapperIndexDeletionPolicy.clearPolicies();
        compass = TestCompassFactory.getCompass([CompassTestObject], [], true);

        BackupAction action = new BackupAction(compass, [CompassTestObject], destDir.getPath());
        WrapperIndexDeletionPolicy.takeGlobalSnapshot(action);
        def compassObjectIndexDir = new File("${destDir.getPath()}/${CompassTestObject.simpleName.toLowerCase()}");
        assertTrue (compassObjectIndexDir.exists())

        compass.close();

        FileUtils.copyDirectory (destDir, new File(TestCompassFactory.indexDirectory+"/index"));
        compass = TestCompassFactory.getCompass([CompassTestObject], [], true);
        CompassTestObject obj1 = loadFromCompass(CompassTestObject, savedCompassObject1.id)
        assertNotNull (obj1);






    }
}
