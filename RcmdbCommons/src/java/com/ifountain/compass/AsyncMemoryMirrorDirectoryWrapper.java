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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.RAMOutputStream;
import org.compass.core.util.concurrent.SingleThreadThreadFactory;

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
public class AsyncMemoryMirrorDirectoryWrapper extends Directory {

    private static final Log log = LogFactory.getLog(AsyncMemoryMirrorDirectoryWrapper.class);

    private Directory dir;

    private RAMDirectory ramDir;

    private ExecutorService executorService;

    private long awaitTermination;

    private long numberOfUnProcessedBytes = 0;
    private Object byteProcessLock = new Object();
    private Object waitLock = new Object();
    private long maxNumberOfUnProcessedBytes, minNumberOfUnProcessedBytes;

    public AsyncMemoryMirrorDirectoryWrapper(Directory dir, long awaitTermination, long maxNumberOfUnProcessedBytes, long minNumberOfUnProcessedBytes, ExecutorService executorService) throws IOException {
        this.dir = dir;
        this.maxNumberOfUnProcessedBytes = maxNumberOfUnProcessedBytes;
        this.minNumberOfUnProcessedBytes = minNumberOfUnProcessedBytes;
        this.ramDir = new RAMDirectory(dir);
        this.executorService = executorService;
        this.awaitTermination = awaitTermination;
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
                        logAsyncErrorMessage("delete [" + name + "]");
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
                        logAsyncErrorMessage("rename from[" + from + "] to[" + to + "]");
                    }
                }
            });
        }
    }

    public void touchFile(final String name) throws IOException {
        try {
            checkWaitingBytesToBeProcessed();
        }
        catch (InterruptedException e) {
            return;
        }
        ramDir.touchFile(name);
        if(willBeProcessed(name))
        {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        dir.touchFile(name);
                    } catch (IOException e) {
                        logAsyncErrorMessage("touch [" + name + "]");
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

    public void checkWaitingBytesToBeProcessed() throws InterruptedException {
         synchronized (waitLock) {
            if (numberOfUnProcessedBytes > maxNumberOfUnProcessedBytes) {
                waitLock.wait();
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
        if (log.isDebugEnabled()) {
            log.debug("Directory [" + dir + "] shutsdown, waiting for [" + awaitTermination +
                    "] minutes for tasks to finish executing");
        }
        executorService.shutdown();
        if (!executorService.isTerminated()) {
            try {
                if (!executorService.awaitTermination(60 * awaitTermination, TimeUnit.SECONDS)) {
                    logAsyncErrorMessage("wait for async tasks to shutdown");
                }
            } catch (InterruptedException e) {
                logAsyncErrorMessage("wait for async tasks to shutdown");
            }
        }
        dir.close();
    }

    public IndexInput openInput(String name) throws IOException {
        return ramDir.openInput(name);
    }

    public IndexOutput createOutput(String name) throws IOException {
        return new AsyncMemoryMirrorIndexOutput(name, (RAMOutputStream) ramDir.createOutput(name));
    }

    protected boolean willBeProcessed(String name)
    {
        return !(name.endsWith(".fnm") || name.endsWith(".prx") || name.endsWith(".tis") || name.endsWith(".tii") || name.endsWith(".nrm") || name.endsWith(".fdx") || name.endsWith(".fdt")|| name.endsWith(".frq"));
    }

    private void logAsyncErrorMessage(String message) {
        log.error("Async wrapper for [" + dir + "] failed to " + message);
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
                            changeNumberOfBytes(-ramIndexOutput.length());
                            checkNotifyWaitingThreads();
                        } catch (IOException e) {
                            logAsyncErrorMessage("write [" + name + "]");
                        }
                    }
                });
            }
        }
    }
}
