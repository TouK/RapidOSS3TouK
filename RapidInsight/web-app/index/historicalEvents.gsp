<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="eventList" url="../search" rootTag="Objects" contentPath="Object" keyAttribute="id" bringAllProperties="true" 
    lineSize="3" title="Historical Events" queryParameter="query" pollingInterval="0" defaultFields='${["name"]}'
     defaultQuery="" extraPropertiesToRequest="" multipleFieldSorting="true"
    defaultSearchClass="RsHistoricalEvent" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsHistoricalEvent' timeout="30" searchInEnabled="true"
    
        onSaveQueryClicked="${['saveQueryAction']}"
    
>
    
        <rui:timeRangeSelector url="../script/run/getTimeRangeData" buttonConfigurationUrl="../script/run/getTimeRangeButtonConfiguration" fromTimeProperty="fromTime"
            tooltipProperty="tooltip" toTimeProperty="toTime" stringToTimeProperty="stringToTime" stringFromTimeProperty="stringFromTime" timeAxisLabelProperty="timeAxisLabel" valueProperties="value">
    </rui:timeRangeSelector>
    
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
        <%
propertyBrowseVisible="params.key == 'elementName' && params.value != ''"
%>

        <rui:slMenuItem id="propertyBrowse" label="Browse" action="${['objectDetailsAction']}" visible="${propertyBrowseVisible}">
               
        </rui:slMenuItem>
        
    </rui:slPropertyMenuItems>
     <rui:slFields>
    <%
fieldExpression102Visible="params.data.rsAlias == 'RsRiHistoricalEvent'"
%>

        <rui:slField exp="${fieldExpression102Visible}" fields='${["elementName", " identifier", " owner", " acknowledged", " severity", " source", "changedAt", "count"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    <%
image103Visible="params.data.severity == '5'"
%>

        <rui:slImage visible="${image103Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:slImage>
    <%
image104Visible="params.data.severity == '4'"
%>

        <rui:slImage visible="${image104Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:slImage>
    <%
image105Visible="params.data.severity == '3'"
%>

        <rui:slImage visible="${image105Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
    <%
image106Visible="params.data.severity == '2'"
%>

        <rui:slImage visible="${image106Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:slImage>
    <%
image107Visible="params.data.severity == '1'"
%>

        <rui:slImage visible="${image107Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:slImage>
    <%
image108Visible="params.data.severity == '0'"
%>

        <rui:slImage visible="${image108Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:slImage>
    
    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=historicalEvent" rootTag="Filters" pollingInterval="0" timeout="30"
        keyAttribute="id" expandAttribute="expanded" contentPath="Filter" title="Saved Queries" expanded="false"

        onNodeClicked="${['setQueryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248" sortBy="true" sortOrder="asc" sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
deleteQueryVisible="params.data.nodeType == 'filter' && (window.currentUserHasRole('Administrator') || params.data.isPublic != 'true')"
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
rootImage110Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage110Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage111Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage111Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="eventDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="objectDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>

<%
functionActionConditionindex_historicalEvents_setQueryActionCondition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId="eventList" condition="$functionActionConditionindex_historicalEvents_setQueryActionCondition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_eventDetailsActionCondition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getHistoricalEventDetails.gsp', {id:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_objectDetailsActionCondition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId="objectDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_copyQueryActionCondition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, searchClass:params.data.searchClass, isPublic:params.data.isPublic, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, parentQueryId:params.data.parentQueryId, expanded:params.data.expanded, mode:'create', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_saveQueryActionCondition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['eventList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['eventList'].getSortOrder(), searchClass:YAHOO.rapidjs.Components['eventList'].getSearchClass(),  mode:'create', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_queryUpdateActionCondition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId="saveQueryForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_queryGroupUpdateActionCondition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId="saveQueryGroupForm" 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'historicalEvent', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_sortAscActionCondition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_sortDescActionCondition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_exceptActionCondition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_greaterThanActionCondition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_lessThanActionCondition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_greaterThanOrEqualToActionCondition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_lessThanOrEqualToActionCondition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId="eventList" 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
requestActionConditionindex_historicalEvents_deleteQueryGroupActionCondition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_historicalEvents_deleteQueryGroupAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_historicalEvents_deleteQueryGroupAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_historicalEvents_deleteQueryActionCondition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" submitType="GET" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameterindex_historicalEvents_deleteQueryAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_historicalEvents_deleteQueryAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_historicalEvents_refreshQueriesActionCondition=""
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





       <rui:include template="pageContents/_historicalEvents.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="157">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='eventList'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='255' gutter='0 5 0 0' resize='true' useShim='false' scroll='false' component='filterTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>