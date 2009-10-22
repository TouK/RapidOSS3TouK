package com.ifountain.rcmdb.domain

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: May 11, 2009
* Time: 5:53:56 PM
*/
class ObjectProcessor extends Observable {
    public static final String EVENT_NAME = "eventName";
    public static final String DOMAIN_OBJECT = "domainObject";
    public static final String UPDATED_PROPERTIES = "updatedProps";

    private static objectProcessor;
    private static Object getInstanceLock = new Object();
    private ObjectProcessor() {
    }

    public static ObjectProcessor getInstance() {
        synchronized (getInstanceLock)
        {
            if (objectProcessor == null) {
                objectProcessor = new ObjectProcessor();
            }
            return objectProcessor;
        }
    }

    public void repositoryChanged(String eventName, Object domainObject, Map updateParams) {
        if (countObservers() > 0) {
            Map repositoryChange = [:]
            repositoryChange.put(EVENT_NAME, eventName)
            repositoryChange.put(DOMAIN_OBJECT, domainObject.cloneObject())
            if (updateParams != null) {
                repositoryChange.put(UPDATED_PROPERTIES, Collections.unmodifiableMap(updateParams));
            }
            setChanged()
            notifyObservers(repositoryChange);
        }
    }
    public void repositoryChanged(String eventName, Object domainObject) {
        repositoryChanged(eventName, domainObject, null)
    }

}