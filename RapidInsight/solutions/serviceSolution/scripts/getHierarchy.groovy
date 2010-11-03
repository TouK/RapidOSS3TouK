import groovy.xml.MarkupBuilder

/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/

def sw = new StringWriter();
builder = new MarkupBuilder(sw);

searchParams = [max: "1000",sort:"name",order:"asc"];
serviceStateMap=[:];
serviceSubServicesMap=[:];

builder.Objects() {
    RsService.searchEvery("serviceName:\"\" ",searchParams).each { service ->
        printService(service);
    }
}
return sw.toString();


def printService(service)
{
   def state=getServiceState(service);
   builder.Object(id: service.id, name: service.name, displayName: service.displayName, nodeType: service.className,state:state){
        getSubServices(service).each{ subService ->
            printService(subService);
        }
        RsComputerSystem.searchEvery("serviceName:${service.name.toQuery()}",searchParams).each{ device ->
           printDevice(device);
        }
   }
}

def printDevice(device)
{
   def state=device.getState(); //device state is already saved by StateCalculator
   builder.Object(id: device.id, name: device.name, displayName: device.displayName, nodeType: device.className,state:state); 
}

def getSubServices(service)
{
    if(! serviceSubServicesMap.containsKey(service.name))
   {
         serviceSubServicesMap[service.name]=RsService.searchEvery("serviceName:${service.name.toQuery()}",searchParams);
   }
   return  serviceSubServicesMap[service.name];
}
def getServiceState(service)
{
   if(! serviceStateMap.containsKey(service.name))
   {
       def state=0;
       def severityResults=RsEvent.getPropertyValues("serviceName:${service.name.toQuery()}", ["severity"], [sort: "severity", order: "desc", max: 1]).severity;
       if (severityResults.size() > 0)
       {
           state = severityResults[0];
       }
       getSubServices(service).each{ subService ->
            def subServiceState=getServiceState(subService);
            if(subServiceState > state)
            {
                state=subServiceState;
            }
       }
       serviceStateMap[service.name]=state;
   }
   return  serviceStateMap[service.name];
}