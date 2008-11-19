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
<rui:events queriesPollingInterval="0" searchResultsPollingInterval="0">
    <rui:evMenus>
        <rui:evMenu id="objectDetails" actionType="htmlDialog" width="850" height="700" x="85" y="50" label="Browse"
            url="params.data.elementName != '' ? 'getObjectDetails.gsp?name=' + params.data.elementName : 'getObjectDetails.gsp?name=' + params.data.instanceName"
            title="params.data.elementName != '' ? 'Details of ' + params.data.elementClassName + ' ' + params.data.elementName : 'Details of ' + params.data.className + ' ' + params.data.instanceName"></rui:evMenu>
        <rui:evMenu id="showMap" actionType="link" label="Show Map"
            url="params.data.elementName != '' ? 'redirectToMap.gsp?name=' + params.data.elementName : 'redirectToMap.gsp?name=' + params.data.instanceName"></rui:evMenu>
        <rui:evMenu id="eventDetails" label="Event Details" actionType="htmlDialog" width="850" height="500"
                url="'getEventDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.name"></rui:evMenu>
        <rui:evMenu id="acknowledge" label="Acknowledge" script="acknowledge" visible="params.data.acknowledged != 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'true']}"></rui:evMenu>
        <rui:evMenu id="unacknowledge" label="Unacknowledge" script="acknowledge" visible="params.data.acknowledged == 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'false']}"></rui:evMenu>
        <rui:evMenu id="takeOwnership" label="Take Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'true']}"></rui:evMenu>
        <rui:evMenu id="releaseOwnership" label="Release Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'false']}"></rui:evMenu>
    </rui:evMenus>
    <rui:evColumns>
        <rui:evColumn attributeName="acknowledged" colLabel="Ack" width="50"></rui:evColumn>
        <rui:evColumn attributeName="owner" colLabel="Owner" width="100"></rui:evColumn>
        <rui:evColumn attributeName="elementName" colLabel="Element Name" width="100"></rui:evColumn>
        <rui:evColumn attributeName="classDisplayName" colLabel="Class" width="100"></rui:evColumn>
        <rui:evColumn attributeName="instanceDisplayName" colLabel="Name" width="100"></rui:evColumn>
        <rui:evColumn attributeName="eventName" colLabel="Event" width="100"></rui:evColumn>
        <rui:evColumn attributeName="sourceDomainName" colLabel="Source" width="100"></rui:evColumn>
        <rui:evColumn attributeName="count" colLabel="Count" width="50"></rui:evColumn>
        <rui:evColumn attributeName="lastNotifiedAt" colLabel="Last Notify" width="120"></rui:evColumn>
        <rui:evColumn attributeName="changedAt" colLabel="Last Change" width="120"></rui:evColumn>
    </rui:evColumns>
    <rui:evConversions>
        <rui:evConversion property="changedAt" type="function" function="formatChangedAt"></rui:evConversion>
        <rui:evConversion property="lastNotifiedAt" type="function" function="formatChangedAt"></rui:evConversion>
        %{--<rui:evConversion property="severity" type="mapping" mapping="['1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:evConversion>--}%
        %{--<rui:evConversion property="clearedAt" type="date" format="d M H:i:s"></rui:evConversion>--}%
    </rui:evConversions>
</rui:events>

</body>
</html>
