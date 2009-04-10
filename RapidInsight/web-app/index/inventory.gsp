<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="inventoryList" url="../search" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Inventory" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["className", "name", " description", " displayName", " isManaged"]}'  defaultQuery=""
    defaultSearchClass="RsTopologyObject" searchClassesUrl="../script/run/getTopologyClassesForSearch" timeout="30"
    
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
fieldExpression5580Visible="params.data.rsAlias == 'RsComputerSystem'"
%>

        <rui:slField exp="${fieldExpression5580Visible}" fields='${["className", " name", " vendor", " model", " managementServer", " location", " snmpAddress"]}'></rui:slField>
    <%
fieldExpression5582Visible="params.data.rsAlias == 'RsLink'"
%>

        <rui:slField exp="${fieldExpression5582Visible}" fields='${["className", " name", " a_ComputerSystemName", " a_Name", " z_ComputerSystemName", "z_Name"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    
    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=topology" rootTag="Filters" pollingInterval="0" timeout="30"
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
rootImage5615Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage5615Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage5617Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage5617Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveQueryForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="inMaintenanceForm" iframe="false"  timeout="30"></rui:html>

<%
functionActionCondition5637Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='inventoryList' condition="$functionActionCondition5637Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5651Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5662Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.className + ' ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5673Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, searchClass: params.data.searchClass, group:params.data.group, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, mode:'create', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5684Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['inventoryList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['inventoryList'].getSortOrder(), searchClass: YAHOO.rapidjs.Components['inventoryList'].getSearchClass(), mode:'create', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5694Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'topology', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5705Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'topology', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5716Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key ]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5727Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5738Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5747Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5756Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5765Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='inventoryList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5774Condition=""
%>

<rui:action id="inMaintenanceAction" type="function" function="show" componentId='inMaintenanceForm' 

>
    
    <rui:functionArg><![CDATA[createURL('inMaintenanceForm.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['In Maintenance']]></rui:functionArg>
    
</rui:action>

<%
requestActionCondition5785Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter5788Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter5788Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition5794Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter5797Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter5797Visible}"></rui:requestParam>
    
</rui:action>

<%
linkUrl5803Visible="createURL('redirectToMap.gsp', {name:params.data.name})"
%>
<%
linkActionCondition5803Condition=""
%>

<rui:action id="showMapAction" type="link" url="${linkUrl5803Visible}" 

>
</rui:action>

<%
functionActionCondition5809Condition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId='filterTree' 

>
    
</rui:action>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="100" resizable="false"
 
 
  title='Save group'
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50' 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="213" resizable="false"
 
 
  title='Save query'
></rui:popupWindow>

<rui:popupWindow componentId="inMaintenanceForm" width="600" height="400" resizable="false"
 
 
  title='In Maintenance'
></rui:popupWindow>





       <rui:include template="pageContents/_inventory.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="5574">
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='5833' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='filterTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='5830' isActive='true' scroll='false' useShim='false' component='inventoryList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>