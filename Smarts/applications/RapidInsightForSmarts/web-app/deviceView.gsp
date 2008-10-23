<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 23, 2008
  Time: 11:01:47 AM
--%>
<html>
<head>
    <meta name="layout" content="smartsLayout" />
</head>
<body>
<rui:autocomplete id="searchDevice" title="Device Search" url="script/run/autocomplete"
        suggestionAttribute="name" contentPath="Suggestion" onSubmit="submitAction">
</rui:autocomplete>

<rui:searchGrid id="eventsGrid" url="search?format=xml&searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
                keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events List"
                pollingInterval="30" fieldsUrl="script/run/getViewFields?format=xml">
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
<rui:searchGrid id="eventsGrid2" url="search?format=xml&searchIn=RsEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
                keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Events List"
                pollingInterval="30" fieldsUrl="script/run/getViewFields?format=xml">
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
<rui:html id="objectDetails"></rui:html>
<rui:popupWindow componentId="eventsGrid2" width="850" height="700" title="Events"></rui:popupWindow>
<rui:action id="submitAction1" type="function" function="setQueryWithView" componentId="eventsGrid2">
    <rui:functionArg>'instanceName:' + params.query</rui:functionArg>
    <rui:functionArg>'default'</rui:functionArg>
</rui:action>

<rui:action id="submitAction" type="function" function="show" componentId="objectDetails">
    <rui:functionArg>'getObjectDetails.gsp?name=' + params.query</rui:functionArg>
    <rui:functionArg>'Details of ' + params.query</rui:functionArg>
</rui:action>

<script type="text/javascript">
    var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
    var autocomplete = YAHOO.rapidjs.Components['searchDevice'];
    var objectDetails = YAHOO.rapidjs.Components['objectDetails'];
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
                { position: 'center', body: objectDetails.container.id, resize: false, gutter: '1px' },
                { position: 'bottom', body: eventsGrid.container.id, resize: false, gutter: '1px', height:300},
                { position: 'left', width: 250, resize: true, body: autocomplete.container.id, scroll: false}
            ]
        });

        layout.render();
        eventsGrid.resize(layout.getUnitByPosition('bottom').body.offsetWidth, layout.getUnitByPosition('bottom').body.offsetHeight);
        layout.on('resize', function() {
            eventsGrid.resize(layout.getUnitByPosition('bottom').body.offsetWidth, layout.getUnitByPosition('bottom').body.offsetHeight);
        });
        autocomplete.resize(layout.getUnitByPosition('left').body.offsetWidth, layout.getUnitByPosition('left').body.offsetHeight);
        layout.on('resize', function() {
            autocomplete.resize(layout.getUnitByPosition('left').body.offsetWidth, layout.getUnitByPosition('left').body.offsetHeight);
        });
        objectDetails.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            objectDetails.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        window.layout = layout;
    })
    

</script>
</body>
</html>