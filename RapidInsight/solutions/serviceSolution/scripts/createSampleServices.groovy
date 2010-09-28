def service1=RsService.add(name:"service1", displayName:"service1", className:"Service");
def service2=RsService.add(name:"service2", displayName:"service2", className:"Service");

//serviceName property will be used for faster calculations via search by serviceName like serviceName:"service1"

//service1 devices
RsComputerSystem.add(name:"router2", displayName:"router2", className:"Router", serviceName:"service1")
RsComputerSystem.add(name:"switch1", displayName:"switch1", className:"Switch", serviceName:"service1")
RsComputerSystem.add(name:"switch2", displayName:"switch2", className:"Switch", serviceName:"service1")
RsComputerSystem.add(name:"host1", displayName:"host1", className:"Host", serviceName:"service1 , service2")

//service2 devices 
RsComputerSystem.add(name:"firewall1", displayName:"firewall1", className:"Firewall", serviceName:"service2")
RsComputerSystem.add(name:"firewall2", displayName:"firewall2", className:"Firewall", serviceName:"service2")
RsComputerSystem.add(name:"host2", displayName:"host2", className:"Host", serviceName:"service2")
RsComputerSystem.add(name:"host3", displayName:"host3", className:"Host", serviceName:"service2")


//service1 & service2 devices both
//comma & space seperated before & after, comma is for readability,
//space is to make serviceName tokenized so that , device can be via search serviceName:"service1" or serviceName:"service2"
RsComputerSystem.add(name:"router1", displayName:"router1", className:"Router", serviceName:"service1 , service2")