package com.ifountain.compass;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassException;
import org.compass.core.ResourceFactory;
import org.compass.core.engine.SearchEngineOptimizer;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.spellcheck.SearchEngineSpellCheckManager;
import org.compass.core.config.CompassSettings;

import javax.naming.Reference;
import javax.naming.NamingException;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:42:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCompass implements Compass{
    Compass compassInstance;
    public RapidCompass(Compass compassInstance)
    {
        this.compassInstance = compassInstance;        
    }
    public boolean isClosed() {
        return compassInstance.isClosed();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CompassSession openSession() throws CompassException {
        return new RapidCompassSession(compassInstance.openSession());  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws CompassException {
        compassInstance.close();
    }

    public Compass clone(CompassSettings compassSettings) {
        return new RapidCompass(compassInstance.clone(compassSettings));
    }

    public ResourceFactory getResourceFactory() {
        return compassInstance.getResourceFactory();
    }

    public SearchEngineOptimizer getSearchEngineOptimizer() {
        return compassInstance.getSearchEngineOptimizer();
    }

    public SearchEngineIndexManager getSearchEngineIndexManager() {
        return compassInstance.getSearchEngineIndexManager();
    }

    public SearchEngineSpellCheckManager getSpellCheckManager() {
        return compassInstance.getSpellCheckManager();
    }

    public CompassSettings getSettings() {
        return compassInstance.getSettings();
    }

    public Reference getReference() throws NamingException {
        return compassInstance.getReference();
    }
}
