import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events

import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import org.apache.log4j.DailyRollingFileAppender
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.apache.log4j.Level
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
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
            defaultColumnsToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo"],
            columnsMapping:[SNMPAddress:"snmpAddress"]
    ],
    RsComputerSystemComponent :[
            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], LogicalDisk:[:], NumericSensor:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsGroup:[
            classes:[CardRedundancyGroup:[:], RedundancyGroup:[:], SystemRedundancyGroup:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsLink: [
            classes:[NetworkConnection:[:], Cable:[:], TrunkCable:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsCard: [
            classes:[Card:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsIp: [
            classes:[IP:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsInterface: [
            classes:[Interface:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsPort: [
            classes:[Port:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
    ],
    RsManagementServer: [
            classes:[ManagementServer:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[:]
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
        def columnMap = [:];
        COLUMN_MAPPING_DATA[rsClassName] = columnMap;
        GrailsDomainClass gdc = ApplicationHolder.getApplication().getDomainClass(rsClassName);
        def dcProperties = gdc.getProperties();
        def relations = DomainClassUtils.getRelations(gdc);
        dcProperties.each{GrailsDomainClassProperty prop->
            if(prop.isPersistent() && !relations.containsKey(prop.name))
            {
                def propName = prop.getName();
                def smartsName = propName.substring(0,1).toUpperCase()+propName.substring(1);
                columnMap[smartsName] = propName;
            }
        }
        columnMap.putAll (CLASS_MAPPINGS[rsClassName].columnsMapping);
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
            monitoredAttribute = colMapping[monitoredAttribute]
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
        def connectedAdapters = [];
        connectionFromSmarts.ConnectedTo.each{networkAdapter->
            def rsNetworkAdapterObjects = RsNetworkAdapter.get(name:networkAdapter.Name);
            if(rsNetworkAdapterObjects)
            {
                connectedAdapters.add(rsNetworkAdapterObjects);
            }
        }
        connObject.addRelation(connectedTo:connectedAdapters);
    }
    else {
        logger.warn("Error adding connection object ${topologyObject}. Reason: ${connObject.errors}");
    }
    return connObject;

}

def getDatasource() {
    return datasource;
}

def getPropsWithLocalNames(String className, topologyObject) {
    def cols = COLUMN_MAPPING_DATA[className]
    def props = [:]
    cols.each {String smartsName, String localName->
        def value = topologyObject[smartsName];
        props.put(localName, value);
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
    def deviceProps = getPropsWithLocalNames("RsComputerSystem", deviceFromSmarts);
    deviceProps["managementServer"] = datasource.connection.domain;
    RsComputerSystem computerSystem = RsComputerSystem.add(deviceProps)
    if (!computerSystem.hasErrors()) {
        def existingCompSystems = getExistingCompouterSystems(computerSystem.name);
        def existingConnections = getExistingConnections(computerSystem.name);
        def computerSystemProcessor = {computerSystemComponents->
            def addedComputerSystemComponents = [];
            computerSystemComponents.each{
                existingCompSystems.remove(it.Name);
                 Map containmentObjectFromSmarts = getDatasource().getObject(it);
                 if (isComputerSystemComponent(containmentObjectFromSmarts.CreationClassName))
                 {
                    logger.debug("Creating ComputerSystemComponent object  ${containmentObjectFromSmarts.Name} of class ${containmentObjectFromSmarts.CreationClassName}");
                    RsComputerSystemComponent containmentObject = addComputerSystemComponent(containmentObjectFromSmarts, computerSystem.name);
                    if (!containmentObject.hasErrors()) {
                        addedComputerSystemComponents.add(containmentObject);
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
            return addedComputerSystemComponents;
        }
        def addedHostAccessPoints = computerSystemProcessor(Arrays.asList(deviceFromSmarts.HostsAccessPoints));
        def addedComposedOf = computerSystemProcessor(Arrays.asList(deviceFromSmarts.ComposedOf));
        computerSystem.addRelation("hostsAccessPoints":addedHostAccessPoints);
        computerSystem.addRelation("composedOf":addedComposedOf);
        existingCompSystems.each{String name, String value->
            RsComputerSystemComponent.get(name:name).remove();
        }

        def connectionProcessor = {connectionTopologyObjects->
            def connectionObjects = [];
            connectionTopologyObjects.each{
                existingConnections.remove(it.Name);
                Map connectionObjectFromSmarts = getDatasource().getObject(it);
                if (isConnection(connectionObjectFromSmarts.CreationClassName)) {
                    logger.debug("Creating connection object  ${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName}");
                    def addedConnObject = addConnectionObject(connectionObjectFromSmarts);
                    if(!addedConnObject.hasErrors())
                    {
                        connectionObjects.add(addedConnObject);
                    }
                }
                else
                {
                    logger.debug("${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName} is not a RsLink object discarding");
                }
            }
            return connectionObjects;
        }
        def addedConnectionObjects = connectionProcessor(deviceFromSmarts.ConnectedVia);
        computerSystem.addRelation("connectedVia":addedConnectionObjects);

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
    def addedRsComputerSystemObject = null;
    if (CLASS_MAPPINGS.RsInterface.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("RsInterface", containmentObjectFromSmarts);
        getLogger().debug("Creating RsInterface with ${props}")
        addedRsComputerSystemObject = RsInterface.add(props);
    }
    else if (CLASS_MAPPINGS.RsCard.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("RsCard", containmentObjectFromSmarts);
        getLogger().debug("Creating RsCard with ${props}")
        addedRsComputerSystemObject = RsCard.add(props);
        def connectedAdapters = [];
        containmentObjectFromSmarts.Realizes.each{networkAdapter->
            def rsNetworkAdapterObjects = RsNetworkAdapter.get(name:networkAdapter.Name);
            if(rsNetworkAdapterObjects)
            {
                connectedAdapters.add(rsNetworkAdapterObjects);
            }
        }
        addedRsComputerSystemObject.addRelation(realizes:connectedAdapters);
    }
    else if (CLASS_MAPPINGS.RsIp.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("RsIp", containmentObjectFromSmarts);
        getLogger().debug("Creating RsIp with ${props}")
        addedRsComputerSystemObject = RsIp.add(props);
        def connectedAdapters = [];
        containmentObjectFromSmarts.Realizes.each{networkAdapter->
            def rsNetworkAdapterObjects = RsNetworkAdapter.get(name:networkAdapter.Name);
            if(rsNetworkAdapterObjects)
            {
                connectedAdapters.add(rsNetworkAdapterObjects);
            }
        }
        addedRsComputerSystemObject.addRelation(realizes:connectedAdapters);
    }
    else if (CLASS_MAPPINGS.RsPort.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("RsPort", containmentObjectFromSmarts);
        getLogger().debug("Creating RsPort with ${props}")
        addedRsComputerSystemObject = RsPort.add(props);
    }
    else
    {

        def props = getPropsWithLocalNames("RsComputerSystemComponent", containmentObjectFromSmarts);
        getLogger().debug("Creating RsComputerSystemComponent with ${props}")
        addedRsComputerSystemObject = RsComputerSystemComponent.add(props);
    }

    def layeredOverObjects = [];
    containmentObjectFromSmarts.LayeredOver.each{connectedComputerSystemObject->
        def rsConnectedComputerSystemObject = RsComputerSystemComponent.get(name:connectedComputerSystemObject.Name);
        if(rsConnectedComputerSystemObject)
        {
            layeredOverObjects.add(rsConnectedComputerSystemObject);
        }
    }
    def underlyingObjects = [];
    containmentObjectFromSmarts.Underlying.each{connectedComputerSystemObject->
        def rsConnectedComputerSystemObject = RsComputerSystemComponent.get(name:connectedComputerSystemObject.Name);
        if(rsConnectedComputerSystemObject)
        {
            underlyingObjects.add(rsConnectedComputerSystemObject);
        }
    }

    addedRsComputerSystemObject.addRelation(layeredOver:layeredOverObjects);
    addedRsComputerSystemObject.addRelation(underlying:underlyingObjects);

    if(addedRsComputerSystemObject instanceof RsNetworkAdapter)
    {
        def realizedByObjects = [];
        containmentObjectFromSmarts.RealizedBy.each{connectedComputerSystemObject->
            def rsConnectedComputerSystemObject = RsCard.get(name:connectedComputerSystemObject.Name);
            if(rsConnectedComputerSystemObject)
            {
                realizedByObjects.add(rsConnectedComputerSystemObject);
            }
        }
        def connectedViaObjects = [];
        containmentObjectFromSmarts.ConnectedVia.each{connectedComputerSystemObject->
            def rsConnectedComputerSystemObject = RsLink.get(name:connectedComputerSystemObject.Name);
            if(rsConnectedComputerSystemObject)
            {
                connectedViaObjects.add(rsConnectedComputerSystemObject);
            }
        }

        addedRsComputerSystemObject.addRelation(realizedBy:realizedByObjects);
        addedRsComputerSystemObject.addRelation(connectedVia:connectedViaObjects);
    }
    return addedRsComputerSystemObject;
}