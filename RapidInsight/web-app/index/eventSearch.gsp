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
fieldExpression4975Visible="params.data.rsAlias == 'RsRiEvent'"
%>

        <rui:slField exp="${fieldExpression4975Visible}" fields='${["elementName", " identifier", " owner", " acknowledged", " severity", " source", " changedAt"]}'></rui:slField>
    
    </rui:slFields>
    <rui:slImages>
    <%
image4977Visible="params.data.severity == '5'"
%>

        <rui:slImage visible="${image4977Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:slImage>
    <%
image4979Visible="params.data.severity == '4'"
%>

        <rui:slImage visible="${image4979Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:slImage>
    <%
image4981Visible="params.data.severity == '3'"
%>

        <rui:slImage visible="${image4981Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
    <%
image4983Visible="params.data.severity == '2'"
%>

        <rui:slImage visible="${image4983Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:slImage>
    <%
image4985Visible="params.data.severity == '1'"
%>

        <rui:slImage visible="${image4985Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:slImage>
    <%
image4987Visible="params.data.severity == '0'"
%>

        <rui:slImage visible="${image4987Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:slImage>
    
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
rootImage5028Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage5028Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage5030Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage5030Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="eventDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveQueryForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="saveQueryGroupForm" iframe="false"  timeout="30"></rui:html>

<rui:html id="inMaintenanceForm" iframe="false"  timeout="30"></rui:html>

<%
functionActionCondition5052Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='eventList' condition="$functionActionCondition5052Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortProperty]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.sortOrder]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.searchClass]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5066Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5077Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5088Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, searchClass: params.data.searchClass, sortProperty:params.data.sortProperty,sortOrder:params.data.sortOrder, mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5099Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  sortProperty: YAHOO.rapidjs.Components['eventList'].getSortAttribute(), sortOrder: YAHOO.rapidjs.Components['eventList'].getSortOrder(), searchClass: YAHOO.rapidjs.Components['eventList'].getSearchClass(), mode:'create', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5109Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'event', searchComponentType:'list'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5120Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'event', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition5131Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5142Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5153Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5164Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5173Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5182Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5191Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='eventList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition5200Condition=""
%>

<rui:action id="inMaintenanceAction" type="function" function="show" componentId='inMaintenanceForm' 

>
    
    <rui:functionArg><![CDATA[createURL('inMaintenanceForm.gsp', {name:params.data.elementName?params.data.elementName:'', refreshComponent:'eventList'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
mergeActionCondition5211Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}"  

>
    <%
parameter5214Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter5214Visible}"></rui:requestParam>
    <%
parameter5216Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter5216Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition5222Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventList']}"  

>
    <%
parameter5225Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter5225Visible}"></rui:requestParam>
    <%
parameter5227Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter5227Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition5233Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}"  

>
    <%
parameter5236Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter5236Visible}"></rui:requestParam>
    <%
parameter5238Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter5238Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition5244Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventList']}"  

>
    <%
parameter5247Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter5247Visible}"></rui:requestParam>
    <%
parameter5249Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter5249Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition5255Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter5258Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter5258Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition5264Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter5267Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter5267Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition5273Condition=""
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

<rui:popupWindow componentId="inMaintenanceForm" width="600" height="400" resizable="true"
 
 
  title='In Maintenance'
></rui:popupWindow>





       <rui:include template="pageContents/_eventSearch.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4969">
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='5300' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='filterTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='5297' isActive='true' scroll='false' useShim='false' component='eventList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>