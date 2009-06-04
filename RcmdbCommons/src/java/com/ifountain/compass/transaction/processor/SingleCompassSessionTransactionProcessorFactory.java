package com.ifountain.compass.transaction.processor;

import org.compass.core.lucene.engine.transaction.TransactionProcessorFactory;
import org.compass.core.lucene.engine.transaction.TransactionProcessor;
import org.compass.core.lucene.engine.LuceneSearchEngine;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.ResourceKey;
import org.compass.core.lucene.engine.LuceneSearchEngineQuery;
import org.compass.core.lucene.engine.LuceneSearchEngineHits;
import org.compass.core.Resource;
import org.compass.core.lucene.engine.LuceneSearchEngineInternalSearch;
import org.compass.core.lucene.engine.transaction.readcommitted.ReadCommittedTransactionProcessor;
import org.compass.core.lucene.engine.transaction.readcommitted.ReadCommittedTransactionProcessorFactory;
import org.compass.core.config.SearchEngineFactoryAware;
import org.compass.core.engine.SearchEngineFactory;
import org.compass.core.lucene.engine.LuceneSearchEngineFactory;
import org.compass.core.lucene.engine.manager.LuceneSearchEngineIndexManager;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.config.RuntimeCompassSettings;
import org.compass.core.lucene.LuceneEnvironment;

import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 26, 2009
 * Time: 10:44:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCompassSessionTransactionProcessorFactory implements TransactionProcessorFactory, TransactionProcessor, SearchEngineFactoryAware, CompassConfigurable {
    public static final String COMMIT_INTERVAL_SETTING_KEY = "compass.transaction.processor.singlesession.commitinterval"; 
    TransactionProcessor trp;
    Object processorLock = new Object();
    LuceneSearchEngineFactory searchEngineFactory;
    RuntimeCompassSettings settings;
    long commitInterval = 1000;
    TrCommitter commiter;
    TransactionProcessorFactory factory;
    public SingleCompassSessionTransactionProcessorFactory() {
        factory = new ReadCommittedTransactionProcessorFactory();
    }

    public long getCommitInterval() {
        return commitInterval;
    }

    public void configure(CompassSettings settings) {
        this.settings = new RuntimeCompassSettings(settings);
        this.settings.setBooleanSetting(LuceneEnvironment.Transaction.Processor.ReadCommitted.CONCURRENT_OPERATIONS, false);
        this.commitInterval = this.settings.getSettingAsLong(COMMIT_INTERVAL_SETTING_KEY, 1000);
        commiter = new TrCommitter();
        commiter.start();
    }

    //for testing purposes
    public void setWrappedTransactionProcessorFactory(TransactionProcessorFactory factory)
    {
        this.factory = factory;
    }

    public void setSearchEngineFactory(SearchEngineFactory searchEngineFactory) {
        this.searchEngineFactory = (LuceneSearchEngineFactory) searchEngineFactory;
    }

    public TransactionProcessor create(LuceneSearchEngine searchEngine) {
        return new SingleCompassSessionTransactionProcessor(this);
    }

    public void close() {
        commiter.stop();
        factory.close();
    }

    public boolean isThreadSafe() {
        return false;
    }

    public String getName() {
        return null;
    }

    public void begin() {
        synchronized (processorLock) {
            getTrp().begin();
        }
    }

    public void prepare() {
        synchronized (processorLock) {
            getTrp().prepare();
        }
    }
    private TransactionProcessor getTrp()
    {
        synchronized (processorLock) {
            if(trp == null)
            {
                LuceneSearchEngine eng = (LuceneSearchEngine) searchEngineFactory.openSearchEngine(this.settings);
                eng.begin();
                trp = factory.create(eng);
                trp.begin();
            }
            return trp;    
        }
    }
    public void commit(boolean onePhase) {
        synchronized (processorLock) {
            getTrp().commit(onePhase);
            trp = null;
        }
    }

    public void rollback() {
        synchronized (processorLock) {
            getTrp().rollback();
        }
    }

    public void flush() {
        synchronized (processorLock) {
            getTrp().flush();
        }
    }

    public void flushCommit(String[] aliases) {
        synchronized (processorLock) {
            getTrp().flushCommit(aliases);
        }
    }

    public void create(InternalResource resource) {
        synchronized (processorLock) {
            getTrp().create(resource);
        }
    }

    public void update(InternalResource resource) {
        synchronized (processorLock) {
            getTrp().update(resource);
        }
    }

    public void delete(ResourceKey resourceKey) {
        synchronized (processorLock) {
            getTrp().delete(resourceKey);
        }
    }

    public void delete(LuceneSearchEngineQuery query) {
        synchronized (processorLock) {
            getTrp().delete(query);
        }
    }

    public LuceneSearchEngineHits find(LuceneSearchEngineQuery query) {
        synchronized (processorLock) {
            return getTrp().find(query); //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public Resource[] get(ResourceKey resourceKey) {
        synchronized (processorLock) {
            return getTrp().get(resourceKey);
        }
    }


    public LuceneSearchEngineInternalSearch internalSearch(String[] subIndexes, String[] aliases) {
        synchronized (processorLock) {
            return getTrp().internalSearch(subIndexes, aliases);
        }
    }

    class TrCommitter implements Runnable {
        boolean stopped = true;

        public void start() {
            stopped = false;
            ((LuceneSearchEngineIndexManager) searchEngineFactory.getIndexManager()).getExecutorManager().submit(this);
        }

        public void stop() {
            stopped = true;
        }

        public void run() {
            try {
                while (true) {

                    if(!stopped)
                    {
                        commit(true);
                    }
                    else
                    {
                        break;
                    }
                    Thread.sleep(commitInterval);
                }
            }
            catch (InterruptedException e) {

            }
        }
    }
}