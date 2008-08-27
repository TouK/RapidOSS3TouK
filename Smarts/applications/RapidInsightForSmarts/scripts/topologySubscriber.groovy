import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.log4j.Level
import org.apache.commons.collections.map.CaseInsensitiveMap

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
 * Date: Aug 6, 2008
 * Time: 3:46:38 PM
 */
DEVICES = ["Host", "Switch", "Router", "Node", "Probe", "TerminalServer", "Bridge", "Hub"]
CONNECTION_OBJECTS = ["NetworkConnection", "Cable", "TrunkCable"]
CONTAINMENT_OBJECTS = ["IP", "Card", "Interface", "Port"]
smartsToRcmdbPropertyMapping = ["A_AdminStatus": "aa_AdminStatus", "A_DisplayName": "aa_DisplayName",
        "A_OperStatus": "aa_OperStatus", "Z_AdminStatus": "zz_AdminStatus",
        "Z_DisplayName": "zz_DisplayName", "Z_OperStatus": "zz_OperStatus",
        "IPStatus": "ipStatus", "Netmask": "netMask"]
logger = null;
topologyMap = null;
//    rcmdbToSmartsPropertyMapping = [:];
//    smartsToRcmdbPropertyMapping.each {key, value ->
//        rcmdbToSmartsPropertyMapping.put(value, key);
//    }
//    DEVICE_PROPS = getClassProperties("Device");
//    LINK_PROPS = getClassProperties("Link");
//    DEVICE_INTERFACE_PROPS = getClassProperties("DeviceInterface");
//    IP_PROPS = getClassProperties("Ip");
//    PORT_PROPS = getClassProperties("Port");
//    CARD_PROPS = getClassProperties("Card");

def getParameters() {
    def params = [];
    DEVICES.each {
        params.add(["CreationClassName": it, "Name": ".*", "Attributes": ["CreationClassName", "Name", "DiscoveredLastAt", "DiscoveryErrorInfo"]]);
    }
    CONTAINMENT_OBJECTS.each {
        params.add(["CreationClassName": it, "Name": ".*", "Attributes": ["CreationClassName", "Name"]]);
    }
    CONNECTION_OBJECTS.each {
        params.add(["CreationClassName": it, "Name": ".*", "Attributes": ["CreationClassName", "Name"]]);
    }
    return ["subscribeParameters": params]
}

def init() {
    logger = Logger.getLogger("topologySubscriber")
    logger.removeAllAppenders();
    def layout = new org.apache.log4j.PatternLayout("%d{yy/MM/dd HH:mm:ss.SSS} %p: %m%n");
    def appender = new DailyRollingFileAppender(layout, "logs/topologySubscriber.log", "'.'yyyy-MM-dd");
    logger.addAppender(appender);
    logger.setAdditivity(false);
    logger.setLevel(Level.toLevel("DEBUG"));

    logger.debug("Marking all devices as deleted.");
    topologyMap = new CaseInsensitiveMap();
    def deviceNames = Device.termFreqs("name").term;
    deviceNames.each {
        topologyMap[it] = "deleted";
    }

}

def cleanUp() {
    getLogger().removeAllAppenders();
}

def update(topologyObject) {

    def eventType = topologyObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    def className = topologyObject["CreationClassName"];

    if (eventType == BaseSmartsListeningAdapter.CREATE) {
        if (DEVICES.contains(className)) {
            handleDeviceCreate(topologyObject);
        }
        else if (CONNECTION_OBJECTS.contains(className)) {
            handleConnectionObjectCreate(topologyObject)
        }
    }
    else if (eventType == BaseSmartsListeningAdapter.CHANGE) {
        if (DEVICES.contains(className)) {
            handleDeviceChange(topologyObject);
        }
    }
    else if (eventType == BaseSmartsListeningAdapter.DELETE) {
        getLogger().info("Removing object ${topologyObject}.");
        SmartsObject.get(name: topologyObject["Name"])?.remove();
    }
}

def handleDeviceCreate(topologyObject) {
    def device = Device.get(name: topologyObject["Name"])
    if (device) {
        if (device.discoveredLastAt != topologyObject["DiscoveredLastAt"]) {
            addSmartsDeviceToRepository(topologyObject);
        }
    }
    else {
        addSmartsDeviceToRepository(topologyObject);
    }
}

def handleDeviceChange(topologyObject) {
    def monitoredAttribute = topologyObject["ModifiedAttributeName"]
    def attributeValue = topologyObject["ModifiedAttributeValue"]
    if (monitoredAttribute == "DiscoveredLastAt") {
        addSmartsDeviceToRepository(topologyObject);
    }
}

