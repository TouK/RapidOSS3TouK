/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created on Mar 12, 2008
 *
 * Author Sezgin Kucukkaraaslan
 */
package com.ifountain.smarts.datasource;

import com.ifountain.core.datasource.BaseListeningAdapter;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.util.DataFromObservable;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.property.MRPropertyNameValuesToMap;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.smarts.remote.SmObserverEvent;
import com.smarts.remote.SmRemoteException;
import com.smarts.repos.MR_AnyValString;
import com.smarts.repos.MR_PropertyNameValue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Observable;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public abstract class BaseSmartsListeningAdapter extends BaseListeningAdapter {

    protected SmartsSubscribeParameters[] subscribeParams;
     protected boolean existingObjectsRetrieved = false;
    protected String logPrefix = "[BaseSmartsObserver]: ";
    protected boolean isObserverCreated;
    public static final String DELETE = "DELETE";
    public static final String NOTIFY = "NOTIFY";
    public static final String CHANGE = "CHANGE";
    public static final String NOCHANGE = "NOCHANGE";
    public static final String CLEAR = "CLEAR";
    public static final String ARCHIVE = "ARCHIVE";
    public static final String CREATE = "CREATE";
    public static final String EVENT_TYPE_NAME = "ICEventType";
    public static final String RECEIVE_EXISTING_FINISHED = "ReceiveExistingCompleted";

    public BaseSmartsListeningAdapter(String connectionName, long reconnectInterval, Logger logger, SmartsSubscribeParameters[] subscribeParams) {
        super(connectionName, reconnectInterval, logger);
        this.subscribeParams = subscribeParams;
    }

    protected abstract void subscribeTo() throws Exception;

    protected abstract void unsubscribeFrom() throws Exception;

    public abstract Object processIncomingData(DataFromObservable data);
    protected abstract void getExistingObjects(SmartsSubscribeParameters []parameters) throws Exception;

    @Override
    protected void _subscribe() throws Exception {
        if (subscribeParams != null) {
            for (int i = 0; i < subscribeParams.length; i++) {
                logger.info(logPrefix + "Observer is subscribing to parameter which has " + subscribeParams[i]);
            }
        } else {
            logger.info(logPrefix + "No parameters specified for observer to subscribe.");
        }
        createSmartsObserver();
        try {
            subscribeTo();
            logger.info(logPrefix + "Observer is subscribed");
            try {
                retrieveExistingObjects(subscribeParams);
            }
            catch (Exception e) {
                throw new Exception("Exception occurred while retrieving existing objects. Reason : " + e.toString());
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().indexOf("Observer not currently attached") >= 0) {
                logger.warn(logPrefix + "Could not subscribe to specified parameters. Reason : " + e);
            } else {
                deleteSmartsObserver();
                throw e;
            }
        }
    }

    protected void createSmartsObserver() throws IOException, SmRemoteException {
        ((SmartsConnectionImpl) getConnection()).getDomainManager().createObserver(this);
        isObserverCreated = true;
    }

    protected void deleteSmartsObserver() {
        if (isObserverCreated) {
            try {
                ((SmartsConnectionImpl) getConnection()).getDomainManager().deleteObserver(this);
            } catch (Exception e) {
                logger.debug(logPrefix + "Cannot delete observer.");
            }
            isObserverCreated = false;
        }
    }

    @Override
    public Object _update(Observable o, Object event) {
        while(!existingObjectsRetrieved && isObserverCreated)
        {
            try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e)
            {
                logger.warn(logPrefix + "Update is interrupted by terminate adapter call");
                return null;
            }
        }
        SmObserverEvent newEvent = (SmObserverEvent) event;
        logger.debug(logPrefix + "Update is called with event type " + newEvent.getType());
        if(!isObserverCreated)
        {
           return null;
        }
        switch (newEvent.getType()) {
            case SmObserverEvent.PROPERTY_SUBSCRIPTION_ACCEPT:
                return null;
            case SmObserverEvent.DOMAIN_DISCONNECT:
            case SmObserverEvent.DOMAIN_DETACH:
                try {
                    disconnectDetected();
                } catch (Exception e) {
                }
                return null;
        }
        String className = newEvent.getClassName();
        String instanceName = newEvent.getInstanceName();
        String propertyName = newEvent.getPropertyName();
        logger.debug(logPrefix + "Data is constructed with ClassName: " + className + ", InstanceName: " + instanceName + ", PropertyName: " + propertyName);
        DataFromObservable incomingData = new DataFromObservable(className, instanceName, propertyName,
                newEvent.getParam(), newEvent.getType());
        return processIncomingData(incomingData);
    }

    @Override
    protected void _unsubscribe() {
        logger.debug(logPrefix + "Observer is unsubscribing.");
        try {
            unsubscribeFrom();
            logger.debug(logPrefix + "Observer is unsubscribed.");
        } catch (Exception e) {
            logger.warn(logPrefix + "Could not unsubscribed.");
        }
        finally {
            deleteSmartsObserver();
        }
    }

    public SmartsSubscribeParameters[] getSubscribeParams() {
        return subscribeParams;
    }

    public void setSubscribeParams(SmartsSubscribeParameters[] subscribeParams) {
        this.subscribeParams = subscribeParams;
    }

    protected MR_PropertyNameValue[] convertIncomingDataToMR_PropertyNameValue(DataFromObservable data) {
        MR_PropertyNameValue[] nameValuePairs;
        if (data.getPropertyName() != null && data.getPropertyName().length() != 0) {
            nameValuePairs = new MR_PropertyNameValue[3];
            nameValuePairs[2] = new MR_PropertyNameValue(data.getPropertyName(), data.getValue());
        } else {
            nameValuePairs = new MR_PropertyNameValue[2];
        }
        nameValuePairs[0] = new MR_PropertyNameValue(SmartsConstants.CLASSNAME, new MR_AnyValString(data.getClassName()));
        nameValuePairs[1] = new MR_PropertyNameValue(SmartsConstants.INSTANCENAME, new MR_AnyValString(data.getInstanceName()));
        return nameValuePairs;
    }

    protected Object createObject(String eventType, MR_PropertyNameValue[] propertyNameValues) {
        return createObject(eventType, propertyNameValues, null);
    }

    protected Object createObject(String eventType, MR_PropertyNameValue[] propertyNameValues, List monitoredAttributes) {
        MRPropertyNameValuesToMap mToMap = new MRPropertyNameValuesToMap();
        Map object = mToMap.getMap(propertyNameValues, monitoredAttributes);
        object.put(EVENT_TYPE_NAME, eventType);
        return object;
    }

    protected void retrieveExistingObjects(SmartsSubscribeParameters []parameters) throws Exception
    {
        if(!existingObjectsRetrieved)
        {
            logger.debug(logPrefix + "Retrieving existing objects.");
            getExistingObjects(parameters);
            existingObjectsRetrieved = true;
            logger.debug(logPrefix + "Existing objects retrieved.");
        }
    }

    public void sendRetrieveExistingObjectsFinished()
    {
        Map data = new HashMap();
        data.put(EVENT_TYPE_NAME, RECEIVE_EXISTING_FINISHED);
        sendDataToObservers(data);
    }

}
