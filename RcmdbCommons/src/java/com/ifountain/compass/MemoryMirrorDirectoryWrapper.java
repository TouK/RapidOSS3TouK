/*
 * Copyright 2004-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifountain.compass;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.*;

/**
 * Wraps a Lucene {@link org.apache.lucene.store.Directory} with
 * an in memory directory which mirrors it asynchronously.
 * <p/>
 * The original directory is read into memory when this wrapper
 * is constructed. All read realted operations are performed
 * against the in memory directory. All write related operations
 * are performed against the in memeory directory and are scheduled
 * to be performed against the original directory (using {@link ExecutorService}).
 * Locking is performed using the in memory directory.
 * <p/>
 * NOTE: This wrapper will only work in cases when either the
 * index is read only (i.e. only search operations are performed
 * against it), or when there is a single instance which updates
 * the directory.
 *
 * @author kimchy
 */
public class MemoryMirrorDirectoryWrapper extends Directory {

    private static final Log log = LogFactory.getLog(MemoryMirrorDirectoryWrapper.class);

    private Directory dir;

    private RAMDirectory ramDir;

    private ExecutorService executorService;

    private long awaitTermination;

    private long numberOfUnProcessedBytes = 0;
    private Object byteProcessLock = new Object();
    private Object waitLock = new Object();
    private long maxNumberOfUnProcessedBytes, minNumberOfUnProcessedBytes;

    public MemoryMirrorDirectoryWrapper(Directory dir, long awaitTermination, long maxNumberOfUnProcessedBytes, long minNumberOfUnProcessedBytes, ExecutorService executorService) throws IOException {
        log.info("Initializing FileAndMemory storage type for directory " + dir.toString() + " awaitTermination :" + awaitTermination + " maxNumberOfUnProcessedBytes:"+maxNumberOfUnProcessedBytes+" minNumberOfUnProcessedBytes:"+minNumberOfUnProcessedBytes);
        this.dir = dir;
        this.maxNumberOfUnProcessedBytes = maxNumberOfUnProcessedBytes;
        this.minNumberOfUnProcessedBytes = minNumberOfUnProcessedBytes;
        this.ramDir = new RAMDirectory(dir);
        this.ramDir.setLockFactory(dir.getLockFactory());
        this.executorService = executorService;
        this.awaitTermination = awaitTermination;
    }

