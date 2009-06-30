package com.ifountain.rcmdb.datasource

import org.apache.log4j.Logger
import com.ifountain.core.datasource.BaseListeningAdapter
import script.CmdbScript
import datasource.BaseListeningDatasource

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 3, 2009
* Time: 9:49:02 AM
* To change this template use File | Settings | File Templates.
*/
public class ListeningAdapterRunner implements AdapterStateProvider
{
    Logger logger = Logger.getLogger("scripting");
    Object stopCalledLock = new Object();
    Object stateLock = new Object();
    Long datasourceId;
    BaseListeningAdapter adapter;
    Date lastStateChangeTime = new Date();
    String state = NOT_STARTED;
    ListeningAdapterObserver observer;
    private boolean stopCalled = false;

    public ListeningAdapterRunner(Long dsId)
    {
        this.datasourceId = dsId;
    }
    public boolean isSubscribed()
    {
        return adapter != null && adapter.isSubscribed();
    }
    protected Object createScriptObject(CmdbScript script, BaseListeningDatasource listeningDatasource)
    {
        try {

            def scriptParams = [:]
            scriptParams.datasource = listeningDatasource;
            return CmdbScript.getScriptObject(script, scriptParams);
        }
        catch (e) {
            throw new Exception("Error creating listening adapter script object. Reason: ${e.getMessage()}", e);
        }
    }

    public void stop() {
        setStopCalled(true);
        setState(STOPPING);
        logger.debug("Stopping listening adapter with datasource id ${datasourceId}");
        try
        {
            adapter.unsubscribe();
            adapter.deleteObservers();
            adapter = null;
        } catch (Exception e)
        {
            adapter = null;
            setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
        }

    }
    public void cleanUp() {
        if (observer) {
            try {
                observer.getScriptInstance().cleanUp();
                def stateBeforeCleanup = getState();
                if (stateBeforeCleanup != STOPPED_WITH_EXCEPTION) {
                    setState(AdapterStateProvider.STOPPED);
                }
            }
            catch (e) {
                logger.warn("Error during script clean up. Reason: " + e.getMessage(), e)
                setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
            }
        }

    }

    public void start(BaseListeningDatasource listeningDatasource) throws Exception {
        def scriptObject = null;
        setState(INITIALIZING);
        logger.debug("Starting listening adapter with datasource id ${datasourceId}");
        CmdbScript script = listeningDatasource.listeningScript;
        if (script && script.type == CmdbScript.LISTENING) {
            scriptObject = createScriptObject(script, listeningDatasource);
            def scriptLogger = scriptObject.logger;
            try {
                scriptObject.run();
            }
            catch (Throwable e) {
                setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
                throw ListeningAdapterException.listeningScriptExecutionException(datasourceId, script.name, "run", e);
            }
            def params = null;
            try
            {
                params = scriptObject.getParameters();
            } catch (Throwable e)
            {
                setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
                throw ListeningAdapterException.listeningScriptExecutionException(datasourceId, script.name, "getParameters", e);
            }
            adapter = (BaseListeningAdapter) listeningDatasource.getListeningAdapter(params, scriptLogger);
            if (adapter == null)
            {
                setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
                throw ListeningAdapterException.noAdapterDefined(datasourceId)
            }
            observer = new ListeningAdapterObserver(scriptObject, scriptLogger);
            adapter.addObserver(observer);
        }
        else {
            setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
            throw ListeningAdapterException.noListeningScript(datasourceId);
        }
        try {
            scriptObject.init();
            setState(AdapterStateProvider.INITIALIZED);
        }
        catch (Throwable e) {
            setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
            throw ListeningAdapterException.listeningScriptExecutionException(datasourceId, listeningDatasource.listeningScript.name, "init", e);
        }
        if (!isStopCalled()) {
            try {
                adapter.subscribe();
                setState(AdapterStateProvider.STARTED);
            }
            catch (Throwable e) {
                setState(AdapterStateProvider.STOPPED_WITH_EXCEPTION);
                throw ListeningAdapterException.couldNotSubscribed(datasourceId, e);
            }
        }

    }

    public void setState(String state) {
        synchronized (stateLock) {
            this.state = state;
            lastStateChangeTime = new Date();
        }
    }

    public String getState() {
        synchronized (stateLock) {
            return state;
        }
    }

    public boolean isFree() {
        synchronized (stateLock)
        {
            return getState() == NOT_STARTED || getState() == STOPPED_WITH_EXCEPTION || getState() == STOPPED;
        }
    }

    public boolean isRunning()
    {
        synchronized (stateLock)
        {
            return getState() == INITIALIZED || getState() == INITIALIZING || getState() == STARTED;
        }
    }

    public Date getLastStateChangeTime() {
        synchronized (stateLock) {
            return (Date) lastStateChangeTime.clone();
        }
    }

    public boolean isStopCalled() {
        synchronized (stopCalledLock) {
            return stopCalled;
        }
    }
    public void setStopCalled(boolean stopCalled) {
        synchronized (stopCalledLock) {
            this.stopCalled = stopCalled;
        }
    }

}