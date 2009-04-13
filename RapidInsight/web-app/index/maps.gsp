<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:treeGrid id="mapTree" url="../script/run/mapList?format=xml" rootTag="Maps" pollingInterval="0" timeout="30"
        keyAttribute="id" contentPath="Map" title="Saved Maps" expanded="true"

        onNodeClicked="${['requestMapAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248"   sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
deleteMapVisible="params.data.isPublic != 'true' && params.data.nodeType == 'map'"
%>

        <rui:tgMenuItem id="deleteMap" label="Delete" visible="${deleteMapVisible}" action="${['deleteMapAction']}">
               
        </rui:tgMenuItem>
        <%
deleteMapGroupVisible="params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'"
%>

        <rui:tgMenuItem id="deleteMapGroup" label="Delete" visible="${deleteMapGroupVisible}" action="${['deleteMapGroupAction']}">
               
        </rui:tgMenuItem>
        <%
mapUpdateVisible="params.data.nodeType == 'map' && params.data.isPublic != 'true'"
%>

        <rui:tgMenuItem id="mapUpdate" label="Update" visible="${mapUpdateVisible}" action="${['updateMapAction']}">
               
        </rui:tgMenuItem>
        <%
mapGroupUpdateVisible="params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'"
%>

        <rui:tgMenuItem id="mapGroupUpdate" label="Update" visible="${mapGroupUpdateVisible}" action="${['mapGroupUpdateAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
rootImage2199Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage2199Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage2201Visible="params.data.nodeType == 'map'"
%>

        <rui:tgRootImage visible="${rootImage2201Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="eventDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="eventList" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveMapGroupForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveMapForm" iframe="false"  timeout="30"></rui:html>


<rui:objectMap id="topologyMap" expandURL="../script/run/expandMap" dataURL="../script/run/getMapData" nodePropertyList="name,rsClassName" nodeSize="60" edgeColorDataKey="state" edgeColors="${['5':0xffde2c26,'4':0xfff79229,'3':0xfffae500,'2':0xff20b4e0,'1':0xffac6bac,'0':0xff62b446,'default':0xff62b446]}" timeout="30"

        onMapInitialized="${['loadMapForNodeAction']}"
    
>
    <rui:omMenuItems>
        
        <rui:omMenuItem id="browse" label="Browse" action="${['objectDetailsAction']}">
        </rui:omMenuItem>
        
        <rui:omMenuItem id="showEvents" label="Show Events" action="${['showEventsAction']}">
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
            
            <rui:omImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${['Host':'server_icon.png', 'Router':'router_icon.png', 'Switch':'switch_icon.png']}"></rui:omImage>
            
            <rui:omImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${['0':'../states/green.png', '5':'../states/red.png', '4':'../states/orange.png', '3':'../states/yellow.png', '2':'../states/blue.png', '1':'../states/purple.png','default':'../states/green.png']}"></rui:omImage>
            
        </rui:omImages>
        <rui:omTexts>
            
            <rui:omText id="name" x="15" y="20" width="70" height="30" dataKey="displayName"></rui:omText>
            
        </rui:omTexts>
        <rui:omGauges>
            
        </rui:omGauges>
    </rui:omNodeContent>
</rui:objectMap>

<%
functionActionCondition2238Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.type + ' ' + params.data.id]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2249Condition=""
%>

<rui:action id="showEventsAction" type="function" function="show" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[createURL('showEvents.gsp', {name:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.type + ' ' + params.data.id]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2260Condition=""
%>

<rui:action id="mapGroupUpdateAction" type="function" function="show" componentId='saveMapGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapGroupForm.gsp', {mode:'edit', mapGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition2271Condition=""
%>

<rui:action id="loadMapForNodeAction" type="function" function="loadMapForNode" componentId='topologyMap' 

>
    
    <rui:functionArg><![CDATA[getURLParams()]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2279Condition=""
%>

<rui:action id="updateMapAction" type="function" function="show" componentId='saveMapForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp', getMapUpdateParams(params.data.id))]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition2290Condition=""
%>

<rui:action id="saveMapAction" type="function" function="show" componentId='saveMapForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp',getMapSaveParams())]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
requestActionCondition2301Condition=""
%>

<rui:action id="deleteMapAction" type="request" url="../topoMap/delete?format=xml" components="${['mapTree']}" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameter2304Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter2304Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition2310Condition=""
%>

<rui:action id="deleteMapGroupAction" type="request" url="../mapGroup/delete?format=xml" components="${['mapTree']}" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameter2313Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter2313Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition2319Condition=""
%>

<rui:action id="requestMapAction" type="request" url="../script/run/getMap?format=xml" components="${[]}" 

        onSuccess="${['loadMapAction']}"
    
>
    <%
parameter2321Visible="params.data.name"
%>

    <rui:requestParam key="mapName" value="${parameter2321Visible}"></rui:requestParam>
    <%
parameter2323Visible="params.data.isPublic"
%>

    <rui:requestParam key="isPublic" value="${parameter2323Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition2328Condition=""
%>

<rui:action id="loadMapAction" type="function" function="loadMap" componentId='topologyMap' 

>
    
    <rui:functionArg><![CDATA[params.response]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2336Condition=""
%>

<rui:action id="refreshMapsAction" type="function" function="poll" componentId='mapTree' 

>
    
</rui:action>

<rui:popupWindow componentId="objectDetails" width="850" height="500" resizable="true"
 
 
x='50' y='85' 
></rui:popupWindow>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="eventList" width="850" height="700" resizable="true"
 
 
x='100' y='80' 
></rui:popupWindow>

<rui:popupWindow componentId="saveMapGroupForm" width="330" height="100" resizable="false"
 
 
  title='Save Group'
></rui:popupWindow>

<rui:popupWindow componentId="saveMapForm" width="385" height="125" resizable="false"
 
 
  title='Save Map'
></rui:popupWindow>





       <rui:include template="pageContents/_maps.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="2191">
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='2363' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='mapTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='2360' isActive='true' scroll='false' useShim='false' component='topologyMap'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>