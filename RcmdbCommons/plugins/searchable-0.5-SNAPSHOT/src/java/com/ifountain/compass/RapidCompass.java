package com.ifountain.compass;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassException;
import org.compass.core.ResourceFactory;
import org.compass.core.metadata.CompassMetaData;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.transaction.LocalTransactionFactory;
import org.compass.core.transaction.TransactionFactory;
import org.compass.core.executor.ExecutorManager;
import org.compass.core.events.CompassEventManager;
import org.compass.core.events.RebuildEventListener;
import org.compass.core.converter.ConverterLookup;
import org.compass.core.spi.InternalCompass;
import org.compass.core.engine.SearchEngineOptimizer;
import org.compass.core.engine.SearchEngineIndexManager;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.engine.naming.PropertyNamingStrategy;
import org.compass.core.engine.spellcheck.SearchEngineSpellCheckManager;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.CompassConfiguration;

import javax.naming.Reference;
import javax.naming.NamingException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:42:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCompass implements InternalCompass{
    InternalCompass compassInstance;
    Timer compassTransactionCommitTimer;
    int maxNumberOfTransaction;
    long maxTime;
    RapidCompassSession lastSession;
    public RapidCompass(InternalCompass compassInstance, int maxNumberOfTransaction, long maxTime)
    {
        this.compassInstance = compassInstance;
        this.maxNumberOfTransaction = maxNumberOfTransaction;
        this.maxTime = maxTime;
    }

    public synchronized void closeSession() {
        System.out.println("Session closed");
        lastSession.realClose();
        lastSession = null;
    }

    public void startTimer()
    {
        System.out.println("Starting timer");
        if(compassTransactionCommitTimer != null)
        {
            compassTransactionCommitTimer.cancel();
        }
        compassTransactionCommitTimer = new Timer();
        compassTransactionCommitTimer.schedule(new CompassSessionCloserTask(this), maxTime);
    }

    public boolean isClosed() {
        return compassInstance.isClosed();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public synchronized CompassSession openSession() throws CompassException {
        if(lastSession.isClosed())
        {
            lastSession = new RapidCompassSession(compassInstance.openSession());
            startTimer();
        }
        else if(lastSession.getNumberOfExecutedTransactions() >= maxNumberOfTransaction && lastSession.getNumberOfUnfinishedTransactions() == 0)
        {
            closeSession();
            startTimer();
        }
        return lastSession;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public synchronized void close() throws CompassException {
        if(lastSession != null)
        {
            lastSession.realClose();
        }
        compassInstance.close();

    }

    public Compass clone(CompassSettings compassSettings) {
        return new RapidCompass((InternalCompass)compassInstance.clone(compassSettings), maxNumberOfTransaction, maxTime);
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

    public TransactionFactory getTransactionFactory() {
        return compassInstance.getTransactionFactory();
    }

    public CompassSession openSession(boolean b, boolean b1) {
        return compassInstance.openSession(b, b1);
    }

    public void start() {
        compassInstance.start();
    }

    public void stop() {
        compassInstance.stop();
    }

    public Reference getReference() throws NamingException {
        return compassInstance.getReference();
    }

    public ConverterLookup getConverterLookup() {
        return compassInstance.getConverterLookup();
    }

    public CompassEventManager getEventManager() {
        return compassInstance.getEventManager();
    }

    public ExecutorManager getExecutorManager() {
        return compassInstance.getExecutorManager();
    }

    public LocalTransactionFactory getLocalTransactionFactory() {
        return compassInstance.getLocalTransactionFactory();
    }

    public CompassMapping getMapping() {
        return compassInstance.getMapping();
    }

    public CompassMetaData getMetaData() {
        return compassInstance.getMetaData();
    }

    public String getName() {
        return compassInstance.getName();
    }

    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return compassInstance.getPropertyNamingStrategy();
    }

    public SearchEngineFactory getSearchEngineFactory() {
        return compassInstance.getSearchEngineFactory();
    }

    public void removeRebuildEventListener(RebuildEventListener rebuildEventListener) {
        compassInstance.removeRebuildEventListener(rebuildEventListener);
    }

    public void addRebuildEventListener(RebuildEventListener rebuildEventListener) {
        compassInstance.addRebuildEventListener(rebuildEventListener);
    }

    public void rebuild() {
        compassInstance.rebuild();
    }

    public CompassConfiguration getConfig() {
        return compassInstance.getConfig();
    }
}

class CompassSessionCloserTask extends TimerTask
{
    RapidCompass compass;
    public CompassSessionCloserTask(RapidCompass compass)
    {
        this.compass = compass;        
    }
    public void run() {
        this.compass.closeSession();
    }
}

