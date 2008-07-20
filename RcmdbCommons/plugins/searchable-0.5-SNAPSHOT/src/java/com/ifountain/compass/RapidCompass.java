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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:42:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCompass extends TimerTask implements Compass{
    Compass compassInstance;
    Timer compassTransactionCommitTimer;
    int maxNumberOfTransaction;
    long maxTime;
    RapidCompassSession lastSession;
    public RapidCompass(Compass compassInstance, int maxNumberOfTransaction, long maxTime)
    {
        this.compassInstance = compassInstance;
        compassTransactionCommitTimer = new Timer();
        compassTransactionCommitTimer.schedule(this,maxTime);
        this.maxNumberOfTransaction = maxNumberOfTransaction;
        this.maxTime = maxTime;
    }

    public synchronized void run() {
        if(lastSession != null)
        {
            lastSession.close();
            lastSession = new RapidCompassSession(compassInstance.openSession());
        }
        startTimer();
    }

    public void startTimer()
    {
        if(compassTransactionCommitTimer != null)
        {
            compassTransactionCommitTimer.cancel();
        }
        compassTransactionCommitTimer = new Timer();
        compassTransactionCommitTimer.schedule(this,maxTime);
    }

    public boolean isClosed() {
        return compassInstance.isClosed();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public synchronized CompassSession openSession() throws CompassException {
        if(lastSession == null)
        {
            lastSession = new RapidCompassSession(compassInstance.openSession());
        }
        else if(lastSession.getNumberOfExecutedTransactions() >= maxNumberOfTransaction && lastSession.getNumberOfUnfinishedTransactions() == 0)
        {
            startTimer();
            lastSession.close();
            lastSession = new RapidCompassSession(compassInstance.openSession());
        }
        return lastSession;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() throws CompassException {
        compassInstance.close();
    }

    public Compass clone(CompassSettings compassSettings) {
        return new RapidCompass(compassInstance.clone(compassSettings), maxNumberOfTransaction, maxTime);
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
