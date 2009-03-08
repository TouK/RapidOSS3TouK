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
image7579Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image7579Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image7581Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image7581Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image7583Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image7583Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image7585Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image7585Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image7587Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image7587Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image7589Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image7589Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
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
functionActionCondition7621Condition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7632Condition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId='eventDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7643Condition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId='eventsGrid' 

>
    
    <rui:functionArg><![CDATA[getEventsQuery(params.data)]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7657Condition=""
%>

<rui:action id="getSummaryAction" type="function" function="refresh" componentId='summaryChart' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Summary View for ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition7667Condition=""
%>

<rui:action id="eventHistoryAction" type="function" function="refresh" componentId='eventHistory' 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Event History of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
mergeActionCondition7678Condition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter7681Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7681Visible}"></rui:requestParam>
    <%
parameter7683Visible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameter7683Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7689Condition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}"  

>
    <%
parameter7692Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7692Visible}"></rui:requestParam>
    <%
parameter7694Visible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameter7694Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7700Condition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter7703Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7703Visible}"></rui:requestParam>
    <%
parameter7705Visible="'false'"
%>

    <rui:requestParam key="act" value="${parameter7705Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition7711Condition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}"  

>
    <%
parameter7714Visible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameter7714Visible}"></rui:requestParam>
    <%
parameter7716Visible="'true'"
%>

    <rui:requestParam key="act" value="${parameter7716Visible}"></rui:requestParam>
    
</rui:action>

<rui:popupWindow componentId="eventDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="objectDetails" width="850" height="700" resizable="true"
 
 
x='85' y='50'
></rui:popupWindow>

<rui:popupWindow componentId="eventHistory" width="730" height="450" resizable="true"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_serviceView.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="7559">
        
            <rui:layoutUnit position='bottom' gutter='5 0 0 0' height='300' id='7734' isActive='true' resize='true' scroll='false' useShim='false' component='eventsGrid'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='7737' isActive='true' resize='true' scroll='false' useShim='false' width='255' component='topologyTree'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='center' gutter='0px' id='7731' isActive='true' scroll='false' useShim='false' component='summaryChart'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>