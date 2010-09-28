import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Dec 25, 2008
* Time: 3:04:11 PM
*/
/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/

def CONTAINER_PROPERTY = "rsDatasource"
def nodeType = params.nodeType;
def name = params.name;

// severity values which are not in severityMap will be put to this index 
// also when all zero this index will be used
def normalSeverityIndex = "0";

def severityMap = ["0": 0, "1": 0, "2": 0, "3": 0, "4": 0, "5": 0]
def severitySummary = [severity:[:]];
def severityData=severitySummary.severity;

def devices=[];
if(nodeType =="Service")
{
    devices=RsComputerSystem.searchEvery("serviceName:${name.toQuery()}");
}
else
{
    def device=RsComputerSystem.get(name:name);
    if(device)
    {
        devices.add(device);
    }
}
devices.each{  device ->
       def state=device.getState();
       def stateKey=state.toString();
       if(severityData.containsKey(stateKey))
       {
         severityData[stateKey]+=1;
       }
       else
       {
          severityData[stateKey]=1;
       }
}

// original code to get summary data from events
//if (nodeType == "Container") {
//    severitySummary = RsEvent.propertySummary("${CONTAINER_PROPERTY}:${name.exactQuery()}", ["severity"]);
//}
//else {
//    severitySummary = RsEvent.propertySummary("elementName:${name.exactQuery()}", ["severity"]);
//}

def isAllZero = true;
def invalidSeverityCount = 0;

severitySummary.severity.each {propValue, numberOfObjects ->
    if (numberOfObjects > 0) {
        isAllZero = false;
    }
    def key = String.valueOf(propValue);
    if (severityMap.containsKey(key))
    {
        severityMap.put(key, numberOfObjects);
    }
    else
    {
        invalidSeverityCount += numberOfObjects;
    }
}


if (isAllZero) {
    severityMap.put(normalSeverityIndex, 1);
}

if (invalidSeverityCount > 0)
{
    severityMap.put(normalSeverityIndex, severityMap.get(normalSeverityIndex) + invalidSeverityCount);
}
def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
builder.chart() {
    builder.set(label: "Critical", value: severityMap.get("5"), color: "0xff0000")
    builder.set(label: "Major", value: severityMap.get("4"), color: "0xff7514")
    builder.set(label: "Minor", value: severityMap.get("3"), color: "0xddc700")
    builder.set(label: "Warning", value: severityMap.get("2"), color: "0x2dbfcd")
    builder.set(label: "Indeterminate", value: severityMap.get("1"), color: "0xac6bac")
    builder.set(label: "Normal", value: severityMap.get("0"), color: "0x00ff00")
}
return sw.toString();