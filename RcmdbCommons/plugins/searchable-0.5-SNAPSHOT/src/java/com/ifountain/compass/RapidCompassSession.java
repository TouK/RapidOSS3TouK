package com.ifountain.compass;

import org.compass.core.*;
import org.compass.core.config.CompassSettings;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:50:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCompassSession implements CompassSession{
    RapidCompassTransaction transaction;
    CompassSession session;
    public RapidCompassSession(CompassSession session)
    {
        this.session = session;
        transaction = new RapidCompassTransaction(session.beginTransaction());
    }

    public CompassAnalyzerHelper analyzerHelper() throws CompassException {
        return session.analyzerHelper();
    }

    public CompassTransaction beginLocalTransaction() throws CompassException {
        throw new RuntimeException("Not supported");
    }

    public CompassTransaction beginTransaction() throws CompassException {
        transaction.startTransaction();
        return transaction;
    }

    public CompassTransaction beginTransaction(CompassTransaction.TransactionIsolation transactionIsolation) throws CompassException {
        throw new RuntimeException("Not supported");
    }

    public int getNumberOfUnfinishedTransactions()
    {
        return transaction.getNumberOfUnfinishedTransactions();
    }

    public int getNumberOfExecutedTransactions()
    {
        return transaction.getNumberOfExecutedTransactions();
    }

    public void close() throws CompassException {
        synchronized (transaction)
        {
            transaction.commit(true);
            session.close();
        }
    }

    public CompassSettings getSettings() {
        return session.getSettings();
    }

    public boolean isClosed() {
        return session.isClosed();
    }

    public CompassQueryBuilder queryBuilder() throws CompassException {
        return session.queryBuilder();
    }

    public CompassQueryFilterBuilder queryFilterBuilder() throws CompassException {
        return session.queryFilterBuilder();
    }

    public ResourceFactory resourceFactory() {
        return session.resourceFactory();
    }

    public CompassTermFreqsBuilder termFreqsBuilder(String... strings) throws CompassException {
        return session.termFreqsBuilder(strings);
    }

    public void create(Object o) throws CompassException {
        session.create(o);
    }

    public void create(String s, Object o) throws CompassException {
        session.create(s, o);
    }

    public void delete(Class aClass, Object o) throws CompassException {
        session.delete(aClass, o);
    }

    public void delete(Class aClass, Object... objects) throws CompassException {
        session.delete(aClass, objects);
    }

    public void delete(CompassQuery compassQuery) throws CompassException {
        session.delete(compassQuery);
    }

    public void delete(Object o) throws CompassException {
        session.delete(o);
    }

    public void delete(Resource resource) throws CompassException {
        session.delete(resource);
    }

    public void delete(String s, Object o) throws CompassException {
        session.delete(s, o);
    }

    public void delete(String s, Object... objects) throws CompassException {
        session.delete(s, objects);
    }

    public void evict(Object o) {
        session.evict(o);
    }

    public void evict(Resource resource) {
        session.evict(resource);
    }

    public void evict(String s, Object o) {
        session.evict(s, o);
    }

    public void evictAll() {
        session.evictAll();
    }

    public CompassHits find(String s) throws CompassException {
        return session.find(s);
    }

    public <T> T get(Class<T> tClass, Object o) throws CompassException {
        return session.get(tClass, o);
    }

    public <T> T get(Class<T> tClass, Object... objects) throws CompassException {
        return session.get(tClass, objects);
    }

    public Object get(String s, Object o) throws CompassException {
        return session.get(s, o);
    }

    public Object get(String s, Object... objects) throws CompassException {
        return session.get(s, objects);
    }

    public Resource getResource(Class aClass, Object o) throws CompassException {
        return session.getResource(aClass, o);
    }

    public Resource getResource(Class aClass, Object... objects) throws CompassException {
        return session.getResource(aClass, objects);
    }

    public Resource getResource(String s, Object o) throws CompassException {
        return session.getResource(s, o);
    }

    public Resource getResource(String s, Object... objects) throws CompassException {
        return session.getResource(s, objects);
    }

    public <T> T load(Class<T> tClass, Object o) throws CompassException {
        return session.load(tClass, o);
    }

    public <T> T load(Class<T> tClass, Object... objects) throws CompassException {
        return session.load(tClass, objects);
    }

    public Object load(String s, Object o) throws CompassException {
        return session.load(s, o);
    }

    public Object load(String s, Object... objects) throws CompassException {
        return session.load(s, objects);
    }

    public Resource loadResource(Class aClass, Object o) throws CompassException {
        return session.loadResource(aClass, o);
    }

    public Resource loadResource(Class aClass, Object... objects) throws CompassException {
        return session.loadResource(aClass, objects);
    }

    public Resource loadResource(String s, Object o) throws CompassException {
        return session.loadResource(s, o);
    }

    public Resource loadResource(String s, Object... objects) throws CompassException {
        return session.loadResource(s, objects);
    }

    public void save(Object o) throws CompassException {
        session.save(o);
    }

    public void save(String s, Object o) throws CompassException {
        session.save(s, o);
    }
}
