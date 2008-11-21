import org.apache.commons.collections.map.CaseInsensitiveMap
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.smarts.datasource.BaseSmartsListeningAdapter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 18, 2008
 * Time: 4:25:31 PM
 * To change this template use File | Settings | File Templates.
 */
CLASS_MAPPINGS = [
    SmartsComputerSystem : [
            classes:[Bridge:[:], Hub:[:], Host:[:], Node:[:], Switch:[:], Router:[:], Firewall:[:], RelayDevice:[:], TerminalServer:[:]],
            defaultColumnsToBeSubscribed: ["Name", "DiscoveredLastAt", "DiscoveryErrorInfo"],
            columnsMapping:[SNMPAddress:"snmpAddress", CreationClassName:"className"],
            relationsMapping:["ConnectedVia":"ConnectedViaVlan"]
    ],
    SmartsComputerSystemComponent :[
            classes:[PowerSupply:[:], Processor:[:], Memory:[:], TemperatureSensor:[:], VoltageSensor:[:], Fan:[:], Disk:[:], FileSystem:[:], LogicalDisk:[:], NumericSensor:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"]
    ],
    SmartsGroup:[
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
            columnsMapping:[CreationClassName:"className"],
            relationsMapping:["PartOf":"partOfVlan"]
    ],
    SmartsManagementServer: [
            classes:[ManagementServer:[:]],
            defaultColumnsToBeSubscribed: ["Name"],
            columnsMapping:[CreationClassName:"className"],
            relationsMapping:[:]
    ],
    SmartsHSRPGroup:[
        classes:[HSRPGroup:[:]],
        defaultColumnsToBeSubscribed: ["Name"],
        columnsMapping:[CreationClassName:"className"],
        relationsMapping:[:]
    ],
    SmartsIpNetwork:[
        classes:[IPNetwork:[:]],
        defaultColumnsToBeSubscribed: ["Name"],
        columnsMapping:[CreationClassName:"className"]
    ],
    SmartsVlan:[
        classes:[VLAN:[:]],
        defaultColumnsToBeSubscribed: ["Name"],
        columnsMapping:[CreationClassName:"className"],
    ],
    SmartsHSRPEndpoint:[
        classes:[HSRPEndpoint:[:]],
        defaultColumnsToBeSubscribed: ["Name"],
        columnsMapping:[CreationClassName:"className"],
        relationsMapping:[:]
    ]
]

CLASSES_TO_BE_SUBSCRIBED = ["SmartsComputerSystem", "SmartsComputerSystemComponent", "SmartsGroup", "SmartsLink", "SmartsCard", "SmartsIp", "SmartsInterface", "SmartsPort"]


REAL_CLASSES_TO_MODELS_MAP = null;


// This is the default property processing closure all of the smarts properties will be converted into ri properties in this closure
//Also users can give some custom closures to properties if they want to create custom mapping by using columnClosures configuration item
defaultPropertyProcessingClosure = {String smartsPropertyName, String rsPropertyName, Map smartsObjectProperties->
    return smartsObjectProperties[smartsPropertyName];
}

// This is the default relation processing closure. All of the smarts relations will be converted into ri relations in this closure
//Also users can give some custom closures to relations if they want to create custom mapping by using relationClosures configuration item
defaultRelationProcessingClosure = {String smartsRelationName, String rsRelationName, Class relatedModelClass, Map smartsObjectProperties->
    def smartsRelations = smartsObjectProperties[smartsRelationName];
    def rsRelationObjects = [];
    if(smartsRelations)
    {
        logger.info("Relations in smarts ${smartsRelations}");
        //Each smarts relation will be processed 
        smartsRelations.each{Map smartsRelatedObject->
            String relatedCreationClassName = smartsRelatedObject.CreationClassName;
            String relatedObjectName = smartsRelatedObject.Name;
            def smartsToRiMappingConfiguration = REAL_CLASSES_TO_MODELS_MAP[relatedCreationClassName];
            //If there is a mapping configuration between related smarts class and ri class this relation will be processed
            //o.w. it will be discarded 
            if(smartsToRiMappingConfiguration != null)
            {
                //If mapped ri class is and instance of ri relation class it will be processed
                //o.w. it will be discarded
                if(relatedModelClass.isAssignableFrom(smartsToRiMappingConfiguration.rsClass))
                {
                    RsTopologyObject relatedObject = RsTopologyObject.get(name:relatedObjectName);
                    //If object does not exist then it will be created first and then it will be added as relation
                    if(relatedObject == null)
                    {
                        logger.info("Related object with CreationClassName:${relatedCreationClassName} and Name:${relatedObjectName} does not exist. Will create object first");
                        relatedObject = addObject(relatedCreationClassName, relatedObjectName)
                    }
                    if(relatedObject != null && !relatedObject.hasErrors())
                    {
                        rsRelationObjects.add(relatedObject)
                    }
                }
                else
                {
                    logger.info("Discarding  related object with CreationClassName:${relatedCreationClassName} and Name:${relatedObjectName}. Class ${smartsToRiMappingConfiguration.rsClass} is not an instanceof ${relatedModelClass}");
                }
            }
            else
            {
                logger.info("Discarding  related object with CreationClassName:${relatedCreationClassName} and Name:${relatedObjectName}.SmartsToRiMapping configuration does not exist.");
            }
        }
    }

    return rsRelationObjects;
}


topologyMap = null;
existingObjectsRetrieved = false;

def getParameters() {
    //All of the classes which will be subscribed are configured in CLASSES_TO_BE_SUBSCRIBED. For each of these classes, defaultColumnsToBeSubscribed mapping property
    //will be used to subscribe to smarts server
    def params = [];
    CLASSES_TO_BE_SUBSCRIBED.each{String rsClassName->
        def colsToBeSubscribed = CLASS_MAPPINGS[rsClassName].defaultColumnsToBeSubscribed;
        CLASS_MAPPINGS[rsClassName].classes.each{String smartsClassName, Map classConfig->
            params.add([CreationClassName: smartsClassName, Name: ".*", Attributes: colsToBeSubscribed]);
        }
    }
    return ["subscribeParameters": params]
}

def init() {
    //The class mapping information contains information from ri to smarts mapping. This information will be used to construct smarts to ri configuration for
    //each of configured smarts class
    createSmartsToRiClassMapping();

    //existing instances will be marked as deleted in init. They will be saved as deleted in a map called topologyMap
    //Instances will be remoeved from map until  BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED event is received.
    //After that each of the remaining instances will be deleted.
    markExistingDevices();
}

def cleanUp() {

}

//This map is needed to prevent stack overflow while creating hierachy in updateRelations method. Actually at the init phase processing relations is ordered
//by classes whihc means that each relation will be added by only one class. However, this is not sufficient for self referencing models.
PROCESSED_OBJECTS = [:];
COMPUTER_SYSTEM_OBJECTS_DISCOVERED_AT = [:];
def update(Map topologyObject) {
    String eventType = topologyObject[BaseSmartsListeningAdapter.EVENT_TYPE_NAME];
    String className = topologyObject["CreationClassName"];
    println topologyObject
    //Removing instance from objects to be deleted map
    if(!existingObjectsRetrieved)
    {
        topologyMap.remove(topologyObject.Name);
    }
    //If existing objects received completely remaining objects will be deleted from the system
    if(eventType == BaseSmartsListeningAdapter.RECEIVE_EXISTING_FINISHED)
    {
        receivingExitingDevicesCompleted();
    }
    else if (eventType == BaseSmartsListeningAdapter.CREATE) {
        //Only computer system objects will be created. All of the hierachy will be traversed according to relations to RsComputerSystem
        //and related objects will be created and relations will be added to objects.
        if (isComputerSystem(className)) {
            def addedObject = addObject(topologyObject.CreationClassName, topologyObject.Name);
            getDatasource.getObjects("");
            if(addedObject != null&& !addedObject.hasErrors())
            {
                PROCESSED_OBJECTS.clear();
                updateRelations(topologyObject.CreationClassName, topologyObject.Name);
            }
        }
        else
        {
            logger.debug("${topologyObject} is not an instance of SmartsComputerSystem create will be discarded.");
        }

    }
    else if (eventType == BaseSmartsListeningAdapter.CHANGE) {
        addObject(topologyObject.CreationClassName, topologyObject.Name);
    }
    else if (eventType == BaseSmartsListeningAdapter.DELETE) {
        logger.info("Removing object ${topologyObject}.");
        RsTopologyObject.get(name: topologyObject["Name"])?.remove();
    }

}

//def getComputerSystemObjects()
//{
//    CLASS_MAPPINGS.SmartsComputerSystem.classes.each{String className, Map classConfig->
//
//    }
//    getDatasource().getObjects("");
//}

def addObject(String className, String name)
{
    logger.debug("adding object with CreationClassName:${className } Name:${name}");
    Map smartsClassConfiguration = REAL_CLASSES_TO_MODELS_MAP[className];
    if(smartsClassConfiguration)
    {
        Map propertiesFromSmarts = getDatasource().getObject([CreationClassName:className, Name:name]);
        if(propertiesFromSmarts)
        {
            def addedObject = addObject(smartsClassConfiguration, propertiesFromSmarts);
            if(addedObject.hasErrors())
            {
                logger.info("Could not add object with CreationClassName:${className } Name:${name}. Reason:${addedObject.errors}");
            }
            return addedObject;
        }
        else
        {
            logger.info("Could not add object with CreationClassName:${className } Name:${name} since smarts server does not have object");
        }
    }
    else
    {
        logger.info("Could not add object with CreationClassName:${className } Name:${name} since SmartsToRiConfiguration does not exist");
    }
    return null;
}

def addObject(Map smartsClassConfiguration, Map propertiesFromSmarts)
{
     def rsProperties = [:]
    smartsClassConfiguration.columnsMapping.each{String smartsPropertyName, String rsPropertyName->
        def propValue = defaultPropertyProcessingClosure(smartsPropertyName, rsPropertyName, propertiesFromSmarts);
        rsProperties[rsPropertyName] = propValue;
    }
    smartsClassConfiguration.columnMappingClosures.each{String rsPropertyName, Closure rsPropertyClosure->
        def propValue = rsPropertyClosure(null, rsPropertyName, propertiesFromSmarts);
        rsProperties[rsPropertyName] = propValue;
    }
    logger.debug("adding object of ${smartsClassConfiguration.rsClass.name} with properties ${rsProperties}");
    return smartsClassConfiguration.rsClass.'add'(rsProperties);
}

def updateRelations(String className, String name)
{
    if(PROCESSED_OBJECTS.containsKey(name)) return;
    Map smartsClassConfiguration = REAL_CLASSES_TO_MODELS_MAP[className];
    if(smartsClassConfiguration)
    {
        Map propertiesFromSmarts = getDatasource().getObject([CreationClassName:className, Name:name]);
        if(propertiesFromSmarts)
        {
            def addedObject = smartsClassConfiguration.rsClass.'get'(name:name);
            updateRelations(addedObject, smartsClassConfiguration, propertiesFromSmarts)
        }
        else
        {
            logger.info("Could not updated relations of object with CreationClassName:${className } Name:${name} since smarts server does not have object");
        }
    }
    else
    {
        logger.info("Could not updated relations of object with CreationClassName:${className } Name:${name} since SmartsToRiConfiguration does not exist");   
    }
}

def updateRelations(RsTopologyObject object, Map smartsClassConfiguration, Map propertiesFromSmarts)
{
    if(PROCESSED_OBJECTS.containsKey(object.name)) return;
    PROCESSED_OBJECTS[object.name] = object.name;
    def relationsToBeAdded = [:]
    def relationsToBeRemoved = [:]
    smartsClassConfiguration.relationsMapping.each{String smartsRelationName, Map rsRelationConfig->
        def existingRelations = [:];
        object[rsRelationConfig.rsRelationName].each{
            existingRelations[it.name] = it;
        }

        List relations = defaultRelationProcessingClosure(smartsRelationName, rsRelationConfig.rsRelationName, rsRelationConfig.relatedModel, propertiesFromSmarts);

        if(relations)
        {
            relations.each{
                existingRelations.remove(it.name);
            }
            relationsToBeAdded[rsRelationConfig.rsRelationName] = relations;
        }
        if(existingRelations)
        {
            relationsToBeRemoved[rsRelationConfig.rsRelationName] = new ArrayList(existingRelations.values());
        }
    }
    smartsClassConfiguration.relationMappingClosures.each{String rsRelationName, Closure rsRelationClosure->
        def existingRelations = [:];
        object[rsRelationName].each{
            existingRelations[it.name] = it;
        }
        List relations = rsRelationClosure(rsRelationName, propertiesFromSmarts);
        if(relations)
        {
            relations.each{
                existingRelations.remove(it.name);
            }
            relationsToBeAdded[rsRelationName] = relations;
        }
        if(existingRelations)
        {
            relationsToBeRemoved[rsRelationName] = new ArrayList(existingRelations.values());
        }
    }
    if(!relationsToBeAdded.isEmpty())
    {
        logger.info("Adding relations ${relationsToBeAdded} of ${object.name}");
        object.addRelation(relationsToBeAdded);

        relationsToBeAdded.each{String relName, List relatedObjects->
            relatedObjects.each{RsTopologyObject relatedObject->
                String objectName = relatedObject.name;
                updateRelations(relatedObject.className, objectName)
            }
        }
    }
    if(!relationsToBeRemoved.isEmpty())
    {
        logger.info("Removing relations ${relationsToBeRemoved} of ${object.name}");
        object.removeRelation(relationsToBeRemoved);
        relationsToBeRemoved.each{String relName, List relatedObjects->
            relatedObjects.each{RsTopologyObject relatedObject->
                String objectName = relatedObject.name;
                updateRelations(relatedObject.className, objectName)
            }
        }
    }
}

boolean isComputerSystem(String className)
{
    return CLASS_MAPPINGS.SmartsComputerSystem.classes.containsKey(className);
}
def getDatasource() {
    return datasource;
}



def constructSmartsToSmartsRelationMappings(Map processedRelations, Class cls)
{
    def rsClassConfig = CLASS_MAPPINGS[cls.name];
    if(rsClassConfig)
    {
        def relClosures = rsClassConfig["relationMappingClosures"];
        relClosures = relClosures==null?[:]:relClosures;
        def relationMap = [:]
        def classesToBeExpanded = [];
        cls.'getPropertiesList'().each{prop->
            if(prop.isRelation && !relClosures.containsKey(prop.name))
            {
                if(!processedRelations.containsKey(prop.relatedModel.name+prop.reverseName+prop.name))
                {
                    processedRelations.put(cls.name+prop.name+prop.reverseName, prop)
                    relationMap[getSmartsPropertyName(prop.name)] = [rsRelationName:prop.name, relatedModel:prop.relatedModel];
                    if(cls != prop.relatedModel &&!classesToBeExpanded.contains(prop.relatedModel))
                    {
                        classesToBeExpanded.add(prop.relatedModel);
                    }
                }
            }
        }

        rsClassConfig.classes.each{String smartsClassName, Map smartsClassConfig->
            REAL_CLASSES_TO_MODELS_MAP[smartsClassName]["relationsMapping"] = relationMap;
            REAL_CLASSES_TO_MODELS_MAP[smartsClassName]["relationMappingClosures"] = relClosures;
        }
        classesToBeExpanded.each{
            constructSmartsToSmartsRelationMappings(processedRelations, it);
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
    def deviceNames = RsTopologyObject.propertySummary("alias:* AND rsDatasource:\"${getDatasource().name}\"", ["name"]);
    deviceNames.name.each {propertyValue, occurrenceCount->
        topologyMap[propertyValue] = "deleted";
    }
    logger.debug("Marked all devices as deleted.");
    existingObjectsRetrieved = false;
}

def receivingExitingDevicesCompleted()
{
    existingObjectsRetrieved = true;
    logger.info("Existing objects retrieved and ${topologyMap.size()} number of objects will be deleted.");
    topologyMap.each{String objectName, String value->
        logger.debug("Deleting non existing object ${objectName}.");
        RsTopologyObject.get(name:objectName)?.remove();
    }
    topologyMap.clear();
}


def createSmartsToRiClassMapping()
{
    REAL_CLASSES_TO_MODELS_MAP = [:];
    CLASS_MAPPINGS.each{String rsClassName, Map rsClassConfiguration->
        GrailsDomainClass rsDomainClass = ApplicationHolder.getApplication().getDomainClass(rsClassName);
        if(rsDomainClass != null)
        {
            def columnsMap = [:]
            def colClosures = rsClassConfiguration["columnMappingClosures"];
            def defaultColMappings = rsClassConfiguration.columnsMapping;
            colClosures = colClosures==null?[:]:colClosures;
            defaultColMappings = defaultColMappings ==null?[:]:defaultColMappings;
            rsDomainClass.clazz.'getPropertiesList'().each{property->
                String propName = property.name;
                if(!property.isOperationProperty && !property.isRelation && !defaultColMappings.containsKey(propName) && !colClosures.containsKey(propName))
                {
                        def smartsName = getSmartsPropertyName(propName)
                        columnsMap[smartsName] = propName;
                }
            }
            columnsMap.putAll (defaultColMappings);
            rsClassConfiguration.classes.each{String smartsClassName, Map smartsClassConfig->
                    REAL_CLASSES_TO_MODELS_MAP[smartsClassName] = [rsClass:rsDomainClass.clazz, columnsMapping:columnsMap, columnMappingClosures:colClosures]
            }
        }
    }
    constructSmartsToSmartsRelationMappings([:], SmartsComputerSystem);
    logger.warn("Created smarts to ri mapping information ${REAL_CLASSES_TO_MODELS_MAP}");
}
