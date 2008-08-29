import datasource.*;

def hypericDs = HttpDatasource.get(name:"hyperic");

// Running the hyperic script
def serverInfo = HypericServer.get(username: "hqadmin")
def username = serverInfo.username
def password = serverInfo.password
def last_timestamp = serverInfo.relation_timestamp
def detailsXml
hypericDs.doRequest("/j_security_check.do", ["j_username":username, "j_password":password]);
if (last_timestamp == "") {
	detailsXml = hypericDs.doRequest("/hqu/rapidcmdb/status/detail.hqu", [:])
}
else {
	detailsXml = hypericDs.doRequest("/hqu/rapidcmdb/status/detail.hqu", ['begin':last_timestamp])
}

// Parsing the xml files and updating the content

def parser = new XmlParser()

def HypericObjects = parser.parseText(detailsXml)
serverInfo.relation_timestamp = HypericObjects.'@timestamp'

for(hypObject in HypericObjects.HypericObject) {
		
	def metrics = hypObject.metric
	def res
		
	switch (hypObject.'@type') {
		case "platform":
			res = Platform.add(resource_name: hypObject.'@name')
			break;
		case "server":
			res = Server.add(resource_name: hypObject.'@name')
			def serPlat = Platform.get(resource_name: hypObject.'@platform')
			res.addRelation(serverOf: serPlat)
			break;
		case "service":
			res = Service.add(resource_name: hypObject.'@name')
			def servSer = Server.get(resource_name: hypObject.'@server')
			res.addRelation(serviceOf: servSer)
			break;
	}
}

return "success!"