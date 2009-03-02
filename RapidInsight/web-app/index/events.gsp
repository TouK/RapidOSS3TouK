<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search?searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events"
        pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" queryEnabled="true"
    
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
    <rui:sgImages>
    <%
image3029Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image3029Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image3031Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image3031Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image3033Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image3033Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image3035Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image3035Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image3037Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image3037Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image3039Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image3039Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="150"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="identifier" colLabel="Event" width="150"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="count" colLabel="Count" width="50"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="source" colLabel="Source" width="100"   type="text"></rui:sgColumn>
    
        <rui:sgColumn attributeName="changedAt" colLabel="Last Change" width="120"   type="text"></rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>

<rui:treeGrid id="filterTree" url="../script/run/queryList?format=xml&type=event" rootTag="Filters" pollingInterval="0"
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
rootImage3057Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage3057Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        <%
rootImage3059Visible="params.data.nodeType == 'filter'"
%>

        <rui:tgRootImage visible="${rootImage3059Visible}" expanded="../images/rapidjs/component/tools/filter.png" collapsed="../images/rapidjs/component/tools/filter.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="eventDetails" iframe="false"></rui:html>

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="saveQueryForm" iframe="false"></rui:html>

<rui:html id="saveQueryGroupForm" iframe="false"></rui:html>

<%
functionActionCondition3079Condition="params.data.nodeType == 'filter'"
%>

<rui:action id="setQueryAction" type="function" function="setQueryWithView" componentId='eventsGrid' condition="$functionActionCondition3079Condition"

>
    
    <rui:functionArg><![CDATA[params.data.query ]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.data.viewName]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3093Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3104Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition3115Condition=""
%>

<rui:action id="copyQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.data.query, group:params.data.group, viewName:params.data.viewName, mode:'create', type:'event', searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3126Condition=""
%>

<rui:action id="saveQueryAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {query:params.query,  viewName:YAHOO.rapidjs.Components['eventsGrid'].getCurrentView(), mode:'create', type:'event', searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3136Condition=""
%>

<rui:action id="queryUpdateAction" type="function" function="show" componentId='saveQueryForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryForm.gsp', {queryId:params.data.id,   mode:'edit', type:'event', searchComponentType:'grid'})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition3147Condition=""
%>

<rui:action id="queryGroupUpdateAction" type="function" function="show" componentId='saveQueryGroupForm' 

>
    
    <rui:functionArg><![CDATA[createURL('queryGroupForm.gsp', {mode:'edit', type:'event', queryGroupId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
mergeActionCondition3158Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter3161Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter3161Visible}"></rui:requestParam>
    <%
parameter3163Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter3163Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition3169Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter3172Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter3172Visible}"></rui:requestParam>
    <%
parameter3174Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter3174Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition3180Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter3183Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter3183Visible}"></rui:requestParam>
    <%
parameter3185Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter3185Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition3191Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter3194Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter3194Visible}"></rui:requestParam>
    <%
parameter3196Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter3196Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition3202Condition=""
%>

<rui:action id="deleteQueryAction" type="request" url="../searchQuery/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter3205Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter3205Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition3211Condition=""
%>

<rui:action id="deleteQueryGroupAction" type="request" url="../searchQueryGroup/delete?format=xml" components="${['filterTree']}" 

        onSuccess="${['refreshQueriesAction']}"
    
>
    <%
parameter3214Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter3214Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition3220Condition=""
%>

<rui:action id="refreshQueriesAction" type="function" function="poll" componentId='filterTree' 

>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50'
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryForm" width="385" height="165" resizable="false"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="saveQueryGroupForm" width="330" height="100" resizable="false"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_events.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="3009">
        
            <rui:layoutUnit position='center' gutter='0px' id='3241' isActive='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0px' id='3244' isActive='true' resize='true' scroll='false' useShim='false' width='250' component='filterTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>