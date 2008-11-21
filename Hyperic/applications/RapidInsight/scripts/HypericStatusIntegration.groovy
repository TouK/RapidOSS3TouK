
import datasource.*;
import groovy.util.XmlParser;

def hypericDs = HttpDatasource.get(name:"hyperic");

// Running the hyperic script(s)
def serverInfo = HypericServer.get(username: "hqadmin")
def username = serverInfo.username
def password = serverInfo.password
def last_timestamp = serverInfo.status_timestamp
def statusXml
hypericDs.doRequest("/j_security_check.do", ["j_username":username, "j_password":password]);
if (last_timestamp == "") {
	statusXml = hypericDs.doRequest("/hqu/rapidcmdb/status/list.hqu", [:])
}
else {
	statusXml = hypericDs.doRequest("/hqu/rapidcmdb/status/list.hqu", ['begin':last_timestamp])
}

// Parsing the xml files and updating the content

def parser = new XmlParser()
def HypericObjects = parser.parseText(statusXml)
serverInfo.status_timestamp = HypericObjects.'@timestamp'

for(hypObject in HypericObjects.HypericObject) {
	switch (hypObject.'@type') {
		case "platform":
			Platform.add(resource_name: hypObject.'@name', status: hypObject.'@Availability')
			break;
		case "server":
			Server.add(resource_name: hypObject.'@name', status: hypObject.'@Availability')
			break;
		case "service":
			Service.add(resource_name: hypObject.'@name', status: hypObject.'@Availability')
			break;
	}
}

return "success!"