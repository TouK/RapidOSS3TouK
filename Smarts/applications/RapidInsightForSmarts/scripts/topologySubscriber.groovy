import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.log4j.Level
import org.apache.commons.collections.map.CaseInsensitiveMap
import datasource.SmartsModel
import datasource.SmartsModelColumn
import org.apache.commons.lang.StringUtils

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

//CLASS_MAPPINGS = [
//    RsComputerSystem : [
//            classes:[Bridge:[:], Hub:[:], Host:[:], Node:[:], Switch:[:], Router:[:], CallServer:[:], Firewall:[:], LoadBalancer:[:], MediaGateway:[:], RelayDevice:[:], TerminalServer:[:]],
//            defaultColumnsToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo", "ComposedOf", "HostsAccessPoints"]
//    ],
//    RsComputerSystemComponent :[
//            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], FileServer:[:], LogicalDisk:[:], NumericSensor:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsGroup:[
//            classes:[CardRedundancyGroup:[:], RedundancyGroup:[:], SystemRedundancyGroup:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsLink: [
//            classes:[NetworkConnection:[:], Cable:[:], TrunkCable:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsCard: [
//            classes:[Card:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsIp: [
//            classes:[Ip:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsInterface: [
//            classes:[Interface:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsPort: [
//            classes:[Port:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsHsrpGroup: [
//            classes:[HsrpGroup:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ],
//    RsManagementServer: [
//            classes:[ManagementServer:[:]],
//            defaultColumnsToBeSubscribed: ["Name"]
//    ]
//]


//CLASSES_TO_BE_SUBSCRIBED = ["RsComputerSystem", "RsComputerSystemComponent", "RsGroup", "RsLink", "RsCard", "RsIp", "RsInterface", "RsPort", "RsHsrpGroup", "RsManagementServer"]



