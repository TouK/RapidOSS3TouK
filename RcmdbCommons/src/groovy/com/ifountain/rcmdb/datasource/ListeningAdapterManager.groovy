package com.ifountain.rcmdb.datasource

import com.ifountain.core.datasource.BaseListeningAdapter
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
            tmpMap.each {String adapterName, ListeningAdapterRunner runner ->
                try
                {
                    removeAdapter (adapterName);
                }
                catch(Throwable e)
                {
                    logger.info ("Exception occurred while stopping adapter ${adapterName}", e);
                }
            }
        }
        logger.info("Destroyed listening adapter manager.");        
    }
   public void initializeListeningDatasources()
   {
       logger.info("Initializing listening datasources");
       BaseListeningDatasource.list().each{BaseListeningDatasource ds->
           try {
               ListeningAdapterManager.getInstance().addAdapter (ds);
           }
           catch (e) {
               logger.warn("Error adding adapter ${ds.name}. Reason: ${e.getMessage()}");
           }
       }
       logger.info("Starting listening datasources");
       listeningScriptInitializerThread = Thread.start{
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
           if(listeningScriptInitializerThread != null && listeningScriptInitializerThread.isAlive())
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
       catch(e)
       {
           logger.warn("Error while destroying listening datasources.Reason ${e.toString()}");
       }

   }



    public boolean hasAdapter(String dsName)
    {
        synchronized (adapterLock)
        {
            return listeningAdapterRunners.containsKey(dsName);
        }
    }

    public void addAdapter(BaseListeningDatasource listeningDatasource)
    {
        synchronized (adapterLock)
        {
            if (!hasAdapter(listeningDatasource.name))
            {
                ListeningAdapterRunner runner = createAdapterRunner(listeningDatasource.name);
                listeningAdapterRunners.put(runner.getAdapterName(), runner);
            }
            else
            {
                throw ListeningAdapterException.adapterAlreadyExists(listeningDatasource.name);
            }
        }
    }
    public void addAdapterIfNotExists(BaseListeningDatasource listeningDatasource) throws Exception
    {
         if(!hasAdapter(listeningDatasource.name))
         {
            addAdapter (listeningDatasource);
         }
    }

   
    public void removeAdapter(BaseListeningDatasource listeningDatasource){
        removeAdapter (listeningDatasource.name);
    }
    public void removeAdapter(String adapterName)
    {
        ListeningAdapterRunner runner = null;
        synchronized (adapterLock)
        {
            if (hasAdapter(adapterName))
            {
                runner = listeningAdapterRunners.remove(adapterName)
            }
            else
            {
                throw ListeningAdapterException.runnerDoesNotExist(adapterName);
            }
        }
        if(runner.isRunning())
        {
            try
            {
                runner.stop();
            }catch(ListeningAdapterException e)
            {
                logger.debug ("Adapter could not be stopped since ${e.getMessage()}", e);
            }
        }
    }

    protected  ListeningAdapterRunner createAdapterRunner(String name)
    {
        return new ListeningAdapterRunner(name);           
    }



    public void startAdapter(BaseListeningDatasource listeningDatasource) throws Exception {

        ListeningAdapterRunner runner = getRunnerAndThrowExceptionIfNotExist(listeningDatasource.name);
        runner.start(listeningDatasource);

    }

    public int getState(BaseListeningDatasource ds)
    {
        return getRunnerAndThrowExceptionIfNotExist(ds.name).getState();
    }
    public Date getLastStateChangeTime(BaseListeningDatasource ds)
    {
        return getRunnerAndThrowExceptionIfNotExist(ds.name).getLastStateChangeTime();        
    }
    
    private ListeningAdapterRunner getRunner(String name)
    {
        synchronized (adapterLock)
        {
            return listeningAdapterRunners.get(name);
        }
    }

    private ListeningAdapterRunner getRunnerAndThrowExceptionIfNotExist(String name)
    {
        ListeningAdapterRunner runner = getRunner(name);
        if(runner == null)
        {
            throw ListeningAdapterException.runnerDoesNotExist(name);
        }
        return runner;
    }
    public void stopAdapter(BaseListeningDatasource listeningDatasource) throws Exception {
        ListeningAdapterRunner runner = getRunnerAndThrowExceptionIfNotExist(listeningDatasource.name);
        runner.stop();
    }

    public boolean isSubscribed(BaseListeningDatasource listeningDatasource) {
        ListeningAdapterRunner runner = getRunner(listeningDatasource.name)
        return runner != null && runner.isSubscribed();
    }
    public boolean isStartable(BaseListeningDatasource listeningDatasource) {
        ListeningAdapterRunner runner = getRunner(listeningDatasource.name)
        boolean result=true;
        if(runner != null)
        {
             result=runner.isStartable();
        }
        return result;
    }
}