def handleConnectionObjectCreate(topologyObject) {
    getLogger().debug("Create event received for connection object ${topologyObject}")
    def connObject = Link.add(getPropsWithLocalNames(topologyObject));
    if (!connObject.hasErrors()) {
        def connObjFromSmarts = getDatasource().getObject(topologyObject);
        logger.info("Connection object ${topologyObject} successfully added.")
        connObjFromSmarts.ConnectedSystems.each {deviceFromSmarts ->
            def device = Device.add(name: deviceFromSmarts.Name, creationClassName: deviceFromSmarts.CreationClassName);
            if (!device.hasErrors()) {
                logger.info("Device ${deviceFromSmarts} successfully added.");
                connObject.addRelation(connectedSystems: device);
                logger.info("Relation between ${topologyObject} and ${deviceFromSmarts} is created.")
            }
            else {
                logger.warn("Error creating device ${deviceFromSmarts}. Reason: ${device.errors}");
            }
        }
        connObjFromSmarts.ConnectedTo.each {containmentObjectFromSmarts ->
            if (CONTAINMENT_OBJECTS.contains(containmentObjectFromSmarts.CreationClassName)) {
                logger.debug("Creating containment object ${containmentObjectFromSmarts}");
                def containmentObject = addContainmentObject(containmentObjectFromSmarts);
                if (!containmentObject.hasErrors()) {
                    logger.info("Containment object ${containmentObjectFromSmarts} successfully added.");
                    connObject.addRelation(connectedTo: containmentObject);
                    logger.info("Relation between ${topologyObject} and ${containmentObjectFromSmarts} is created.")
                }
                else {
                    logger.warn("Error creating containment object ${containmentObjectFromSmarts}. Reason: ${containmentObject.errors}");
                }
            }
        }
    }
    else {
        logger.warn("Error adding connection object ${topologyObject}. Reason: ${connObject.errors}");
    }

}

def getDatasource() {
    return datasource;
}

def getPropsWithLocalNames(topologyObject) {
    def props = [:]
    topologyObject.each {key, value ->
        def localKey;
        if (smartsToRcmdbPropertyMapping.containsKey(key)) {
            localKey = smartsToRcmdbPropertyMapping.get(key)
        }
        else {
            localKey = key.substring(0, 1).toLowerCase() + key.substring(1);
        }
        props.put(localKey, value);
    }
    return props;
}


def getLogger() {
    return logger;
}

def addSmartsDeviceToRepository(topologyObject) {
    logger.debug("creating device ${topologyObject.Name} with class ${topologyObject.CreationClassName}")
    def deviceFromSmarts = getDatasource().getObject(topologyObject);
    def device = Device.add(getPropsWithLocalNames(deviceFromSmarts))
    if (!device.hasErrors()) {
        topologyMap.remove(device.name);
        deviceFromSmarts.ComposedOf.each{
             def containmentObjectFromSmarts = getDatasource().getObject(it);
             if (CONTAINMENT_OBJECTS.contains(containmentObjectFromSmarts.CreationClassName)) {
                logger.debug("Creating containment object  ${containmentObjectFromSmarts}");
                def containmentObject = addContainmentObject(containmentObjectFromSmarts);
                if (!containmentObject.hasErrors()) {
                    logger.info("Containment object ${containmentObjectFromSmarts} successfully added.");
                    //device.addRelation(composedOf: containmentObject);
                    //logger.info("Relation between ${topologyObject} and ${containmentObjectFromSmarts} is created.")
                }
                else {
                    logger.warn("Error creating device ${containmentObjectFromSmarts}. Reason: ${containmentObject.errors}");
                }
            }                        
        }
        deviceFromSmarts.HostsAccessPoints.each{
             def containmentObjectFromSmarts = getDatasource().getObject(it);
             if (CONTAINMENT_OBJECTS.contains(containmentObjectFromSmarts.CreationClassName)) {
                logger.debug("Creating containment object  ${containmentObjectFromSmarts}");
                def containmentObject = addContainmentObject(containmentObjectFromSmarts);
                if (!containmentObject.hasErrors()) {
                    logger.info("Containment object ${containmentObjectFromSmarts} successfully added.");
                    //device.addRelation(hostsAccessPoints: containmentObject);
                    //logger.info("Relation between ${topologyObject} and ${containmentObjectFromSmarts} is created.")
                }
                else {
                    logger.warn("Error creating device ${containmentObjectFromSmarts}. Reason: ${containmentObject.errors}");
                }
            }
        }
    }
    else {
        getLogger().warn("Error creating device ${topologyObject["Name"]}. Reason: ${device.errors}")
    }
}

def addContainmentObject(containmentObjectFromSmarts) {
    def props = getPropsWithLocalNames(containmentObjectFromSmarts);
    if (props.creationClassName == "Interface") {
        getLogger().debug("Creating DeviceInterface with ${props}")
        return DeviceInterface.add(props);
    }
    else if (props.creationClassName == "Card") {
        getLogger().debug("Creating Card with ${props}")
        return Card.add(props);
    }
    else if (props.creationClassName == "Port") {
        getLogger().debug("Creating Port with ${props}")
        return Port.add(props);
    }
    else if (props.creationClassName == "IP") {
        getLogger().debug("Creating Ip with ${props}")
        return Ip.add(props);
    }
    else if (props.creationClassName == "MAC") {

    }
    else if (props.creationClassName == "SNMPAgent") {

    }
}

def getClassProperties(className) {
    def excludedProps = ['version', 'id', 'maxNumberOfConnections', "errors", "__operation_class__", "__is_federated_properties_loaded__",
            Events.ONLOAD_EVENT, Events.BEFORE_DELETE_EVENT, Events.BEFORE_INSERT_EVENT, Events.BEFORE_UPDATE_EVENT];
    def properties = ApplicationHolder.application..getDomainClass(className).getProperties();
    def propNames = [];
    properties.each {
        if (!excludedProps.contains(it.name)) {
            propNames.add(it.name);
        }
    }
    return propNames;
}