<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 27, 2008
  Time: 2:59:13 PM
--%>

<html>
<head>
    <meta name="layout" content="smartsLayout" />
</head>
<body>
<rui:pieChart id="summaryChart" url="piechart.xml" fields="${['state', 'count']}" dataField="count" contentPath="Item" title="Summary View" 
        categoryField="state" legend="bottom" swfURL="js/yui/charts/assets/charts.swf" colors="${['0xff0000', '0xff7514', '0xddc700', '0x2dbfcd', '0x00ff00']}"></rui:pieChart>
<rui:treeGrid id="topologyTree" url="script/run/getHierarchy?format=xml" rootTag="Objects" pollingInterval="60"
        keyAttribute="id" contentPath="Object" title="Service View">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
</rui:treeGrid>
<rui:searchGrid id="eventsGrid" url="search?format=xml&searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events List"
        pollingInterval="0" fieldsUrl="script/run/getViewFields?format=xml">
    <rui:sgMenuItems>
        <rui:sgMenuItem id="browseInstance" label="Browse" visible="!params.data.elementName || params.data.elementName == ''" action="browseInstanceAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="browseElement" label="Browse" visible="params.data.elementName && params.data.elementName != ''" action="browseElementAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="eventDetails" label="Event Details" action="eventDetailsAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="acknowledge" label="Acknowledge" visible="params.data.acknowledged != 'true'" action="acknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="unacknowledge" label="Unacknowledge" visible="params.data.acknowledged == 'true'" action="unacknowledgeAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="takeOwnership" label="Take Ownership" action="takeOwnAction"></rui:sgMenuItem>
        <rui:sgMenuItem id="releaseOwnership" label="Release Ownership" action="releaseOwnAction"></rui:sgMenuItem>
    </rui:sgMenuItems>
    <rui:sgImages>
        <rui:sgImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/red.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/orange.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/blue.png"></rui:sgImage>
        <rui:sgImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/green.png"></rui:sgImage>
    </rui:sgImages>
    <rui:sgColumns>
        <rui:sgColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="owner" colLabel="Owner" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="elementName" colLabel="Element Name" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="classDisplayName" colLabel="Class" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="instanceDisplayName" colLabel="Name" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="eventName" colLabel="Event" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="sourceDomainName" colLabel="Source" width="100"></rui:sgColumn>
        <rui:sgColumn attributeName="occurrenceCount" colLabel="Count" width="50"></rui:sgColumn>
        <rui:sgColumn attributeName="lastNotifiedAt" colLabel="Last Notify" width="120"></rui:sgColumn>
        <rui:sgColumn attributeName="lastChangedAt" colLabel="Last Change" width="120"></rui:sgColumn>
    </rui:sgColumns>
</rui:searchGrid>
<rui:html id="objectDetailsmenuHtml" iframe="false"></rui:html>
<rui:popupWindow componentId="objectDetailsmenuHtml" width="850" height="700" x="85" y="50"></rui:popupWindow>
<rui:html id="eventDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="eventDetails" width="850" height="500"></rui:popupWindow>
<rui:action id="eventDetailsAction" type="function" function="show" componentId="eventDetails">
    <rui:functionArg>'getEventDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.name</rui:functionArg>
</rui:action>
<rui:action id="browseInstanceAction" type="function" function="show" componentId="objectDetailsmenuHtml">
    <rui:functionArg>'getObjectDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.className + ' ' + params.data.instanceName</rui:functionArg>
</rui:action>
<rui:action id="browseElementAction" type="function" function="show" componentId="objectDetailsmenuHtml">
    <rui:functionArg>'getObjectDetails.gsp?name=' + params.data.name</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.elementClassName + ' ' + params.data.elementName</rui:functionArg>
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
    <script type="text/javascript">
        var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
        var topologyTree = YAHOO.rapidjs.Components['topologyTree'];
        var summaryChart = YAHOO.rapidjs.Components['summaryChart'];
        eventsGrid.renderCellFunction = function(key, value, data, el){
            if(key == "lastNotifiedAt" || key == "lastChangedAt"){
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