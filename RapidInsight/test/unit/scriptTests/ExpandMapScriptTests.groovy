package scriptTests

import com.ifountain.rcmdb.test.util.RapidCmdbWithCompassTestCase
import com.ifountain.rcmdb.test.util.scripting.ScriptManagerForTest

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 19, 2009
* Time: 9:06:30 AM
* To change this template use File | Settings | File Templates.
*/
class ExpandMapScriptTests  extends RapidCmdbWithCompassTestCase {


    def RsComputerSystem;
    def RsTopologyObject;
    def RsLink;
    def RsMapConnection;
    def RsEvent;
    

    public void setUp() {
        super.setUp();

        ["RsComputerSystem","RsTopologyObject","RsLink","RsMapConnection","RsEvent"].each{ className ->
             setProperty(className,gcl.loadClass(className));
        }

        initialize([RsComputerSystem,RsTopologyObject,RsLink,RsMapConnection,RsEvent], []);

        initializeScriptManager();

    }

    public void tearDown() {

        super.tearDown();
    }
    void initializeScriptManager()
    {
        def base_directory = getWorkspacePath()+"/RapidModules/RapidInsight/scripts"
        println "base path is :"+new File(base_directory).getCanonicalPath();

        ScriptManagerForTest.initialize (gcl,base_directory);
        ScriptManagerForTest.addScript("expandMap");
    }
    public void testExpandMapWith1Node()
    {
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
        checkNodeData(nodeData,source,"true","false","50","100");

    }

     public void testExpandMapWith1To1NodesAndWithLinkDuplicateAndWithLinkReverseGeneratesDuplicatedLinks()
    {

        def source=RsComputerSystem.add(name:"start",model:"smodel",className:"sclass");
        assertFalse(source.hasErrors())
        def target=RsComputerSystem.add(name:"end",model:"emodel",className:"eclass");
        assertFalse(target.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1.hasErrors())


        def params=[:];
        params.nodes="start,false,50,100;"
        params.expandedNodeName="start";
        def expandData=getExpandMapData(params);
        assertEquals(2,expandData.nodes.size());
        assertEquals(1,expandData.edges.size());


        def sourceNodeData=expandData.nodes[source.name];
        def targetNodeData=expandData.nodes[target.name];
        def edgeData=getEdgeFrom(expandData.edges,link1.name)

        checkNodeData(sourceNodeData,source,"true","false","50","100");
        checkNodeData(targetNodeData,target,"false","false","","");
        checkEdgeData(edgeData,source.name,target.name,link1.name);

        // add a duplicate link ,  and check that duplicate link also exists in xml
        def link1Duplicate=RsLink.add(name:"l1Duplicate",a_ComputerSystemName:source.name,z_ComputerSystemName:target.name);
        assertFalse(link1Duplicate.hasErrors())

        def duplicateExpandData=getExpandMapData(params);

        assertEquals(2,duplicateExpandData.nodes.size());
        assertEquals(2,duplicateExpandData.edges.size());

        def sourceDuplicateNodeData=duplicateExpandData.nodes[source.name];
        def targetDuplicateNodeData=duplicateExpandData.nodes[target.name];
        def edgeDuplicateData=getEdgeFrom(duplicateExpandData.edges,link1Duplicate.name)
        edgeData=getEdgeFrom(duplicateExpandData.edges,link1.name)

        checkNodeData(sourceDuplicateNodeData,source,"true","false","50","100");
        checkNodeData(targetDuplicateNodeData,target,"false","false","","");
        checkEdgeData(edgeDuplicateData,source.name,target.name,link1Duplicate.name);
        checkEdgeData(edgeData,source.name,target.name,link1.name);


        // add a reverse link , link is duplicated , and check that reverse link also exists in xml
        def link1Reverse=RsLink.add(name:"l1Reverse",a_ComputerSystemName:target.name,z_ComputerSystemName:source.name);
        assertFalse(link1Reverse.hasErrors())

        def reverseExpandData=getExpandMapData(params);
        assertEquals(2,reverseExpandData.nodes.size());
        assertEquals(3,reverseExpandData.edges.size());

        def sourceReverseNodeData=reverseExpandData.nodes[source.name];
        def targetReverseNodeData=reverseExpandData.nodes[target.name];
        def edgeReverseData=getEdgeFrom(reverseExpandData.edges,link1Reverse.name)
        edgeDuplicateData=getEdgeFrom(reverseExpandData.edges,link1Duplicate.name)
        edgeData=getEdgeFrom(reverseExpandData.edges,link1.name)

        checkNodeData(sourceReverseNodeData,source,"true","false","50","100");
        checkNodeData(targetReverseNodeData,target,"false","false","","");
        checkEdgeData(edgeReverseData,source.name,target.name,link1Reverse.name);
        checkEdgeData(edgeDuplicateData,source.name,target.name,link1Duplicate.name);
        checkEdgeData(edgeData,source.name,target.name,link1.name);

    }
    public void testExpandMapWith1To1To1Nodes()
    {

        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node2.name,z_ComputerSystemName:node3.name);
        assertFalse(link2.hasErrors())


