<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="eventList" url="../search" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Event Search" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["name"]}'  defaultQuery=""
    defaultSearchClass="RsEvent" searchClassesUrl="../script/run/getEventClassesForSearch" timeout="30"
    
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
        <%
inMaintenanceVisible="true"
%>

        <rui:slMenuItem id="inMaintenance" label="In Maintenance" visible="${inMaintenanceVisible}" action="${['inMaintenanceAction']}">
               
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
        
    </rui:slPropertyMenuItems>
     <rui:slFields>
    <%
fieldExpression330Visible="params.data.rsAlias == 'RsRiEvent'"
%>

        <rui:slField exp="${fieldExpression330Visible}" fields='${["elementName", " identifier", " owner", " acknowledged", " severity", " source", " changedAt"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    <%
image332Visible="params.data.severity == '5'"
%>

        <rui:slImage visible="${image332Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:slImage>
    <%
image334Visible="params.data.severity == '4'"
%>

        <rui:slImage visible="${image334Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:slImage>
    <%
image336Visible="params.data.severity == '3'"
%>

        <rui:slImage visible="${image336Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
    <%
image338Visible="params.data.severity == '2'"
%>

        <rui:slImage visible="${image338Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:slImage>
    <%
image340Visible="params.data.severity == '1'"
%>

        <rui:slImage visible="${image340Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:slImage>
    <%
image342Visible="params.data.severity == '0'"
%>

        <rui:slImage visible="${image342Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:slImage>
    
    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=event" rootTag="Filters" pollingInterval="0" timeout="30"
        keyAttribute="id" contentPath="Filter" title="Saved Queries" expanded="true"

        onNodeClicked="${['setQueryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Name" width="248" sortBy="true" sortOrder="asc" sortType="string">
            
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
rootImage383Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage383Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage385Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage385Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="eventDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveQueryForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="inMaintenanceForm" iframe="false"  timeout="30"></rui:html>

<%
functionActionCondition407Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='eventList' condition="$functionActionCondition407Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition421Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition432Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition443Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, searchClass: params.data.searchClass, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition454Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['eventList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['eventList'].getSortOrder(), searchClass: YAHOO.rapidjs.Components['eventList'].getSearchClass(), mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition464Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition475Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'event', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition486Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition497Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition508Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition519Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition528Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition537Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition546Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition555Condition=""
%>

<rui:action id="inMaintenanceAction" type="function" function="show" componentId='inMaintenanceForm' 

>
    
    <rui:functionArg><![CDATA[createURL('inMaintenanceForm.gsp', {name:params.data.elementName?params.data.elementName:'', refreshComponent:'eventList'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
mergeActionCondition566Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}"  

>
    <%
parameter569Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter569Visible}"></rui:requestParam>
    <%
parameter571Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter571Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition577Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}"  

>
    <%
parameter580Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter580Visible}"></rui:requestParam>
    <%
parameter582Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter582Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition588Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}"  

>
    <%
parameter591Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter591Visible}"></rui:requestParam>
    <%
parameter593Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter593Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition599Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}"  

>
    <%
parameter602Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter602Visible}"></rui:requestParam>
    <%
parameter604Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter604Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition610Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter613Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter613Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition619Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter622Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter622Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition628Condition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId='filterTree' 

>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50' 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="213" resizable="false"
 
 
  title='Save query'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="100" resizable="false"
 
 
  title='Save group'
></rui:popupWindow>

<rui:popupWindow componentId="inMaintenanceForm" width="600" height="500" resizable="false"
 
 
  title='In Maintenance'
></rui:popupWindow>





       <rui:include template="pageContents/_eventSearch.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="324">
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='655' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='filterTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='652' isActive='true' scroll='false' useShim='false' component='eventList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>