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
import groovy.xml.MarkupBuilder;
MAPDATA=extractMapDataFromParameter();
// ------------------ CONFIGURATION --------------------------------------
CONFIG=new mapConfiguration().getConfiguration(MAPDATA.mapType);
// ------------------ END OF CONFIGURATION --------------------------------

//Default params.nodePropertyList ["name","rsClassName","expanded","x","y"]

def startTime = System.nanoTime();
def expandedNodeName = params.expandedNodeName;
def nodeString = params.nodes;
def nodes = [];
if( nodeString !=  null)
{
   nodes = nodeString.splitPreserveAllTokens(";").findAll {it != ""};
}

def edgeMap = [:]


def deviceMap = [:];
nodes.each{nodeParam->
    def nodeData = extractNodeDataFromParameter(nodeParam);
    def device = nodeData.nodeModel.get( name : nodeData.name);
    if(device != null)
    {
        if(nodeData.name == expandedNodeName)
        {
            nodeData.expanded = "true";
        }
        deviceMap[device.name] = buildNodeData(device,nodeData.expanded,nodeData.x,nodeData.y);
    }

}

println "deviceMap"
deviceMap.each{ key, val ->
    println "device ${key} ${val}"
}

def deviceSet = [:]
deviceMap.each{deviceName, deviceConfigMap->
    println "iterating device ${deviceName}"
    deviceSet[deviceName] = deviceConfigMap;
    if(deviceConfigMap.expanded == "true")
    {
        println "gonna expand device"
        deviceConfigMap.expandable = "true"        
        def links=getLinksofDevice(deviceName);
        println "links of device ${links}"
        links.each {link->
            def otherSide = getOtherSideName(link, deviceName);

            if(otherSide != null && !edgeMap.containsKey(link.name))
            {
                def otherSideModel = getOtherSideModel(link,deviceName);

                def otherSideDevice = otherSideModel.get(name:otherSide);
                if(otherSideDevice != null){
                    edgeMap[link.name] = [ "source" : deviceName, "target" : otherSide,"id":link.name];
                    if(!deviceMap.containsKey(otherSide) && !deviceSet.containsKey(otherSide))
                    {
                        deviceSet[otherSide]= buildNodeData(otherSideDevice,"false","","");
                    }
                }
            }

        }
    }
}

//we should generate isExpandable after all map is generated for only unexpanded devices
deviceSet.each{devName, devConfig->
     if(devConfig.expanded=="false")
     {
        devConfig.expandable = isExpandable(devName, edgeMap);
     }
}


def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

mapBuilder.graph()
{
    deviceSet.each {devName, devConfig->
        mapBuilder.node( devConfig);
    }

    edgeMap.each {edge, edgeConf->
        mapBuilder.edge( edgeConf);
    }
}

def endTime = System.nanoTime();
return writer.toString();


//utility functions
def getLinksofDevice(deviceName)
{
   return getLinkModel().searchEvery("( ${CONFIG.CONNECTION_SOURCE_PROPERTY}:${deviceName.exactQuery()} OR ${CONFIG.CONNECTION_TARGET_PROPERTY}:${deviceName.exactQuery()} ) ${getMapTypeQuery()} ");
}
def getMapTypeQuery()
{
    def query="";
    if(CONFIG.USE_MAP_TYPE)
    {
        def mapType=MAPDATA.mapType;
        if(mapType == null || mapType == "")
        {
            mapType=CONFIG.DEFAULT_MAP_TYPE;
        }

        query=" AND mapType:${mapType.exactQuery()}";
    }
    return query;
}
def extractNodeDataFromParameter(nodeParam)
{
    def nodeData=[:];
    def nodePropertyList=params.nodePropertyList.splitPreserveAllTokens(",")
    def nodeProperties = nodeParam.splitPreserveAllTokens(",")

    nodePropertyList.size().times{ index ->
        nodeData[nodePropertyList[index]]=nodeProperties[index];
    }

    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        nodeData.rsClassName=CONFIG.DEFAULT_NODE_MODEL;
    }
    nodeData.nodeModel=this.class.classLoader.loadClass(nodeData.rsClassName);
    return nodeData;
}
def extractMapDataFromParameter()
{
    def mapData=[:];
    if(params.mapPropertyList && params.mapProperties)
    {
        def mapPropertyList=params.mapPropertyList.splitPreserveAllTokens(",")
        def mapProperties = params.mapProperties.splitPreserveAllTokens(",")

        mapPropertyList.size().times{ index ->
            mapData[mapPropertyList[index]]=mapProperties[index];
        }
    }
    return mapData;
}
def buildNodeData(device,expanded,x,y)
{
     def nodeData=["expanded":expanded,"expandable":"false","x":x,"y":y]
     nodeData["id"]=device.name;
     nodeData["rsClassName"]=device.class.name;
     nodeData["name"]=device.name;
     return nodeData;

}
def getLinkModel()
{
    return this.class.classLoader.loadClass(CONFIG.DEFAULT_CONNECTION_MODEL);
}
def getOtherSideModel(link, deviceName)
{
    def otherSideClassName = null;
    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        otherSideClassName=CONFIG.DEFAULT_NODE_MODEL;
    }
    else
    {
        if(link.getProperty(CONFIG.CONNECTION_SOURCE_PROPERTY) != deviceName)
        {
            otherSideClassName = link.getProperty(CONFIG.CONNECTION_SOURCE_CLASS_PROPERTY);
        }
        else if(link.getProperty(CONFIG.CONNECTION_TARGET_PROPERTY) != deviceName)
        {
            otherSideClassName = link.getProperty(CONFIG.CONNECTION_TARGET_CLASS_PROPERTY);
        }
    }

    return this.class.classLoader.loadClass(otherSideClassName);
}

def getOtherSideName(link, deviceName)
{
    def otherSide = null;
    if(link.getProperty(CONFIG.CONNECTION_SOURCE_PROPERTY) != deviceName)
    {
        otherSide = link.getProperty(CONFIG.CONNECTION_SOURCE_PROPERTY);
    }
    else if(link.getProperty(CONFIG.CONNECTION_TARGET_PROPERTY) != deviceName)
    {
        otherSide = link.getProperty(CONFIG.CONNECTION_TARGET_PROPERTY)
    }

    return otherSide;
}






// if device has a link with another device that is not in map
// returns true
def isExpandable( devName, edgeMap)
{
    def expandable = "false";
    def links = getLinksofDevice(devName);
    links.each{link->
        def otherSide = getOtherSideName(link, devName);
        if( otherSide != null ){
            if( !edgeMap.containsKey(link.name) )
            {
                expandable = "true";
                return;
            }
		}
    }
    return expandable;
}
