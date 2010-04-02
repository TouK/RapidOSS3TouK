<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" title="Events" viewType="event"
        pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" queryEnabled="true" defaultQuery="" timeout="30"
        defaultSearchClass="RsEvent" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsEvent'
    
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
image2567Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image2567Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image2569Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image2569Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image2571Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image2571Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image2573Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image2573Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image2575Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image2575Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image2577Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image2577Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="eventDetails" iframe="false"  timeout="30"></rui:html>

<rui:autocomplete id="searchDevice" url="../script/run/autocomplete" contentPath="Suggestion" animated="false"
        title="Device Search" suggestionAttribute="name" cacheSize="0" timeout="0"

        onSubmit="${['getEventsAction','autocompleteObjectDetailsAction']}"
    
>
</rui:autocomplete>

<%
functionActionCondition2597Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2608Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2619Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA['elementName:' + params.query.toExactQuery()]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.query]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2633Condition=""
%>

<rui:action id="autocompleteObjectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.query})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.query]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition2643Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter2646Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2646Visible}"></rui:requestParam>
    <%
parameter2648Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter2648Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2654Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter2657Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2657Visible}"></rui:requestParam>
    <%
parameter2659Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter2659Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2665Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter2668Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2668Visible}"></rui:requestParam>
    <%
parameter2670Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter2670Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2676Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter2679Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2679Visible}"></rui:requestParam>
    <%
parameter2681Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter2681Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>





       <rui:include template="pageContents/_deviceView.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="2547">
        
            <rui:layoutUnit position='bottom' gutter='5 0 0 0' height='300' id='2693' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='2696' isActive='true' resize='true' scroll='false' useShim='false' width='280' component='searchDevice'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='2690' isActive='true' scroll='false' useShim='false' component='objectDetails'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>