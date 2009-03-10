<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="inventoryList" url="../search" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Inventory" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["className", "name", " description", " displayName", " isManaged"]}'  defaultQuery=""
    defaultSearchClass="RsTopologyObject" searchClassesUrl="../script/run/getTopologyClassesForSearch"
    
        onSaveQueryClicked="${['saveQueryAction']}"
    
>
    <rui:slMenuItems>
        <%
browseVisible="true"
%>

        <rui:slMenuItem id="browse" label="Browse" visible="${browseVisible}" action="${['objectDetailsAction']}">
               
        </rui:slMenuItem>
        <%
showMapVisible="params.data.rsAlias == 'RsComputerSystem' || params.data.rsAlias == 'RsLink'"
%>

        <rui:slMenuItem id="showMap" label="Show Map" visible="${showMapVisible}" action="${['showMapAction']}">
               
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
greaterThanVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="greaterThan" label="Greater Than" action="${['greaterThanAction']}" visible="${greaterThanVisible}">
               
        </rui:slMenuItem>
        <%
lessThanVisible="YAHOO.lang.isNumber(parseInt(params.value))"
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
fieldExpression7146Visible="params.data.rsAlias == 'RsComputerSystem'"
%>

        <rui:slField exp="${fieldExpression7146Visible}" fields='${["className", " name", " vendor", " model", " managementServer", " location", " snmpAddress"]}'></rui:slField>
    <%
fieldExpression7148Visible="params.data.rsAlias == 'RsLink'"
%>

        <rui:slField exp="${fieldExpression7148Visible}" fields='${["className", " name", " a_ComputerSystemName", " a_Name", " z_ComputerSystemName", "z_Name"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    
    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=topology" rootTag="Filters" pollingInterval="0"
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
rootImage7179Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage7179Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage7181Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage7181Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="saveQueryGroupForm" iframe="false"></rui:html>

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="saveQueryForm" iframe="false"></rui:html>

<%
functionActionCondition7199Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='inventoryList' condition="$functionActionCondition7199Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7213Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7224Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.className + ' ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7235Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, searchClass: params.data.searchClass, group:params.data.group, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, mode:'create', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition7246Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['inventoryList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['inventoryList'].getSortOrder(), searchClass: YAHOO.rapidjs.Components['inventoryList'].getSearchClass(), mode:'create', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition7256Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition7267Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'topology', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition7278Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key ]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7289Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7300Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7309Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7318Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7327Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
requestActionCondition7336Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter7339Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter7339Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition7345Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter7348Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter7348Visible}"></rui:requestParam>
    
</rui:action>

<%
linkUrl7354Visible="createURL('redirectToMap.gsp', {name:params.data.name})"
%>
<%
linkActionCondition7354Condition=""
%>

<rui:action id="showMapAction" type="link" url="${linkUrl7354Visible}" 

>
</rui:action>

<%
functionActionCondition7360Condition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId='filterTree' 

>
    
</rui:action>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="100" resizable="false"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="188" resizable="false"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_inventory.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="7140">
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='7381' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='filterTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='7378' isActive='true' scroll='false' useShim='false' component='inventoryList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>