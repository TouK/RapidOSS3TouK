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
                throw ListeningAdapterException.adapterDoesNotExist(adapterName);
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

        ListeningAdapterRunner runner = getRunnerAnThrowExceptionIfNotExist(listeningDatasource.name);
        runner.start(listeningDatasource);

    }

    public int getState(BaseListeningDatasource ds)
    {
        return getRunnerAnThrowExceptionIfNotExist(ds.name).getState();
    }
    
    private ListeningAdapterRunner getRunner(String name)
    {
        synchronized (adapterLock)
        {
            return listeningAdapterRunners.get(name);
        }
    }

    private ListeningAdapterRunner getRunnerAnThrowExceptionIfNotExist(String name)
    {
        ListeningAdapterRunner runner = getRunner(name);
        if(runner == null)
        {
            throw ListeningAdapterException.adapterDoesNotExist(name);
        }
        return runner;
    }
    public void stopAdapter(BaseListeningDatasource listeningDatasource) throws Exception {
        ListeningAdapterRunner runner = getRunnerAnThrowExceptionIfNotExist(listeningDatasource.name);
        runner.stop();
    }

    public boolean isSubscribed(BaseListeningDatasource listeningDatasource) {
        ListeningAdapterRunner runner = getRunner(listeningDatasource.name)
        return runner != null && runner.isSubscribed();
    }
}
