package com.ifountain.rcmdb.datasource

import datasource.BaseListeningDatasource
import org.apache.log4j.Logger
import com.ifountain.core.datasource.AdapterStateProvider

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 12, 2009
* Time: 5:51:17 PM
* To change this template use File | Settings | File Templates.
*/
class RunnerObject {
    Logger logger = Logger.getLogger("scripting");
    AdapterRunnerThread runnerThread;
    ListeningAdapterRunner runner;
    Object adapterLock = new Object();
    Long datasourceId;
    Date lastStateChangeTime = new Date();
    public RunnerObject(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    public void start(BaseListeningDatasource listeningDatasource) {
        this.datasourceId = listeningDatasource.id;
        synchronized (adapterLock) {
            if (!isFree()) {
                if (getState() == AdapterStateProvider.STOPPING) {
                    throw ListeningAdapterException.stoppingStateException(listeningDatasource.id, "start")
                }
                else {
                    throw ListeningAdapterException.adapterAlreadyStartedException(listeningDatasource.id);
                }
            }
            runner = ListeningAdapterRunnerFactory.getRunner(listeningDatasource.id);
            runner.setState(AdapterStateProvider.INITIALIZING);
        }
        runnerThread = new AdapterRunnerThread(runner: runner, datasource: listeningDatasource, logger: logger);
        runnerThread.start();
    }

    public void stop() throws Exception {
        if (runner != null) {
            synchronized (adapterLock) {
                if (getState() == AdapterStateProvider.STOPPING)
                {
                    throw ListeningAdapterException.stoppingStateException(datasourceId, "stop");
                }
                else if (getState() == AdapterStateProvider.NOT_STARTED ||
                        getState() == AdapterStateProvider.STOPPED ||
                        getState() == AdapterStateProvider.STOPPED_WITH_EXCEPTION)
                {
                    throw ListeningAdapterException.adapterAlreadyStoppedException(datasourceId);
                }
            }
            runner.stop();
            runnerThread.join();
            runner.cleanUp();
        }
        else {
            throw ListeningAdapterException.adapterAlreadyStoppedException(datasourceId);
        }
    }

    public String getState() {
        if (runner != null) {
            return runner.getState();
        }
        return AdapterStateProvider.NOT_STARTED;
    }

    public boolean isFree() {
        if (runner != null) {
            return runner.isFree();
        }
        return true;
    }

    public boolean isRunning()
    {
        if (runner != null) {
            return runner.isRunning();
        }
        return false;
    }

    public boolean isSubscribed() {
        synchronized (adapterLock) {
            if (runner != null) {
                return runner.isSubscribed();
            }
        }
        return false;
    }

    public Date getLastStateChangeTime() {
        if (runner != null) {
            return runner.getLastStateChangeTime();
        }
        return lastStateChangeTime;
    }
}

class AdapterRunnerThread extends Thread {
    ListeningAdapterRunner runner;
    BaseListeningDatasource datasource;
    Logger logger;

    public void run() {
        try {
            runner.start(datasource);
        }
        catch (Exception e) {
            runner.logger.warn("Exception occurred while starting adapter with datasource id ${datasource.id}. Reason: " + e.getMessage(),e);
        }
    }
}
