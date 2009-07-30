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
import groovy.xml.MarkupBuilder
MAPDATA=extractMapDataFromParameter();
// ------------------ CONFIGURATION --------------------------------------
CONFIG=new mapConfiguration().getConfiguration(MAPDATA.mapType);
// ------------------ END OF CONFIGURATION --------------------------------


def nodeString = params.nodes;
def edgeString = params.edges;
def nodes = [];
def edges = [];
if(nodeString != null)
{
    nodes = nodeString.splitPreserveAllTokens(";").findAll {it != ""};
}
if(edgeString != null)
{
    edges = edgeString.splitPreserveAllTokens(";").findAll {it != ""};
}

def writer = new StringWriter();
def mapDataBuilder = new MarkupBuilder(writer);

mapDataBuilder.graphData {

    nodes.each {  nodeParam->
        def nodeData = extractNodeDataFromParameter(nodeParam);
        def device = nodeData.nodeModel.get( name : nodeData.name);
        mapDataBuilder.node( buildNodeData(device));
    }

    edges.each { edgeParam->
        def edgeTokens = edgeParam.splitPreserveAllTokens(",");
        def linkName=edgeTokens[0];
        def source = edgeTokens[1];
        def target = edgeTokens[2];

        def links = getLinkModel().searchEvery("name:${linkName.exactQuery()} ${getMapTypeQuery()}");

        if( links.size() != 0 )
        {
            def link=links[0];
            
            mapDataBuilder.edge( id:link.name,source : source, target : target, state : link.getState());
        }

    }

}
return writer.toString();

//utility functions
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
def buildNodeData(device)
{
     def nodeData=["gauged":"true"]
     nodeData["id"]=device.name;
     nodeData["name"]=device.name;
     nodeData["rsClassName"]=device.class.name;
     nodeData["state"]=device.getState();     
     CONFIG.NODE_PROPERTY_MAPPING.each{ dataPropName,modelPropName ->
        nodeData[dataPropName]=device.getProperty(modelPropName);
     }
     return nodeData;

}

def getLinkModel()
{
    return this.class.classLoader.loadClass(CONFIG.DEFAULT_CONNECTION_MODEL);
}
