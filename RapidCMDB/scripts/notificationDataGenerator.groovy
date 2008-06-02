import groovy.text.SimpleTemplateEngine

/*
classname: router, switch, host,     interface,port
eventname unresponsive, down,    downOrFlapping, highutilization
instancename: random names (cityname + number etc.) if interface IF-devicename/number (1,2,3) IF/newyork1/1
severity 1-5
lastnotifiedat, lastchangedat dates, normall unix timestamp, how should we do to make it searchable?
elementname: same as instance name if classname is router/switch/host. devicename if interface/port. for example if instancename is IF-newyork1/1 elementname is newyork1,
elementclassname : router/switch/host (not interface/port)
isroot (if eventname unresponsive isroot:false, otherwise
sourcedomainname US-AM-1, US-AM-2, EMEA-AM, AP-AM
count 1-10
impact 1-1000*/
def sourceDomainNames = ["US-AM-1", "US-AM-2", "EMEA-AM", "AP-AM"]
def cityNames = ["Newyork", "London", "Rome", "Paris"];
def classNameList = ["Router", "Siwtch", "Host", "Interface", "Port"];
def eventNameList = ["Unresponsive", "Down", "DownOrFlapping", "HighUtilization"];
SimpleTemplateEngine engine = new SimpleTemplateEngine();
def instanceNameTemplates = ["Default":engine.createTemplate("\${cityName}\${number}"), "Interface":engine.createTemplate("IF-\${cityName}\${number}/\${deviceNumber}")];
def elementNameTemplates = ["Default":engine.createTemplate("\${cityName}\${number}")];
def elementClassNameList = ["Router", "Siwtch", "Host"];
for(int i=0; i < 50; i++)
{
    int classNameIndex =  getANumber(0, classNameList.size());
    int sourceDomainNameIndex =  getANumber(0, sourceDomainNames.size());
    int cityNameIndex = getANumber(0, cityNames.size());
    int eventNameIndex = getANumber(0, eventNameList.size());
    int elementClassNameIndex = getANumber(0, elementClassNameList.size()); 
    int deviceNumber = getANumber(1, 4);
    int number = getANumber(1, 1000); 
    def props = [cityName:cityNames[cityNameIndex], number:number, deviceNumber:deviceNumber];

    def className = classNameList[classNameIndex];
    def eventName = eventNameList[eventNameIndex];
    def sourceDomainName = sourceDomainNames[sourceDomainNameIndex];
    def instanceNameTemplate = instanceNameTemplates[className]?instanceNameTemplates[className]:instanceNameTemplates["Default"];
    def instanceName = instanceNameTemplate.make(props).toString();
    def elementNameTemplate = elementNameTemplates[className]?elementNameTemplates[className]:elementNameTemplates["Default"];
    def elementName = elementNameTemplate.make(props).toString();
    def elementClassName = className;
    if(className == "Interface" || className == "Port")
    {
        elementClassName = elementClassNameList[elementClassNameIndex];
    }
    def isRoot = false;
    if(eventName != "Unresponsive")
    {
        isRoot = true;    
    }

    int impact = getANumber(1, 1001);
    int count = getANumber(1, 11);
    int severity = getANumber(1, 6);
    def lastNotifiedAt = new Date();
    def lastChangedAt = new Date();
    Notification.add(className:className, instanceName:instanceName, eventName:eventName, elementName:elementName, elementClassName:elementClassName,
    severity:severity, impact:impact, count:count, lastChangedAt:lastChangedAt, lastNotifiedAt:lastNotifiedAt, isRoot:String.valueOf(isRoot),
            eventText:"${eventName} event is created.".toString(), sourceDomainName:sourceDomainName);
}


def getANumber(int start, int end)
{
    return (int)(Math.random() * (end - start));
}