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
    SmartsComputerSystem : [
            classes:[Bridge:[:], Hub:[:], Host:[:], Node:[:], Switch:[:], Router:[:], Firewall:[:], RelayDevice:[:], TerminalServer:[:]],
            defaultColumnsToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo"],
            columnsMapping:[SNMPAddress:"snmpAddress", CreationClassName:"className"]
    ],
    SmartsComputerSystemComponent :[
            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], LogicalDisk:[:], NumericSensor:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    RsGroup:[
            classes:[CardRedundancyGroup:[:], RedundancyGroup:[:], SystemRedundancyGroup:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsLink: [
            classes:[NetworkConnection:[:], Cable:[:], TrunkCable:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsCard: [
            classes:[Card:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsIp: [
            classes:[IP:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsInterface: [
            classes:[Interface:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsPort: [
            classes:[Port:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    RsManagementSystem: [
            classes:[ManagementServer:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ]
]


CLASSES_TO_BE_SUBSCRIBED = ["SmartsComputerSystem", "SmartsComputerSystemComponent", "RsGroup", "SmartsLink", "SmartsCard", "SmartsIp", "SmartsInterface", "SmartsPort"]







COLUMN_MAPPING_DATA = null;

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
    logger.debug("Marking all devices as deleted.");
    topologyMap = new CaseInsensitiveMap();
    def deviceNames = RsTopologyObject.propertySummary("alias:* AND rsDatasource:\"${getDatasource().name}\"", ["name"]);
    deviceNames.name.each {propertyValue, occurrenceCount->
        topologyMap[propertyValue] = "deleted";
    }
    logger.debug("Marked all devices as deleted.");
    existingObjectsRetrieved = false;

}

def cleanUp() {
    
}

boolean isComputerSystemComponent(String className)
{
    return CLASS_MAPPINGS.SmartsComputerSystemComponent.classes.containsKey(className) || CLASS_MAPPINGS.SmartsIp.classes.containsKey(className) || CLASS_MAPPINGS.SmartsPort.classes.containsKey(className) || CLASS_MAPPINGS.SmartsInterface.classes.containsKey(className) || CLASS_MAPPINGS.SmartsCard.classes.containsKey(className);
}

boolean isComputerSystem(String className)
{
    return CLASS_MAPPINGS.SmartsComputerSystem.classes.containsKey(className);
}

boolean isConnection(String className)
{
    return CLASS_MAPPINGS.SmartsLink.classes.containsKey(className);
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
            RsTopologyObject.get(name:objectName)?.remove();
        }
        topologyMap.clear();
    }
    else if (eventType == BaseSmartsListeningAdapter.CREATE) {
        if (isComputerSystem(className)) {
            handleComputerSystemCreate(topologyObject);
        }
        else if(isConnection(className))
        {

            Map connectionObjectFromSmarts = getDatasource().getObject(topologyObject);
            Map connectedSystemIdentifiers = getConnectedComputerSystemProps(connectionObjectFromSmarts);
            def aComputerSystem = SmartsComputerSystem.get(name:connectedSystemIdentifiers.A_Name);
            def zComputerSystem = SmartsComputerSystem.get(name:connectedSystemIdentifiers.Z_Name);
            if(aComputerSystem != null && zComputerSystem != null)
            {
                logger.debug("Creating connection object  ${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName}");
                def addedConnObject = addConnectionObject(connectionObjectFromSmarts);
                if(!addedConnObject.hasErrors())
                {
                    addedConnObject.addRelation("connectedSystem":[aComputerSystem, zComputerSystem]);                    
                }
                else
                {
                    logger.debug("Could not created connection object  ${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName}. Reason:"+addedConnObject.errors);    
                }
            }
            else
            {
                logger.debug("Could not created connection object  ${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName} because connected SmartsComputerSystem instances does not exist in repository.");   
            }
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
        RsTopologyObject.get(name: topologyObject["Name"])?.remove();
    }
}

def handleComputerSystemCreate(Map topologyObject) {
    SmartsComputerSystem computerSystem = SmartsComputerSystem.get(name: topologyObject["Name"])
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
    def smartsObject = RsTopologyObject.get(name:name)
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

def getConnectedComputerSystemProps(connectionFromSmarts)
{
    def topologyObject = [:]
    def aDisplayName = connectionFromSmarts.A_DisplayName
    def zDisplayName = connectionFromSmarts.Z_DisplayName
    topologyObject.A_ComputerSystemName = StringUtils.substringBetween(aDisplayName, "-", "/");
    topologyObject.A_Name = StringUtils.substringBefore(aDisplayName," [");
    topologyObject.Z_ComputerSystemName = StringUtils.substringBetween(zDisplayName, "-", "/");
    topologyObject.Z_Name = StringUtils.substringBefore(zDisplayName, " [");
    return topologyObject;
}
def addConnectionObject(topologyObject) {
    getLogger().debug("Create event received for connection object ${topologyObject}")
    Map connectionFromSmarts = getDatasource().getObject(topologyObject);
    def connectedSystemProps = getConnectedComputerSystemProps(topologyObject)
    topologyObject.putAll(connectedSystemProps);
    def connObject = SmartsLink.add(getPropsWithLocalNames("SmartsLink", topologyObject));
    if (!connObject.hasErrors()) {
        logger.info("Connection object ${topologyObject} successfully added.")
        def connectedAdapters = [];
        connectionFromSmarts.ConnectedTo.each{networkAdapter->
            def rsNetworkAdapterObjects = SmartsNetworkAdapter.get(name:networkAdapter.Name);
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
    def computerSystemComponentNames = SmartsComputerSystemComponent.propertySummary("computerSystemName:\"${objectName}\"", ["name"]);
    computerSystemComponentNames.name.each {propertyValue, occurrenceCount->
        existingComputerSystemComponents[propertyValue] = propertyValue;
    }
    return existingComputerSystemComponents;
}

def getExistingConnections(String objectName)
{
    def existingConnections = new CaseInsensitiveMap();
    def connectionNames = SmartsLink.propertySummary("a_ComputerSystemName:\"${objectName}\"", ["name"]);
    connectionNames.name.each {propertyValue, occurrenceCount->
        existingConnections[propertyValue] = propertyValue;
    }

    connectionNames = SmartsLink.propertySummary("z_ComputerSystemName:\"${objectName}\"", ["name"]);
    connectionNames.name.each {propertyValue, occurrenceCount->
        existingConnections[propertyValue] = propertyValue;
    }
    return existingConnections;
}

def addComputerSystemToRepository(topologyObject) {
    logger.debug("creating ComputertSystem object ${topologyObject.Name} of class ${topologyObject.CreationClassName}")
    Map deviceFromSmarts = getDatasource().getObject(topologyObject);
    def deviceProps = getPropsWithLocalNames("SmartsComputerSystem", deviceFromSmarts);
    deviceProps["managementServer"] = datasource.connection.domain;
    SmartsComputerSystem computerSystem = SmartsComputerSystem.add(deviceProps)
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
                    SmartsComputerSystemComponent containmentObject = addComputerSystemComponent(containmentObjectFromSmarts, computerSystem.name);
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
                    logger.debug("${containmentObjectFromSmarts.Name} of class ${containmentObjectFromSmarts.CreationClassName} is not a SmartsComputerSystemComponent object discarding");
                }
            }
            return addedComputerSystemComponents;
        }
        def addedHostAccessPoints = computerSystemProcessor(Arrays.asList(deviceFromSmarts.HostsAccessPoints));
        def addedComposedOf = computerSystemProcessor(Arrays.asList(deviceFromSmarts.ComposedOf));
        computerSystem.addRelation("hostsAccessPoints":addedHostAccessPoints);
        computerSystem.addRelation("composedOf":addedComposedOf);
        existingCompSystems.each{String name, String value->
            SmartsComputerSystemComponent.get(name:name).remove();
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
                    logger.debug("${connectionObjectFromSmarts.Name} of class ${connectionObjectFromSmarts.CreationClassName} is not a SmartsLink object discarding");
                }
            }
            return connectionObjects;
        }
        def addedConnectionObjects = connectionProcessor(deviceFromSmarts.ConnectedVia);
        computerSystem.addRelation("connectedVia":addedConnectionObjects);

        existingConnections.each{String name, String value->
            SmartsLink.get(name:name).remove();
        }
    }
    else {
        getLogger().warn("Error creating device ${topologyObject["Name"]}. Reason: ${computerSystem.errors}")
    }
}

SmartsComputerSystemComponent addComputerSystemComponent(containmentObjectFromSmarts, computerSystemName) {
    containmentObjectFromSmarts.ComputerSystemName =computerSystemName;
    def addedSmartsComputerSystemObject = null;
    if (CLASS_MAPPINGS.SmartsInterface.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("SmartsInterface", containmentObjectFromSmarts);
        getLogger().debug("Creating SmartsInterface with ${props}")
        addedSmartsComputerSystemObject = SmartsInterface.add(props);
    }
    else if (CLASS_MAPPINGS.SmartsCard.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("SmartsCard", containmentObjectFromSmarts);
        getLogger().debug("Creating SmartsCard with ${props}")
        addedSmartsComputerSystemObject = SmartsCard.add(props);
        def connectedAdapters = [];
        containmentObjectFromSmarts.Realizes.each{networkAdapter->
            def rsNetworkAdapterObjects = SmartsNetworkAdapter.get(name:networkAdapter.Name);
            if(rsNetworkAdapterObjects)
            {
                connectedAdapters.add(rsNetworkAdapterObjects);
            }
        }
        addedSmartsComputerSystemObject.addRelation(realizes:connectedAdapters);
    }
    else if (CLASS_MAPPINGS.SmartsIp.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("SmartsIp", containmentObjectFromSmarts);
        getLogger().debug("Creating SmartsIp with ${props}")
        addedSmartsComputerSystemObject = SmartsIp.add(props);
        def connectedAdapters = [];
        containmentObjectFromSmarts.Realizes.each{networkAdapter->
            def rsNetworkAdapterObjects = SmartsNetworkAdapter.get(name:networkAdapter.Name);
            if(rsNetworkAdapterObjects)
            {
                connectedAdapters.add(rsNetworkAdapterObjects);
            }
        }
        addedSmartsComputerSystemObject.addRelation(realizes:connectedAdapters);
    }
    else if (CLASS_MAPPINGS.SmartsPort.classes.containsKey(containmentObjectFromSmarts.CreationClassName)) {

        def props = getPropsWithLocalNames("SmartsPort", containmentObjectFromSmarts);
        getLogger().debug("Creating SmartsPort with ${props}")
        addedSmartsComputerSystemObject = SmartsPort.add(props);
    }
    else
    {

        def props = getPropsWithLocalNames("SmartsComputerSystemComponent", containmentObjectFromSmarts);
        getLogger().debug("Creating SmartsComputerSystemComponent with ${props}")
        addedSmartsComputerSystemObject = SmartsComputerSystemComponent.add(props);
    }

    def layeredOverObjects = [];
    containmentObjectFromSmarts.LayeredOver.each{connectedComputerSystemObject->
        def rsConnectedComputerSystemObject = SmartsComputerSystemComponent.get(name:connectedComputerSystemObject.Name);
        if(rsConnectedComputerSystemObject)
        {
            layeredOverObjects.add(rsConnectedComputerSystemObject);
        }
    }
    def underlyingObjects = [];
    containmentObjectFromSmarts.Underlying.each{connectedComputerSystemObject->
        def rsConnectedComputerSystemObject = SmartsComputerSystemComponent.get(name:connectedComputerSystemObject.Name);
        if(rsConnectedComputerSystemObject)
        {
            underlyingObjects.add(rsConnectedComputerSystemObject);
        }
    }

    addedSmartsComputerSystemObject.addRelation(layeredOver:layeredOverObjects);
    addedSmartsComputerSystemObject.addRelation(underlying:underlyingObjects);

    if(addedSmartsComputerSystemObject instanceof SmartsNetworkAdapter)
    {
        def realizedByObjects = [];
        containmentObjectFromSmarts.RealizedBy.each{connectedComputerSystemObject->
            def rsConnectedComputerSystemObject = SmartsCard.get(name:connectedComputerSystemObject.Name);
            if(rsConnectedComputerSystemObject)
            {
                realizedByObjects.add(rsConnectedComputerSystemObject);
            }
        }
        def connectedViaObjects = [];
        containmentObjectFromSmarts.ConnectedVia.each{connectedComputerSystemObject->
            def rsConnectedComputerSystemObject = SmartsLink.get(name:connectedComputerSystemObject.Name);
            if(rsConnectedComputerSystemObject)
            {
                connectedViaObjects.add(rsConnectedComputerSystemObject);
            }
        }

        addedSmartsComputerSystemObject.addRelation(realizedBy:realizedByObjects);
        addedSmartsComputerSystemObject.addRelation(connectedVia:connectedViaObjects);
    }
    return addedSmartsComputerSystemObject;
}