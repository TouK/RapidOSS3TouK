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
image4273Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image4273Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image4275Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image4275Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image4277Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image4277Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image4279Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image4279Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image4281Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image4281Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image4283Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image4283Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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

<rui:treeGrid id="topologyTree" url="../script/run/getHierarchy?format=xml" rootTag="Objects" pollingInterval="60"
        keyAttribute="id" contentPath="Object" title="Service View" expanded="false"

        onNodeClicked="${['getEventsAction','getSummaryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="displayName" colLabel="Name" width="248" sortBy="true" sortOrder="asc">
            
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

<rui:html id="eventDetails" iframe="false"></rui:html>

<rui:html id="objectDetails" iframe="false"></rui:html>


<rui:flexPieChart id="summaryChart" url="../script/run/getSummaryData?format=xml" rootTag="chart"
        swfURL="../images/rapidjs/component/chart/PieChart.swf" title="Summary View"

>
</rui:flexPieChart>

<rui:timeline id="eventHistory" url="../script/run/getEventHistory?format=xml" title="Event History" pollingInterval="0"

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
functionActionCondition4315Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4326Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4337Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA[getEventsQuery(params.data)]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.displayName]]></rui:functionArg>
    
    <rui:functionArg>null</rui:functionArg>
    
</rui:action>

<%
functionActionCondition4351Condition=""
%>

<rui:action id="getSummaryAction" type="function" function="refresh" componentId='summaryChart' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Summary View for ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4361Condition=""
%>

<rui:action id="eventHistoryAction" type="function" function="refresh" componentId='eventHistory' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Event History of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition4372Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter4375Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4375Visible}"></rui:requestParam>
    <%
parameter4377Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter4377Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4383Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter4386Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4386Visible}"></rui:requestParam>
    <%
parameter4388Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter4388Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4394Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter4397Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4397Visible}"></rui:requestParam>
    <%
parameter4399Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter4399Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition4405Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter4408Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter4408Visible}"></rui:requestParam>
    <%
parameter4410Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter4410Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50'
></rui:popupWindow>

<rui:popupWindow componentId="eventHistory" width="730" height="450" resizable="true"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_serviceView.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4253">
        
            <rui:layoutUnit position='bottom' gutter='0px' height='300' id='4428' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='4425' isActive='true' scroll='false' useShim='false' component='summaryChart'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0px' id='4431' isActive='true' resize='true' scroll='false' useShim='false' width='250' component='topologyTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>