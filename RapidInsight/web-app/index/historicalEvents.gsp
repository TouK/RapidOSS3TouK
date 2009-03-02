<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="eventList" url="../search?searchIn=RsHistoricalEvent" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Historical Events" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["name"]}' 
    
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
        
    </rui:slMenuItems>
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
fieldExpression3569Visible="params.data.rsAlias == 'RsRiHistoricalEvent'"
%>

        <rui:slField exp="${fieldExpression3569Visible}" fields='${["elementName", " identifier", " node", "active", " owner", " acknowledged", " severity", " source", "changedAt", "visibility", "count"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    <%
image3571Visible="params.data.severity == '5'"
%>

        <rui:slImage visible="${image3571Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:slImage>
    <%
image3573Visible="params.data.severity == '4'"
%>

        <rui:slImage visible="${image3573Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:slImage>
    <%
image3575Visible="params.data.severity == '3'"
%>

        <rui:slImage visible="${image3575Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
    <%
image3577Visible="params.data.severity == '2'"
%>

        <rui:slImage visible="${image3577Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:slImage>
    <%
image3579Visible="params.data.severity == '1'"
%>

        <rui:slImage visible="${image3579Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:slImage>
    <%
image3581Visible="params.data.severity == '0'"
%>

        <rui:slImage visible="${image3581Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:slImage>
    
    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=historicalEvent" rootTag="Filters" pollingInterval="0"
        keyAttribute="id" contentPath="Filter" title="Saved Queries" expanded="true"

        onNodeClicked="${['setQueryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248" sortBy="true" sortOrder="asc">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
deleteQueryVisible="params.data.isPublic != 'true' && params.data.nodeType == 'filter' "
%>

        <rui:tgMenuItem id="deleteQuery" label="Delete" visible="${deleteQueryVisible}" action="${['deleteQueryAction']}">
               
        </rui:tgMenuItem>
        <%
deleteQueryGroupVisible="params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'"
%>

        <rui:tgMenuItem id="deleteQueryGroup" label="Delete" visible="${deleteQueryGroupVisible}" action="${['deleteQueryGroupAction']}">
               
        </rui:tgMenuItem>
        <%
queryUpdateVisible="params.data.nodeType == 'filter' && params.data.isPublic != 'true'"
%>

        <rui:tgMenuItem id="queryUpdate" label="Update" visible="${queryUpdateVisible}" action="${['queryUpdateAction']}">
               
        </rui:tgMenuItem>
        <%
queryGroupUpdateVisible="params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'"
%>

        <rui:tgMenuItem id="queryGroupUpdate" label="Update" visible="${queryGroupUpdateVisible}" action="${['queryGroupUpdateAction']}">
               
        </rui:tgMenuItem>
        <%
copyQueryVisible="params.data.nodeType == 'filter'"
%>

        <rui:tgMenuItem id="copyQuery" label="Copy Query" visible="${copyQueryVisible}" action="${['copyQueryAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
rootImage3615Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage3615Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage3617Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage3617Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="eventDetails" iframe="false"></rui:html>

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="saveQueryForm" iframe="false"></rui:html>

<rui:html id="saveQueryGroupForm" iframe="false"></rui:html>

<%
functionActionCondition3637Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='eventList' condition="$functionActionCondition3637Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3651Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getHistoricalEventDetails.gsp', {id:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3662Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3677Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, mode:'create', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3688Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['eventList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['eventList'].getSortOrder(), mode:'create', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3698Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'historicalEvent', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3709Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'historicalEvent', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3720Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3731Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3742Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3753Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3762Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3771Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3780Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value + ']']]></rui:functionArg>
    
</rui:action>

<%
requestActionCondition3789Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter3792Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter3792Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition3798Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter3801Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter3801Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition3807Condition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId='filterTree' 

>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="188" resizable="false"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="100" resizable="false"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_historicalEvents.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="3563">
        
            <rui:layoutUnit position='center' gutter='0px' id='3828' isActive='true' scroll='false' useShim='false' component='eventList'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0px' id='3831' isActive='true' resize='true' scroll='false' useShim='false' width='250' component='filterTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>