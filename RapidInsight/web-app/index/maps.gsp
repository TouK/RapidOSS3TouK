<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../script/run/getMapNodeEvents?format=xml" queryParameter="query" rootTag="Objects" contentPath="Object" bringAllProperties="true" 
        keyAttribute="id"  title="Events" pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml"
        queryEnabled="false" searchInEnabled="true" defaultQuery="" timeout="30" multipleFieldSorting="true"
        defaultSearchClass="RsEvent" defaultView="default"  extraPropertiesToRequest=""
    
>
    
    <rui:sgMenuItems>
    <%
eventDetailsVisible="true"
%>

        <rui:sgMenuItem id="eventDetails" label="Event Details" visible="${eventDetailsVisible}" action="${['eventDetailsAction']}">
            
        </rui:sgMenuItem>
    
    </rui:sgMenuItems>
     <rui:sgMultiSelectionMenuItems>
    
    </rui:sgMultiSelectionMenuItems>
    <rui:sgImages>
    <%
image210Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image210Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image211Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image211Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image212Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image212Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image213Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image213Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image214Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image214Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image215Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image215Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="150"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="identifier" colLabel="Event" width="150"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="count" colLabel="Count" width="50"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="source" colLabel="Source" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="changedAt" colLabel="Last Change" width="120"   type="text">
            

        </rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>

<rui:treeGrid id="mapTree" url="../topoMap/listWithGroups?format=xml" rootTag="Maps" pollingInterval="0" timeout="30"
        keyAttribute="id" expandAttribute="" contentPath="Map" title="Saved Maps" expanded="true"

        onNodeClicked="${['requestMapAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248"   sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
deleteMapVisible="params.data.nodeType == 'map' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="deleteMap" label="Delete" visible="${deleteMapVisible}" action="${['deleteMapAction']}">
               
        </rui:tgMenuItem>
        <%
deleteMapGroupVisible="params.data.nodeType == 'group' && params.data.name != 'My Maps' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="deleteMapGroup" label="Delete" visible="${deleteMapGroupVisible}" action="${['deleteMapGroupAction']}">
               
        </rui:tgMenuItem>
        <%
mapUpdateVisible="params.data.nodeType == 'map' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="mapUpdate" label="Update" visible="${mapUpdateVisible}" action="${['updateMapAction']}">
               
        </rui:tgMenuItem>
        <%
