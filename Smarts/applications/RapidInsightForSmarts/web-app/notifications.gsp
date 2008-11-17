<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<script>
     function formatChangedAt(key, value, data, el){
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
            {
                return value;
            }
        }
     }
</script>
<rui:notifications queriesPollingInterval="0" searchResultsPollingInterval="0">
    <rui:ntMenus>
        <rui:ntMenu id="objectDetails" actionType="htmlDialog" width="850" height="700" x="85" y="50" label="Browse"
            url="params.data.elementName != '' ? 'getObjectDetails.gsp?name=' + params.data.elementName : 'getObjectDetails.gsp?name=' + params.data.instanceName"
            title="params.data.elementName != '' ? 'Details of ' + params.data.elementClassName + ' ' + params.data.elementName : 'Details of ' + params.data.className + ' ' + params.data.instanceName"></rui:ntMenu>
        <rui:ntMenu id="showMap" actionType="link" label="Show Map"
            url="params.data.elementName != '' ? 'redirectToMap.gsp?name=' + params.data.elementName : 'redirectToMap.gsp?name=' + params.data.instanceName"></rui:ntMenu>
        <rui:ntMenu id="eventDetails" label="Event Details" actionType="htmlDialog" width="850" height="500"
                url="'getEventDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.name"></rui:ntMenu>
        <rui:ntMenu id="acknowledge" label="Acknowledge" script="acknowledge" visible="params.data.acknowledged != 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'true']}"></rui:ntMenu>
        <rui:ntMenu id="unacknowledge" label="Unacknowledge" script="acknowledge" visible="params.data.acknowledged == 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'false']}"></rui:ntMenu>
        <rui:ntMenu id="takeOwnership" label="Take Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'true']}"></rui:ntMenu>
        <rui:ntMenu id="releaseOwnership" label="Release Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'false']}"></rui:ntMenu>
    </rui:ntMenus>
    <rui:ntColumns>
        <rui:ntColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:ntColumn>
        <rui:ntColumn attributeName="owner" colLabel="Owner" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="elementName" colLabel="Element Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="classDisplayName" colLabel="Class" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="instanceDisplayName" colLabel="Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="eventName" colLabel="Event" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="sourceDomainName" colLabel="Source" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="count" colLabel="Count" width="50"></rui:ntColumn>
        <rui:ntColumn attributeName="lastNotifiedAt" colLabel="Last Notify" width="120"></rui:ntColumn>
        <rui:ntColumn attributeName="changedAt" colLabel="Last Change" width="120"></rui:ntColumn>
    </rui:ntColumns>
    <rui:ntConversions>
        <rui:ntConversion property="changedAt" type="function" function="formatChangedAt"></rui:ntConversion>
        <rui:ntConversion property="lastNotifiedAt" type="function" function="formatChangedAt"></rui:ntConversion>
        %{--<rui:nsConversion property="severity" type="mapping" mapping="['1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:nsConversion>--}%
        %{--<rui:nsConversion property="clearedAt" type="date" format="d M H:i:s"></rui:nsConversion>--}%
    </rui:ntConversions>
</rui:notifications>

</body>
</html>
