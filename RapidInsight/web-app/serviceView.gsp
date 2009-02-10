<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 27, 2008
  Time: 2:59:13 PM
--%>

<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:flexPieChart id="summaryChart" url="script/run/getSummaryData?format=xml" rootTag="chart" swfURL="images/rapidjs/component/chart/PieChart.swf" title="Summary View">
</rui:flexPieChart>
<rui:treeGrid id="topologyTree" url="script/run/getHierarchy?format=xml" rootTag="Objects" pollingInterval="60"
        keyAttribute="id" contentPath="Object" title="Service View" onNodeClicked="treeNodeClickedAction">
    <rui:tgColumns>
        <rui:tgColumn attributeName="displayName" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="eventHistory" label="Get Event History" action="eventHistoryAction"></rui:tgMenuItem>
    </rui:tgMenuItems>
</rui:treeGrid>
<rui:searchGrid id="eventsGrid" url="search?format=xml&searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events List"
        pollingInterval="0" fieldsUrl="script/run/getViewFields?format=xml">
    <rui:sgMenuItems>
        <rui:sgMenuItem id="browse" label="Browse" visible="params.data.elementName && params.data.elementName != ''" action="browseAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="eventDetails" label="Event Details" action="eventDetailsAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="acknowledge" label="Acknowledge" visible="params.data.acknowledged != 'true'" action="acknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="unacknowledge" label="Unacknowledge" visible="params.data.acknowledged == 'true'" action="unacknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="takeOwnership" label="Take Ownership" action="takeOwnAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="releaseOwnership" label="Release Ownership" action="releaseOwnAction"></rui:sgMenuItem>
    </rui:sgMenuItems>
    <rui:sgImages>
        <rui:sgImage visible="params.data.severity == '0'" src="images/rapidjs/component/searchlist/green.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/purple.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/red.png"></rui:sgImage>
    </rui:sgImages>
    <rui:sgColumns>
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="name" colLabel="Name" width="150"></rui:sgColumn>
        <rui:sgColumn attributeName="identifier" colLabel="Event Name" width="150"></rui:sgColumn>
        <rui:sgColumn attributeName="count" colLabel="Count" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="source" colLabel="Source" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="changedAt" colLabel="Last Change" width="120"></rui:sgColumn>
    </rui:sgColumns>
</rui:searchGrid>
<rui:timeline id="eventHistory" url="script/run/getEventHistory?format=xml" title="Event History">
    <rui:tlBands>
        <rui:tlBand width="70%" intervalUnit="hour" intervalPixels="100"></rui:tlBand>
        <rui:tlBand width="30%" intervalUnit="day" intervalPixels="200" intervalPixels="200" syncWith="0" highlight="true" showText="false" trackHeight="0.5"></rui:tlBand>
    </rui:tlBands>
</rui:timeline>
<rui:popupWindow componentId="eventHistory" width="730" height="450"></rui:popupWindow>

<rui:html id="objectDetailsmenuHtml" iframe="false"></rui:html>
<rui:popupWindow componentId="objectDetailsmenuHtml" width="850" height="700" x="85" y="50"></rui:popupWindow>
<rui:html id="eventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="eventDetails" width="850" height="500"></rui:popupWindow>

<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails">
    <rui:functionArg>createURL('getEventDetails.gsp', {name:params.data.name})</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.name</rui:functionArg>
</rui:action>
<rui:action id="browseAction" type="function" function="show" componentId="objectDetailsmenuHtml">
    <rui:functionArg>createURL('getObjectDetails.gsp', {name:params.data.elementName})</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.elementName</rui:functionArg>
</rui:action>
<rui:action id="acknowledgeAction" type="merge" url="script/run/acknowledge" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="true"></rui:requestParam>
</rui:action>
<rui:action id="unacknowledgeAction" type="merge" url="script/run/acknowledge" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="acknowledged" value="false"></rui:requestParam>
</rui:action>
<rui:action id="takeOwnAction" type="merge" url="script/run/setOwnership" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="act" value="true"></rui:requestParam>
</rui:action>
<rui:action id="releaseOwnAction" type="merge" url="script/run/setOwnership" components="${['eventsGrid']}">
    <rui:requestParam key="name" value="params.data.name"></rui:requestParam>
    <rui:requestParam key="act" value="false"></rui:requestParam>
</rui:action>
<rui:action id="getEventsAction" type="function" componentId="eventsGrid" function="setQueryWithView">
    <rui:functionArg>getEventsQuery(params.data)</rui:functionArg>
    <rui:functionArg>'default'</rui:functionArg>
    <rui:functionArg>'Events of ' + params.data.displayName</rui:functionArg>
</rui:action>
<rui:action id="getSummaryAction" type="function" componentId="summaryChart" function="refresh">
    <rui:functionArg>{nodeType:params.data.nodeType, name:params.data.name}</rui:functionArg>
    <rui:functionArg>'Summary View for ' + params.data.displayName</rui:functionArg>
</rui:action>
<rui:action id="treeNodeClickedAction" type="combined" actions="${['getEventsAction', 'getSummaryAction']}"></rui:action>
<rui:action id="eventHistoryAction" componentId="eventHistory" type="function" function="refresh">
    <rui:functionArg>{nodeType:params.data.nodeType, name:params.data.name}</rui:functionArg>
    <rui:functionArg>'Event History of ' + params.data.displayName</rui:functionArg>
</rui:action>
    <script type="text/javascript">
        function getEventsQuery(data){
            if(data.nodeType == 'Container'){
                return 'rsDatasource:"' + data.name + '"'
            }
            else{
                return 'rsDatasource:"' + data.rsDatasource + '" AND elementName:"' + data.name + '"';
            }
        }

        var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
        var topologyTree = YAHOO.rapidjs.Components['topologyTree'];
        var summaryChart = YAHOO.rapidjs.Components['summaryChart'];
        eventsGrid.renderCellFunction = function(key, value, data, el){
            if(key == "lastNotifiedAt" || key == "changedAt"){
                if(value == "0" || value == "")
                {
                    return "never"
                }
                else
                {
                    try
                    {
                        var d = new Date();
                        d.setTime(parseFloat(value))
                        return d.format("d M H:i:s");
                    }
                    catch(e)
                    {}
                }
            }
            return value;
         }
        YAHOO.util.Event.onDOMReady(function() {
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'top', body: 'top', resize: false, height:45},
                    { position: 'center', body: summaryChart.container.id, resize: false, gutter: '1px' },
                    { position: 'bottom', resize: true, body: eventsGrid.container.id, gutter: '1px', height:300},
                    { position: 'left', width: 280, resize: true, body: topologyTree.container.id, scroll: false}
                ]
            });

            layout.render();
            var bottomUnit = layout.getUnitByPosition('bottom');
            var leftUnit = layout.getUnitByPosition('left');
            var centerUnit = layout.getUnitByPosition('center');
            eventsGrid.resize(bottomUnit.getSizes().body.w, bottomUnit.getSizes().body.h);
            layout.on('resize', function() {
                eventsGrid.resize(bottomUnit.getSizes().body.w, bottomUnit.getSizes().body.h);
            });
            topologyTree.resize(leftUnit.getSizes().body.w, leftUnit.getSizes().body.h);
            layout.on('resize', function() {
                topologyTree.resize(leftUnit.getSizes().body.w, leftUnit.getSizes().body.h);
            });
            summaryChart.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
            layout.on('resize', function() {
                summaryChart.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
            });
            window.layout = layout;
        })

    </script>
</body>
</html>