<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object" bringAllProperties="true" 
        keyAttribute="id"  title="Events" pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" viewType="event"
        queryEnabled="true" searchInEnabled="true" defaultQuery="" timeout="30" multipleFieldSorting="true"
        defaultSearchClass="RsEvent" defaultView="default" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsEvent&format=xml' extraPropertiesToRequest=""
    
        onSaveQueryClicked="${['saveQueryAction']}"
    
>
    
    <rui:sgMenuItems>
    <%
eventDetailsVisible="true"
%>

        <rui:sgMenuItem id="eventDetails" label="Event Details" visible="${eventDetailsVisible}" action="${['eventDetailsAction']}">
            
        </rui:sgMenuItem>
    <%
browseVisible="params.data.elementName && params.data.elementName != ''"
%>

        <rui:sgMenuItem id="browse" label="Browse" visible="${browseVisible}" action="${['objectDetailsAction']}">
            
        </rui:sgMenuItem>
    <%
acknowledgeVisible="params.data.acknowledged != 'true'"
%>

        <rui:sgMenuItem id="acknowledge" label="Acknowledge" visible="${acknowledgeVisible}" action="${['acknowledgeAction']}">
            
        </rui:sgMenuItem>
    <%
unacknowledgeVisible="params.data.acknowledged == 'true'"
%>

        <rui:sgMenuItem id="unacknowledge" label="Unacknowledge" visible="${unacknowledgeVisible}" action="${['unacknowledgeAction']}">
            
        </rui:sgMenuItem>
    <%
takeOwnershipVisible="true"
%>

        <rui:sgMenuItem id="takeOwnership" label="Take Ownership" visible="${takeOwnershipVisible}" action="${['takeOwnershipAction']}">
            
        </rui:sgMenuItem>
    <%
releaseOwnershipVisible="true"
%>

        <rui:sgMenuItem id="releaseOwnership" label="Release Ownership" visible="${releaseOwnershipVisible}" action="${['releaseOwnershipAction']}">
            
        </rui:sgMenuItem>
    
    </rui:sgMenuItems>
     <rui:sgMultiSelectionMenuItems>
    
    </rui:sgMultiSelectionMenuItems>
    <rui:sgImages>
    <%
image0Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image0Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image1Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image1Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image2Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image2Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image3Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image3Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image4Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image4Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image5Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image5Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=event" rootTag="Filters" pollingInterval="0" timeout="30"
        keyAttribute="id" expandAttribute="expanded" contentPath="Filter" title="Saved Queries" expanded="false"

        onNodeClicked="${['setQueryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248" sortBy="true" sortOrder="asc" sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
deleteQueryVisible="params.data.nodeType == 'filter' &&  (window.currentUserHasRole('Administrator')  || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="deleteQuery" label="Delete" visible="${deleteQueryVisible}" action="${['deleteQueryAction']}">
               
        </rui:tgMenuItem>
        <%
deleteQueryGroupVisible="params.data.nodeType == 'group' && params.data.name != 'My Queries' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="deleteQueryGroup" label="Delete" visible="${deleteQueryGroupVisible}" action="${['deleteQueryGroupAction']}">
               
        </rui:tgMenuItem>
        <%
queryUpdateVisible="params.data.nodeType == 'filter' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="queryUpdate" label="Update" visible="${queryUpdateVisible}" action="${['queryUpdateAction']}">
               
        </rui:tgMenuItem>
        <%
queryGroupUpdateVisible="params.data.nodeType == 'group' && params.data.name != 'My Queries' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
%>

        <rui:tgMenuItem id="queryGroupUpdate" label="Update" visible="${queryGroupUpdateVisible}" action="${['queryGroupUpdateAction']}">
               
        </rui:tgMenuItem>
        <%
copyQueryVisible="params.data.nodeType == 'filter'"
%>

        <rui:tgMenuItem id="copyQuery" label="Copy Query" visible="${copyQueryVisible}" action="${['copyQueryAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
     <rui:tgMultiSelectionMenuItems>
    
    </rui:tgMultiSelectionMenuItems>
    <rui:tgRootImages>
        <%
rootImage6Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage6Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage7Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage7Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="eventDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="objectDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>

<%
functionActionConditionindex_events_setQueryActionCondition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQueryWithView" componentId="eventsGrid" condition="$functionActionConditionindex_events_setQueryActionCondition"

>
    
    <rui:functionArg><![CDATA[params.data.query ]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.viewName]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_eventDetailsActionCondition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_objectDetailsActionCondition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId="objectDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_copyQueryActionCondition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, viewName:params.data.viewName, searchClass:params.data.searchClass, isPublic:params.data.isPublic, mode:'create', type:'event', searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_saveQueryActionCondition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  viewName:YAHOO.rapidjs.Components['eventsGrid'].getCurrentView(), mode:'create', type:'event', searchClass: YAHOO.rapidjs.Components['eventsGrid'].getSearchClass(), searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_queryUpdateActionCondition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'event', searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_events_queryGroupUpdateActionCondition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId="saveQueryGroupForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'event', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
mergeActionConditionindex_events_acknowledgeActionCondition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_events_acknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_events_acknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_events_acknowledgeAction_acknowledgedVisible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_events_acknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_events_unacknowledgeActionCondition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_events_unacknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_events_unacknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_events_unacknowledgeAction_acknowledgedVisible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_events_unacknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_events_takeOwnershipActionCondition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_events_takeOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_events_takeOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_events_takeOwnershipAction_actVisible="'true'"
%>

    <rui:requestParam key="act" value="${parameterindex_events_takeOwnershipAction_actVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_events_releaseOwnershipActionCondition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_events_releaseOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_events_releaseOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_events_releaseOwnershipAction_actVisible="'false'"
%>

    <rui:requestParam key="act" value="${parameterindex_events_releaseOwnershipAction_actVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_events_deleteQueryGroupActionCondition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_events_deleteQueryGroupAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_events_deleteQueryGroupAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_events_deleteQueryActionCondition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_events_deleteQueryAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_events_deleteQueryAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_events_refreshQueriesActionCondition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId="filterTree" 

>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50' 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="245" resizable="false"
 
 
  title='Save query'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="165" resizable="false"
 
 
  title='Save group'
></rui:popupWindow>





       <rui:include template="pageContents/_events.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="39">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='eventsGrid'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='255' gutter='0 5 0 0' resize='true' useShim='false' scroll='false' component='filterTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>