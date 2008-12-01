import datasource.*
import com.ifountain.comp.utils.CaseInsensitiveMap;

datasourceName = staticParam;
topologyMap = null;
def hypericDs = HypericDatasource.get(name: datasourceName);
if (hypericDs == null) {
    logger.warn("Could not get hyperic topolgy, because no hyperic datasource with name ${datasourceName} is defined");
    throw new Exception("Could not get hyperic topolgy, because no hyperic datasource with name ${datasourceName} is defined")
}
markExistingObjects();
def response = hypericDs.doRequest("/hqu/rapidcmdb/exporter/list.hqu", [:]);
def objectsXml = new XmlSlurper().parseText(response);
def platforms = objectsXml.Platforms.Platform;
platforms.each{plat ->
    def props = [name:plat.@id.toString(), className:"Platform", hypericName:plat.@name.toString(), location:plat.@location.toString(),
            description:plat.@description.toString(), displayName:plat.@name.toString(), rsDatasource:datasourceName]
    logger.debug("Adding hyperic platform with properties ${props}");
    def platform = HypericPlatform.add(props)
    if(!platform.hasErrors()){
        topologyMap.remove(platform.name);
        logger.info("HypericPlatform successfully added")
    }
    else{
        logger.warn("Could not add HypericPlatform. Reason: ${platform.errors}")
    }
}
def servers = objectsXml.Servers.Server;
servers.each{serv ->
   def props = [name:serv.@id.toString(), className:"Server", hypericName:serv.@name.toString(), location:serv.@location.toString(),
            description:serv.@description.toString(), displayName:serv.@name.toString(), rsDatasource:datasourceName]
    logger.debug("Adding hyperic server with properties ${props}");
    def server = HypericServer.add(props)
    if(!server.hasErrors()){
        topologyMap.remove(server.name);
        logger.info("HypericServer successfully added")
        def platformId =serv.@platformId.toString()
        def platform = HypericPlatform.get(name:platformId)
        if(platform){
            logger.debug("Adding relation with platform ${platform.name}")
            server.addRelation(platform:platform)
        }
        else{
            logger.warn("Could not find platform with name ${platformId}")
        }
    }
    else{
        logger.warn("Could not add HypericServer. Reason: ${server.errors}")
    }
}
def services = objectsXml.Services.Service;
services.each{serv ->
   def props = [name:serv.@id.toString(), className:"Service", hypericName:serv.@name.toString(), location:serv.@location.toString(),
            description:serv.@description.toString(), displayName:serv.@name.toString(), rsDatasource:datasourceName]
    logger.debug("Adding hyperic service with properties ${props}");
    def service = HypericService.add(props)
    if(!service.hasErrors()){
        topologyMap.remove(service.name);
        logger.info("HypericService successfully added")
        def serverId =serv.@serverId.toString()
        def server = HypericServer.get(name:serverId)
        if(server){
            logger.debug("Adding relation with server ${server.name}")
            service.addRelation(server:server)
        }
        else{
            logger.warn("Could not find server with name ${serverId}")
        }
    }
    else{
        logger.warn("Could not add HypericService. Reason: ${service.errors}")
    }
}
receivingExitingDevicesCompleted();
return "success!"

def markExistingObjects()
{
    logger.info("Marking all hyperic objects as deleted.");
    topologyMap = new CaseInsensitiveMap();
    def objectNames = HypericResource.propertySummary("alias:* AND rsDatasource:\"${datasourceName}\"", ["name"]);
    objectNames.name.each {propertyValue, occurrenceCount ->
        topologyMap[propertyValue] = "deleted";
    }
    logger.info("Marked all hyperic objects as deleted.");
}
def receivingExitingDevicesCompleted()
{
    logger.info("Existing objects retrieved and ${topologyMap.size()} number of objects will be deleted.");
    topologyMap.each {String objectName, String value ->
        if (logger.isDebugEnabled())
            logger.debug("Deleting non existing object ${objectName}.");
        HypericResource.get(name: objectName)?.remove();
    }
    topologyMap.clear();
}