CLASS_MAPPINGS = [
    RsComputerSystem : [
            classes:[Bridge:[:], Hub:[:], Host:[:], Node:[:], Switch:[:], Router:[:], Firewall:[:], RelayDevice:[:], TerminalServer:[:]],
            defaultColumnsToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo"]
    ],
    RsComputerSystemComponent :[
            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], LogicalDisk:[:], NumericSensor:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsGroup:[
            classes:[CardRedundancyGroup:[:], RedundancyGroup:[:], SystemRedundancyGroup:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsLink: [
            classes:[NetworkConnection:[:], Cable:[:], TrunkCable:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsCard: [
            classes:[Card:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsIp: [
            classes:[IP:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsInterface: [
            classes:[Interface:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsPort: [
            classes:[Port:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ],
    RsManagementServer: [
            classes:[ManagementServer:[:]],
            defaultColumnsToBeSubscribed: ["Name"]
    ]
]


CLASSES_TO_BE_SUBSCRIBED = ["RsComputerSystem", "RsComputerSystemComponent", "RsGroup", "RsLink", "RsCard", "RsIp", "RsInterface", "RsPort"]







COLUMN_MAPPING_DATA = null;
logger = null;
Map topologyMap = null;
existingObjectsRetrieved = false;

def getParameters() {
    COLUMN_MAPPING_DATA = [:];
    def params = [];

    CLASSES_TO_BE_SUBSCRIBED.each{String rsClassName->

        SmartsModel model = SmartsModel.get(name:rsClassName);
        def columnMap = [:];
        COLUMN_MAPPING_DATA[rsClassName] = columnMap;
        while(model != null)
        {
            model.columns.each{SmartsModelColumn col->
                columnMap[col.smartsName] = col;
            }
            if(model.parentName != null)
            {
                model = SmartsModel.get(name:model.parentName);
            }
            else
            {
                model = null;
            }
        }

        def colsToBeSubscribed = CLASS_MAPPINGS[rsClassName].defaultColumnsToBeSubscribed;
        
        CLASS_MAPPINGS[rsClassName].classes.each{String smartsClassName, Map classConfig->
            params.add([CreationClassName: smartsClassName, Name: ".*", Attributes: colsToBeSubscribed]);
        }
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
    def deviceNames = RsSmartsObject.termFreqs("name", [size:10000000000]);
    deviceNames.each {
        topologyMap[it.getTerm()] = "deleted";
    }
    logger.debug("Marked all devices as deleted.");
    existingObjectsRetrieved = false;

}

def cleanUp() {
    getLogger().removeAllAppenders();
}

boolean isComputerSystemComponent(String className)
{
    return CLASS_MAPPINGS.RsComputerSystemComponent.classes.containsKey(className) || CLASS_MAPPINGS.RsIp.classes.containsKey(className) || CLASS_MAPPINGS.RsPort.classes.containsKey(className) || CLASS_MAPPINGS.RsInterface.classes.containsKey(className) || CLASS_MAPPINGS.RsCard.classes.containsKey(className);
}

boolean isComputerSystem(String className)
{
    return CLASS_MAPPINGS.RsComputerSystem.classes.containsKey(className);
}

boolean isConnection(String className)
{
    return CLASS_MAPPINGS.RsLink.classes.containsKey(className);
}

def update(topologyObject) {

    String eventType = topologyObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    String className = topologyObject["CreationClassName"];
    if(!existingObjectsRetrieved)
    {
        topologyMap.remove(topologyObject.Name);
    }
    if(eventType == BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED)
    {
        existingObjectsRetrieved = true;
        logger.info("Existing objects retrieved and ${topologyMap.size()} number of objects will be deleted.");
        topologyMap.each{String objectName, String value->
            logger.debug("Deleting non existing object ${objectName}.");
            RsSmartsObject.get(name:objectName)?.remove();
        }
        topologyMap.clear();
    }
    else if (eventType == BaseSmartsListeningAdapter.CREATE) {
        if (isComputerSystem(className)) {
            handleComputerSystemCreate(topologyObject);
        }
    }
    else if (eventType == BaseSmartsListeningAdapter.CHANGE) {
        if (isComputerSystem(className)) {
            handleComputerSystemChange(topologyObject);
        }
        else
        {
            handleChange(topologyObject)
        }

    }
    else if (eventType == BaseSmartsListeningAdapter.DELETE) {
        getLogger().info("Removing object ${topologyObject}.");
        RsSmartsObject.get(name: topologyObject["Name"])?.remove();
    }
}

def handleComputerSystemCreate(Map topologyObject) {
    RsComputerSystem computerSystem = RsComputerSystem.get(name: topologyObject["Name"])
    if (!computerSystem || computerSystem && computerSystem.discoveredLastAt != topologyObject["DiscoveredLastAt"]) {
        addComputerSystemToRepository(topologyObject);
    }
    else
    {
        getLogger().info("DiscoveredLastAt did not changed for object ${topologyObject.Name} of class ${topologyObject.CreationClassName}. It will be ignored");   
    }
}

def handleComputerSystemChange(updateParams) {
    def monitoredAttribute = updateParams["ModifiedAttributeName"]
    def attributeValue = updateParams["ModifiedAttributeValue"]
    if (monitoredAttribute == "DiscoveredLastAt") {
        def topologyObject = [CreationClassName:updateParams.CreationClassName, Name:updateParams.Name]
        topologyObject[monitoredAttribute] = attributeValue;
        handleComputerSystemCreate(topologyObject);
    }
    else
    {
        handleChange(updateParams)

    }
}

def handleChange(updateParams)
{
    def monitoredAttribute = updateParams["ModifiedAttributeName"]
    def attributeValue = updateParams["ModifiedAttributeValue"]
    def name =  updateParams.Name;
    def smartsObject = RsSmartsObject.get(name:name)
    if(smartsObject)
    {
        def colMapping = COLUMN_MAPPING_DATA[smartsObject.class.simpleName];
        if(colMapping && colMapping.containsKey(monitoredAttribute))
        {
            monitoredAttribute = colMapping[monitoredAttribute].localName
            smartsObject.setProperty(monitoredAttribute, attributeValue);
        }

    }
}


def addConnectionObject(topologyObject) {
    getLogger().debug("Create event received for connection object ${topologyObject}")
    Map connectionFromSmarts = getDatasource().getObject(topologyObject);
    def aDisplayName = connectionFromSmarts.A_DisplayName
    def zDisplayName = connectionFromSmarts.Z_DisplayName
    topologyObject.A_ComputerSystemName = StringUtils.substringBetween(aDisplayName, "-", "/");
    topologyObject.A_Name = StringUtils.substringBefore(aDisplayName," [");
    topologyObject.Z_ComputerSystemName = StringUtils.substringBetween(zDisplayName, "-", "/");
    topologyObject.Z_Name = StringUtils.substringBefore(zDisplayName, " [");
    def connObject = RsLink.add(getPropsWithLocalNames("RsLink", topologyObject));
    if (!connObject.hasErrors()) {
        logger.info("Connection object ${topologyObject} successfully added.")
    }
    else {
        logger.warn("Error adding connection object ${topologyObject}. Reason: ${connObject.errors}");
    }

}

def getDatasource() {
    return datasource;
}

def getPropsWithLocalNames(String className, topologyObject) {
    def cols = COLUMN_MAPPING_DATA[className]
    def props = [:]
    cols.each {String colName, SmartsModelColumn col->
        def value = topologyObject[col.smartsName];
        props.put(col.localName, value);
    }
    props.put("rsDatasource",getDatasource().name)
    return props;
}


Logger getLogger() {
    return logger;
}

def getExistingCompouterSystems(String objectName)
{
    def existingComputerSystemComponents = new CaseInsensitiveMap();
    def terms = RsComputerSystemComponent.termFreqs("computerSystemName:\""+objectName+"\"");
    terms.each{
        existingComputerSystemComponents[it.getTerm()] = it.getTerm();
    }

    return existingComputerSystemComponents;
}

def getExistingConnections(String objectName)
{
    def existingConnections = new CaseInsensitiveMap();
    def terms = RsLink.termFreqs("a_ComputerSystemName:\""+objectName+"\"");
    terms.each{
        existingConnections[it.getTerm()] = it.getTerm();
    }

    terms = RsLink.termFreqs("z_ComputerSystemName:\""+objectName+"\"");
    terms.each{
        existingConnections[it.getTerm()] = it.getTerm();
    }

    return existingConnections;
}

def addComputerSystemToRepository(topologyObject) {
    logger.debug("creating ComputertSystem object ${topologyObject.Name} of class ${topologyObject.CreationClassName}")
    Map deviceFromSmarts = getDatasource().getObject(topologyObject);
    RsComputerSystem computerSystem = RsComputerSystem.add(getPropsWithLocalNames("RsComputerSystem", deviceFromSmarts))
    if (!computerSystem.hasErrors()) {
        def existingCompSystems = getExistingCompouterSystems(computerSystem.name);
        def existingConnections = getExistingConnections(computerSystem.name);
        def computerSystemComponents = [];
        computerSystemComponents.addAll(Arrays.asList(deviceFromSmarts.HostsAccessPoints))
        computerSystemComponents.addAll(Arrays.asList(deviceFromSmarts.ComposedOf))
        computerSystemComponents.each{
            existingCompSystems.remove(it.Name);
             Map containmentObjectFromSmarts = getDatasource().getObject(it);
             if (isComputerSystemComponent(containmentObjectFromSmarts.CreationClassName))
             {
                logger.debug("Creating ComputerSystemComponent object  ${containmentObjectFromSmarts.Name} of class ${containmentObjectFromSmarts.CreationClassName}");
                RsComputerSystemComponent containmentObject = addComputerSystemComponent(containmentObjectFromSmarts, computerSystem.name);
                if (!containmentObject.hasErrors()) {
                    logger.info("ComputerSystemComponent object ${containmentObjectFromSmarts} successfully added.");
                }
                else {
                    logger.warn("Error creating ComputerSystemComponent ${containmentObjectFromSmarts}. Reason: ${containmentObject.errors}");
                }
            }
            else
            {
                logger.debug("${containmentObjectFromSmarts.Name} of class ${containmentObjectFromSmarts.CreationClassName} is not a RsComputerSystemComponent object discarding");
            }
        }

        existingCompSystems.each{String name, String value->
            RsComputerSystemComponent.get(name:name).remove();
        }
        deviceFromSmarts.ConnectedVia.each{
            existingConnections.remove(it.Name);
            Map connectionObjectFromSmarts = getDatasource().getObject(it);
            if (isConnection(connectionObjectFromSmarts.CreationClassName)) {
                logger.debug("Creating connection object  ${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName}");
                addConnectionObject(connectionObjectFromSmarts);
            }
            else
            {
                logger.debug("${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName} is not a RsLink object discarding");
            }
        }

        existingConnections.each{String name, String value->
            RsLink.get(name:name).remove();
        }
    }
    else {
        getLogger().warn("Error creating device ${topologyObject["Name"]}. Reason: ${computerSystem.errors}")
    }
}

RsComputerSystemComponent addComputerSystemComponent(containmentObjectFromSmarts, computerSystemName) {
    containmentObjectFromSmarts.ComputerSystemName =computerSystemName;

    if (CLASS_MAPPINGS.RsInterface.classes.containsKey("Interface")) {

        def props = getPropsWithLocalNames("RsInterface", containmentObjectFromSmarts);
        getLogger().debug("Creating RsInterface with ${props}")
        return RsInterface.add(props);
    }
    else if (CLASS_MAPPINGS.RsCard.classes.containsKey("Card")) {

        def props = getPropsWithLocalNames("RsCard", containmentObjectFromSmarts);
        getLogger().debug("Creating RsCard with ${props}")
        return RsCard.add(props);
    }
    else if (CLASS_MAPPINGS.RsIp.classes.containsKey("Ip")) {

        def props = getPropsWithLocalNames("RsIp", containmentObjectFromSmarts);
        getLogger().debug("Creating RsIp with ${props}")
        return RsIp.add(props);
    }
    else if (CLASS_MAPPINGS.RsPort.classes.containsKey("Port")) {

        def props = getPropsWithLocalNames("RsPort", containmentObjectFromSmarts);
        getLogger().debug("Creating RsPort with ${props}")
        return RsPort.add(props);
    }
    else
    {

        def props = getPropsWithLocalNames("RsComputerSystemComponent", containmentObjectFromSmarts);
        getLogger().debug("Creating RsComputerSystemComponent with ${props}")
        return RsComputerSystemComponent.add(props);
    }
}