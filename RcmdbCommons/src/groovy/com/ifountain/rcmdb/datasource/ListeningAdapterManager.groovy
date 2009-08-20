package com.ifountain.rcmdb.datasource

import datasource.BaseListeningDatasource
import org.apache.log4j.Logger
import script.CmdbScript

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 18, 2008
 * Time: 3:15:12 PM
 */
class ListeningAdapterManager {
    private static ListeningAdapterManager manager;
    private Map listeningAdapterRunners;
    private def adapterLock = new Object();
    private Thread listeningScriptInitializerThread;

    Logger logger = Logger.getLogger("scripting");
    public static ListeningAdapterManager getInstance() {
        if (manager == null) {
            manager = new ListeningAdapterManager();

        }
        return manager;
    }

    public static void destroyInstance() {
        if (manager != null) {
            manager.destroy();
            manager = null;
        }
    }
    private ListeningAdapterManager() {
    }

    public void initialize() {
        listeningAdapterRunners = [:];
    }

    private void destroy() {
        logger.debug("Destroying listening adapter manager.");
        destroyListeningDatasourceInitializerThread();
        if (listeningAdapterRunners != null)
        {
            def tmpMap = new HashMap(listeningAdapterRunners);
            tmpMap.each {Long datasourceId, RunnerObject runnerObj ->
                try
                {
                    removeAdapter(datasourceId);
                }
                catch (Throwable e)
                {
                    logger.info("Exception occurred while stopping adapter with datasource id ${datasourceId}", e);
                }
            }
        }
        logger.info("Destroyed listening adapter manager.");
    }
    public void initializeListeningDatasources()
    {
        logger.info("Initializing listening datasources");
        BaseListeningDatasource.list().each {BaseListeningDatasource ds ->
            try {
                ListeningAdapterManager.getInstance().addAdapterIfNotExists(ds);
            }
            catch (e) {
                logger.info("Error adding adapter ${ds.name}. Reason: ${e.getMessage()}");
            }
        }
        logger.info("Starting listening datasources");
        listeningScriptInitializerThread = Thread.start {
            BaseListeningDatasource.searchEvery("isSubscribed:true").each {BaseListeningDatasource ds ->
                if (ds.listeningScript) {
                    try {
                        logger.debug("Starting listening script ${ds.listeningScript}")
                        CmdbScript.startListening(ds.listeningScript);
                        logger.info("Listening script ${ds.listeningScript} successfully started.")
                    }
                    catch (e) {
                        logger.warn("Error starting listening script ${ds.listeningScript}. Reason: ${e.getMessage()}");
                    }
                }
            }
        }
        logger.info("Initialized listening datasources");
    }
    private void destroyListeningDatasourceInitializerThread()
    {
        try {
            logger.info("Checking listening script initializer thread is alive");
            if (listeningScriptInitializerThread != null && listeningScriptInitializerThread.isAlive())
            {
                logger.info("Stopping listening script initializer thread");
                listeningScriptInitializerThread.interrupt();
                listeningScriptInitializerThread.join();
                logger.info("Stopped listening script initializer thread");
            }
            else
            {
                logger.info("Not destroyed. Listening script initializer thread is not alive");
            }

        }
        catch (e)
        {
            logger.warn("Error while destroying listening datasources.Reason ${e.toString()}");
        }

    }



    public boolean hasAdapter(Long datasourceId)
    {
        synchronized (adapterLock)
        {
            return listeningAdapterRunners.containsKey(datasourceId);
        }
    }

    public void addAdapter(BaseListeningDatasource listeningDatasource)
    {
        synchronized (adapterLock)
        {
            if (!hasAdapter(listeningDatasource.id))
            {
                RunnerObject runnerObject = createAdapterRunner(listeningDatasource);
                listeningAdapterRunners.put(listeningDatasource.id, runnerObject);
            }
            else
            {
                throw ListeningAdapterException.adapterAlreadyExists(listeningDatasource.id);
            }
        }
    }
    public void addAdapterIfNotExists(BaseListeningDatasource listeningDatasource) throws Exception
    {
        if (!hasAdapter(listeningDatasource.id))
        {
            addAdapter(listeningDatasource);
        }
    }


    public void removeAdapter(BaseListeningDatasource listeningDatasource) {
        removeAdapter(listeningDatasource.id);
    }
    public void removeAdapter(Long datasourceId)
    {
        RunnerObject runnerObj = null;
        synchronized (adapterLock)
        {
            if (hasAdapter(datasourceId))
            {
                runnerObj = listeningAdapterRunners.remove(datasourceId)
            }
            else
            {
                throw ListeningAdapterException.runnerDoesNotExist(datasourceId);
            }
        }
        if (runnerObj.isRunning())
        {
            try
            {
                runnerObj.stop();
            } catch (ListeningAdapterException e)
            {
                logger.debug("Adapter could not be stopped since ${e.getMessage()}", e);
            }
        }
    }

    protected RunnerObject createAdapterRunner(BaseListeningDatasource datasource)
    {
        return new RunnerObject(datasource.id);
    }

    public void startAdapter(BaseListeningDatasource listeningDatasource) throws Exception {

        RunnerObject runnerObj = getRunnerAndThrowExceptionIfNotExist(listeningDatasource.id);
        runnerObj.start(listeningDatasource);
    }

    public String getState(BaseListeningDatasource ds)
    {
        return getRunnerAndThrowExceptionIfNotExist(ds.id).getState();
    }
    public Date getLastStateChangeTime(BaseListeningDatasource ds)
    {
        return getRunnerAndThrowExceptionIfNotExist(ds.id).getLastStateChangeTime();
    }

    private RunnerObject getRunner(Long datasourceId)
    {
        synchronized (adapterLock)
        {
            return listeningAdapterRunners.get(datasourceId);
        }
    }

    private RunnerObject getRunnerAndThrowExceptionIfNotExist(Long datasourceId)
    {
        RunnerObject runner = getRunner(datasourceId);
        if (runner == null)
        {
            throw ListeningAdapterException.runnerDoesNotExist(datasourceId);
        }
        return runner;
    }
    public void stopAdapter(BaseListeningDatasource listeningDatasource) throws Exception {
        RunnerObject runnerObj = getRunnerAndThrowExceptionIfNotExist(listeningDatasource.id);
        runnerObj.stop();
    }

    public boolean isSubscribed(BaseListeningDatasource listeningDatasource) {
        RunnerObject runner = getRunner(listeningDatasource.id)
        return runner != null && runner.isSubscribed();
    }
    public boolean isFree(BaseListeningDatasource listeningDatasource) {
        RunnerObject runner = getRunner(listeningDatasource.id)
        boolean result = true;
        if (runner != null)
        {
            result = runner.isFree();
        }
        return result;
    }
}