    public LockFactory getLockFactory() {
        return ramDir.getLockFactory();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void deleteFile(final String name) throws IOException {
        try {
            checkWaitingBytesToBeProcessed();
        }
        catch (InterruptedException e) {
            return;
        }
        ramDir.deleteFile(name);
        if(willBeProcessed(name))
        {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        dir.deleteFile(name);
                    } catch (IOException e) {
                        logAsyncErrorMessage("delete [" + name + "]", e);
                    }
                }
            });
        }
    }

    public boolean fileExists(String name) throws IOException {
        return ramDir.fileExists(name);
    }

    public long fileLength(String name) throws IOException {
        return ramDir.fileLength(name);
    }

    public long fileModified(String name) throws IOException {
        return ramDir.fileModified(name);
    }

    public String[] list() throws IOException {
        return ramDir.list();
    }

    public void renameFile(final String from, final String to) throws IOException {
        try {
            checkWaitingBytesToBeProcessed();
        }
        catch (InterruptedException e) {
            return;
        }
        ramDir.renameFile(from, to);
        if(willBeProcessed(from))
        {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        dir.renameFile(from, to);
                    } catch (IOException e) {
                        logAsyncErrorMessage("rename from[" + from + "] to[" + to + "]", e);
                    }
                }
            });
        }
    }

    public void touchFile(final String name) throws IOException {
        ramDir.touchFile(name);
        if(willBeProcessed(name))
        {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        dir.touchFile(name);
                    } catch (IOException e) {
                        logAsyncErrorMessage("touch [" + name + "]", e);
                    }
                }
            });
        }
    }

    public Lock makeLock(String name) {
        return ramDir.makeLock(name);
    }

    public void checkNotifyWaitingThreads(){
        if (numberOfUnProcessedBytes <= minNumberOfUnProcessedBytes) {
            synchronized (waitLock) {
                waitLock.notifyAll();
            }
        }
    }

    public synchronized void checkWaitingBytesToBeProcessed() throws InterruptedException {
         synchronized (waitLock) {
            if (numberOfUnProcessedBytes >= maxNumberOfUnProcessedBytes) {
                log.info("waiting to process compass data queue. Current queue lnegth is "+numberOfUnProcessedBytes+" bytes at "+System.currentTimeMillis());
                waitLock.wait();
                log.info("continue to process compass data queue. Current queue lnegth is "+numberOfUnProcessedBytes+" bytes at "+System.currentTimeMillis());
            }
        }
    }

    public void changeNumberOfBytes(long numberOfBytes){
        synchronized (byteProcessLock) {
            numberOfUnProcessedBytes += numberOfBytes;
        }
    }

    public void close() throws IOException {
        ramDir.close();
        log.info("Directory [" + dir + "] shutsdown, waiting for [" + awaitTermination +"] minutes for tasks to finish executing");
        executorService.shutdown();
        if (!executorService.isTerminated()) {
            try {
                if (!executorService.awaitTermination(60 * awaitTermination, TimeUnit.SECONDS)) {
                    logAsyncErrorMessage("wait for async tasks to shutdown");
                }
            } catch (InterruptedException e) {
                logAsyncErrorMessage("wait for async tasks to shutdown", e);
            }
        }
        dir.close();
    }

    public IndexInput openInput(String name) throws IOException {
        return ramDir.openInput(name);
    }

    public IndexOutput createOutput(String name) throws IOException {
        try {
            checkWaitingBytesToBeProcessed();
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AsyncMemoryMirrorIndexOutput(name, (RAMOutputStream) ramDir.createOutput(name));
    }

    protected boolean willBeProcessed(String name)
    {
        return !(name.endsWith(".fnm") || name.endsWith(".prx") || name.endsWith(".tis") || name.endsWith(".tii") || name.endsWith(".nrm") || name.endsWith(".fdx") || name.endsWith(".fdt")|| name.endsWith(".frq"));
    }

    private void logAsyncErrorMessage(String message) {
        log.error("Async wrapper for [" + dir + "] failed to " + message);
    }
     private void logAsyncErrorMessage(String message,Throwable e) {
        log.error("Async wrapper for [" + dir + "] failed to " + message + ". Reason : "+e.getMessage(),e);
    }

    public class AsyncMemoryMirrorIndexOutput extends IndexOutput {

        private String name;

        private RAMOutputStream ramIndexOutput;

        public AsyncMemoryMirrorIndexOutput(String name, RAMOutputStream ramIndexOutput) {
            this.name = name;
            this.ramIndexOutput = ramIndexOutput;
        }

        public void writeByte(byte b) throws IOException {
            ramIndexOutput.writeByte(b);
        }

        public void writeBytes(byte[] b, int offset, int length) throws IOException {
            ramIndexOutput.writeBytes(b, offset, length);
        }

        public void seek(long size) throws IOException {
            ramIndexOutput.seek(size);
        }

        public long length() throws IOException {
            return ramIndexOutput.length();
        }

        public long getFilePointer() {
            return ramIndexOutput.getFilePointer();
        }

        public void flush() throws IOException {
            ramIndexOutput.flush();
        }


        public void close() throws IOException {
            ramIndexOutput.close();
            if (willBeProcessed(name)) {
                try {
                    checkWaitingBytesToBeProcessed();
                }
                catch (InterruptedException e) {
                    return;
                }
                changeNumberOfBytes(ramIndexOutput.length());
                executorService.submit(new Runnable() {
                    public void run() {
                        try {
                            IndexOutput indexOutput = dir.createOutput(name);
                            ramIndexOutput.writeTo(indexOutput);
                            indexOutput.close();

                        } catch (IOException e) {
                            logAsyncErrorMessage("write [" + name + "]", e);
                        }
                        synchronized (byteProcessLock)
                        {
                            changeNumberOfBytes(-ramIndexOutput.length());
                            checkNotifyWaitingThreads();
                        }
                    }
                });
            }
        }
    }
}
