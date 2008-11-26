import org.apache.commons.collections.map.CaseInsensitiveMap
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter
import org.apache.commons.lang.StringUtils
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////CONFIGURATION DATA///////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
CLASS_MAPPINGS = [
    SmartsComputerSystem : [
            classes:[Bridge:[:], Hub:[:], Host:[:], Node:[:], Switch:[:], Router:[:], Firewall:[:], RelayDevice:[:], TerminalServer:[:]],
            defaultPropertiesToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo"],
            propertyMapping:[SNMPAddress:"snmpAddress", CreationClassName:"className"],
            relationMapping:["ConnectedVia":"ConnectedViaVlan"]
    ],
    SmartsComputerSystemComponent :[
            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], LogicalDisk:[:], NumericSensor:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"]
    ],
    SmartsGroup:[
            classes:[CardRedundancyGroup:[:], RedundancyGroup:[:], SystemRedundancyGroup:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"]
    ],
    SmartsLink: [
            classes:[NetworkConnection:[:], Cable:[:], TrunkCable:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"],
            customPropertyConfig:[closure:"linkCustomProperties", customProperties:["a_Name", "a_ComputerSystemName","z_ComputerSystemName","z_Name"]]
    ],
    SmartsCard: [
            classes:[Card:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"]
    ],
    SmartsIp: [
            classes:[IP:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"]
    ],
    SmartsInterface: [
            classes:[Interface:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"]
    ],
    SmartsPort: [
            classes:[Port:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"],
            relationMapping:["PartOf":"partOfVlan"]
    ],
    SmartsManagementServer: [
            classes:[ManagementServer:[:]],
            defaultPropertiesToBeSubscribed: ["Name"],
            propertyMapping:[CreationClassName:"className"],
            relationMapping:[:]
    ],
    SmartsHSRPGroup:[
        classes:[HSRPGroup:[:]],
        defaultPropertiesToBeSubscribed: ["Name"],
        propertyMapping:[CreationClassName:"className"],
        relationMapping:[:]
    ],
    SmartsIpNetwork:[
        classes:[IPNetwork:[:]],
        defaultPropertiesToBeSubscribed: ["Name"],
        propertyMapping:[CreationClassName:"className"]
    ],
    SmartsVlan:[
        classes:[VLAN:[:]],
        defaultPropertiesToBeSubscribed: ["Name"],
        propertyMapping:[CreationClassName:"className"],
    ],
    SmartsHSRPEndpoint:[
        classes:[HSRPEndpoint:[:]],
        defaultPropertiesToBeSubscribed: ["Name"],
        propertyMapping:[CreationClassName:"className"],
        relationMapping:[:]
    ]
]

CLASSES_TO_BE_SUBSCRIBED = ["SmartsComputerSystem", "SmartsComputerSystemComponent", "SmartsGroup", "SmartsLink", "SmartsCard", "SmartsIp", "SmartsInterface", "SmartsPort"]
linkCustomProperties = {Class rsClass, List closureProperties, Map smartsObjectProperties->
    def zDisplayName = smartsObjectProperties.Z_DisplayName
    def aDisplayName = smartsObjectProperties.A_DisplayName
    return [
            a_ComputerSystemName:StringUtils.substringBetween(aDisplayName, "-", "/"),
            a_Name:StringUtils.substringBefore(aDisplayName," ["),
            z_ComputerSystemName:StringUtils.substringBetween(zDisplayName, "-", "/"),
            z_Name:StringUtils.substringBefore(zDisplayName," ["),
    ]
}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
TOPOLOGY_MANAGER_CLASS = "ICF_TopologyManager";
TOPOLOGY_MANAGER_CLASS_DISCOVERY_PROPERTY = "lastProbeFinishedAt";
SMARTS_TO_RS_CLASS_NAME_MAPPING = [:];
DOMAIN_CLASSES = [:];
DATASOURCE_NAME = getDatasource().name;
topologyMap = null;
firstTimeRetrieved = false;

def getParameters() {
    def params = [[CreationClassName: TOPOLOGY_MANAGER_CLASS, Name: ".*", Attributes: []]];
    CLASSES_TO_BE_SUBSCRIBED.each{String rsClassName->
        def colsToBeSubscribed = CLASS_MAPPINGS[rsClassName].defaultPropertiesToBeSubscribed;
        CLASS_MAPPINGS[rsClassName].classes.each{String smartsClassName, Map classConfig->
            params.add([CreationClassName: smartsClassName, Name: ".*", Attributes: colsToBeSubscribed]);
        }
    }
    return ["subscribeParameters": params]
}

def init() {

    ApplicationHolder.application.getDomainClasses().each{GrailsDomainClass domainClass->
        DOMAIN_CLASSES[domainClass.clazz.name] = domainClass;
    }
    createMappingConfiguration();
    markExistingDevices()
}

def cleanUp() {

}


def update(Map topologyObject) {
    String eventType = topologyObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    String className = topologyObject["CreationClassName"];
    if (eventType == BaseSmartsListeningAdapter.CREATE) {
        if (className == "ICF_TopologyManager") {
            createTopology();
        }
    }
    else if (eventType == BaseSmartsListeningAdapter.CHANGE) {
        if (className == "ICF_TopologyManager") {
            //Changes to DiscoveredLastAt also will trigger recreating hierarchy
            def monitoredAttribute = topologyObject["ModifiedAttributeName"]
            def attributeValue = topologyObject["ModifiedAttributeValue"]
            if (monitoredAttribute == TOPOLOGY_MANAGER_CLASS_DISCOVERY_PROPERTY) {
                logger.warn("Topology discovery finished at ${attributeValue}. Will create topology");
                createTopology();
            }
        }
        else
        {
            addObject(topologyObject.CreationClassName, topologyObject.Name);
        }
    }
    else if (eventType == BaseSmartsListeningAdapter.DELETE) {
        logger.info("Removing object ${topologyObject}.");
        RsTopologyObject.get(name: topologyObject["Name"])?.remove();
    }

}
RELATIONS_TO_BE_ADDED = [:]
def createTopology(topologyObject)
{
    RELATIONS_TO_BE_ADDED = [:]
    logger.warn("Starting creating topology");
    CLASS_MAPPINGS.each{String rsClassName, Map rsClassConfiguration->
        rsClassConfiguration.classes.each{String smartsClassName, Map classConfMap->
            addSmartsObjects(rsClassConfiguration, smartsClassName)
        }
    }
    if(!firstTimeRetrieved)
    {
        receivingExitingDevicesCompleted();
        firstTimeRetrieved = true;
    }

    addRelations();
    logger.warn("Finished creating topology");
}

def addSmartsObjects(Map rsClassConfiguration, String smartsClassName) {
    Class rsClass = rsClassConfiguration.rsClass;
    if (rsClass != null) {
        Map propertyMappingConfiguration = rsClassConfiguration.propertyMapping;
        String customPropertyClosureName = rsClassConfiguration.customPropertyConfig?.closure;
        Closure customPropertyClosure = null;
        if (customPropertyClosureName != null) {
            customPropertyClosure = this.getProperty(customPropertyClosureName);
        }
        List closureProperties = rsClassConfiguration.customPropertyConfig?.customProperties
        Map relationMappingConfiguration = rsClassConfiguration.relationMapping
        logger.info("adding objects from smarts with CreationClassName:${smartsClassName} to model ${rsClass}");
        List smartsObjects = getDatasource().getObjects([CreationClassName: smartsClassName, Name: ".*"], [], true);
        logger.info("Got ${smartsObjects.size()} number of objects from smarts");
        smartsObjects.each {Map smartsobjectProperties ->
            def rsProperties = [rsDatasource: DATASOURCE_NAME]
            propertyMappingConfiguration.each {String smartsPropertyName, String rsPropertyName ->
                def propValue = smartsobjectProperties[smartsPropertyName]
                rsProperties[rsPropertyName] = propValue;
            }
            if (customPropertyClosure != null) {
                def customProps = customPropertyClosure(rsClass, closureProperties, smartsobjectProperties);
                if (customProps instanceof Map) {
                    rsProperties.putAll(customProps);
                }
            }
            logger.debug("adding object of ${rsClass.name} with properties ${rsProperties}");
            def addedObject = rsClass.'add'(rsProperties);
            if (addedObject.hasErrors()) {
                logger.warn("Could not add object with CreationClassName:${smartsClassName } Name:${smartsobjectProperties.Name}. Reason:${addedObject.errors}");
            }
            else {
                if (!firstTimeRetrieved) {
                    topologyMap.remove(smartsobjectProperties.Name);
                }
                def relations = [:]
                relationMappingConfiguration.each {String smartsRelationName, Map rsRelationConfig ->
                    List smartsRelations = smartsobjectProperties[smartsRelationName];
                    def rsRelationName = rsRelationConfig.rsName;
                    Class relatedModel = rsRelationConfig.relatedModel;
                    if (smartsRelations) {
                        def relatedObjectNames = [];
                        smartsRelations.each {Map smartsRelation ->
                            def configuration = SMARTS_TO_RS_CLASS_NAME_MAPPING[smartsRelation.CreationClassName];
                            if (configuration != null && relatedModel.isAssignableFrom(configuration.rsClass)) {
                                relatedObjectNames.add(smartsRelation.Name);
                            }
                            else {
                                logger.debug("Discarding related object ${smartsRelation.Name} of object ${smartsobjectProperties.Name} because related objects is not an instance of ${relatedModel.name}");
                            }
                        }
                        if (!relatedObjectNames.isEmpty()) {
                            relations[rsRelationConfig.rsName] = relatedObjectNames;
                        }
                    }
                }
                if (!relations.isEmpty()) {
                    RELATIONS_TO_BE_ADDED[smartsobjectProperties.Name] = relations;
                }
                else {
                    logger.debug("object with CreationClassName:${smartsClassName} Name:${smartsobjectProperties.Name} does not have any relations");
                }
            }
        }
    }
    else
    {
        logger.warn("Discarding objects of ${smartsClassName} class. Mapped ri model does not exist.");    
    }
}


def addRelations()
{
    logger.warn("started adding relations");
    RELATIONS_TO_BE_ADDED.each{String objectName, Map relationWithObjectName->
        def object = RsTopologyObject.get(name:objectName);
        if(object)
        {
            Map rsRelations = [:]
            Map relationsToBeRemoved = [:]
            relationWithObjectName.each{String relationName, List relatedObjectNames->
                def existingRelations = [:];
                def existingRelation = object[relationName];
                if(existingRelation instanceof Collection)
                {
                    existingRelation.each{
                        existingRelations[it.name] = it;
                    }
                }
                else if(existingRelation != null)
                {
                    existingRelations[existingRelation.name] = existingRelation;    
                }
                def relatedObjects = [];
                relatedObjectNames.each{String relatedObjectName->
                    def rsObject = RsTopologyObject.get(name:relatedObjectName);
                    existingRelations.remove(rsObject.name);
                    if(rsObject)
                    {
                        relatedObjects.add(rsObject);
                    }
                    else
                    {
                        logger.info("Could not added relation between RsObject with name ${objectName} and ${relatedObjectName}. Because ${relatedObjectName} does not exist");
                    }
                }
                if(!relatedObjects.isEmpty())
                {
                    rsRelations[relationName] = relatedObjects;
                }
                if(!existingRelations.isEmpty())
                {
                    relationsToBeRemoved[relationName] = existingRelations.values();
                }

            }
            if(!rsRelations.isEmpty())
            {
                logger.debug("Adding relations ${rsRelations} to RsObject ${object}.");
                object.addRelation(rsRelations);
            }
            else
            {
                logger.info("RsObject ${object} does not have any related objects.");
            }

            if(!relationsToBeRemoved.isEmpty())
            {
                logger.debug("Removing relations ${relationsToBeRemoved} from RsObject ${object}.");
                object.removeRelation(relationsToBeRemoved);
            }
        }
        else
        {
            logger.info("Could not add relations since RsObject with name ${objectName} does not exists.");
        }
    }
    RELATIONS_TO_BE_ADDED.clear();
    logger.warn("finished adding relations");
}

def getDatasource() {
    return datasource;
}


def createMappingConfiguration()
{
    CLASS_MAPPINGS.each{String rsClassName, Map configuration->
        GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass(rsClassName);
        if(domainClass != null)
        {
            configuration.rsClass = domainClass.clazz;
            Map colMapping = configuration.propertyMapping!= null?configuration.propertyMapping:[:];
            Map customPropertyConfig = configuration.customPropertyConfig!= null?configuration.customPropertyConfig:[closure:null, customProperties:[]];
            configuration.customPropertyConfig = customPropertyConfig; 
            configuration.propertyMapping = colMapping;
            configuration.rsClass.'getPropertiesList'().each{prop->
                if(!prop.isRelation && !prop.isOperationProperty)
                {
                    def smartsRelationName = getSmartsPropertyName(prop.name);
                    if(!colMapping.values().contains(prop.name) && !configuration.customPropertyConfig.customProperties.contains(prop.name))
                    {
                        colMapping[smartsRelationName] = prop.name;
                    }
                }
            }
            configuration.classes.each{String smartsClassName, smartsClassConfig->
                SMARTS_TO_RS_CLASS_NAME_MAPPING[smartsClassName] = configuration;                
            }
        }
    }
    createRelationMapping([:], SmartsComputerSystem)
}
def createRelationMapping(Map processedClasses, Class cls)
{
    logger.debug("Creating relation configuration for ${cls.name} ${processedClasses}");
    def rsClassConfig = CLASS_MAPPINGS[cls.name];
    processedClasses.put(cls.name,cls.name)
    if(rsClassConfig)
    {
        Map relationMap = rsClassConfig.relationMapping == null?[:]:rsClassConfig.relationMapping;
        Map rsToSmarts = [:]
        relationMap.each{String smartsRelName, String rsRelName->
            rsToSmarts[rsRelName] = smartsRelName
        }
        relationMap.clear();
        rsClassConfig.relationMapping = relationMap;
        cls.'getPropertiesList'().each{prop->
            if(prop.isRelation)
            {
                def smartsRelationName = rsToSmarts[prop.name];
                if(smartsRelationName == null)
                {
                    smartsRelationName = getSmartsPropertyName(prop.name);
                }
                relationMap[smartsRelationName] = [rsName:prop.name, relatedModel:prop.relatedModel];
                if(!processedClasses.containsKey(prop.relatedModel.name))
                {
                    processedClasses.put(prop.relatedModel.name,prop.relatedModel.name)
                    createRelationMapping(processedClasses, prop.relatedModel);
                }
            }
        }
    }
}

def getSmartsPropertyName(String propName)
{
    return propName.substring(0,1).toUpperCase()+propName.substring(1)
}

def markExistingDevices()
{
    logger.debug("Marking all devices as deleted.");
    topologyMap = new CaseInsensitiveMap();
    def deviceNames = RsTopologyObject.propertySummary("alias:* AND rsDatasource:\"${DATASOURCE_NAME}\"", ["name"]);
    deviceNames.name.each {propertyValue, occurrenceCount->
        topologyMap[propertyValue] = "deleted";
    }
    logger.debug("Marked all devices as deleted.");
    firstTimeRetrieved = false;
}

def receivingExitingDevicesCompleted()
{
    firstTimeRetrieved = true;
    logger.info("Existing objects retrieved and ${topologyMap.size()} number of objects will be deleted.");
    topologyMap.each{String objectName, String value->
        logger.debug("Deleting non existing object ${objectName}.");
        RsTopologyObject.get(name:objectName)?.remove();
    }
    topologyMap.clear();
}


