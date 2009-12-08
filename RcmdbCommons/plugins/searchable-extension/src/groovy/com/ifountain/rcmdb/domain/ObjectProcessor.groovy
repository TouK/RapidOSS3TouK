package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.domain.batch.BatchExecutionContext
import com.ifountain.rcmdb.domain.batch.AbstractBatchExecutionManager

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 11, 2009
* Time: 5:53:56 PM
*/
class ObjectProcessor extends Observable implements BatchExecutionContext {
    public static final String EVENT_NAME = "eventName";
    public static final String DOMAIN_OBJECT = "domainObject";
    public static final String UPDATED_PROPERTIES = "updatedProps";

    private static objectProcessor;
    private static Object getInstanceLock = new Object();
    private ObjectProcessorBatchExecutionManager batchManager;
    private ObjectProcessor() {
        batchManager = new ObjectProcessorBatchExecutionManager(this);
    }

    public static ObjectProcessor getInstance() {
        synchronized (getInstanceLock)
        {
            if (objectProcessor == null) {
                objectProcessor = new ObjectProcessor();
            }
            return objectProcessor;
        }
    }

    public void repositoryChanged(String eventName, Object domainObject, Map updateParams) {
        if (countObservers() > 0) {
            Map repositoryChange = [:]
            repositoryChange.put(EVENT_NAME, eventName)
            repositoryChange.put(DOMAIN_OBJECT, domainObject.cloneObject())
            if (updateParams != null) {
                repositoryChange.put(UPDATED_PROPERTIES, Collections.unmodifiableMap(updateParams));
            }
            ObjectProcessorBatchExecutionContext batchExecutionContext = (ObjectProcessorBatchExecutionContext) batchManager.getBatchExecutionContextStorage().get();
            if (batchExecutionContext != null) {
                batchExecutionContext.repositoryChanged(repositoryChange)
            }
            else {
                setChanged()
                notifyObservers(repositoryChange);
            }
        }

    }
    public void repositoryChanged(String eventName, Object domainObject) {
        repositoryChanged(eventName, domainObject, null)
    }

    public void batchStarted() {
        batchManager.batchStarted();
    }

    public void batchFinished() {
        batchManager.batchFinished();
    }

}
class ObjectProcessorBatchExecutionManager extends AbstractBatchExecutionManager {
    private ObjectProcessor objectProcessor;
    protected ObjectProcessorBatchExecutionManager(ObjectProcessor processor) {
        batchExecutionContextStorage = new ThreadLocal<BatchExecutionContext>()
        objectProcessor = processor;
    }
    protected BatchExecutionContext makeStorageInstance() {
        return new ObjectProcessorBatchExecutionContext(objectProcessor);
    }
}

class ObjectProcessorBatchExecutionContext implements BatchExecutionContext {
    private ObjectProcessor objectProcessor;
    private List repositoryChanges = new LinkedList();
    protected ObjectProcessorBatchExecutionContext(ObjectProcessor processor) {
        objectProcessor = processor;
    }
    public void batchStarted() {
    }

    public void batchFinished() {
        repositoryChanges.each {Map repositoryChange ->
            objectProcessor.setChanged();
            objectProcessor.notifyObservers(repositoryChange)
        }
        repositoryChanges.clear();
    }

    public void repositoryChanged(Map repositorChange) {
        repositoryChanges.add(repositorChange);
    }

}