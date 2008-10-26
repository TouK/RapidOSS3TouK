package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import org.compass.core.CompassSession
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.apache.commons.io.FileUtils
import org.apache.lucene.store.FSDirectory
import org.compass.core.util.concurrent.SingleThreadThreadFactory
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import org.apache.lucene.store.IndexOutput
import java.util.concurrent.ThreadFactory
import com.ifountain.rcmdb.test.util.ClosureRunnerThread
import org.apache.lucene.store.Directory
import org.apache.lucene.store.IndexInput
import org.apache.lucene.store.Lock
import org.apache.lucene.store.LockFactory

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
        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));
    }

    protected void tearDown() {
        super.tearDown();
        if(compass)
        {
            compass.close();
        }
    }

//    public void testWaitsProcessingSpecifiedNumberOfbytes()
//    {
//        System.setProperty("maxNumberOfUnProcessedBytes", "20")
//        System.setProperty("minNumberOfUnProcessedBytes", "10")
//        compass = TestCompassFactory.getCompass([ParentObject, CompassTestObject,CompassTestObject2], null, true)
//        println compass.openSession().beginTransaction().getSession().queryBuilder().queryString("alias:*").toQuery().hits().length();
//        long t = System.currentTimeMillis();
//        for(int i=0; i < 10; i++)
//        {
//
//            CompassSession session = compass.openSession();
//            def tr = session.beginTransaction();
//            if(i % 2 == 0)
//            {
//                session.save (new CompassTestObject(id:4001+i));
//            }
//            else
//            {
//                session.save (new CompassTestObject2(id:4001+i));
//            }
//            tr.commit();
//            session.close();
//        }
//        println compass.openSession().beginTransaction().getSession().queryBuilder().queryString("alias:ParentObject").toQuery().hits().each{
//            println "DDDD:"+it.getData()
//        }
//        println System.currentTimeMillis()-t;
//    }

    public void testWithAFileBiggerThanMaxNumberOfBytes()
    {
        MockFsDirectory dir = new MockFsDirectory(FSDirectory.getDirectory(TestCompassFactory.indexDirectory));
        long maxNumberOfBytes = 2;
        long minNumberOfBytes = 1;
        ExecutorService serv = Executors.newSingleThreadExecutor(new SingleThreadThreadFactory("AsyncMirror Directory Wrapper", false));

        MemoryMirrorDirectoryWrapper wrapper = new MemoryMirrorDirectoryWrapper(dir, 10, maxNumberOfBytes, minNumberOfBytes, serv);
        IndexOutput output = wrapper.createOutput("1.cfs")
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.close();
        assertNotNull(wrapper.createOutput("1.cfs"));
        

    }
    public void testWaitsProcessingSpecifiedNumberOfbytes()
    {
        MockFsDirectory dir = new MockFsDirectory(FSDirectory.getDirectory(TestCompassFactory.indexDirectory));
        dir.blockFileList.add("1.cfs");
        dir.blockFileList.add("2.cfs");
        long maxNumberOfBytes = 6;
        long minNumberOfBytes = 4;
        ExecutorService serv = Executors.newSingleThreadExecutor(new SingleThreadThreadFactory("AsyncMirror Directory Wrapper", false));

        MemoryMirrorDirectoryWrapper wrapper = new MemoryMirrorDirectoryWrapper(dir, 10, maxNumberOfBytes, minNumberOfBytes, serv);
        IndexOutput output = wrapper.createOutput("1.cfs")
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.close();

        output = wrapper.createOutput("2.cfs")
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.writeByte ((byte)1);
        output.close();
        
        ClosureRunnerThread createOutputThread = new ClosureRunnerThread(closure:{
            output = wrapper.createOutput("3.cfs");
            output.writeByte ((byte)1);
        });
        ClosureRunnerThread deleteFileThread = new ClosureRunnerThread(closure:{
            output = wrapper.deleteFile("3.cfs");
        });
        ClosureRunnerThread renameFileThread = new ClosureRunnerThread(closure:{
            output = wrapper.renameFile("3.cfs", "x.cfs");
        });
        createOutputThread.start();
        deleteFileThread.start();
        renameFileThread.start();
        Thread.sleep (1000);
        assertTrue (createOutputThread.isStarted);
        assertFalse (createOutputThread.isFinished);

        assertTrue (deleteFileThread.isStarted);
        assertFalse (deleteFileThread.isFinished);

        assertTrue (renameFileThread.isStarted);
        assertFalse (renameFileThread.isFinished);

        //check threads cannot continue if numberof bytes is not less than min limit
        dir.blockFileList.remove("1.cfs")
        Thread.sleep (1000);
        assertFalse(createOutputThread.isFinished);
        assertFalse(deleteFileThread.isFinished);
        assertFalse(renameFileThread.isFinished);

        ClosureRunnerThread antherNewThread = new ClosureRunnerThread(closure:{
            output = wrapper.renameFile("thisIsAFile.cfs", "x.cfs");
        });
        antherNewThread.start();
        Thread.sleep (1000);
        //check a new thread cannot start if max limit exeeded and number of bytes is not below min limit
        assertTrue (antherNewThread.isStarted);
        assertFalse (antherNewThread.isFinished);

        //check if below limit threads continue to process
        dir.blockFileList.remove("2.cfs")
        Thread.sleep (1000);
        assertTrue(createOutputThread.isFinished);
        assertTrue(deleteFileThread.isFinished);
        assertTrue(renameFileThread.isFinished);
        assertTrue(antherNewThread.isFinished);

    }


    public void testIgnoreUnNeccessaryFiles()
    {
        MockFsDirectory dir = new MockFsDirectory(FSDirectory.getDirectory(TestCompassFactory.indexDirectory));
        long maxNumberOfBytes = 600;
        long minNumberOfBytes = 4;
        ExecutorService serv = Executors.newSingleThreadExecutor(new SingleThreadThreadFactory("AsyncMirror Directory Wrapper", false));

        List validFiles = ["1.cfs", "segments.gen", "1.del", "segments"];
        List files = ["1.fnm", "1.prx", "1.tis", "1.tii", "1.nrm", "1.fdx", "1.fdt","1.frq"];
        files.addAll(validFiles);

        MemoryMirrorDirectoryWrapper wrapper = new MemoryMirrorDirectoryWrapper(dir, 10, maxNumberOfBytes, minNumberOfBytes, serv);
        files.each {
            def out = wrapper.createOutput(it);
            out.writeByte ((byte)1);
            out.close();
        }

        files.each {
            wrapper.touchFile(it);
        }
        files.each {
            wrapper.renameFile(it, it);
        }
        files.each {
            wrapper.deleteFile (it);
        }
        serv.shutdown();
        assertEquals (validFiles.size(), dir.deletedFileList.size());
        assertEquals (validFiles.size(), dir.touchFileList.size());
        assertEquals (validFiles.size(), dir.renameFileList.size());
        assertEquals (validFiles.size(), dir.createOutputFileList.size());
        assertTrue (dir.deletedFileList.containsAll(validFiles));
        assertTrue (dir.touchFileList.containsAll(validFiles));
        assertTrue (dir.renameFileList.containsAll(validFiles));
        assertTrue (dir.createOutputFileList.containsAll(validFiles));
    }
}

