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
public class ListeningAdapterRunner
{
    Logger logger = Logger.getLogger("scripting");
    Object adapterLock = new Object();
    Object stateLock = new Object();
    public static final int NOT_STARTED = 0;
    public static final int INITIALIZING = 1;
    public static final int INITIALIZED = 2;
    public static final int STARTED = 3;
    public static final int STOPPING = 4;
    public static final int STOPPED_WITH_EXCEPTION = 5;
    public static final int STOPPED = 6;
    String adapterName;
    int state = NOT_STARTED;
    BaseListeningAdapter adapter;
    ListeningAdapterObserver observer;
    public ListeningAdapterRunner(String adapterName)
    {
        this.adapterName = adapterName;
    }

    public void setState(int state)
    {
        synchronized (stateLock)
        {
            this.state = state
        }
    }
    public boolean isRunning()
    {
        synchronized (stateLock)
        {
            return getState() == INITIALIZED || getState() == INITIALIZING || getState() == STARTED;
        }
    }
    public boolean isSubscribed()
    {
        synchronized (adapterLock)
        {
            return adapter != null && adapter.isSubscribed();
        }
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

    public void stop() throws Exception {
        synchronized (stateLock)
        {
            if(getState() == STOPPING)
            {
                throw ListeningAdapterException.stoppingStateException(adapterName, "stop");    
            }
            else if (getState() == NOT_STARTED || getState() == STOPPED || getState() == STOPPED_WITH_EXCEPTION)
            {
                throw ListeningAdapterException.adapterAlreadyStoppedException(adapterName);
            }
            setState(STOPPING);
        }
        logger.debug("Stopping listening adapter ${adapterName}");
        try
        {
            adapter.unsubscribe();
            adapter.deleteObservers();
            try {
                observer.getScriptInstance().cleanUp();
            }
            catch (e) {
                throw new Exception("Error during script clean up. Reason: " + e.getMessage(), e)
            }
            setState(STOPPED);
        } catch (Exception e)
        {
            setState(STOPPED_WITH_EXCEPTION);
        }

    }
    public void start(BaseListeningDatasource listeningDatasource) throws Exception {
        def scriptObject = null;
        synchronized (adapterLock)
        {
            if(getState() == STOPPING)
            {
                throw ListeningAdapterException.stoppingStateException(adapterName, "start");
            }
            else if (state != NOT_STARTED && state != STOPPED_WITH_EXCEPTION && state != STOPPED)
            {
                throw ListeningAdapterException.adapterAlreadyStartedException(adapterName);
            }
            logger.debug("Starting listening adapter ${adapterName}");
            setState(INITIALIZING);
            CmdbScript script = listeningDatasource.listeningScript;
            if (script && script.type == CmdbScript.LISTENING) {
                scriptObject = createScriptObject(script, listeningDatasource);
                def scriptLogger = scriptObject.logger;
                try {
                    scriptObject.run();
                }
                catch (Exception e) {
                    setState(STOPPED_WITH_EXCEPTION);
                    throw ListeningAdapterException.listeningScriptExecutionException(adapterName, script.name, "run", e);
                }
                def params = null;
                try
                {
                    params = scriptObject.getParameters();
                } catch (Exception e)
                {
                    setState(STOPPED_WITH_EXCEPTION);
                    throw ListeningAdapterException.listeningScriptExecutionException(adapterName, script.name, "getParameters", e);
                }
                adapter = (BaseListeningAdapter) listeningDatasource.getListeningAdapter(params, scriptLogger);
                if (adapter == null)
                {
                    setState(STOPPED_WITH_EXCEPTION);
                    throw ListeningAdapterException.noAdapterDefined(listeningDatasource.name)
                }
                observer = new ListeningAdapterObserver(scriptObject, scriptLogger);
                adapter.addObserver(observer);
            }
            else {
                setState(STOPPED_WITH_EXCEPTION);
                throw ListeningAdapterException.noListeningScript(listeningDatasource.name);
            }
        }
        try {
            scriptObject.init();
            setState(INITIALIZED);
        }
        catch (Throwable e) {
            setState(STOPPED_WITH_EXCEPTION);
            throw ListeningAdapterException.listeningScriptExecutionException(adapterName, listeningDatasource.listeningScript.name, "init", e);
        }
        try {
            adapter.subscribe();
            setState(STARTED);
        }
        catch (Throwable e) {
            setState(STOPPED_WITH_EXCEPTION);
            throw ListeningAdapterException.couldNotSubscribed(adapterName, e);
        }
    }

}