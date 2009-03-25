def processTrap(t){
	def props = [:]
	println "t: " + t
	props.severity = 1
	props.identifier = "UnknownEvent"
	props.source = "TrapConnector"	
	t.Agent = "192.168.100.1"
	
	def deviceQuery = "snmpAddress:${t.Agent}"
	def device = RsComputerSystem.searchTop(deviceQuery)
	if (device) {
		props.elementName = device.name
		props.elementId = device.id
	} else {
		props.elementName = t.Agent
	}
		
	if (t.SpecificType == "1") {
		props.severity = 5
		props.identifier = "Down"
		props.action = "add"
		props.name = props.elementName + props.identifier
	}
	if (t.SpecificType == "2") {
		props.identifier = "Down"
		props.name = props.elementName + props.identifier
		props.action = "clear"
	}
	return props
}
