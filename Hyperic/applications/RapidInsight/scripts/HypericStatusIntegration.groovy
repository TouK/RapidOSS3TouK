import datasource.*

datasourceName = staticParam;
def lastTimestampIdentifierName = "hypericstatus${datasourceName}"
def lastTimestampIdentifier = RsManagementSystem.get(name:lastTimestampIdentifierName)
if(lastTimestampIdentifier == null){
   lastTimestampIdentifier = RsManagementSystem.add(name:lastTimestampIdentifierName)
}
def hypericDs = HypericDatasource.get(name: datasourceName);
if (hypericDs == null) {
    logger.warn("Could not get hyperic topolgy, because no hyperic datasource with name ${datasourceName} is defined");
    throw new Exception("Could not get hyperic topolgy, because no hyperic datasource with name ${datasourceName} is defined")
}
def response = hypericDs.doRequest("/hqu/rapidcmdb/status/list.hqu", ["lasttimestamp":lastTimestampIdentifier.lastPolledAt.toString()]);
def objectsXml = new XmlSlurper().parseText(response);
lastTimestampIdentifier.update(lastPolledAt:objectsXml.@timestamp);
def platforms = objectsXml.Platforms.Platform;
platforms.each {plat ->
    def props = [name: plat.@id.toString(), rsDatasource: datasourceName]
    if (plat.@availabilty.toString() != "") {
        props.put(availability, plat.@availability);
    }
    logger.debug("Adding hyperic platform with properties ${props}");
    def platform = HypericPlatform.add(props)
    if (!platform.hasErrors()) {
        logger.info("HypericPlatform successfully added")
    }
    else {
        logger.warn("Could not add HypericPlatform. Reason: ${platform.errors}")
    }
}
def servers = objectsXml.Servers.Server;
servers.each {serv ->
    def props = [name: serv.@id.toString(), rsDatasource: datasourceName]
    if (serv.@availabilty.toString() != "") {
        props.put(availability, plat.@availability);
    }
    logger.debug("Adding hyperic server with properties ${props}");
    def server = HypericServer.add(props)
    if (!server.hasErrors()) {
        logger.info("HypericServer successfully added")
    }
    else {
        logger.warn("Could not add HypericServer. Reason: ${server.errors}")
    }
}
def services = objectsXml.Services.Service;
services.each {serv ->
    def props = [name: serv.@id.toString(), rsDatasource: datasourceName]
    if (serv.@availabilty.toString() != "") {
        props.put(availability, plat.@availability);
    }
    logger.debug("Adding hyperic service with properties ${props}");
    def service = HypericService.add(props)
    if (!service.hasErrors()) {
        logger.info("HypericService successfully added")
    }
    else {
        logger.warn("Could not add HypericService. Reason: ${service.errors}")
    }
}
return "success!"

