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
        deviceMap[device.name] = buildNodeData(device,nodeData.expanded,nodeData.x,nodeData.y,nodeData.ownerEdgeId);
    }

}

def deviceEdgeCountMap=[:];
def deviceSet = [:]
deviceMap.each{deviceName, deviceConfigMap->
    deviceSet[deviceName] = deviceConfigMap;
    if(deviceConfigMap.expanded == "true")
    {
        deviceConfigMap.expandable = "true"
        def edges=getEdgesOfDevice(deviceConfigMap["domainObject"]);
        deviceEdgeCountMap[deviceName]=edges.size();
        edges.each {edge->
            def otherSide = getOtherSideName(edge, deviceName);

            if(otherSide != null && !edgeMap.containsKey(edge.name))
            {
                def otherSideModel = getOtherSideModel(edge,deviceName);

                def otherSideDevice = otherSideModel.get(name:otherSide);
                if(otherSideDevice != null){
                    edgeMap[edge.name] = [ "source" : deviceName, "target" : otherSide,"id":edge.name];
                    if(!deviceMap.containsKey(otherSide) && !deviceSet.containsKey(otherSide))
                    {
                        deviceSet[otherSide]= buildNodeData(otherSideDevice,"false","","",edge.name);
                    }
                }
            }

        }
    }
}

def collapsedNodeName = params.collapsedNodeName;
if(collapsedNodeName)
{
	def collapsedNodeNames=collapseNode(collapsedNodeName,deviceSet,edgeMap);
	collapsedNodeNames.each{ nodeName ->
		def edgesOfNode=getEdgesOfNodeFromEdgeMap(nodeName,edgeMap);
		//remove all sub collapsed nodes which have no remaining edge
		if(edgesOfNode.size()==0 && nodeName!=collapsedNodeName)
		{
			deviceSet.remove(nodeName);
		}
	}
}

//we should change expanded state of nodes, who have no edges except ownerEdge , this happens when other nodes are collapsed
deviceSet.each{nodeName, nodeData->
	if(nodeData.expanded=="true")
	{
        def deviceEdgeCount=deviceEdgeCountMap[nodeName];
		def ownerEdgeId=nodeData.ownerEdgeId;
        def edgesOfNodeExceptOwnerEdge=getEdgesOfNodeExceptOwnerEdgeFromEdgeMap(nodeName,ownerEdgeId,edgeMap);
		if(edgesOfNodeExceptOwnerEdge.size()==0 && deviceEdgeCount!=0)
		{
			nodeData.expanded = "false";
		}
	}
}

//we should generate collapsible after all map is generated
deviceSet.each{nodeName, nodeData->
     if(nodeData.expanded=="true") //if a node is expanded it is collapsible
     {
    	 nodeData.collapsible = "true";
     }
     else //if a node is not expanded , but have expanded edges except the ownerEdge , then it is also collapsible
     {
    	 def ownerEdgeId=nodeData.ownerEdgeId;
         def edgesOfNodeExceptOwnerEdge=getEdgesOfNodeExceptOwnerEdgeFromEdgeMap(nodeName,ownerEdgeId,edgeMap);
		 if(edgesOfNodeExceptOwnerEdge.size()>0 )
		 {
			 nodeData.collapsible = "true";
		 }
     }
}

//we should generate isExpandable after all map is generated
deviceSet.each{devName, devConfig->
	 if(devConfig.expanded=="false") // for only unexpanded devices
     {
        devConfig.expandable = isExpandable(devConfig, edgeMap);
     }
     else //if a node is expanded, but have unexpanded edges then it is expandable
     {
    	 def deviceEdgeCount=deviceEdgeCountMap[devName];
    	 def edgesOfNodeOnMap=getEdgesOfNodeFromEdgeMap(devName,edgeMap);
    	 if(deviceEdgeCount==edgesOfNodeOnMap.size())
    	 {
    		 devConfig.expandable="false";
    	 }
    	 else
    	 {
    		 devConfig.expandable="true";
    	 }
     }
}

def writer = new StringWriter();
def mapBuilder = new MarkupBuilder(writer);

def graphProps=[:];
if(params.layout)
{
    graphProps.layout=params.layout;
}
mapBuilder.graph(graphProps)
{
    deviceSet.each {devName, devConfig->
        devConfig.remove("domainObject");
        mapBuilder.node( devConfig);
    }

    edgeMap.each {edge, edgeConf->
        mapBuilder.edge( edgeConf);
    }
}

def endTime = System.nanoTime();
return writer.toString();

//utility functions
def getEdgesOfDevice(domainObject) {
  def deviceName = domainObject.name;
  def edges = [];
  def edgesFromEdgeModel = getEdgeModel().searchEvery("( ${CONFIG.CONNECTION_SOURCE_PROPERTY}:${deviceName.exactQuery()} OR ${CONFIG.CONNECTION_TARGET_PROPERTY}:${deviceName.exactQuery()} ) ${getMapTypeQuery()} ");
  edges.addAll(edgesFromEdgeModel);
  def otherObjects = [];

  /*
  // Sample RsComputerSystem - RsApplication Edge Creation
  if (domainObject instanceof RsComputerSystem) {
    otherObjects.addAll(RsApplication.searchEvery("computerSystemName:${domainObject.name.exactQuery()}"));
  }

  if (domainObject instanceof RsApplication) {
    otherObjects.addAll(RsComputerSystem.searchEvery("name:${domainObject.computerSystemName.exactQuery()}"));
  }
  */
  otherObjects.each {otherObj ->
    edges.add(generateEdgeDataBetweenObjects(domainObject, otherObj))
  }
  return edges;
}

