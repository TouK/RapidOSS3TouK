package com.ifountain.smarts.datasource;

import com.ifountain.smarts.util.DataFromObservable;
import com.ifountain.smarts.util.SmartsConstants;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.smarts.remote.SmRemoteDomainManager;
import com.smarts.remote.SmRemoteException;
import com.smarts.remote.SmObserverEvent;
import com.smarts.repos.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.io.IOException;
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
 * Date: Jul 15, 2008
 * Time: 11:52:32 AM
 */
public class SmartsTopologyListeningAdapter extends BaseSmartsListeningAdapter {

    private HashMap requestedSubscriptionParameters = new HashMap();
    private List subscribedMrPropertyList = new ArrayList();
    private HashMap createdClasses = new HashMap();
    boolean isProcessingPreviousInstancesFinished = false;
    private HashMap createdClassesDuringStatup = new HashMap();

    private static int UNKNOWN_ACCESS_TYPE = 7;

    public SmartsTopologyListeningAdapter(String connectionName, long reconnectInterval, Logger logger, SmartsSubscribeParameters[] subscribeParams) {
        super(connectionName, reconnectInterval, logger, subscribeParams);
        logPrefix = "[SmartsTopologyObserver]: ";
    }

    protected void subscribeTo() throws Exception {
        if (subscribeParams == null || subscribeParams.length == 0) {
            throw new Exception("There is no parameter specified to retrive topology objects");
        }
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        domainManager.topologySubscribe();
        logger.info(logPrefix + "Subscribed to topology objects.");

        for (int i = 0; i < subscribeParams.length; i++) {
            String className = subscribeParams[i].getClassName();
            try {
                if (domainManager.isInstrumented(className)) {
                    logger.warn(logPrefix + "Instrumented class \"" + className + "\" cannot be monitored");
                    continue;
                }
                Map filteredProps = getFilteredPropertiesAsMap(className, subscribeParams[i].getParameters());
                List classHierarchyList = new ArrayList();
                getClassHierarchyList(classHierarchyList, className);
                Iterator it = filteredProps.keySet().iterator();

                while (it.hasNext()) {
                    String property = it.next().toString();
                    MR_PropertyChoice propChoice = new MR_PropertyChoice(subscribeParams[i].getClassName(), subscribeParams[i].getInstanceName(), property, MR_PropertyChoice.EXPAND_SUBCLASSES | MR_PropertyChoice.STICKY);
                    domainManager.propertySubscribeAll(propChoice, 1000);
                    subscribedMrPropertyList.add(propChoice);

                    for (int j = 0; j < classHierarchyList.size(); j++) {
                        String tmpClassName = classHierarchyList.get(j).toString();
                        Map currentMap = (Map) requestedSubscriptionParameters.get(tmpClassName + subscribeParams[i].getInstanceName());
                        if (currentMap == null) {
                            currentMap = new HashMap();
                            requestedSubscriptionParameters.put(tmpClassName + subscribeParams[i].getInstanceName(), currentMap);
                            currentMap.put(SmartsConstants.CLASSNAME, tmpClassName);
                            currentMap.put(SmartsConstants.INSTANCENAME, subscribeParams[i].getInstanceName());
                        }
                        if (!(SmartsConstants.CLASSNAME.equals(property) || SmartsConstants.INSTANCENAME.equals(property))) {
                            currentMap.put(property, property);
                        }
                    }
                }
            }
            catch (Exception ex) {
                throw new Exception("Could not registered to properties of "+className+". Reason : " + ex.toString());
            }
        }

    }

    protected void unsubscribeFrom() throws Exception {
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        try {
            domainManager.topologyUnsubscribe();
            for (int i = 0; i < subscribedMrPropertyList.size(); i++) {
                MR_PropertyChoice choice = (MR_PropertyChoice) subscribedMrPropertyList.get(i);
                domainManager.propertyUnsubscribeAll(choice);
            }
        }
        finally {
            subscribedMrPropertyList.clear();
            requestedSubscriptionParameters.clear();
            existingObjectsRetrieved = false;
            createdClasses.clear();
        }
    }

