package com.ifountain.rcmdb.domain.batch
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Dec 7, 2009
 * Time: 4:40:54 PM
 */
public abstract class AbstractBatchExecutionManager implements BatchExecutionContext {
    protected ThreadLocal<BatchExecutionContext> batchExecutionContextStorage;
    
    public void batchStarted() {
        BatchExecutionContext batchExecutionContext = batchExecutionContextStorage.get();
        if (batchExecutionContext == null) {
            batchExecutionContext = makeStorageInstance();
            batchExecutionContextStorage.set(batchExecutionContext);
        }
        batchExecutionContext.batchStarted();
    }
    public void batchFinished() {
        BatchExecutionContext batchExecutionContext = batchExecutionContextStorage.get();
        if (batchExecutionContext != null) {
            batchExecutionContext.batchFinished();
        }
        batchExecutionContextStorage.set(null);
    }
    public ThreadLocal getBatchExecutionContextStorage(){
        return batchExecutionContextStorage;
    }

    protected abstract BatchExecutionContext makeStorageInstance();
}