import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.scripting.ScriptManager
import script.CmdbScript
import script.CmdbScriptOperations
import com.ifountain.rcmdb.test.util.CompassForTests

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class ExpandMapScriptTests  extends RapidCmdbWithCompassTestCase {


    public void setUp() {
        super.setUp();
        initialize([CmdbScript,RsComputerSystem,RsTopologyObject,RsLink,relation.Relation], []);
        CompassForTests.addOperationSupport (CmdbScript,CmdbScriptOperations);
        initializeScriptManager();
    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
          //to run in Hudson
        def base_directory = "../RapidSuite";
        //def canonicalPath=new File(System.getProperty("base.dir", ".")).getCanonicalPath();
        def canonicalPath=new File(".").getCanonicalPath();
        //to run in developer pc
        if(canonicalPath.endsWith("RapidModules"))
        {
            base_directory = "RapidInsight"
        }
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManager manager = ScriptManager.getInstance();
        manager.initialize(this.class.getClassLoader(), base_directory, [], [:]);
        //new File("$base_directory/$ScriptManager.SCRIPT_DIRECTORY").mkdirs();

    }
    public void testExpandMapWith1Node()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())

        //calling with no expandedNodeName
        def params=[:];
        params.nodes="start,false,50,100;"

        def expandData=getExpandMapData(params);
        assertEquals(1,expandData.nodes.size());
        assertEquals(0,expandData.edges.size());

        def nodeData=expandData.nodes[source.name];
        checkNodeData(nodeData,source,"false","false","50","100");

        //calling with expandedNodeName
        params=[:];
        params.nodes="start,false,50,100;"
        params.expandedNodeName="start";
        expandData=getExpandMapData(params);
        assertEquals(1,expandData.nodes.size());
        assertEquals(0,expandData.edges.size());

        nodeData=expandData.nodes[source.name];
        checkNodeData(nodeData,source,"true","true","50","100");

    }

     public void testExpandMapWith1To1NodesAndWithLinkDuplicateAndWithLinkReverse()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())
        def target=RsComputerSystem.add(name:"end",model:"emodel",className:"eclass");
        assertFalse(target.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name,connectedSystems:[source,target]);
        assertFalse(link1.hasErrors())

        def params=[:];
        params.nodes="start,false,50,100;"
        params.expandedNodeName="start";
        def expandData=getExpandMapData(params);
        assertEquals(2,expandData.nodes.size());
        assertEquals(1,expandData.edges.size());


        def sourceNodeData=expandData.nodes[source.name];
        def targetNodeData=expandData.nodes[target.name];
        def edgeData=getEdgeFrom(expandData.edges,source.name,target.name)

        checkNodeData(sourceNodeData,source,"true","true","50","100");
        checkNodeData(targetNodeData,target,"false","false","","");
        checkEdgeData(edgeData,source.name,target.name);

        // add a duplicate link ,  and check that result is same
        def link1Duplicate=RsLink.add(name:"l1Duplicate",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name,connectedSystems:[source,target]);
        assertFalse(link1Duplicate.hasErrors())

        def duplicateExpandData=getExpandMapData(params);

        assertEquals(2,duplicateExpandData.nodes.size());
        assertEquals(1,duplicateExpandData.edges.size());

        def sourceDuplicateNodeData=duplicateExpandData.nodes[source.name];
        def targetDuplicateNodeData=duplicateExpandData.nodes[target.name];
        def edgeDuplicateData=getEdgeFrom(duplicateExpandData.edges,source.name,target.name)

        checkNodeData(sourceDuplicateNodeData,source,"true","true","50","100");
        checkNodeData(targetDuplicateNodeData,target,"false","false","","");
        checkEdgeData(edgeDuplicateData,source.name,target.name);

        // add a reverse link , link is duplicated , and check that result is same
        def link1Reverse=RsLink.add(name:"l1Reverse",a_ComputerSystemName:target.name,z_ComputerSystemName:source.name,connectedSystems:[source,target]);
        assertFalse(link1Reverse.hasErrors())

        def reverseExpandData=getExpandMapData(params);
        assertEquals(2,reverseExpandData.nodes.size());
        assertEquals(1,reverseExpandData.edges.size());
    
        def sourceReverseNodeData=reverseExpandData.nodes[source.name];
        def targetReverseNodeData=reverseExpandData.nodes[target.name];
        def edgeReverseData=getEdgeFrom(reverseExpandData.edges,source.name,target.name)

        checkNodeData(sourceReverseNodeData,source,"true","true","50","100");
        checkNodeData(targetReverseNodeData,target,"false","false","","");
        checkEdgeData(edgeReverseData,source.name,target.name);

    }
    public void testExpandMapWith1To1To1Nodes()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name,connectedSystems:[node1,node2]);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node2.name,z_ComputerSystemName:node3.name,connectedSystems:[node2,node3]);
        assertFalse(link2.hasErrors())


        def params=[:];
        params.nodes="node1,false,,;"
        params.expandedNodeName="node1";
        def expandData=getExpandMapData(params);
        assertEquals(2,expandData.nodes.size());
        assertEquals(1,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def link1Data=getEdgeFrom(expandData.edges,node1.name,node2.name)

        checkNodeData(node1NodeData,node1,"true","true","","");
        checkNodeData(node2NodeData,node2,"false","true","","");
        checkEdgeData(link1Data,node1.name,node2.name);

        //expand node1 again,and see the results is same
        params.nodes="node1,true,,;node2,false,,"
        params.expandedNodeName="node1";
        def expandAgainData=getExpandMapData(params);
        assertEquals(2,expandAgainData.nodes.size());
        assertEquals(1,expandAgainData.edges.size());

        def node1AgainNodeData=expandAgainData.nodes[node1.name];
        def node2AgainNodeData=expandAgainData.nodes[node2.name];
        def link1AgainData=getEdgeFrom(expandAgainData.edges,node1.name,node2.name)


        checkNodeData(node1AgainNodeData,node1,"true","true","","");
        checkNodeData(node2AgainNodeData,node2,"false","true","","");
        checkEdgeData(link1AgainData,node1.name,node2.name);

        //expand node2
        params.nodes="node1,true,,;node2,false,,"
        params.expandedNodeName="node2";
        def expandNode2Data=getExpandMapData(params);

        assertEquals(3,expandNode2Data.nodes.size());
        assertEquals(2,expandNode2Data.edges.size());


        def node1NodeData2=expandNode2Data.nodes[node1.name];
        def node2NodeData2=expandNode2Data.nodes[node2.name];
        def node3NodeData2=expandNode2Data.nodes[node3.name];
        def link1Data2=getEdgeFrom(expandNode2Data.edges,node1.name,node2.name)
        def link2Data2=getEdgeFrom(expandNode2Data.edges,node2.name,node3.name)

        checkNodeData(node1NodeData2,node1,"true","true","","");
        checkNodeData(node2NodeData2,node2,"true","true","","");
        checkNodeData(node3NodeData2,node3,"false","false","","");
        checkEdgeData(link1Data2,node1.name,node2.name);
        checkEdgeData(link2Data2,node2.name,node3.name);




    }
    public void testExpandMapWithTriangleNodes()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name,connectedSystems:[node1,node2]);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node2.name,z_ComputerSystemName:node3.name,connectedSystems:[node2,node3]);
        assertFalse(link2.hasErrors())
        def link3=RsLink.add(name:"l3",a_ComputerSystemName:node3.name,z_ComputerSystemName:node1.name,connectedSystems:[node3,node1]);
        assertFalse(link3.hasErrors())

        def params=[:];
        params.nodes="node1,false,,;"
        params.expandedNodeName="node1";
        def expandData=getExpandMapData(params);
        assertEquals(3,expandData.nodes.size());
        assertEquals(2,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def node3NodeData=expandData.nodes[node3.name];
        def link1Data=getEdgeFrom(expandData.edges,node1.name,node2.name)
        def link2Data=getEdgeFrom(expandData.edges,node1.name,node3.name)

        checkNodeData(node1NodeData,node1,"true","true","","");
        checkNodeData(node2NodeData,node2,"false","true","","");
        checkNodeData(node3NodeData,node3,"false","true","","");
        checkEdgeData(link1Data,node1.name,node2.name);
        checkEdgeData(link2Data,node1.name,node3.name);


        //expand node2
        params.nodes="node1,true,,;node2,false,,;node3,false,,"
        params.expandedNodeName="node2";
        def expandNode2Data=getExpandMapData(params);

        assertEquals(3,expandNode2Data.nodes.size());
        assertEquals(3,expandNode2Data.edges.size());


        def node1NodeData2=expandNode2Data.nodes[node1.name];
        def node2NodeData2=expandNode2Data.nodes[node2.name];
        def node3NodeData2=expandNode2Data.nodes[node3.name];
        def link1Data2=getEdgeFrom(expandNode2Data.edges,node1.name,node2.name)
        def link2Data2=getEdgeFrom(expandNode2Data.edges,node2.name,node3.name)
        def link3Data2=getEdgeFrom(expandNode2Data.edges,node1.name,node3.name)


        checkNodeData(node1NodeData2,node1,"true","true","","");
        checkNodeData(node2NodeData2,node2,"true","true","","");
        checkNodeData(node3NodeData2,node3,"false","false","","");
        checkEdgeData(link1Data2,node1.name,node2.name);
        checkEdgeData(link2Data2,node2.name,node3.name);
        checkEdgeData(link3Data2,node1.name,node3.name);


        //expand node3
        params.nodes="node1,true,,;node2,false,,;node3,false,,"
        params.expandedNodeName="node3";
        def expandNode3Data=getExpandMapData(params);

        assertEquals(3,expandNode3Data.nodes.size());
        assertEquals(3,expandNode3Data.edges.size());


        def node1NodeData3=expandNode3Data.nodes[node1.name];
        def node2NodeData3=expandNode3Data.nodes[node2.name];
        def node3NodeData3=expandNode3Data.nodes[node3.name];
        def link1Data3=getEdgeFrom(expandNode3Data.edges,node1.name,node2.name)
        def link2Data3=getEdgeFrom(expandNode3Data.edges,node2.name,node3.name)
        def link3Data3=getEdgeFrom(expandNode3Data.edges,node1.name,node3.name)


        checkNodeData(node1NodeData3,node1,"true","true","","");
        checkNodeData(node2NodeData3,node2,"false","false","","");
        checkNodeData(node3NodeData3,node3,"true","true","","");
        checkEdgeData(link1Data3,node1.name,node2.name);
        checkEdgeData(link2Data3,node2.name,node3.name);
        checkEdgeData(link3Data3,node1.name,node3.name);


    }
    public void  testExpandMapWith1ToNToMNodes()
    {
        def script=CmdbScript.addScript([name:"expandMap",type: CmdbScript.ONDEMAND])
        assertFalse(script.hasErrors());

        def sourceNode=RsComputerSystem.add(name:"sourceNode",model:"model1",className:"class1");
        assertFalse(sourceNode.hasErrors())


        def targetCount=3;
        def subTargetCount=2;

        def targets=[:];
        targetCount.times{ counter ->
            def target=RsComputerSystem.add(name:"target${counter}",model:"modelt",className:"classt");
            targets[target.name]=target;

            assertFalse(target.hasErrors())
            def link=RsLink.add(name:"l${counter}",a_ComputerSystemName:sourceNode.name,z_ComputerSystemName:target.name,connectedSystems:[sourceNode,target]);
            assertFalse(link.hasErrors())
            subTargetCount.times{ subCounter ->
                def subTarget=RsComputerSystem.add(name:"subtarget${counter}_${subCounter}",model:"modelt",className:"classt");
                targets[subTarget.name]=subTarget;

                assertFalse(subTarget.hasErrors())
                def subLink=RsLink.add(name:"subl${counter}_${subCounter}",a_ComputerSystemName:subTarget.name,z_ComputerSystemName:target.name,connectedSystems:[subTarget,target]);
                assertFalse(link.hasErrors())

            }
        }
        assertEquals(1+targetCount+targetCount*subTargetCount,RsComputerSystem.countHits("alias:*"))
        assertEquals(targetCount+targetCount*subTargetCount,RsLink.countHits("alias:*"))

        def params=[:];
        params.nodes="sourceNode,false,,;"
        params.expandedNodeName="sourceNode";
        def expandData=getExpandMapData(params);
        assertEquals(1+targetCount,expandData.nodes.size());
        assertEquals(targetCount,expandData.edges.size());


        def sourceNodeData=expandData.nodes[sourceNode.name];
        checkNodeData(sourceNodeData,sourceNode,"true","true","","");


        targetCount.times{ counter ->
            def targetName="target${counter}".toString()
            def targetNode=targets[targetName];
            def targetNodeData=expandData.nodes[targetName];
            checkNodeData(targetNodeData,targetNode,"false","true","","");

            def linkData=getEdgeFrom(expandData.edges,sourceNode.name,targetNode.name);
            checkEdgeData (linkData,sourceNode.name,targetNode.name);


        }

        /*************************************************************************************************/
        //expand each targetNode when sourceNode is expanded only
        def nodeParamString="sourceNode,true,,;";
        targetCount.times{ counter ->
            def targetName="target${counter}".toString();
            nodeParamString+="${targetName},false,,;";

        }
        targetCount.times{ counter ->
            def targetName="target${counter}".toString();
            params.nodes=nodeParamString;
            params.expandedNodeName=targetName;
            
            def targetExpandData=getExpandMapData(params);

            assertEquals(1+targetCount+1*subTargetCount,targetExpandData.nodes.size());
            assertEquals(targetCount+1*subTargetCount,targetExpandData.edges.size());

            //check sourceNode data
            sourceNodeData=targetExpandData.nodes[sourceNode.name];
            checkNodeData(sourceNodeData,sourceNode,"true","true","","");

            //check expanded node
            def expandedNode=targets[targetName];
            def expandedNodeData=targetExpandData.nodes[targetName];
            
            checkNodeData(expandedNodeData,expandedNode,"true","true","","");
            def linkData=getEdgeFrom(targetExpandData.edges,sourceNode.name,expandedNode.name);
            checkEdgeData (linkData,sourceNode.name,expandedNode.name);

            subTargetCount.times{ subTargetCounter ->
                def targetNameToCheck="subtarget${counter}_${subTargetCounter}".toString();
                def targetNode=targets[targetNameToCheck];
                def targetNodeData=targetExpandData.nodes[targetNameToCheck];
                checkNodeData(targetNodeData,targetNode,"false","false","","");

                def targetLinkData=getEdgeFrom(targetExpandData.edges,expandedNode.name,targetNode.name);
                checkEdgeData (targetLinkData,expandedNode.name,targetNode.name);
            }

            //check targets which are not expanded
            targetCount.times{ targetCounter ->
                def targetNameToCheck="target${targetCounter}".toString()
                if(targetName != targetNameToCheck )
                {
                    def targetNode=targets[targetNameToCheck];
                    def targetNodeData=targetExpandData.nodes[targetNameToCheck];
                    checkNodeData(targetNodeData,targetNode,"false","true","","");
                    def targetLinkData=getEdgeFrom(targetExpandData.edges,sourceNode.name,targetNode.name);
                    checkEdgeData (targetLinkData,sourceNode.name,targetNode.name);
                }
            }
        }

        /*************************************************************************************************/
        //expand each target node one after another

        def expandedTargetNames=[:];

        targetCount.times{ counter ->

            nodeParamString="sourceNode,true,,;";
            println "expandedTargetNames ${expandedTargetNames}"
            targetCount.times{ targetCounter ->
                def targetName="target${targetCounter}".toString();
                if(expandedTargetNames.containsKey(targetName))
                {
                    nodeParamString+="${targetName},true,,;";
                    subTargetCount.times{ subTargetCounter ->
                        def subTargetName="subtarget${counter}_${subTargetCounter}"
                        nodeParamString+="${subTargetName},false,,;";
                    }
                }
                else
                {
                    nodeParamString+="${targetName},false,,;";
                }

            }

            def targetName="target${counter}".toString();


            params.nodes=nodeParamString;
            params.expandedNodeName=targetName;
            expandedTargetNames[targetName]=targetName;

            def targetExpandData=getExpandMapData(params);
            
            assertEquals(1+targetCount+expandedTargetNames.size()*subTargetCount,targetExpandData.nodes.size());
            assertEquals(targetCount+expandedTargetNames.size()*subTargetCount,targetExpandData.edges.size());

             //check sourceNode data
            sourceNodeData=targetExpandData.nodes[sourceNode.name];
            checkNodeData(sourceNodeData,sourceNode,"true","true","","");

            //check expanded nodes
            expandedTargetNames.each{ targetNameKey, targetNameValue ->                
                def expandedNode=targets[targetNameKey];
                def expandedNodeData=targetExpandData.nodes[targetNameKey];

               checkNodeData(expandedNodeData,expandedNode,"true","true","","");
               def linkData=getEdgeFrom(targetExpandData.edges,sourceNode.name,expandedNode.name);
               checkEdgeData (linkData,sourceNode.name,expandedNode.name);

               subTargetCount.times{ subTargetCounter ->
                    def targetIndex=targetNameKey.replace("target","");
                    def subTargetNameToCheck="subtarget${targetIndex}_${subTargetCounter}".toString();
                    def targetNode=targets[subTargetNameToCheck];
                    def targetNodeData=targetExpandData.nodes[subTargetNameToCheck];
                    checkNodeData(targetNodeData,targetNode,"false","false","","");

                    def targetLinkData=getEdgeFrom(targetExpandData.edges,expandedNode.name,targetNode.name);
                    checkEdgeData (targetLinkData,expandedNode.name,targetNode.name);
               }
            }


            //check targets which are not expanded
            targetCount.times{ targetCounter ->
                def targetNameToCheck="target${targetCounter}".toString()
                if(! expandedTargetNames.containsKey(targetNameToCheck) )
                {
                    def targetNode=targets[targetNameToCheck];
                    def targetNodeData=targetExpandData.nodes[targetNameToCheck];
                    checkNodeData(targetNodeData,targetNode,"false","true","","");
                    def targetLinkData=getEdgeFrom(targetExpandData.edges,sourceNode.name,targetNode.name);
                    checkEdgeData (targetLinkData,sourceNode.name,targetNode.name);
                }
            }
         }
    }
    def checkNodeData(nodeData,node,expanded,expandable,x,y)
    {
        assertEquals(node.name,nodeData.id);
        assertEquals(node.model,nodeData.model);
        assertEquals(node.className,nodeData.type);
        assertEquals(expanded,nodeData.expanded);
        assertEquals(expandable,nodeData.expandable);
        assertEquals(x,nodeData.x);
        assertEquals(y,nodeData.y);

        assertEquals("true",nodeData.gauged);
    }
    def getEdgeFrom(edges,sourceName,targetName)
    {
        def edgeData=edges[getEdgeMapKey(sourceName,targetName)];
        if(edgeData==null)
        {
            edgeData=edges[getEdgeMapKey(targetName,sourceName)];
        }
        return edgeData;

    }
    def getEdgeMapKey(sourceName,targetName)
    {
        return sourceName+""+targetName;
    }
    def checkEdgeData(edgeData,sourceName,targetName)
    {
        if(edgeData==null)
        {
            fail("edge ${edgeData} is not same as ${sourceName} to ${targetName}")
        }
        if(edgeData.source==sourceName)
        {
            assertEquals(edgeData.target,targetName)
        }
        else if(edgeData.source==targetName)
        {
            assertEquals(edgeData.target,sourceName)
        }
        else
        {
            fail("edge ${edgeData} is not same as ${sourceName} to ${targetName}")
        }
    }

     def getExpandMapData(params){
        def script=CmdbScript.get(name:"expandMap")

        def scriptResult=CmdbScript.runScript(script,["params":params]);

        def resultXml = new XmlSlurper().parseText(scriptResult);

        println scriptResult

        def results=[:]
        results.nodes=[:];
        results.edges=[:];

        def nodeProps=["id","model","type","gauged","expanded","expandable","x","y"];

        resultXml.node.each {    dataRow->
            def nodeData=[:];
            nodeProps.each{ propName ->
                nodeData[propName]=dataRow.@"${propName}".toString();
            }
            results.nodes.put(nodeData.id,nodeData);
        }
        def edgeProps=["source","target"];
        resultXml.edge.each {    dataRow->
            def edgeData=[:];
            edgeProps.each{ propName ->
                edgeData[propName]=dataRow.@"${propName}".toString();
            }            
            results.edges.put(getEdgeMapKey(edgeData.source,edgeData.target),edgeData);
        }
        println "result parsed from xml ${results}"
        return results;
    }


}