class MockFsDirectory extends Directory
{
    FSDirectory dir;
    List blockFileList = [];
    List deletedFileList = new ArrayList();
    List renameFileList = new ArrayList();
    List touchFileList = new ArrayList();
    List createOutputFileList = new ArrayList();
    def MockFsDirectory(FSDirectory dir) {
        this.dir = dir;
        setLockFactory(dir.getLockFactory());
    }

    public String[] list() {
        return dir.list();
    }

    public boolean fileExists(String s) {
        return dir.fileExists(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long fileModified(String s) {
        return dir.fileModified(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void touchFile(String s) {
        touchFileList.add(s);
        dir.touchFile (s);
    }

    public void deleteFile(String s) {
        deletedFileList.add (s);
        dir.deleteFile (s);
    }

    public void renameFile(String s, String s1) {
        renameFileList.add (s);
        dir.renameFile(s, s1);
    }

    public long fileLength(String s) {
        return dir.fileLength(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IndexOutput createOutput(String s) {
        createOutputFileList.add (s);
        while(blockFileList.contains(s))
        {
            Thread.sleep (1);
        }
        return dir.createOutput(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IndexInput openInput(String s) {
        return dir.openInput(s);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() {
        dir.close();
    }

    public IndexInput openInput(String s, int i) {
        return dir.openInput(s, i);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Lock makeLock(String s) {
        return dir.makeLock(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void clearLock(String s) {
        dir.clearLock(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void setLockFactory(LockFactory lockFactory) {
        dir.setLockFactory(lockFactory);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public LockFactory getLockFactory() {
        return dir.getLockFactory();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String getLockID() {
        return dir.getLockID();    //To change body of overridden methods use File | Settings | File Templates.
    }


}