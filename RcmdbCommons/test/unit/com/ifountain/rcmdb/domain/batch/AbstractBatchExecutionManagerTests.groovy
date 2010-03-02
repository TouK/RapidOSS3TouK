package com.ifountain.rcmdb.domain.batch

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 2, 2010
* Time: 9:50:27 AM
*/
class AbstractBatchExecutionManagerTests extends RapidCmdbTestCase {

    public void testBatchExecutionContextCannotAccessTheSameExecutionContexInBatchFinishedMethod() {
        TempBatchExecutionManager executionManager = new TempBatchExecutionManager();
        executionManager.batchStarted();
        ThreadLocal storage1 = executionManager.getBatchExecutionContextStorage();
        TempBatchExecutionContext executionContext = storage1.get();
        executionManager.batchFinished();
        TempBatchExecutionContext contextInBatchFinished = executionContext.contextInBatchFinished;
        assertNull(contextInBatchFinished)
    }

}
class TempBatchExecutionManager extends AbstractBatchExecutionManager {
    public TempBatchExecutionManager(){
        batchExecutionContextStorage = new ThreadLocal<BatchExecutionContext>()
    }
    protected BatchExecutionContext makeStorageInstance() {
        return new TempBatchExecutionContext(this);
    }
}

class TempBatchExecutionContext implements BatchExecutionContext {
    def manager;
    def contextInBatchFinished;
    public TempBatchExecutionContext(executionManager) {
        manager = executionManager;
    }
    public void batchStarted() {
    }

    public void batchFinished() {
        contextInBatchFinished = manager.getBatchExecutionContextStorage().get();
    }

}