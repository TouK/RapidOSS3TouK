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
// ------------------ CONFIGURATION --------------------------------------
CONFIG=new mapConfiguration().getConfiguration();
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

    nodes.each {
        def deviceName=it;
        def rsAlias="";
        def nodeModel=getNodeModel(rsAlias)
        def device = nodeModel.get( name : deviceName);
        mapDataBuilder.node( buildNodeData(device));
    }

    edges.each {
        def edgeTokens = it.splitPreserveAllTokens(",");
        def source = edgeTokens[0];
        def target = edgeTokens[1];
        def links = getLinkModel().searchEvery( "${CONFIG.CONNECTION_SOURCE_PROPERTY}:${source.exactQuery()} ${CONFIG.CONNECTION_TARGET_PROPERTY}: ${target.exactQuery()}");
        if( links.size() == 0 )
        	links = getLinkModel().searchEvery( "${CONFIG.CONNECTION_SOURCE_PROPERTY}:${target.exactQuery()} ${CONFIG.CONNECTION_TARGET_PROPERTY}: ${source.exactQuery()}");

        if( links.size() != 0 )
        {
            mapDataBuilder.edge( source : source, target : target, state : links[0].getState());
        }

    }

}
return writer.toString();

//utility functions
def buildNodeData(device)
{
     def nodeData=["gauged":"true"]     
     nodeData["id"]=device.name;
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
def getNodeModel(rsAlias)
{
    def className=null;
    if(CONFIG.USE_DEFAULT_NODE_MODEL)
    {
        className=CONFIG.DEFAULT_NODE_MODEL;
    }
    else
    {
        className=rsAlias;
    }
    return this.class.classLoader.loadClass(className);
}