        def params=[:];
        params.nodes="node1,false,,;"
        params.expandedNodeName="node1";
        def expandData=getExpandMapData(params);
        assertEquals(2,expandData.nodes.size());
        assertEquals(1,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def link1Data=getEdgeFrom(expandData.edges,link1.name)

        checkNodeData(node1NodeData,node1,"true","false","","");
        checkNodeData(node2NodeData,node2,"false","true","","");
        checkEdgeData(link1Data,node1.name,node2.name,link1.name);

        //expand node1 again,and see the results is same
        params.nodes="node1,true,,;node2,false,,"
        params.expandedNodeName="node1";
        def expandAgainData=getExpandMapData(params);
        assertEquals(2,expandAgainData.nodes.size());
        assertEquals(1,expandAgainData.edges.size());

        def node1AgainNodeData=expandAgainData.nodes[node1.name];
        def node2AgainNodeData=expandAgainData.nodes[node2.name];
        def link1AgainData=getEdgeFrom(expandAgainData.edges,link1.name)


        checkNodeData(node1AgainNodeData,node1,"true","false","","");
        checkNodeData(node2AgainNodeData,node2,"false","true","","");
        checkEdgeData(link1AgainData,node1.name,node2.name,link1.name);

        //expand node2
        params.nodes="node1,true,,;node2,false,,"
        params.expandedNodeName="node2";
        def expandNode2Data=getExpandMapData(params);

        assertEquals(3,expandNode2Data.nodes.size());
        assertEquals(2,expandNode2Data.edges.size());


        def node1NodeData2=expandNode2Data.nodes[node1.name];
        def node2NodeData2=expandNode2Data.nodes[node2.name];
        def node3NodeData2=expandNode2Data.nodes[node3.name];
        def link1Data2=getEdgeFrom(expandNode2Data.edges,link1.name)
        def link2Data2=getEdgeFrom(expandNode2Data.edges,link2.name)

        checkNodeData(node1NodeData2,node1,"true","false","","");
        checkNodeData(node2NodeData2,node2,"true","false","","");
        checkNodeData(node3NodeData2,node3,"false","false","","");
        checkEdgeData(link1Data2,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data2,node2.name,node3.name,link2.name);




    }
    public void testExpandMapWithTriangleNodes()
    {
        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node2.name,z_ComputerSystemName:node3.name);
        assertFalse(link2.hasErrors())
        def link3=RsLink.add(name:"l3",a_ComputerSystemName:node3.name,z_ComputerSystemName:node1.name);
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
        def link1Data=getEdgeFrom(expandData.edges,link1.name)
        def link2Data=getEdgeFrom(expandData.edges,link3.name)

        checkNodeData(node1NodeData,node1,"true","false","","");
        checkNodeData(node2NodeData,node2,"false","true","","");
        checkNodeData(node3NodeData,node3,"false","true","","");
        checkEdgeData(link1Data,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data,node1.name,node3.name,link3.name);


        //expand node2
        params.nodes="node1,true,,;node2,false,,;node3,false,,"
        params.expandedNodeName="node2";
        def expandNode2Data=getExpandMapData(params);

        assertEquals(3,expandNode2Data.nodes.size());
        assertEquals(3,expandNode2Data.edges.size());


        def node1NodeData2=expandNode2Data.nodes[node1.name];
        def node2NodeData2=expandNode2Data.nodes[node2.name];
        def node3NodeData2=expandNode2Data.nodes[node3.name];
        def link1Data2=getEdgeFrom(expandNode2Data.edges,link1.name)
        def link2Data2=getEdgeFrom(expandNode2Data.edges,link2.name)
        def link3Data2=getEdgeFrom(expandNode2Data.edges,link3.name)


        checkNodeData(node1NodeData2,node1,"true","false","","");
        checkNodeData(node2NodeData2,node2,"true","false","","");
        checkNodeData(node3NodeData2,node3,"false","false","","");
        checkEdgeData(link1Data2,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data2,node2.name,node3.name,link2.name);
        checkEdgeData(link3Data2,node1.name,node3.name,link3.name);


        //expand node3
        params.nodes="node1,true,,;node2,false,,;node3,false,,"
        params.expandedNodeName="node3";
        def expandNode3Data=getExpandMapData(params);