    public Object processIncomingData(DataFromObservable data) {
        if (!isProcessingPreviousInstancesFinished) {
            processExisting();
            isProcessingPreviousInstancesFinished = true;
        }
        MR_PropertyNameValue[] nameValuePairs = convertIncomingDataToMR_PropertyNameValue(data);
        String incomingDataKey = data.getClassName() + data.getInstanceName();
        String regularExpressionKey = getHashMapKeyMatchesInputKey(requestedSubscriptionParameters, incomingDataKey);
        if (regularExpressionKey == null) {
            logger.debug(logPrefix + "Topology object whose classname <" + data.getClassName() + "> instancename <" + data.getInstanceName() + "> is not expected. It will be discarded.");
            return null;
        }
        switch (data.getEventType()) {
            case SmObserverEvent.INSTANCE_CREATE: {
                logger.debug(logPrefix + "Create event recieved for " + data);
                return processCreateEvent(data.getClassName(), data.getInstanceName(), regularExpressionKey);
            }
            case SmObserverEvent.INSTANCE_DELETE: {
                logger.debug(logPrefix + "Delete event recieved for " + data);
                return createObject(DELETE, nameValuePairs);
            }

            case SmObserverEvent.PROPERTY_SUBSCRIPTION_SUSPEND: {
                data.setValue(null);
                try {
                    SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
                    MR_AnyVal[] values = domainManager.getProperties(data.getClassName(), data.getInstanceName(), new String[]{data.getPropertyName()});
                    if (values != null && values.length != 0) {
                        data.setValue(values[0]);
                    }
                }
                catch (Exception e) {
                    logger.debug(logPrefix + "Exception occurred while getting value of read-only attribute <" + data.getPropertyName() + ">. Reason :" + e.toString());
                }
                return processAttributeChange(data);
            }
            case SmObserverEvent.PROPERTY_SUBSCRIPTION_REJECT: {
                Object[] mapEntry = (Object[]) createdClasses.get(incomingDataKey);
                if (mapEntry != null) {
                    data.setEventType(SmObserverEvent.ATTRIBUTE_CHANGE);
                    MR_AnyValString value = new MR_AnyValString("Could not subscribe to this parameter. Reason : " + data.getValue().toString());
                    data.setValue(value);
                    processAttributeChange(data);
                }
                return null;
            }
            case SmObserverEvent.RELATION_CHANGE: {
                return processAttributeChange(data);
            }
            case SmObserverEvent.ATTRIBUTE_CHANGE: {
                return processAttributeChange(data);
            }
            default:
                return null;
        }
    }

    protected void getExistingObjects(SmartsSubscribeParameters[] parameters) throws Exception {
        isProcessingPreviousInstancesFinished = false;
        Collection col = requestedSubscriptionParameters.values();
        Iterator it = col.iterator();
        while (it.hasNext()) {
            if (!isObserverCreated) {
                return;
            }
            HashMap requestedProperties = (HashMap) it.next();
            String className = requestedProperties.get(SmartsConstants.CLASSNAME).toString();
            String instanceName = requestedProperties.get(SmartsConstants.INSTANCENAME).toString();
            String regExpKey = className + instanceName;
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            MR_Ref[] instances = domainManager.findInstances(className, instanceName, MR_Choice.NONE);
            for (int i = 0; i < instances.length; i++) {
                if (!isObserverCreated) {
                    return;
                }
                PreviouslyCreatedInstance previouslyCreatedInstance = new PreviouslyCreatedInstance(instances[i].getClassName(), instances[i].getInstanceName(), requestedProperties.size(), regExpKey);
                createdClassesDuringStatup.put(instances[i].getInstanceName(), previouslyCreatedInstance);
            }
        }
    }

    protected void deleteSmartsObserver() {
        super.deleteSmartsObserver();
        createdClasses.clear();
    }

