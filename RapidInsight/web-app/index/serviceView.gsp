<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" title="Events"
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
image2386Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image2386Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image2388Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image2388Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image2390Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image2390Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image2392Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image2392Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image2394Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image2394Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image2396Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image2396Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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

<rui:treeGrid id="topologyTree" url="../script/run/getHierarchy?format=xml" rootTag="Objects" pollingInterval="60" timeout="30"
        keyAttribute="id" contentPath="Object" title="Service View" expanded="false"

        onNodeClicked="${['getEventsAction','getSummaryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="displayName" colLabel="Name" width="248" sortBy="true" sortOrder="asc" sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
eventHistoryVisible="true"
%>

        <rui:tgMenuItem id="eventHistory" label="Get Event History" visible="${eventHistoryVisible}" action="${['eventHistoryAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
    <rui:tgRootImages>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="eventDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>


<rui:flexPieChart id="summaryChart" url="../script/run/getSummaryData?format=xml" rootTag="chart"  timeout="30"
        swfURL="../images/rapidjs/component/chart/PieChart.swf" title="Summary View" pollingInterval="0"

>
</rui:flexPieChart>

<rui:timeline id="eventHistory" url="../script/run/getEventHistory?format=xml" title="Event History" pollingInterval="0" timeout="30"

>
    <rui:tlBands>
        
        <rui:tlBand width="70%" intervalUnit="hour" intervalPixels="100" 
                 highlight="false" showText="true"
                textWidth="200"  
                ></rui:tlBand>
        
        <rui:tlBand width="30%" intervalUnit="day" intervalPixels="200" 
                syncWith="0" highlight="true" showText="false"
                textWidth="200" trackHeight="0.5" 
                ></rui:tlBand>
        
    </rui:tlBands>
</rui:timeline>

<%
functionActionCondition2428Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2439Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2450Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA[getEventsQuery(params.data)]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2464Condition=""
%>

<rui:action id="getSummaryAction" type="function" function="refresh" componentId='summaryChart' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Summary View for ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition2474Condition=""
%>

<rui:action id="eventHistoryAction" type="function" function="refresh" componentId='eventHistory' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Event History of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition2485Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter2488Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2488Visible}"></rui:requestParam>
    <%
parameter2490Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter2490Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2496Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter2499Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2499Visible}"></rui:requestParam>
    <%
parameter2501Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter2501Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2507Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter2510Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2510Visible}"></rui:requestParam>
    <%
parameter2512Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter2512Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition2518Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter2521Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter2521Visible}"></rui:requestParam>
    <%
parameter2523Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter2523Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50' 
></rui:popupWindow>

<rui:popupWindow componentId="eventHistory" width="730" height="450" resizable="true"
 
 
  title='Event History'
></rui:popupWindow>





       <rui:include template="pageContents/_serviceView.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="2366">
        
            <rui:layoutUnit position='bottom' gutter='5 0 0 0' height='300' id='2541' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='2544' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='topologyTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='2538' isActive='true' scroll='false' useShim='false' component='summaryChart'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>