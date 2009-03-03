<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:treeGrid id="mapTree" url="../script/run/mapList?format=xml" rootTag="Maps" pollingInterval="0"
        keyAttribute="id" contentPath="Map" title="Saved Maps" expanded="true"

        onNodeClicked="${['requestMapAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248"  >
            
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
rootImage4086Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage4086Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage4088Visible="params.data.nodeType == 'map'"
%>

        <rui:tgRootImage visible="${rootImage4088Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="eventDetails" iframe="false"></rui:html>

<rui:html id="eventList" iframe="false"></rui:html>

<rui:html id="saveMapGroupForm" iframe="false"></rui:html>

<rui:html id="saveMapForm" iframe="false"></rui:html>


<rui:objectMap id="topologyMap" expandURL="../script/run/expandMap" dataURL="../script/run/getMapData" nodeSize="60" edgeColorDataKey="state" edgeColors="${['5':0xffde2c26,'4':0xfff79229,'3':0xfffae500,'2':0xff20b4e0,'1':0xffac6bac,'0':0xff62b446,'default':0xff62b446]}"

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
            
            <rui:omText id="name" x="15" y="20" width="70" height="30" dataKey="id"></rui:omText>
            
        </rui:omTexts>
        <rui:omGauges>
            
        </rui:omGauges>
    </rui:omNodeContent>
</rui:objectMap>

<%
functionActionCondition4125Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.type + ' ' + params.data.id]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4136Condition=""
%>

<rui:action id="showEventsAction" type="function" function="show" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[createURL('showEvents.gsp', {name:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.type + ' ' + params.data.id]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4147Condition=""
%>

<rui:action id="mapGroupUpdateAction" type="function" function="show" componentId='saveMapGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapGroupForm.gsp', {mode:'edit', mapGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition4158Condition="getURLParam('name') != null"
%>

<rui:action id="loadMapForNodeAction" type="function" function="loadMapForNode" componentId='topologyMap' condition="$functionActionCondition4158Condition"

>
    
    <rui:functionArg><![CDATA[getURLParam('name')]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4166Condition=""
%>

<rui:action id="updateMapAction" type="function" function="show" componentId='saveMapForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp', {mapId:params.data.id, mode:'edit'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition4177Condition=""
%>

<rui:action id="saveMapAction" type="function" function="show" componentId='saveMapForm' 

>
    
    <rui:functionArg><![CDATA[createURL('mapForm.gsp', {mode:'create', nodes: YAHOO.rapidjs.Components['topologyMap'].getNodesString(), layout: YAHOO.rapidjs.Components['topologyMap'].getLayout()})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
requestActionCondition4188Condition=""
%>

<rui:action id="deleteMapAction" type="request" url="../topoMap/delete?format=xml" components="${['mapTree']}" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameter4191Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter4191Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition4197Condition=""
%>

<rui:action id="deleteMapGroupAction" type="request" url="../mapGroup/delete?format=xml" components="${['mapTree']}" 

        onSuccess="${['refreshMapsAction']}"
    
>
    <%
parameter4200Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter4200Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition4206Condition=""
%>

<rui:action id="requestMapAction" type="request" url="../script/run/getMap?format=xml" components="${[]}" 

        onSuccess="${['loadMapAction']}"
    
>
    <%
parameter4208Visible="params.data.name"
%>

    <rui:requestParam key="mapName" value="${parameter4208Visible}"></rui:requestParam>
    <%
parameter4210Visible="params.data.isPublic"
%>

    <rui:requestParam key="isPublic" value="${parameter4210Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition4215Condition=""
%>

<rui:action id="loadMapAction" type="function" function="loadMap" componentId='topologyMap' 

>
    
    <rui:functionArg><![CDATA[params.response]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4223Condition=""
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
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="saveMapForm" width="385" height="125" resizable="false"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_maps.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4078">
        
            <rui:layoutUnit position='center' gutter='0px' id='4247' isActive='true' scroll='false' useShim='false' component='topologyMap'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0px' id='4250' isActive='true' resize='true' scroll='false' useShim='false' width='250' component='mapTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>