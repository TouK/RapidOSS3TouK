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
// ------------------ CONFIGURATION --------------------------------------
CONFIG=new mapConfiguration().getConfiguration();
// ------------------ END OF CONFIGURATION --------------------------------


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
    println nodeData;
    def device = nodeData.nodeModel.get( name : nodeData.nodeName);
    if(device != null)
    {
        if(nodeData.nodeName == expandedNodeName)
        {
            nodeData.isExpanded = "true";
        }
        deviceMap[device.name] = buildNodeData(device,nodeData.isExpanded,nodeData.x,nodeData.y);
    }

}

def deviceSet = [:]
deviceMap.each{deviceName, deviceConfigMap->
    deviceSet[deviceName] = deviceConfigMap;
    if(deviceConfigMap.expanded == "true")
    {
        deviceConfigMap.expandable = "true"
        def links=getLinkModel().searchEvery("${CONFIG.CONNECTION_SOURCE_PROPERTY}:${deviceName.exactQuery()} OR ${CONFIG.CONNECTION_TARGET_PROPERTY}:${deviceName.exactQuery()}");
        links.each {link->
            def otherSide = getOtherSideName(link, deviceName);

            if(otherSide != null && !edgeMap.containsKey(deviceName + otherSide)
                        && !edgeMap.containsKey(otherSide + deviceName))
            {
                def otherSideModel = getOtherSideModel(link,deviceName);

                def otherSideDevice = otherSideModel.get(name:otherSide);
                if(otherSideDevice != null){
                    edgeMap[deviceName + otherSide] = [ "source" : deviceName, "target" : otherSide];
                    if(!deviceMap.containsKey(otherSide) && !deviceSet.containsKey(otherSide))
                    {
                        //deviceSet[otherSide] = [ "id" : otherSide, "model" : otherSideDevice.model, "type": otherSideDevice.className, "gauged" : "true", "expandable" : "false", "expanded":"false","rsAlias":getNodeModelNameForOutput(otherSideDevice) ];
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
def extractNodeDataFromParameter(nodeParam)
{
    def nodeData=[:];

    def nodeTokens = nodeParam.splitPreserveAllTokens(",")
    def nodeNameParam = nodeTokens[0];

    nodeData.isExpanded = nodeTokens[1];
    nodeData.x = nodeTokens[2];
    nodeData.y = nodeTokens[3];

    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        nodeData.nodeName=nodeNameParam;
        nodeData.nodeModelName=CONFIG.DEFAULT_NODE_MODEL;
    }
    else
    {
        def nameTokens=nodeNameParam.splitPreserveAllTokens("/");
        nodeData.nodeName=nodeTokens[0];
        nodeData.nodeModelName=nodeTokens[1];        
    }
    nodeData.nodeModel=this.class.classLoader.loadClass(nodeData.nodeModelName);
    return nodeData;
}

def buildNodeData(device,isExpanded,x,y)
{
     def nodeData=["expanded":isExpanded,"expandable":"false","x":x,"y":y]
     //nodeData["rsAlias"]=getNodeModelNameForOutput(device);
     nodeData["id"]=device.name;
     return nodeData;

}
def getLinkModel()
{
    return this.class.classLoader.loadClass(CONFIG.DEFAULT_CONNECTION_MODEL);
}
def getNodeModelNameForOutput(node)
{
    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        return "";
    }
    else
    {
        return node.class.name;
    }
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
    def links = getLinkModel().searchEvery("${CONFIG.CONNECTION_SOURCE_PROPERTY}:${devName.exactQuery()} OR ${CONFIG.CONNECTION_TARGET_PROPERTY}:${devName.exactQuery()}");
    links.each{link->
        def otherSide = getOtherSideName(link, devName);
        if( otherSide != null ){
            if( !edgeMap.containsKey(devName + otherSide)
                    && !edgeMap.containsKey(otherSide + devName) )
            {
                expandable = "true";
                return;
            }
		}
    }
    return expandable;
}