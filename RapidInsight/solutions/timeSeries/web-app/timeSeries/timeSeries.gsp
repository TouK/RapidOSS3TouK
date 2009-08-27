<html>
<head>
    <meta name="layout" content="timeSeriesLayout" />
</head>
<body>
<rui:searchGrid id="eventsGrid" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events"
        pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml" queryEnabled="true" defaultQuery="" timeout="30"
        defaultSearchClass="RsEvent" searchClassesUrl='../script/run/getClassesForSearch?rootClass=RsEvent'

>
    <rui:sgMenuItems>
    <%
eventDetailsVisible="true"
%>

        <rui:sgMenuItem id="eventDetails" label="Event Details" visible="${eventDetailsVisible}" >

        </rui:sgMenuItem>
    <%
browseVisible="!params.data.instanceName || params.data.instanceName == ''"
%>

        <rui:sgMenuItem id="browse" label="Browse" visible="${browseVisible}" >

        </rui:sgMenuItem>
    <%
acknowledgeVisible="params.data.acknowledged != 'true'"
%>

        <rui:sgMenuItem id="acknowledge" label="Acknowledge" visible="${acknowledgeVisible}" >

        </rui:sgMenuItem>
    <%
unacknowledgeVisible="params.data.acknowledged == 'true'"
%>

        <rui:sgMenuItem id="unacknowledge" label="Unacknowledge" visible="${unacknowledgeVisible}" >

        </rui:sgMenuItem>
    <%
takeOwnershipVisible="true"
%>

        <rui:sgMenuItem id="takeOwnership" label="Take Ownership" visible="${takeOwnershipVisible}" >

        </rui:sgMenuItem>
    <%
releaseOwnershipVisible="true"
%>

        <rui:sgMenuItem id="releaseOwnership" label="ReleaseOwnership" visible="${releaseOwnershipVisible}" >

        </rui:sgMenuItem>

    </rui:sgMenuItems>
    <rui:sgImages>

    </rui:sgImages>
    <rui:sgColumns>

        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="100"   type="text">


        </rui:sgColumn>

        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"   type="text">


        </rui:sgColumn>

        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="100"   type="text">


        </rui:sgColumn>

        <rui:sgColumn attributeName="eventName" colLabel="Event" width="100"   type="text">


        </rui:sgColumn>

        <rui:sgColumn attributeName="count" colLabel="Count" width="100"   type="text">


        </rui:sgColumn>

        <rui:sgColumn attributeName="changedAt" colLabel="Last Change" width="100"   type="text">


        </rui:sgColumn>

    </rui:sgColumns>
    <rui:sgRowColors>

    </rui:sgRowColors>
</rui:searchGrid>

<rui:treeGrid id="RRDVariables" url="../script/run/getRrdVariableList" rootTag="Objects" pollingInterval="60" timeout="30"
        keyAttribute="id" contentPath="Object" title="RRDVariables" expanded="false"

        onNodeClicked="${['graph']}"

>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="RRD Variables" width="200"   sortType="string">

        </rui:tgColumn>

    </rui:tgColumns>
    <rui:tgMenuItems>

    </rui:tgMenuItems>
    <rui:tgRootImages>

    </rui:tgRootImages>
</rui:treeGrid>


<rui:flexLineChart id="FlexLineChart" url="../script/run/rrdXmlLoader" rootTag="RootTag"  timeout="30"
        dataRootTag="Variable"  dataTag="Data"  annotationTag="Annotation"  dateAttribute="time"
        valueAttribute="value"  annLabelAttr="label"  annTimeAttr="time"
        swfURL="../images/rapidjs/component/chart/FlexLineChart.swf" title="FlexLineChart" pollingInterval="0"
        durations="1h, 2h, 5h,    12h, 1d, 3d,   1w, 2w, 1m"

        onItemClicked="${['showVariableDetails']}"

        onRangeChanged="${['showAnnotations']}"

>
</rui:flexLineChart>


<rui:flexLineChart id="FlexLineChartDialog" url="../script/run/rrdXmlLoader" rootTag="RootTag"  timeout="30"
        dataRootTag="Variable"  dataTag="Data"  annotationTag="Annotation"  dateAttribute="time"
        valueAttribute="value"  annLabelAttr="label"  annTimeAttr="time"
        swfURL="../images/rapidjs/component/chart/FlexLineChart.swf" title="FlexLineChartDialog" pollingInterval="0"
        durations=""

        onItemClicked="${['showVariableDetails']}"

        onRangeChanged="${['showAnnotations']}"

>
</rui:flexLineChart>

<%
functionActionCondition21283Condition="window.currentDevice=params.data.resource;true;"
%>

<rui:action id="graph" type="function" function="refresh" componentId='FlexLineChart' condition="$functionActionCondition21283Condition"

>

    <rui:functionArg><![CDATA[{name:params.data.name}]]></rui:functionArg>

    <rui:functionArg><![CDATA['Flex Line Chart: ' + params.data.name]]></rui:functionArg>

</rui:action>

<%
functionActionCondition21293Condition=""
%>

<rui:action id="showVariableDetails" type="function" function="setQueryWithView" componentId='eventsGrid'

>

    <rui:functionArg><![CDATA['changedAt: ' +params.data.time +' AND elementName: ' +window.currentDevice.toExactQuery()]]></rui:functionArg>

    <rui:functionArg><![CDATA['default']]></rui:functionArg>

    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>

    <rui:functionArg><![CDATA['Events of time: ' + params.data.time]]></rui:functionArg>

    <rui:functionArg>null</rui:functionArg>

</rui:action>

<%
functionActionCondition21312Condition=""
%>

<rui:action id="showAnnotations" type="function" function="setQueryWithView" componentId='eventsGrid'

>

    <rui:functionArg><![CDATA[ 'changedAt:[' +params.data.start  + ' TO ' +params.data.end +'] AND elementName: ' +window.currentDevice.toExactQuery()]]></rui:functionArg>

    <rui:functionArg><![CDATA['default']]></rui:functionArg>

    <rui:functionArg><![CDATA['RsEvent']]></rui:functionArg>

    <rui:functionArg><![CDATA['Events between ' + params.data.start + ' and ' + params.data.end]]></rui:functionArg>

    <rui:functionArg>null</rui:functionArg>

</rui:action>

<rui:popupWindow componentId="FlexLineChartDialog" width="600" height="500" resizable="true"


  title='Flex Line Chart Dialog'
></rui:popupWindow>






<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">



    <rui:innerLayout id="21245">

            <rui:layoutUnit position='center' gutter='0px' id='21334' isActive='true' scroll='false' useShim='false' component='FlexLineChart'>

            </rui:layoutUnit>

            <rui:layoutUnit position='left' gutter='0px' id='21340' isActive='true' resize='false' scroll='false' useShim='false' width='200' component='RRDVariables'>

            </rui:layoutUnit>

            <rui:layoutUnit position='bottom' gutter='0px' height='200' id='21337' isActive='true' resize='false' scroll='false' useShim='false' component='eventsGrid'>

            </rui:layoutUnit>

        </rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>