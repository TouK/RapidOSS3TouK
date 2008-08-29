import datasource.*;

def hypericDs = HttpDatasource.get(name:"hyperic");

// Running the hyperic script
def serverInfo = HypericServer.get(username: "hqadmin")
def username = serverInfo.username
def password = serverInfo.password
def last_timestamp = serverInfo.event_timestamp
def alertsXml
hypericDs.doRequest("/j_security_check.do", ["j_username":username, "j_password":password]);
if (last_timestamp == "") {
	alertsXml = hypericDs.doRequest("/hqu/rapidcmdb/alert/list.hqu", [:])
}
else {
	alertsXml = hypericDs.doRequest("/hqu/rapidcmdb/alert/list.hqu", ['begin':last_timestamp])
}

// Parsing the xml files and updating the content

def parser = new XmlParser()
def HypericEvents = parser.parseText(alertsXml)
serverInfo.event_timestamp = HypericEvents.'@timestamp'

for(hypEvent in HypericEvents.HypericEvent) {

	def event = HypericEvent.add(aid: hypEvent.'@id', name: hypEvent.'@name', timestamp: hypEvent.'@timestamp', fixed: hypEvent.'@fixed')
	def eventOwner = Resource.get(resource_name: hypEvent.'@owner_name')
	event.addRelation(owner: eventOwner)
}

return "success!"