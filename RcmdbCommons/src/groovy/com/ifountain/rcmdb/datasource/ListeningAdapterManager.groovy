package com.ifountain.rcmdb.datasource

import com.ifountain.core.datasource.BaseListeningAdapter
import datasource.BaseListeningDatasource
import script.CmdbScript
import com.ifountain.rcmdb.scripting.ScriptManager
import org.apache.log4j.Logger;

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
    private def listeningAdapters;
    private def observers;
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
        listeningAdapters = [:];
        observers = [:];
    }

    public void destroy() {
        logger.debug("Destroying listening adapter manager.");
        listeningAdapters.each {String adapterName, BaseListeningAdapter adapter ->
            adapter.unsubscribe();
            adapter.deleteObservers();
            ListeningAdapterObserver observer = observers.remove(adapter);
            try {
                observer.scriptInstance.cleanUp();
            }
            catch (e) {
                logger.warn("Error during script ${observer.scriptInstance} clean up . Reason: ${e.getMessage()}", e)
            }
        }
        logger.info("Destroyed listening adapter manager.");
    }

    public void startAdapter(BaseListeningDatasource listeningDatasource) throws Exception {
        logger.debug("Starting listening adapter ${listeningDatasource.name}");
        CmdbScript script = listeningDatasource.listeningScript;
        if (script && script.type == CmdbScript.LISTENING) {
            stopAdapter(listeningDatasource);
            BaseListeningAdapter listeningAdapter = null;
            def scriptObject;
            def scriptLogger;
            try {
                scriptLogger=CmdbScript.getScriptLogger(script);
                scriptObject = ScriptManager.getInstance().getScriptObject(script.scriptFile);
                scriptObject.setProperty("datasource", listeningDatasource);
                scriptObject.setProperty("staticParam", script.staticParam);
                scriptObject.setProperty("logger", scriptLogger);
                scriptObject.setProperty("staticParamMap", CmdbScript.getStaticParamMap(script));
                scriptObject.run();
                def params = scriptObject.getParameters();
                if (params == null) {
                    throw new Exception("Subscription parameters cannot be null");
                }
                params.logger=scriptLogger;
                listeningAdapter = listeningDatasource.getListeningAdapter(params);
            }
            catch (e) {
                throw new Exception("Error creating listening adapter. Reason: ${e.getMessage()}", e);
            }

            try {
                scriptObject.init();
            }
            catch (e) {
                throw new Exception("Error script initialization. Reason: ${e.getMessage()}", e);
            }

            try {
                ListeningAdapterObserver adapterObserver = new ListeningAdapterObserver(scriptObject, scriptLogger);
                listeningAdapter.addObserver(adapterObserver);
                listeningAdapter.subscribe();
                listeningAdapters.put(listeningDatasource.name, listeningAdapter);
                observers.put(listeningAdapter, adapterObserver);
                
            }
            catch (e) {
                throw new Exception("Error during subscription. Reason: ${e.getMessage()}", e);
            }

        }
        else {
            throw new Exception("No listening script is defined for ${listeningDatasource.name}");
        }
    }

    public void stopAdapter(BaseListeningDatasource listeningDatasource) throws Exception {
        BaseListeningAdapter adapter = listeningAdapters.remove(listeningDatasource.name);
        if (adapter) {
            logger.debug("Stopping listening adapter ${listeningDatasource.name}");
            adapter.unsubscribe();
            adapter.deleteObservers();
            ListeningAdapterObserver observer = observers.remove(adapter);
            
            try {
                observer.getScriptInstance().cleanUp();
            }
            catch (e) {
                throw new Exception("Error during script clean up. Reason: " + e.getMessage(), e)
            }

        }        
    }

    public boolean isSubscribed(BaseListeningDatasource listeningDatasource) {
        return listeningAdapters.containsKey(listeningDatasource.name);
    }
}