    private Map getFilteredPropertiesAsMap(String className, String[] propertyFilterList) throws Exception {
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        Map filteredPropertiesMap = new HashMap();
        String[] attributes = domainManager.getAttributeNames(className);
        String[] relations = domainManager.getRelationNames(className);
        String[] allAttributes = new String[attributes.length + relations.length];
        System.arraycopy(attributes, 0, allAttributes, 0, attributes.length);
        System.arraycopy(relations, 0, allAttributes, attributes.length, relations.length);
        if (propertyFilterList == null || propertyFilterList.length == 0) {
            propertyFilterList = new String[]{".*"};
        }
        for (int j = 0; j < propertyFilterList.length; j++) {
            boolean foundOneInstance = false;
            String currentProperty = propertyFilterList[j];
            int propAccess = -1;
            if (!currentProperty.equals(".*")) {
                propAccess = domainManager.getPropAccess(className, currentProperty);
            }
            if (propAccess == MR_AccessType.MR_COMPUTED_WITH_EXPRESSION || propAccess == MR_AccessType.MR_STORED ||
                    propAccess == MR_AccessType.MR_STORED || propAccess == -1 || propAccess == UNKNOWN_ACCESS_TYPE) {
                for (int k = 0; k < allAttributes.length; k++) {
                    String attributeNameInClass = allAttributes[k];
                    if (attributeNameInClass.matches(propertyFilterList[j])) {
                        foundOneInstance = true;
                        filteredPropertiesMap.put(attributeNameInClass, attributeNameInClass);
                    }
                }
                if (!foundOneInstance) {
                    throw new Exception("No matches found for requested property : " + propertyFilterList[j] + " in class " + className);
                }
            } else {
                logger.warn(logPrefix + "Cannot subscribe to the attribute: " + currentProperty);
            }
        }
        filteredPropertiesMap.put(SmartsConstants.CLASSNAME, SmartsConstants.CLASSNAME);
        filteredPropertiesMap.put(SmartsConstants.INSTANCENAME, SmartsConstants.INSTANCENAME);
        return filteredPropertiesMap;
    }

    private void getClassHierarchyList(List classList, String className) throws IOException, SmRemoteException {
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        classList.add(className);
        String[] childClasses = domainManager.getChildren(className);
        if (childClasses != null) {
            for (int j = 0; j < childClasses.length; j++) {
                getClassHierarchyList(classList, childClasses[j]);
            }
        }
    }

    private String getHashMapKeyMatchesInputKey(Map map, String key) {
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        String currentKey;
        while (it.hasNext()) {
            currentKey = (String) it.next();
            if (key.matches(currentKey)) {
                return currentKey;
            }
        }
        return null;
    }

    private Object processCreateEvent(String className, String instanceName, String regularExpressionKey) {
        String currentKey = className + instanceName;

        if (!createdClasses.containsKey(currentKey)) {
            HashMap propertymap = (HashMap) requestedSubscriptionParameters.get(regularExpressionKey);
            int expectedNumberOfParameters = propertymap.size();
            HashMap properties = new HashMap();
            properties.put(SmartsConstants.CLASSNAME, new MR_AnyValString(className));
            properties.put(SmartsConstants.INSTANCENAME, new MR_AnyValString(instanceName));
            if (expectedNumberOfParameters <= 2) {
                return createObject(CREATE, convertPropertiesMapToPropertiesArray(properties));
            } else {
                Object[] mapEntry = new Object[2];
                mapEntry[0] = new Integer(expectedNumberOfParameters);
                mapEntry[1] = properties;
                createdClasses.put(currentKey, mapEntry);
            }
        } else {
            logger.debug(logPrefix + "Duplicate create event recieved for notification " + currentKey);
        }
        return null;
    }

