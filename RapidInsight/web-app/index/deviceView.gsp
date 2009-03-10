<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events"
        pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" queryEnabled="true" defaultQuery=""
        defaultSearchClass="RsEvent" searchClassesUrl='../script/run/getEventClassesForSearch'
    
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
image7760Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image7760Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image7762Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image7762Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image7764Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image7764Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image7766Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image7766Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image7768Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image7768Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image7770Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image7770Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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
functionActionCondition7790Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7801Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7812Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA['elementName:"' + params.query.toQuery() + '"']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.query]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7826Condition=""
%>

<rui:action id="autocompleteObjectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.query})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.query]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition7836Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter7839Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7839Visible}"></rui:requestParam>
    <%
parameter7841Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter7841Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7847Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter7850Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7850Visible}"></rui:requestParam>
    <%
parameter7852Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter7852Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7858Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter7861Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7861Visible}"></rui:requestParam>
    <%
parameter7863Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter7863Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7869Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter7872Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7872Visible}"></rui:requestParam>
    <%
parameter7874Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter7874Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_deviceView.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="7740">
        
            <rui:layoutUnit position='bottom' gutter='5 0 0 0' height='300' id='7886' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='7889' isActive='true' resize='true' scroll='false' useShim='false' width='280' component='searchDevice'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='7883' isActive='true' scroll='false' useShim='false' component='objectDetails'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>