        assertEquals(3,expandNode3Data.nodes.size());
        assertEquals(3,expandNode3Data.edges.size());


        def node1NodeData3=expandNode3Data.nodes[node1.name];
        def node2NodeData3=expandNode3Data.nodes[node2.name];
        def node3NodeData3=expandNode3Data.nodes[node3.name];
        def link1Data3=getEdgeFrom(expandNode3Data.edges,link1.name)
        def link2Data3=getEdgeFrom(expandNode3Data.edges,link2.name)
        def link3Data3=getEdgeFrom(expandNode3Data.edges,link3.name)


        checkNodeData(node1NodeData3,node1,"true","false","","");
        checkNodeData(node2NodeData3,node2,"false","false","","");
        checkNodeData(node3NodeData3,node3,"true","false","","");
        checkEdgeData(link1Data3,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data3,node2.name,node3.name,link2.name);
        checkEdgeData(link3Data3,node1.name,node3.name,link3.name);


    }
    public void testExpandMapWithTriangleNodesAndMapType()
    {
        
        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsEvent.add(name:"node2");
        assertFalse(node2.hasErrors())
        def node3=RsEvent.add(name:"node3");
        assertFalse(node3.hasErrors())
        //this node  will not exist on the results, since connection is of other map type
        def node4=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node4.hasErrors());
        
        def link1=RsMapConnection.add(name:"l1",a_Name:node1.name,a_RsClassName:node1.class.name,z_Name:node2.name,z_RsClassName:node2.class.name,mapType:"sampleMap");
        assertFalse(link1.hasErrors())
        def link2=RsMapConnection.add(name:"l2",a_Name:node2.name,a_RsClassName:node2.class.name,z_Name:node3.name,z_RsClassName:node3.class.name,mapType:"sampleMap");
        assertFalse(link2.hasErrors())
        def link3=RsMapConnection.add(name:"l3",a_Name:node3.name,a_RsClassName:node3.class.name,z_Name:node1.name,z_RsClassName:node1.class.name,mapType:"sampleMap");
        assertFalse(link3.hasErrors())
        //this link will be ignored because have different mapType
        def link4=RsMapConnection.add(name:"l4",a_Name:node1.name,a_RsClassName:node1.class.name,z_Name:node4.name,z_RsClassName:node4.class.name,mapType:"otherMap");
        assertFalse(link3.hasErrors())

        def params=[:];
        params.nodes="node1,RsComputerSystem,false,,;"
        params.expandedNodeName="node1";
        params.nodePropertyList="name,rsClassName,expanded,x,y"
        params.mapProperties="sampleMap";


        def expandData=getExpandMapData(params);
        assertEquals(3,expandData.nodes.size());
        assertEquals(2,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def node3NodeData=expandData.nodes[node3.name];
        def link1Data=getEdgeFrom(expandData.edges,link1.name)
        def link2Data=getEdgeFrom(expandData.edges,link3.name)

        //check rsClassName
        assertEquals("RsComputerSystem",node1NodeData.rsClassName);
        assertEquals("RsEvent",node2NodeData.rsClassName);
        assertEquals("RsEvent",node3NodeData.rsClassName);


        checkNodeData(node1NodeData,node1,"true","false","","");
        checkNodeData(node2NodeData,node2,"false","true","","");
        checkNodeData(node3NodeData,node3,"false","true","","");
        checkEdgeData(link1Data,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data,node1.name,node3.name,link3.name);


        //expand node2
        params.nodes="node1,RsComputerSystem,true,,;node2,RsEvent,false,,;node3,RsEvent,false,,"
        params.expandedNodeName="node2";
        def expandNode2Data=getExpandMapData(params);

        assertEquals(3,expandNode2Data.nodes.size());
        assertEquals(3,expandNode2Data.edges.size());


        def node1NodeData2=expandNode2Data.nodes[node1.name];
        def node2NodeData2=expandNode2Data.nodes[node2.name];
        def node3NodeData2=expandNode2Data.nodes[node3.name];
        def link1Data2=getEdgeFrom(expandNode2Data.edges,link1.name)
        def link2Data2=getEdgeFrom(expandNode2Data.edges,link2.name)
        def link3Data2=getEdgeFrom(expandNode2Data.edges,link3.name)


        //check rsClassName
        assertEquals("RsComputerSystem",node1NodeData.rsClassName);
        assertEquals("RsEvent",node2NodeData.rsClassName);
        assertEquals("RsEvent",node3NodeData.rsClassName);

        checkNodeData(node1NodeData2,node1,"true","false","","");
        checkNodeData(node2NodeData2,node2,"true","false","","");
        checkNodeData(node3NodeData2,node3,"false","false","","");
        checkEdgeData(link1Data2,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data2,node2.name,node3.name,link2.name);
        checkEdgeData(link3Data2,node1.name,node3.name,link3.name);


        //expand node3
        params.nodes="node1,RsComputerSystem,true,,;node2,RsEvent,false,,;node3,RsEvent,false,,"
        params.expandedNodeName="node3";
        def expandNode3Data=getExpandMapData(params);

        assertEquals(3,expandNode3Data.nodes.size());
        assertEquals(3,expandNode3Data.edges.size());


        def node1NodeData3=expandNode3Data.nodes[node1.name];
        def node2NodeData3=expandNode3Data.nodes[node2.name];
        def node3NodeData3=expandNode3Data.nodes[node3.name];
        def link1Data3=getEdgeFrom(expandNode3Data.edges,link1.name)
        def link2Data3=getEdgeFrom(expandNode3Data.edges,link2.name)
        def link3Data3=getEdgeFrom(expandNode3Data.edges,link3.name)


        //check rsClassName
        assertEquals("RsComputerSystem",node1NodeData.rsClassName);
        assertEquals("RsEvent",node2NodeData.rsClassName);
        assertEquals("RsEvent",node3NodeData.rsClassName);

        checkNodeData(node1NodeData3,node1,"true","false","","");
        checkNodeData(node2NodeData3,node2,"false","false","","");
        checkNodeData(node3NodeData3,node3,"true","false","","");
        checkEdgeData(link1Data3,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data3,node2.name,node3.name,link2.name);
        checkEdgeData(link3Data3,node1.name,node3.name,link3.name);


    }
    public void  testExpandMapWith1ToNToMNodes()
    {
        def sourceNode=RsComputerSystem.add(name:"sourceNode",model:"model1",className:"class1");
        assertFalse(sourceNode.hasErrors())


        def targetCount=3;
        def subTargetCount=2;

        def targets=[:];
        targetCount.times{ counter ->
            def target=RsComputerSystem.add(name:"target${counter}",model:"modelt",className:"classt");
            targets[target.name]=target;

            assertFalse(target.hasErrors())
            def link=RsLink.add(name:"l${counter}",a_ComputerSystemName:sourceNode.name,z_ComputerSystemName:target.name);
            assertFalse(link.hasErrors())
            subTargetCount.times{ subCounter ->
                def subTarget=RsComputerSystem.add(name:"subtarget${counter}_${subCounter}",model:"modelt",className:"classt");
                targets[subTarget.name]=subTarget;

                assertFalse(subTarget.hasErrors())
                def subLink=RsLink.add(name:"subl${counter}_${subCounter}",a_ComputerSystemName:subTarget.name,z_ComputerSystemName:target.name);
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
        checkNodeData(sourceNodeData,sourceNode,"true","false","","");


        targetCount.times{ counter ->
            def targetName="target${counter}".toString()
            def targetNode=targets[targetName];
            def targetNodeData=expandData.nodes[targetName];
            checkNodeData(targetNodeData,targetNode,"false","true","","");

            def linkData=getEdgeFrom(expandData.edges,"l${counter}");
            checkEdgeData (linkData,sourceNode.name,targetNode.name,"l${counter}");


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
            checkNodeData(sourceNodeData,sourceNode,"true","false","","");

            //check expanded node
            def expandedNode=targets[targetName];
            def expandedNodeData=targetExpandData.nodes[targetName];

            checkNodeData(expandedNodeData,expandedNode,"true","false","","");
            def linkData=getEdgeFrom(targetExpandData.edges,"l${counter}");
            checkEdgeData (linkData,sourceNode.name,expandedNode.name,"l${counter}");

            subTargetCount.times{ subTargetCounter ->
                def targetNameToCheck="subtarget${counter}_${subTargetCounter}".toString();
                def targetNode=targets[targetNameToCheck];
                def targetNodeData=targetExpandData.nodes[targetNameToCheck];
                checkNodeData(targetNodeData,targetNode,"false","false","","");

                def targetLinkData=getEdgeFrom(targetExpandData.edges,"subl${counter}_${subTargetCounter}");
                checkEdgeData (targetLinkData,expandedNode.name,targetNode.name,"subl${counter}_${subTargetCounter}");
            }

            //check targets which are not expanded
            targetCount.times{ targetCounter ->
                def targetNameToCheck="target${targetCounter}".toString()
                if(targetName != targetNameToCheck )
                {
                    def targetNode=targets[targetNameToCheck];
                    def targetNodeData=targetExpandData.nodes[targetNameToCheck];
                    checkNodeData(targetNodeData,targetNode,"false","true","","");
                    def targetLinkData=getEdgeFrom(targetExpandData.edges,"l${targetCounter}");
                    checkEdgeData (targetLinkData,sourceNode.name,targetNode.name,"l${targetCounter}");
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
            checkNodeData(sourceNodeData,sourceNode,"true","false","","");

            //check expanded nodes
            expandedTargetNames.each{ targetNameKey, targetNameValue ->
                def expandedNode=targets[targetNameKey];
                def expandedNodeData=targetExpandData.nodes[targetNameKey];



               checkNodeData(expandedNodeData,expandedNode,"true","false","","");
               def linkData=getEdgeFrom(targetExpandData.edges,"l${targetNameValue.substringAfter('target')}");
               checkEdgeData (linkData,sourceNode.name,expandedNode.name,"l${targetNameValue.substringAfter('target')}");

               subTargetCount.times{ subTargetCounter ->
                    def targetIndex=targetNameKey.replace("target","");
                    def subTargetNameToCheck="subtarget${targetIndex}_${subTargetCounter}".toString();
                    def targetNode=targets[subTargetNameToCheck];
                    def targetNodeData=targetExpandData.nodes[subTargetNameToCheck];
                    checkNodeData(targetNodeData,targetNode,"false","false","","");

                    def targetLinkData=getEdgeFrom(targetExpandData.edges,"subl${targetIndex}_${subTargetCounter}");
                    checkEdgeData (targetLinkData,expandedNode.name,targetNode.name,"subl${targetIndex}_${subTargetCounter}");
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
                    def targetLinkData=getEdgeFrom(targetExpandData.edges,"l${targetCounter}");
                    checkEdgeData (targetLinkData,sourceNode.name,targetNode.name,"l${targetCounter}");
                }
            }
         }
    }
    public void testCollapseMapWith1To2Nodes()
    {
       /* Map looks like
                 1
              2    3
       */
        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node1.name,z_ComputerSystemName:node3.name);
        assertFalse(link2.hasErrors())

        def params=[:];
        params.nodes="node1,,false,,;"
        params.expandedNodeName="node1";
        params.nodePropertyList="name,ownerEdgeId,expanded,x,y"
        def expandData=getExpandMapData(params);
        assertEquals(3,expandData.nodes.size());
        assertEquals(2,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def node3NodeData=expandData.nodes[node3.name];
        def link1Data=getEdgeFrom(expandData.edges,link1.name)
        def link2Data=getEdgeFrom(expandData.edges,link2.name)

        checkNodeDataForCollapse(node1NodeData,node1,"true","false","","","","true");
        checkNodeDataForCollapse(node2NodeData,node2,"false","false","","","l1","false");
        checkNodeDataForCollapse(node3NodeData,node3,"false","false","","","l2","false");
        checkEdgeData(link1Data,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data,node1.name,node3.name,link2.name);


        //collapse node1 , will remove links and nodes 2&3
        params.nodes="node1,,true,,;node2,l1,false,,;node3,l2,false,,"
        params.collapsedNodeName="node1";
        def collapseNodeData=getExpandMapData(params);
        assertEquals(1,collapseNodeData.nodes.size());
        assertEquals(0,collapseNodeData.edges.size());

        node1NodeData=collapseNodeData.nodes[node1.name];
        checkNodeDataForCollapse(node1NodeData,node1,"false","true","","","","false"); //expanded false , expandable true , collapsible false

        //collapse node2 will change nothing , because it have only one link node2 - node1 , and that link is an owner link , owner links are not removed
        params.nodes="node1,,true,,;node2,l1,false,,;node3,l2,false,,"
        params.collapsedNodeName="node2";
        def collapseNode2Data=getExpandMapData(params);
        assertEquals(3,collapseNode2Data.nodes.size());
        assertEquals(2,collapseNode2Data.edges.size());
        assertEquals(expandData.nodes,collapseNode2Data.nodes);
        assertEquals(expandData.edges,collapseNode2Data.edges);
    }
    public void testCollapseMapWith1To2To2NodesWithSharedNode()
    {
       /* Map looks like
                 1
              2    3
            4    5    6
       */
        def node1=RsComputerSystem.add(name:"node1",model:"model1",className:"class1");
        assertFalse(node1.hasErrors())
        def node2=RsComputerSystem.add(name:"node2",model:"model2",className:"class2");
        assertFalse(node2.hasErrors())
        def node3=RsComputerSystem.add(name:"node3",model:"model3",className:"class3");
        assertFalse(node3.hasErrors())
        def node4=RsComputerSystem.add(name:"node4",model:"model4",className:"class4");
        assertFalse(node4.hasErrors())
        def node5=RsComputerSystem.add(name:"node5",model:"model5",className:"class5");
        assertFalse(node5.hasErrors())
        def node6=RsComputerSystem.add(name:"node6",model:"model6",className:"class6");
        assertFalse(node5.hasErrors())
        def link1=RsLink.add(name:"l1",a_ComputerSystemName:node1.name,z_ComputerSystemName:node2.name);
        assertFalse(link1.hasErrors())
        def link2=RsLink.add(name:"l2",a_ComputerSystemName:node1.name,z_ComputerSystemName:node3.name);
        assertFalse(link2.hasErrors())
        def link3=RsLink.add(name:"l3",a_ComputerSystemName:node2.name,z_ComputerSystemName:node4.name);
        assertFalse(link3.hasErrors())
        def link4=RsLink.add(name:"l4",a_ComputerSystemName:node2.name,z_ComputerSystemName:node5.name);
        assertFalse(link4.hasErrors())
        def link5=RsLink.add(name:"l5",a_ComputerSystemName:node3.name,z_ComputerSystemName:node5.name);
        assertFalse(link5.hasErrors())
        def link6=RsLink.add(name:"l6",a_ComputerSystemName:node3.name,z_ComputerSystemName:node6.name);
        assertFalse(link6.hasErrors())

        def params=[:];
        params.nodes="node1,,false,,;"
        params.expandedNodeName="node1";
        params.nodePropertyList="name,ownerEdgeId,expanded,x,y"
        def expandData=getExpandMapData(params);
        assertEquals(3,expandData.nodes.size());
        assertEquals(2,expandData.edges.size());


        def node1NodeData=expandData.nodes[node1.name];
        def node2NodeData=expandData.nodes[node2.name];
        def node3NodeData=expandData.nodes[node3.name];
        def link1Data=getEdgeFrom(expandData.edges,link1.name)
        def link2Data=getEdgeFrom(expandData.edges,link2.name)

        checkNodeDataForCollapse(node1NodeData,node1,"true","false","","","","true");
        checkNodeDataForCollapse(node2NodeData,node2,"false","true","","","l1","false");
        checkNodeDataForCollapse(node3NodeData,node3,"false","true","","","l2","false");
        checkEdgeData(link1Data,node1.name,node2.name,link1.name);
        checkEdgeData(link2Data,node1.name,node3.name,link2.name);


       //expand node2
       params.nodes="node1,,true,,;node2,l1,false,,;node3,l2,false,,"
       params.expandedNodeName="node2";
       def expandNode2Data=getExpandMapData(params);

       assertEquals(5,expandNode2Data.nodes.size());
       assertEquals(4,expandNode2Data.edges.size());


       def node1NodeData2=expandNode2Data.nodes[node1.name];
       def node2NodeData2=expandNode2Data.nodes[node2.name];
       def node3NodeData2=expandNode2Data.nodes[node3.name];
       def node4NodeData2=expandNode2Data.nodes[node4.name];
       def node5NodeData2=expandNode2Data.nodes[node5.name];
       def link1Data2=getEdgeFrom(expandNode2Data.edges,link1.name)
       def link2Data2=getEdgeFrom(expandNode2Data.edges,link2.name)
       def link3Data2=getEdgeFrom(expandNode2Data.edges,link3.name)
       def link4Data2=getEdgeFrom(expandNode2Data.edges,link4.name)


       checkNodeDataForCollapse(node1NodeData2,node1,"true","false","","","","true");
       checkNodeDataForCollapse(node2NodeData2,node2,"true","false","","","l1","true");
       checkNodeDataForCollapse(node3NodeData2,node3,"false","true","","","l2","false");
       checkNodeDataForCollapse(node4NodeData2,node4,"false","false","","","l3","false");
       checkNodeDataForCollapse(node5NodeData2,node5,"false","true","","","l4","false");     //this node is expandable because there is 1 more link to node 3
       checkEdgeData(link1Data2,node1.name,node2.name,link1.name);
       checkEdgeData(link2Data2,node1.name,node3.name,link2.name);
       checkEdgeData(link3Data2,node2.name,node4.name,link3.name);
       checkEdgeData(link4Data2,node2.name,node5.name,link4.name);


      //expand node3
       params.nodes="node1,,true,,;node2,l1,true,,;node3,l2,false,,;node4,l3,false,,;node5,l4,false,,"
       params.expandedNodeName="node3";
       def expandNode3Data=getExpandMapData(params);

       assertEquals(6,expandNode3Data.nodes.size());
       assertEquals(6,expandNode3Data.edges.size());


       def node1NodeData3=expandNode3Data.nodes[node1.name];
       def node2NodeData3=expandNode3Data.nodes[node2.name];
       def node3NodeData3=expandNode3Data.nodes[node3.name];
       def node4NodeData3=expandNode3Data.nodes[node4.name];
       def node5NodeData3=expandNode3Data.nodes[node5.name];
       def node6NodeData3=expandNode3Data.nodes[node6.name];
       def link1Data3=getEdgeFrom(expandNode3Data.edges,link1.name)
       def link2Data3=getEdgeFrom(expandNode3Data.edges,link2.name)
       def link3Data3=getEdgeFrom(expandNode3Data.edges,link3.name)
       def link4Data3=getEdgeFrom(expandNode3Data.edges,link4.name)
       def link5Data3=getEdgeFrom(expandNode3Data.edges,link5.name)
       def link6Data3=getEdgeFrom(expandNode3Data.edges,link6.name)

       checkNodeDataForCollapse(node1NodeData3,node1,"true","false","","","","true");
       checkNodeDataForCollapse(node2NodeData3,node2,"true","false","","","l1","true");
       checkNodeDataForCollapse(node3NodeData3,node3,"true","false","","","l2","true");
       checkNodeDataForCollapse(node4NodeData3,node4,"false","false","","","l3","false");
       checkNodeDataForCollapse(node5NodeData3,node5,"false","false","","","l4","true");    //this node is collapible because there is a link to node 3 which is not ownerLink
       checkNodeDataForCollapse(node6NodeData3,node6,"false","false","","","l6","false");
       checkEdgeData(link1Data3,node1.name,node2.name,link1.name);
       checkEdgeData(link2Data3,node1.name,node3.name,link2.name);
       checkEdgeData(link3Data3,node2.name,node4.name,link3.name);
       checkEdgeData(link4Data3,node2.name,node5.name,link4.name);
       checkEdgeData(link5Data3,node3.name,node5.name,link5.name);
       checkEdgeData(link6Data3,node3.name,node6.name,link6.name);

        //collapse node5
        //will remove link l5 between node 5 - node 3
        //will not remove link l4 because it is the owner link of node 5
        //node3 will become expandable because one of its links is gone, but it will stay also collapsible because node3-node6 connection still remains
      
        params.nodes="node1,,true,,;node2,l1,true,,;node3,l2,true,,;node4,l3,false,,;node5,l4,false,,;node6,l6,false,,"
        params.collapsedNodeName="node5";
        def collapseNode5Data=getExpandMapData(params);
        assertEquals(6,collapseNode5Data.nodes.size());
        assertEquals(5,collapseNode5Data.edges.size());

         def node1NodeData5=collapseNode5Data.nodes[node1.name];
         def node2NodeData5=collapseNode5Data.nodes[node2.name];
         def node3NodeData5=collapseNode5Data.nodes[node3.name];
         def node4NodeData5=collapseNode5Data.nodes[node4.name];
         def node5NodeData5=collapseNode5Data.nodes[node5.name];
         def node6NodeData5=collapseNode5Data.nodes[node6.name];
         def link1Data5=getEdgeFrom(collapseNode5Data.edges,link1.name)
         def link2Data5=getEdgeFrom(collapseNode5Data.edges,link2.name)
         def link3Data5=getEdgeFrom(collapseNode5Data.edges,link3.name)
         def link4Data5=getEdgeFrom(collapseNode5Data.edges,link4.name)
         def link6Data5=getEdgeFrom(collapseNode5Data.edges,link6.name)

         checkNodeDataForCollapse(node1NodeData5,node1,"true","false","","","","true");
         checkNodeDataForCollapse(node2NodeData5,node2,"true","false","","","l1","true");
         checkNodeDataForCollapse(node3NodeData5,node3,"true","true","","","l2","true"); //node 3 becomes expandable because this is one more link to node5 , node 3 is collapsible because link to node6 still remains
         checkNodeDataForCollapse(node4NodeData5,node4,"false","false","","","l3","false");
         checkNodeDataForCollapse(node5NodeData5,node5,"false","true","","","l4","false");    //this node is expandable because there is one more link to node 3
         checkNodeDataForCollapse(node6NodeData5,node6,"false","false","","","l6","false");
         checkEdgeData(link1Data5,node1.name,node2.name,link1.name);
         checkEdgeData(link2Data5,node1.name,node3.name,link2.name);
         checkEdgeData(link3Data5,node2.name,node4.name,link3.name);
         checkEdgeData(link4Data5,node2.name,node5.name,link4.name);
         checkEdgeData(link6Data5,node3.name,node6.name,link6.name);


        //collapse node3
        //will remove link l6 between node3 - node6
        //will not remove link l2 because it is the owner link of node3
        params.nodes="node1,,true,,;node2,l1,true,,;node3,l2,true,,;node4,l3,false,,;node5,l4,false,,;node6,l6,false,,"
        params.collapsedNodeName="node3";
        def collapseNode3Data=getExpandMapData(params);
        assertEquals(5,collapseNode3Data.nodes.size());
        assertEquals(4,collapseNode3Data.edges.size());

         node1NodeData3=collapseNode3Data.nodes[node1.name];
         node2NodeData3=collapseNode3Data.nodes[node2.name];
         node3NodeData3=collapseNode3Data.nodes[node3.name];
         node4NodeData3=collapseNode3Data.nodes[node4.name];
         node5NodeData3=collapseNode3Data.nodes[node5.name];
         link1Data3=getEdgeFrom(collapseNode3Data.edges,link1.name)
         link2Data3=getEdgeFrom(collapseNode3Data.edges,link2.name)
         link3Data3=getEdgeFrom(collapseNode3Data.edges,link3.name)
         link4Data3=getEdgeFrom(collapseNode3Data.edges,link4.name)


         checkNodeDataForCollapse(node1NodeData3,node1,"true","false","","","","true");
         checkNodeDataForCollapse(node2NodeData3,node2,"true","false","","","l1","true");
         checkNodeDataForCollapse(node3NodeData3,node3,"false","true","","","l2","false"); //node 3 becomes expanded false and expandable all links are gone except owner link , it is not collapsible because there is no link except owner link
         checkNodeDataForCollapse(node4NodeData3,node4,"false","false","","","l3","false");
         checkNodeDataForCollapse(node5NodeData3,node5,"false","true","","","l4","false");    //this node is expandable because there is one more link to node 3
         checkEdgeData(link1Data3,node1.name,node2.name,link1.name);
         checkEdgeData(link2Data3,node1.name,node3.name,link2.name);
         checkEdgeData(link3Data3,node2.name,node4.name,link3.name);
         checkEdgeData(link4Data3,node2.name,node5.name,link4.name);


        //collapse node1
        //all nodes & links except node1 will go away

         params.nodes="node1,,true,,;node2,l1,true,,;node3,l2,false,,;node4,l3,false,,;node5,l4,false,,;"
         params.collapsedNodeName="node1";
         def collapseNode1Data=getExpandMapData(params);
         assertEquals(1,collapseNode1Data.nodes.size());
         assertEquals(0,collapseNode1Data.edges.size());

         def node1NodeData1=collapseNode1Data.nodes[node1.name];


         checkNodeDataForCollapse(node1NodeData1,node1,"false","true","","","","false");

    }
    def checkNodeData(nodeData,node,expanded,expandable,x,y)
    {
        assertEquals(node.name,nodeData.id);
        assertEquals(expanded,nodeData.expanded);
        assertEquals(expandable,nodeData.expandable);
        assertEquals(x,nodeData.x);
        assertEquals(y,nodeData.y);

    }
    def checkNodeDataForCollapse(nodeData,node,expanded,expandable,x,y,ownerEdgeId,collapsible)
    {
        checkNodeData(nodeData,node,expanded,expandable,x,y)
        assertEquals(ownerEdgeId,nodeData.ownerEdgeId);
        assertEquals(collapsible,nodeData.collapsible);
    }
    def getEdgeFrom(edges,edgeName)
    {
        return edges[edgeName];
    }
    def checkEdgeData(edgeData,sourceName,targetName,edgeName)
    {
        if(edgeData==null)
        {
            fail("edge ${edgeData} is not same as ${sourceName} to ${targetName}")
        }
        assertEquals(edgeData.id,edgeName)
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
        if(!params.containsKey("nodePropertyList"))
            params.nodePropertyList="name,expanded,x,y"

        if(!params.containsKey("mapPropertyList"))            
            params.mapPropertyList="mapType";

        def scriptResult=ScriptManagerForTest.runScript("expandMap",["params":params]);

        def resultXml = new XmlSlurper().parseText(scriptResult);

        println scriptResult

        def results=[:]
        results.nodes=[:];
        results.edges=[:];
        def nodeList=[];
        def edgeList=[];

        resultXml.node.each {    dataRow->
            def nodeData=dataRow.attributes();                        
            results.nodes.put(nodeData.id,nodeData);
            nodeList.add(nodeData);
        }
        resultXml.edge.each {    dataRow->
            def edgeData=dataRow.attributes();
            results.edges.put(edgeData.id,edgeData);
            edgeList.add(edgeData);
        }
        assertEquals("duplicate node entry exists",nodeList.size(),results.nodes.size());
        assertEquals("duplicate edge entry exists",edgeList.size(),results.edges.size());
        println "result parsed from xml ${results}"
        return results;
    }


}