mapGroupUpdateVisible="params.data.nodeType == 'group' && params.data.name != 'My Maps' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="mapGroupUpdate" label="Update" visible="${mapGroupUpdateVisible}" action="${['mapGroupUpdateAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
     <rui:tgMultiSelectionMenuItems>
    
    </rui:tgMultiSelectionMenuItems>
    <rui:tgRootImages>
        <%
rootImage216Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage216Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage217Visible="params.data.nodeType == 'map'"
%>

        <rui:tgRootImage visible="${rootImage217Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="objectDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="eventDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveMapGroupForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveMapForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:objectMap id="topologyMap" expandURL="../script/run/expandMap" dataURL="../script/run/getMapData" nodePropertyList="name,rsClassName" mapPropertyList="mapType" nodeSize="60" edgeColorDataKey="state" edgeColors="${['5':0xffde2c26,'4':0xfff79229,'3':0xfffae500,'2':0xff20b4e0,'1':0xffac6bac,'0':0xff62b446,'default':0xff62b446]}" timeout="30"

        onMapInitialized="${['loadMapForNodeAction']}"
    
>
    <rui:omMenuItems>
        <%
browseVisible="true"
%>

        <rui:omMenuItem id="browse" label="Browse" visible="${browseVisible}" action="${['objectDetailsAction']}">
        </rui:omMenuItem>
        <%
showEventsVisible="true"
%>

        <rui:omMenuItem id="showEvents" label="Show Events" visible="${showEventsVisible}" action="${['showEventsAction']}">
        </rui:omMenuItem>
        
    </rui:omMenuItems>

    <rui:omToolbarMenus>
        
        <rui:omToolbarMenu label="Map">
            
            <rui:omMenuItem id="saveMap" label="Save Map" action="${['saveMapAction']}">
            </rui:omMenuItem>
            

        </rui:omToolbarMenu>
        
    </rui:omToolbarMenus>
    <rui:omNodeContent>
        <rui:omImages>
            
            <rui:omImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${['Host':'../images/map/server_icon.png', 'Router':'../images/map/router_icon.png', 'Switch':'../images/map/switch_icon.png']}"></rui:omImage>
            
            <rui:omImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${['0':'../images/rapidjs/component/states/green.png', '5':'../images/rapidjs/component/states/red.png', '4':'../images/rapidjs/component/states/orange.png', '3':'../images/rapidjs/component/states/yellow.png', '2':'../images/rapidjs/component/states/blue.png', '1':'../images/rapidjs/component/states/purple.png','default':'../images/rapidjs/component/states/green.png']}"></rui:omImage>
            
        </rui:omImages>
        <rui:omTexts>
            
            <rui:omText id="name" x="15" y="20" width="70" height="30" dataKey="displayName"></rui:omText>
            
        </rui:omTexts>
        <rui:omGauges>
            
        </rui:omGauges>
    </rui:omNodeContent>
</rui:objectMap>

<%
functionActionConditionindex_maps_objectDetailsActionCondition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId="objectDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.type + ' ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_showEventsActionCondition=""
%>

<rui:action id="showEventsAction" type="function" function="setQueryWithView" componentId="eventsGrid" 

>
    
    <rui:functionArg><![CDATA['']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.type + ' ' + params.data.displayName]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[{name:params.data.name}]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_mapGroupUpdateActionCondition=""
%>

<rui:action id="mapGroupUpdateAction" type="function" function="show" componentId="saveMapGroupForm" 

>
    
    <rui:functionArg><![CDATA[createURL('mapGroupForm.gsp', {mode:'edit', mapGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_loadMapForNodeActionCondition=""
%>

<rui:action id="loadMapForNodeAction" type="function" function="loadMapForNode" componentId="topologyMap" 

>
    
    <rui:functionArg><![CDATA[getURLParams()]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[getURLParams()]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_updateMapActionCondition=""
%>

<rui:action id="updateMapAction" type="function" function="show" componentId="saveMapForm" 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp', getMapUpdateParams(params.data.id))]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_saveMapActionCondition=""
%>

<rui:action id="saveMapAction" type="function" function="show" componentId="saveMapForm" 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp',getMapSaveParams())]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_eventDetailsActionCondition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
requestActionConditionindex_maps_deleteMapActionCondition=""
%>

<rui:action id="deleteMapAction" type="request" url="../topoMap/delete?format=xml" components="${['mapTree']}" submitType="GET" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameterindex_maps_deleteMapAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_maps_deleteMapAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_maps_deleteMapGroupActionCondition=""
%>

<rui:action id="deleteMapGroupAction" type="request" url="../mapGroup/delete?format=xml" components="${['mapTree']}" submitType="GET" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameterindex_maps_deleteMapGroupAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_maps_deleteMapGroupAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_maps_requestMapActionCondition="params.data.nodeType == 'map'"
%>

<rui:action id="requestMapAction" type="request" url="../topoMap/load?format=xml" components="${[]}" submitType="GET" condition="$requestActionConditionindex_maps_requestMapActionCondition"

        onSuccess="${['loadMapAction']}"
    
>
    <%
parameterindex_maps_requestMapAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_maps_requestMapAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_maps_loadMapActionCondition=""
%>

<rui:action id="loadMapAction" type="function" function="loadMap" componentId="topologyMap" 

>
    
    <rui:functionArg><![CDATA[params.response]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_maps_refreshMapsActionCondition=""
%>

<rui:action id="refreshMapsAction" type="function" function="poll" componentId="mapTree" 

>
    
</rui:action>

<rui:popupWindow componentId="objectDetails" width="850" height="500" resizable="true"
 
 
x='50' y='85' 
></rui:popupWindow>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="saveMapForm" width="385" height="165" resizable="false"
 
 
  title='Save Map'
></rui:popupWindow>

<rui:popupWindow componentId="saveMapGroupForm" width="330" height="165" resizable="false"
 
 
  title='Save Group'
></rui:popupWindow>

<rui:popupWindow componentId="eventsGrid" width="800" height="400" resizable="false"
 
 
  
></rui:popupWindow>





       <rui:include template="pageContents/_maps.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="250">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='topologyMap'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='255' gutter='0 5 0 0' resize='true' useShim='false' scroll='false' component='mapTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>