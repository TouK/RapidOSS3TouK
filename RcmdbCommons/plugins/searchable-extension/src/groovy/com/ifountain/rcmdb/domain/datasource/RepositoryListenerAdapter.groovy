package com.ifountain.rcmdb.domain.datasource

import com.ifountain.core.datasource.BaseListeningAdapter
import com.ifountain.rcmdb.domain.ObjectProcessor
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.method.EventTriggeringUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 12, 2009
* Time: 4:46:17 PM
*/
class RepositoryListenerAdapter extends BaseListeningAdapter {
    protected Object changeWaitingLock = new Object();
    protected ChangeProcessorThread changeProcessorThread;
    protected List changeQueue = Collections.synchronizedList(new ArrayList());
    private Map subscriptionFilters;
    private Closure filterClosure;
    private Map subscribedClasses = [:]

    public RepositoryListenerAdapter(String connectionName, Logger logger, Map subsFilters, Closure fClosure) {
        super(connectionName, 0, logger);
        this.subscriptionFilters = subsFilters;
        this.filterClosure = fClosure;
    }
    public RepositoryListenerAdapter(String connectionName, Logger logger, Map subsFilters) {
        this(connectionName, logger, subsFilters, null);
    }
    public Object _update(Observable o, Object arg) {
        def changeObjectClone = arg.clone();
        def domainObject = arg[ObjectProcessor.DOMAIN_OBJECT];
        def domainObjectClone = domainObject.cloneObject();
        changeObjectClone[ObjectProcessor.DOMAIN_OBJECT] = domainObjectClone;
        return changeObjectClone;
    }

    public void update(Observable o, Object arg) {
        addChangeEventToQueue(arg);
    }
    public boolean isConversionEnabledForUpdate()
    {
        return false;
    }

    private void addChangeEventToQueue(Map changeEvent) {
        def domainObject = changeEvent[ObjectProcessor.DOMAIN_OBJECT];
        def eventName = changeEvent[ObjectProcessor.EVENT_NAME];
        def className = domainObject.class.name;
        def classSubscriptionConfig = subscribedClasses.get(className);
        if (classSubscriptionConfig && classSubscriptionConfig.contains(eventName) && (filterClosure == null || filterClosure(changeEvent))) {
            logger.debug(getLogPrefix() + "Change received and passed the filter, adding to the queue.");
            synchronized (changeWaitingLock) {
                changeQueue.add(changeEvent);
                changeWaitingLock.notifyAll();
            }
        }
    }
    private void addClassToSubscriptionMap(GrailsDomainClass domainClass, List eventNames) {
        def className = domainClass.fullName;
        def clonedEventNames = [];
        clonedEventNames.addAll(eventNames);
        def allEvents = [EventTriggeringUtils.AFTER_INSERT_EVENT, EventTriggeringUtils.AFTER_DELETE_EVENT, EventTriggeringUtils.AFTER_UPDATE_EVENT]
        if (clonedEventNames.isEmpty()) {
            clonedEventNames.addAll(allEvents);
        }
        if (subscribedClasses.containsKey(className)) {
            def classEvents = subscribedClasses.get(className);
            clonedEventNames.each {eventName ->
                if (!classEvents.contains(eventName)) {
                    classEvents.add(eventName);
                }
            }
        }
        else {
            subscribedClasses.put(className, clonedEventNames);
        }
    }
    private void createSubscriptionClassesMap() {
        subscriptionFilters.each {String className, List events ->
            GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass(className);
            if (domainClass) {
                addClassToSubscriptionMap(domainClass, events);
                domainClass.getSubClasses().each {subClass ->
                    addClassToSubscriptionMap(subClass, events);
                }
            }
            else {
                throw new Exception("Cannot load domain class ${className}")
            }
        }
    }
    protected void _subscribe() {
        logger.debug(getLogPrefix() + "Subscribing.");
        createSubscriptionClassesMap();
        Closure runClosure = {
            try {
                while (true) {
                    Map changeObject = null;
                    synchronized (changeWaitingLock) {
                        if (changeQueue.isEmpty()) {
                            logger.debug(getLogPrefix() + "Queue is empty, waiting...");
                            changeWaitingLock.wait();
                        }
                        changeObject = changeQueue.remove(0);
                    }
                    super.update(null, changeObject);
                }
            }
            catch (InterruptedException e) {
                logger.info(getLogPrefix() + "Change processor thread stopped.");
            }
        }
        logger.debug(getLogPrefix() + "Starting change processor thread");
        changeProcessorThread = new ChangeProcessorThread(runClosure);
        changeProcessorThread.start();
        ObjectProcessor.getInstance().addObserver(this)
        logger.info(getLogPrefix() + "Subscribed.");
    }

    protected void _unsubscribe() {
        logger.debug(getLogPrefix() + "Unsubscribing");
        subscribedClasses.clear();
        if (changeProcessorThread != null) {
            if (changeProcessorThread.isAlive()) {
                changeProcessorThread.interrupt();
                logger.debug(getLogPrefix() + "Interrupted change processor thread. Waiting for change processor thread to die.");
                try {
                    changeProcessorThread.join();
                }
                catch (InterruptedException e) {
                    logger.warn(getLogPrefix() + "InterruptedException occured during changeProcessorThread.join .");
                }
                logger.debug(getLogPrefix() + "Change processor thread died.");
            } else {
                logger.debug(getLogPrefix() + "Change processor is not alive. No need to interrupt.");
            }
        }
        logger.info(getLogPrefix() + "Unsubscribed.");
        ObjectProcessor.getInstance().deleteObserver(this);
    }

    public String getLogPrefix() {
        return "[RepositoryListenerAdapter]: ";
    }

}

class ChangeProcessorThread extends Thread {
    Closure runClosure;
    public ChangeProcessorThread(Closure closure) {
        runClosure = closure;
    }
    public void run() {
        runClosure();
    }

}