    private Object processAttributeChange(DataFromObservable data) {
        if (isPrevious(data.getClassName(), data.getInstanceName())) {
            return null;
        }
        String incomingDataKey = data.getClassName() + data.getInstanceName();
        String propName = data.getPropertyName();
        if (propName.equals(SmartsConstants.CLASSNAME) || propName.equals(SmartsConstants.INSTANCENAME)) {
            return null;
        }

        Object[] mapEntry = (Object[]) createdClasses.get(incomingDataKey);

        if (mapEntry != null) {
            HashMap properties = (HashMap) mapEntry[1];
            properties.put(propName, data.getValue());
            int expectedSize = ((Integer) mapEntry[0]).intValue();
            if (expectedSize == properties.size()) {
                MR_PropertyNameValue[] nameValuePairs = convertPropertiesMapToPropertiesArray(properties);
                createdClasses.remove(incomingDataKey);
                return createObject(CREATE, nameValuePairs);
            }

        } else {
            MR_PropertyNameValue[] nameValuePairs = convertIncomingDataToMR_PropertyNameValue(data);
            MR_PropertyNameValue[] convertedNameValuePairs = new MR_PropertyNameValue[4];
            convertedNameValuePairs[0] = nameValuePairs[0];
            convertedNameValuePairs[1] = nameValuePairs[1];
            convertedNameValuePairs[2] = (new MR_PropertyNameValue("ModifiedAttributeName", new MR_AnyValString(nameValuePairs[2].getPropertyName())));
            convertedNameValuePairs[3] = (new MR_PropertyNameValue("ModifiedAttributeValue", nameValuePairs[2].getPropertyValue()));
            return createObject(CHANGE, convertedNameValuePairs);
        }
        return null;
    }

    protected MR_PropertyNameValue[] convertPropertiesMapToPropertiesArray(HashMap properties) {
        MR_PropertyNameValue[] nameValuePairs = new MR_PropertyNameValue[properties.size()];
        int count = 0;
        Set keys = properties.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String propertyName = (String) it.next();
            MR_AnyVal value = (MR_AnyVal) properties.get(propertyName);
            nameValuePairs[count] = new MR_PropertyNameValue(propertyName, value);
            count++;
        }
        return nameValuePairs;
    }

    private boolean isPrevious(String className, String instanceName) {
        if (createdClassesDuringStatup != null) {
            PreviouslyCreatedInstance ins;
            synchronized (createdClassesDuringStatup) {
                ins = (PreviouslyCreatedInstance) createdClassesDuringStatup.get(instanceName);
            }
            if (ins != null) {
                if (ins.numberOfParameters > 0) {
                    ins.decreaseNumbeOfExpected();
                    return true;
                } else {
                    createdClassesDuringStatup.remove(instanceName);
                }
            }
        }
        return false;
    }

    public void processExisting() {
        int numberOfCreates = 0;
        HashMap allRequestedProps = new HashMap();
        Iterator it = requestedSubscriptionParameters.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            HashMap allReqProps = (HashMap) requestedSubscriptionParameters.get(key);
            Iterator it2 = allReqProps.keySet().iterator();
            String[] allProps = new String[allReqProps.size()];
            int count = 0;
            while (it2.hasNext()) {
                allProps[count] = it2.next().toString();
                count++;
            }
            allRequestedProps.put(key, allProps);
        }
        it = createdClassesDuringStatup.values().iterator();
        SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
        while (it.hasNext()) {
            PreviouslyCreatedInstance ins = (PreviouslyCreatedInstance) it.next();
            String[] requestedProps = (String[]) allRequestedProps.get(ins.regExpKey);
            if (requestedProps != null) {
                try {
                    MR_AnyVal[] props = domainManager.getProperties(ins.className, ins.instanceName, requestedProps);
                    MR_PropertyNameValue[] nameValues = new MR_PropertyNameValue[props.length];
                    for (int i = 0; i < nameValues.length; i++) {
                        nameValues[i] = new MR_PropertyNameValue(requestedProps[i], props[i]);
                    }
                    numberOfCreates++;
                    sendDataToObservers(createObject(CREATE, nameValues));
                }
                catch (IOException e) {

                }
                catch (SmRemoteException e) {
                    it.remove();
                    sendDataToObservers(processCreateEvent(new String(ins.className), new String(ins.instanceName), new String(ins.regExpKey)));
                }
                catch (Exception e) {
                }
            }
        }
    }

    class PreviouslyCreatedInstance {
        String className;
        String instanceName;
        String regExpKey;
        int numberOfParameters;

        public PreviouslyCreatedInstance(String className, String instanceName, int numberOfParameters, String regExpKey) {
            super();
            this.className = className;
            this.instanceName = instanceName;
            this.numberOfParameters = numberOfParameters;
            this.regExpKey = regExpKey;
        }


        public void decreaseNumbeOfExpected() {
            numberOfParameters--;
        }

        public boolean willBeDeleted() {
            return numberOfParameters <= 0;
        }


    }

}
