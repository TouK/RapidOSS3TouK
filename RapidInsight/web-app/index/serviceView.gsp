<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object" bringAllProperties="true" 
        keyAttribute="id"  title="Events" pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml"
        queryEnabled="true" searchInEnabled="true" defaultQuery="" timeout="30" multipleFieldSorting="true"
        defaultSearchClass="RsEvent" defaultView="default" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsEvent&format=xml' extraPropertiesToRequest=""
    
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
     <rui:sgMultiSelectionMenuItems>
    
    </rui:sgMultiSelectionMenuItems>
    <rui:sgImages>
    <%
image253Visible="params.data.severity == '5'"
%>

        <rui:sgImage visible="${image253Visible}" src="../images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    <%
image254Visible="params.data.severity == '4'"
%>

        <rui:sgImage visible="${image254Visible}" src="../images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
    <%
image255Visible="params.data.severity == '3'"
%>

        <rui:sgImage visible="${image255Visible}" src="../images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
    <%
image256Visible="params.data.severity == '2'"
%>

        <rui:sgImage visible="${image256Visible}" src="../images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
    <%
image257Visible="params.data.severity == '1'"
%>

        <rui:sgImage visible="${image257Visible}" src="../images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
    <%
image258Visible="params.data.severity == '0'"
%>

        <rui:sgImage visible="${image258Visible}" src="../images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="150"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="identifier" colLabel="Event" width="150"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="count" colLabel="Count" width="50"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="source" colLabel="Source" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="changedAt" colLabel="Last Change" width="120"   type="text">
            

        </rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>

<rui:treeGrid id="topologyTree" url="../script/run/getHierarchy?format=xml" rootTag="Objects" pollingInterval="60" timeout="30"
        keyAttribute="id" expandAttribute="" contentPath="Object" title="Service View" expanded="false"

        onNodeClicked="${['getEventsAction','getSummaryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="displayName" colLabel="Name" width="310" sortBy="true" sortOrder="asc" sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="image" attributeName="state" colLabel="State" width="65"   sortType="int">
            
            <rui:tgImages>
                <%
image259Visible="params.data.state == '5'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/red.png" visible="${image259Visible}" align="center"></rui:tgImage>
                <%
image260Visible="params.data.state == '4'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/orange.png" visible="${image260Visible}" align="center"></rui:tgImage>
                <%
image261Visible="params.data.state == '3'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/yellow.png" visible="${image261Visible}" align="center"></rui:tgImage>
                <%
image262Visible="params.data.state == '2'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/blue.png" visible="${image262Visible}" align="center"></rui:tgImage>
                <%
image263Visible="params.data.state == '1'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/purple.png" visible="${image263Visible}" align="center"></rui:tgImage>
                <%
image264Visible="params.data.state == '0'"
%>

                <rui:tgImage src="../images/rapidjs/component/states/green.png" visible="${image264Visible}" align="center"></rui:tgImage>
                
            </rui:tgImages>
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
eventHistoryVisible="true"
%>

        <rui:tgMenuItem id="eventHistory" label="Get Event History" visible="${eventHistoryVisible}" action="${['eventHistoryAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
     <rui:tgMultiSelectionMenuItems>
    
    </rui:tgMultiSelectionMenuItems>
    <rui:tgRootImages>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="eventDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="objectDetails" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


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
functionActionConditionindex_serviceView_objectDetailsActionCondition=""
%>

<rui:action id="objectDetailsAction" type="function" function="show" componentId="objectDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getObjectDetails.gsp', {name:params.data.elementName})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.elementName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_serviceView_eventDetailsActionCondition=""
%>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails" 

>
    
    <rui:functionArg><![CDATA[createURL('getEventDetails.gsp', {name:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_serviceView_getEventsActionCondition=""
%>

<rui:action id="getEventsAction" type="function" function="setQueryWithView" componentId="eventsGrid" 

>
    
    <rui:functionArg><![CDATA[getEventsQuery(params.data)]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['default']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Events of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_serviceView_getSummaryActionCondition=""
%>

<rui:action id="getSummaryAction" type="function" function="refresh" componentId="summaryChart" 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Summary View for ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_serviceView_eventHistoryActionCondition=""
%>

<rui:action id="eventHistoryAction" type="function" function="refresh" componentId="eventHistory" 

>
    
    <rui:functionArg><![CDATA[{nodeType:params.data.nodeType, name:params.data.name}]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Event History of ' + params.data.displayName]]></rui:functionArg>
    
</rui:action>

<%
mergeActionConditionindex_serviceView_unacknowledgeActionCondition=""
%>

<rui:action id="unacknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_serviceView_unacknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_serviceView_unacknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_serviceView_unacknowledgeAction_acknowledgedVisible="'false'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_serviceView_unacknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_serviceView_acknowledgeActionCondition=""
%>

<rui:action id="acknowledgeAction" type="merge" url="../script/run/acknowledge" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_serviceView_acknowledgeAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_serviceView_acknowledgeAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_serviceView_acknowledgeAction_acknowledgedVisible="'true'"
%>

    <rui:requestParam key="acknowledged" value="${parameterindex_serviceView_acknowledgeAction_acknowledgedVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_serviceView_releaseOwnershipActionCondition=""
%>

<rui:action id="releaseOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_serviceView_releaseOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_serviceView_releaseOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_serviceView_releaseOwnershipAction_actVisible="'false'"
%>

    <rui:requestParam key="act" value="${parameterindex_serviceView_releaseOwnershipAction_actVisible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionConditionindex_serviceView_takeOwnershipActionCondition=""
%>

<rui:action id="takeOwnershipAction" type="merge" url="../script/run/setOwnership" components="${['eventsGrid']}" submitType="GET"  

>
    <%
parameterindex_serviceView_takeOwnershipAction_nameVisible="params.data.name "
%>

    <rui:requestParam key="name" value="${parameterindex_serviceView_takeOwnershipAction_nameVisible}"></rui:requestParam>
    <%
parameterindex_serviceView_takeOwnershipAction_actVisible="'true'"
%>

    <rui:requestParam key="act" value="${parameterindex_serviceView_takeOwnershipAction_actVisible}"></rui:requestParam>
    
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
        


<rui:innerLayout id="288">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='summaryChart'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='bottom' gutter='5 0 0 0' resize='true' height='300' useShim='false' scroll='false' component='eventsGrid'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='400' gutter='0 5 0 0' resize='true' useShim='false' scroll='false' component='topologyTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>