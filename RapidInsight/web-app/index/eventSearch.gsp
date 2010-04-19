<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="eventList" url="../search" rootTag="Objects" contentPath="Object" keyAttribute="id" bringAllProperties="true" 
    lineSize="3" title="Event Search" queryParameter="query" pollingInterval="0" defaultFields='${["name"]}'
     defaultQuery="" extraPropertiesToRequest="" multipleFieldSorting="true"
    defaultSearchClass="RsEvent" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsEvent' timeout="30" searchInEnabled="true"
    
        onSaveQueryClicked="${['saveQueryAction']}"
    
>
    
    <rui:slMenuItems>
        <%
eventDetailsVisible="true"
%>

        <rui:slMenuItem id="eventDetails" label="Event Details" visible="${eventDetailsVisible}" action="${['eventDetailsAction']}">
               
        </rui:slMenuItem>
        <%
browseVisible="params.data.elementName && params.data.elementName != ''"
%>

        <rui:slMenuItem id="browse" label="Browse" visible="${browseVisible}" action="${['objectDetailsAction']}">
               
        </rui:slMenuItem>
        <%
acknowledgeVisible="params.data.acknowledged != 'true'"
%>

        <rui:slMenuItem id="acknowledge" label="Acknowledge" visible="${acknowledgeVisible}" action="${['acknowledgeAction']}">
               
        </rui:slMenuItem>
        <%
unacknowledgeVisible="params.data.acknowledged == 'true'"
%>

        <rui:slMenuItem id="unacknowledge" label="Unacknowledge" visible="${unacknowledgeVisible}" action="${['unacknowledgeAction']}">
               
        </rui:slMenuItem>
        <%
takeOwnershipVisible="true"
%>

        <rui:slMenuItem id="takeOwnership" label="Take Ownership" visible="${takeOwnershipVisible}" action="${['takeOwnershipAction']}">
               
        </rui:slMenuItem>
        <%
releaseOwnershipVisible="true"
%>

        <rui:slMenuItem id="releaseOwnership" label="Release Ownership" visible="${releaseOwnershipVisible}" action="${['releaseOwnershipAction']}">
               
        </rui:slMenuItem>
        
    </rui:slMenuItems>
    <rui:slMultiSelectionMenuItems>
        
    </rui:slMultiSelectionMenuItems>
    <rui:slPropertyMenuItems>
        <%
sortAscVisible="true"
%>

        <rui:slMenuItem id="sortAsc" label="Sort Asc" action="${['sortAscAction']}" visible="${sortAscVisible}">
               
        </rui:slMenuItem>
        <%
sortDescVisible="true"
%>

        <rui:slMenuItem id="sortDesc" label="Sort Desc" action="${['sortDescAction']}" visible="${sortDescVisible}">
               
        </rui:slMenuItem>
        <%
exceptVisible="true"
%>

        <rui:slMenuItem id="except" label="Except" action="${['exceptAction']}" visible="${exceptVisible}">
               
        </rui:slMenuItem>
        <%
greaterThanVisible="(params.key == 'severity' && params.value != '1') || (params.key != 'severity' && YAHOO.lang.isNumber(parseInt(params.value)))"
%>

        <rui:slMenuItem id="greaterThan" label="Greater Than" action="${['greaterThanAction']}" visible="${greaterThanVisible}">
               
        </rui:slMenuItem>
        <%
lessThanVisible="(params.key == 'severity' && params.value != '5') || (params.key != 'severity' && YAHOO.lang.isNumber(parseInt(params.value)))"
%>

        <rui:slMenuItem id="lessThan" label="Less Than" action="${['lessThanAction']}" visible="${lessThanVisible}">
               
        </rui:slMenuItem>
        <%
greaterThanOrEqualToVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="greaterThanOrEqualTo" label="Greater than or equal to" action="${['greaterThanOrEqualToAction']}" visible="${greaterThanOrEqualToVisible}">
               
        </rui:slMenuItem>
        <%
lessThanOrEqualToVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="lessThanOrEqualTo" label="Less than or equal to" action="${['lessThanOrEqualToAction']}" visible="${lessThanOrEqualToVisible}">
               
        </rui:slMenuItem>
        
    </rui:slPropertyMenuItems>
     <rui:slFields>
    <%
fieldExpression42Visible="params.data.rsAlias == 'RsRiEvent'"
%>

        <rui:slField exp="${fieldExpression42Visible}" fields='${["elementName", " identifier", " owner", " acknowledged", " severity", " source", " changedAt"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    <%
image43Visible="params.data.severity == '5'"
%>

        <rui:slImage visible="${image43Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:slImage>
    <%
image44Visible="params.data.severity == '4'"
%>

        <rui:slImage visible="${image44Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:slImage>
    <%
image45Visible="params.data.severity == '3'"
%>

        <rui:slImage visible="${image45Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
    <%
image46Visible="params.data.severity == '2'"
%>

        <rui:slImage visible="${image46Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:slImage>
    <%
image47Visible="params.data.severity == '1'"
%>

        <rui:slImage visible="${image47Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:slImage>
    <%
image48Visible="params.data.severity == '0'"
%>

        <rui:slImage visible="${image48Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:slImage>
    
    </rui:slImages>
</rui:searchList>

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
rootImage49Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage49Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage50Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage50Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="eventDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="objectDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>

<%
functionActionConditionindex_eventSearch_setQueryActionCondition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId="eventList" condition="$functionActionConditionindex_eventSearch_setQueryActionCondition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_eventDetailsActionCondition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_objectDetailsActionCondition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId="objectDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_copyQueryActionCondition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, searchClass: params.data.searchClass, isPublic:params.data.isPublic, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, parentQueryId:params.data.parentQueryId, expanded:params.data.expanded, mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_saveQueryActionCondition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['eventList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['eventList'].getSortOrder(), searchClass: YAHOO.rapidjs.Components['eventList'].getSearchClass(), mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_queryUpdateActionCondition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_queryGroupUpdateActionCondition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId="saveQueryGroupForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'event', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_sortAscActionCondition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_sortDescActionCondition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_exceptActionCondition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_greaterThanActionCondition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_lessThanActionCondition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_greaterThanOrEqualToActionCondition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_lessThanOrEqualToActionCondition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
mergeActionConditionindex_eventSearch_acknowledgeActionCondition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}" submitType="GET"  

>
    <%
parameterindex_eventSearch_acknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_eventSearch_acknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_eventSearch_acknowledgeAction_acknowledgedVisible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_eventSearch_acknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_eventSearch_unacknowledgeActionCondition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}" submitType="GET"  

>
    <%
parameterindex_eventSearch_unacknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_eventSearch_unacknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_eventSearch_unacknowledgeAction_acknowledgedVisible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_eventSearch_unacknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_eventSearch_takeOwnershipActionCondition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}" submitType="GET"  

>
    <%
parameterindex_eventSearch_takeOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_eventSearch_takeOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_eventSearch_takeOwnershipAction_actVisible="'true'"
%>

    <rui:requestParam key="act" value="${parameterindex_eventSearch_takeOwnershipAction_actVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_eventSearch_releaseOwnershipActionCondition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}" submitType="GET"  

>
    <%
parameterindex_eventSearch_releaseOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_eventSearch_releaseOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_eventSearch_releaseOwnershipAction_actVisible="'false'"
%>

    <rui:requestParam key="act" value="${parameterindex_eventSearch_releaseOwnershipAction_actVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_eventSearch_deleteQueryGroupActionCondition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_eventSearch_deleteQueryGroupAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_eventSearch_deleteQueryGroupAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_eventSearch_deleteQueryActionCondition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_eventSearch_deleteQueryAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_eventSearch_deleteQueryAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_eventSearch_refreshQueriesActionCondition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId="filterTree" 

>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50' 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="320" resizable="false"
 
 
  title='Save query'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="165" resizable="false"
 
 
  title='Save group'
></rui:popupWindow>





       <rui:include template="pageContents/_eventSearch.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="99">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='eventList'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='255' gutter='0 5 0 0' resize='true' useShim='false' scroll='false' component='filterTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>