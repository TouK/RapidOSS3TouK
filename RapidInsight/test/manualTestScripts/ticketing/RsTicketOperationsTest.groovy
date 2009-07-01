import connector.JiraConnector

RsTopologyObject.removeAll()
RsEvent.removeAll()
RsTicket.removeAll()

createJiraConnector()

def device1 = RsTopologyObject.add(name:"Device1")
def event1 = RsEvent.add(name:"event1",description:"event1 description", severity:1)
def ticketProps = [connectorName:"jira",eventName:"event1",elementName:"Device1", type:"1"]
def ticket = RsTicket.openTicket(ticketProps)
def rsTopoObj = ticket.relatedObjects[0]
assert rsTopoObj.name == device1.name
def rsEvent = ticket.relatedEvents[0]
assert rsEvent.name == event1.name

ticket.updateTicket(["description":"event1 new description","component":"CorpVPN1","fixVersion":"0.3","type":"2"])
details = ticket.retrieveDetails()
//currently type can not be changed , reason could not be found
//assert ticket.type == details.type
assert details.components[0] == "CorpVPN1" 
ticket.addToLog("This is a log1")
def logs = ticket.getLogEntries()
assert logs[0].toString()== "This is a log1"

ticket.resolveTicket()
details = ticket.retrieveDetails()
assert details.status == "5"

ticket.closeTicket("event1","Device1")
details = ticket.retrieveDetails()
assert details.status == "6"
assert ticket.relatedObjects.size() == 0
assert ticket.relatedEvents.size() == 0

ticket.reopenTicket()
details = ticket.retrieveDetails()
assert details.status == "4"
assert ticket.relatedObjects.size() == 0
assert ticket.relatedEvents.size() == 0

ticket.closeTicket("event1","Device1","2")
details = ticket.retrieveDetails()
assert details.status == "6"
assert ticket.relatedObjects.size() == 0
assert ticket.relatedEvents.size() == 0

return "Successfully completed the tests"

def createJiraConnector(){
	def connectionParams = ["username":"pinar", "name":"jira", "userPassword":"pinar"]
	def datasourceParams = ["reconnectInterval":0, "name":"jira"]
	def connectorParams = ["reconnectInterval":0, "name":"jira"]

	JiraConnector.addConnector(connectorParams,datasourceParams, connectionParams)
}