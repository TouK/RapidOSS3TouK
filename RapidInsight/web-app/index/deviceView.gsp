<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search?searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events"
        pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" queryEnabled="true"
    
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
image4454Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image4454Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image4456Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image4456Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image4458Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image4458Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image4460Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image4460Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image4462Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image4462Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image4464Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image4464Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="eventDetails" iframe="false"></rui:html>

<rui:autocomplete id="searchDevice" url="../script/run/autocomplete" contentPath="Suggestion" animated="false"
        title="Device Search" suggestionAttribute="name" cacheSize="0"

        onSubmit="${['getEventsAction','autocompleteObjectDetailsAction']}"
    
>
</rui:autocomplete>

<%
functionActionCondition4484Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4495Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4506Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA['elementName:' + params.query + '']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.query]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition4520Condition=""
%>

<rui:action id="autocompleteObjectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.query})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.query]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition4530Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter4533Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4533Visible}"></rui:requestParam>
    <%
parameter4535Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter4535Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4541Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter4544Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4544Visible}"></rui:requestParam>
    <%
parameter4546Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter4546Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4552Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter4555Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4555Visible}"></rui:requestParam>
    <%
parameter4557Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter4557Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4563Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter4566Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4566Visible}"></rui:requestParam>
    <%
parameter4568Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter4568Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_deviceView.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4434">
        
            <rui:layoutUnit position='bottom' gutter='0px' height='300' id='4580' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='4577' isActive='true' scroll='false' useShim='false' component='objectDetails'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0px' id='4583' isActive='true' resize='true' scroll='false' useShim='false' width='280' component='searchDevice'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>