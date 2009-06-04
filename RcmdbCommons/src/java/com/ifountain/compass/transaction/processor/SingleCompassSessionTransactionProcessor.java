package com.ifountain.compass.transaction.processor;

import org.compass.core.lucene.engine.transaction.TransactionProcessor;
import org.compass.core.spi.InternalResource;
import org.compass.core.spi.ResourceKey ;
import org.compass.core.lucene.engine.LuceneSearchEngineQuery   ;
import org.compass.core.lucene.engine.LuceneSearchEngineHits;
import org.compass.core.Resource                               ;
import org.compass.core.lucene.engine.LuceneSearchEngineInternalSearch  ;
import org.compass.core.lucene.engine.LuceneSearchEngine      ;
import org.compass.core.lucene.engine.transaction.readcommitted.ReadCommittedTransactionProcessor ;

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 26, 2009
* Time: 10:44:53 AM
* To change this template use File | Settings | File Templates.
*/
public class SingleCompassSessionTransactionProcessor implements TransactionProcessor{
    public static final String NAME = "singlesession";
    SingleCompassSessionTransactionProcessorFactory factory;
    public SingleCompassSessionTransactionProcessor(SingleCompassSessionTransactionProcessorFactory factory)
    {
        this.factory = factory;
    }
    public String getName() {
        return NAME; //To change body of implemented methods use File | Settings | File Templates.
    }

    public void begin() {
    }

    public void prepare() {
    }

    public void commit(boolean onePhase) {
        factory.flush();
    }

    public void rollback() {
    }

    public void flush() {
    }

    public void flushCommit(String[] aliases)
    {

    }

    public void create(InternalResource resource) {
        factory.create (resource);
    }

    public void update(InternalResource resource) {
        factory.update (resource);
    }

    public void delete(ResourceKey resourceKey) {
        factory.delete (resourceKey);
    }

    public void delete(LuceneSearchEngineQuery query) {
        factory.delete (query) ;
    }

    public LuceneSearchEngineHits find(LuceneSearchEngineQuery query) {
        return factory.find (query); //To change body of implemented methods use File | Settings | File Templates.
    }

    public Resource[] get(ResourceKey resourceKey) {
        return factory.get(resourceKey); //To change body of implemented methods use File | Settings | File Templates.
    }

    public LuceneSearchEngineInternalSearch internalSearch(String[] subIndexes, String[] aliases) {
        return factory.internalSearch(subIndexes, aliases); //To change body of implemented methods use File | Settings | File Templates.
    }

}