def generateEdgeDataBetweenObjects(object1,object2)
{
	def sourceObject;
	def targetObject;

	//edge source-target is in alphabetical order, to prevent duplicate edges
	if(object1.className.compareTo(object2.className)<0)
	{
		sourceObject=object1;
		targetObject=object2;
	}
	else
	{
		sourceObject=object2;
		targetObject=object1;
	}


	//<---> is a unique mark so that getMapData distinguishes that this edge is virtual
	def edgeName = "${sourceObject.className} ${sourceObject.name}<--->${targetObject.className} ${targetObject.name}".toString()
    def edge = [name: edgeName, id: edgeName]
    edge[CONFIG.CONNECTION_SOURCE_PROPERTY] = sourceObject.name
    edge[CONFIG.CONNECTION_TARGET_PROPERTY] = targetObject.name

    return edge;
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
def buildNodeData(device,expanded,x,y,ownerEdgeId)
{
     def nodeData=["expanded":expanded,"expandable":"false","x":x,"y":y,"collapsible":"false"]
     nodeData["id"]=device.name;
     nodeData["rsClassName"]=device.class.name;
     nodeData["name"]=device.name;
     nodeData["ownerEdgeId"]=ownerEdgeId;
     nodeData["domainObject"] = device;
     return nodeData;

}
def getEdgeModel()
{
    return this.class.classLoader.loadClass(CONFIG.DEFAULT_CONNECTION_MODEL);
}
def getOtherSideModel(edge, deviceName)
{
    def otherSideClassName = null;
    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        otherSideClassName=CONFIG.DEFAULT_NODE_MODEL;
    }
    else
    {
        if(edge[CONFIG.CONNECTION_SOURCE_PROPERTY] != deviceName)
        {
            otherSideClassName = edge[CONFIG.CONNECTION_SOURCE_CLASS_PROPERTY];
        }
        else if(edge[CONFIG.CONNECTION_TARGET_PROPERTY] != deviceName)
        {
            otherSideClassName = edge[CONFIG.CONNECTION_TARGET_CLASS_PROPERTY];
        }
    }

    return this.class.classLoader.loadClass(otherSideClassName);
}


def getOtherSideName(edge, deviceName)
{
    def otherSide = null;
    if(edge[CONFIG.CONNECTION_SOURCE_PROPERTY] != deviceName)
    {
        otherSide = edge[CONFIG.CONNECTION_SOURCE_PROPERTY];
    }
    else if(edge[CONFIG.CONNECTION_TARGET_PROPERTY] != deviceName)
    {
        otherSide = edge[CONFIG.CONNECTION_TARGET_PROPERTY]
    }

    return otherSide;
}

// if device has a edge with another device that is not in map
// returns true
def isExpandable( devConfig, edgeMap)
{
    def domainObject = devConfig["domainObject"];
	def devName=domainObject.name;
    def expandable = "false";
    def edges = getEdgesOfDevice(domainObject);
    edges.each{edge->
        def otherSide = getOtherSideName(edge, devName);
        if( otherSide != null ){
            if( !edgeMap.containsKey(edge.name) )
            {
                expandable = "true";
                return;
            }
		}
    }
    return expandable;
}


def collapseNode(nodeName,deviceSet,edgeMap)
{
  def collapsedNodeNames=[nodeName];
  if(nodeName)
  {
      def nodeData=deviceSet[nodeName];
      //mark the target node expanded false , so that + icon will be visible
      nodeData.expanded="false";
      if(nodeData)
      {
          def ownerEdgeId=nodeData.ownerEdgeId;
          def edgesOfNodeExceptOwnerEdge=getEdgesOfNodeExceptOwnerEdgeFromEdgeMap(nodeName,ownerEdgeId,edgeMap);
          edgesOfNodeExceptOwnerEdge.each{ edgeName , edgeData ->
              edgeMap.remove(edgeName);
              def otherNodeName=edgeData.target;
              if(edgeData.target==nodeName)
              {
                  otherNodeName=edgeData.source;
              }
              def otherNodeData=deviceSet[otherNodeName];
              if(otherNodeData)
              {
                  //collapse the target node if it is owned by this node
                  if(otherNodeData.ownerEdgeId==edgeName)
                  {
                      collapsedNodeNames.addAll(collapseNode(otherNodeName,deviceSet,edgeMap));
                  }
              }
          }
      }
  }
  return collapsedNodeNames;
}
def getEdgesOfNodeFromEdgeMap(nodeName,edgeMap)
{
  return edgeMap.findAll{ it.value.source == nodeName || it.value.target == nodeName };
}
def getEdgesOfNodeExceptOwnerEdgeFromEdgeMap(nodeName,ownerEdgeId,edgeMap)
{
    def edgesOfNode=getEdgesOfNodeFromEdgeMap(nodeName,edgeMap);
    return edgesOfNode.findAll{it.value.id != ownerEdgeId && !areEdgesDuplicate(edgeMap,ownerEdgeId,it.value.id) };
}
def areEdgesDuplicate(edgeMap,edge1Name,edge2Name)
{
  def isDuplicate=false;
  def edge1Data=edgeMap[edge1Name];
  def edge2Data=edgeMap[edge2Name];

  if(edge1Data!=null && edge2Data!=null)
  {
      isDuplicate= isDuplicate || (edge1Data.source == edge2Data.source && edge1Data.target == edge2Data.target ) ;
      isDuplicate= isDuplicate || (edge1Data.source == edge2Data.target && edge1Data.target == edge2Data.source ) ;
  }
  return isDuplicate;
}
