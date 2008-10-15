<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<script>
     function formatLastChangedAt(key, value, data, el){
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
        <rui:ntMenu id="eventDetails" label="Event Details" actionType="htmlDialog" width="850" height="500"
                url="'getEventDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.name"></rui:ntMenu>
        <rui:ntMenu id="acknowledge" label="Acknowledge" script="acknowledge" visible="params.data.acknowledged != 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'true']}"></rui:ntMenu>
        <rui:ntMenu id="unacknowledge" label="Unacknowledge" script="acknowledge" visible="params.data.acknowledged == 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'false']}"></rui:ntMenu>
        <rui:ntMenu id="takeOwnership" label="Take Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'true']}"></rui:ntMenu>
        <rui:ntMenu id="releaseOwnership" label="Release Ownership" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'false']}"></rui:ntMenu>
    </rui:ntMenus>
    <rui:ntColumns>
        <rui:ntColumn attributeName="className" colLabel="Class Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="instanceName" colLabel="Instance Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="eventName" colLabel="Event Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="sourceDomainName" colLabel="Source Domain Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="acknowledged" colLabel="Acknowledged" width="50"></rui:ntColumn>
        <rui:ntColumn attributeName="owner" colLabel="Owner" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="lastChangedAt" colLabel="Last Changed" width="80"></rui:ntColumn>
        <rui:ntColumn attributeName="elementClassName" colLabel="Element Class Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="elementName" colLabel="Element Name" width="100"></rui:ntColumn>
        <rui:ntColumn attributeName="isRoot" colLabel="Is Root" width="50"></rui:ntColumn>
        <rui:ntColumn attributeName="severity" colLabel="Severity" width="50" sortBy="true"></rui:ntColumn>
    </rui:ntColumns>
    <rui:ntConversions>
        <rui:ntConversion property="lastChangedAt" type="function" function="formatLastChangedAt"></rui:ntConversion>
        %{--<rui:nsConversion property="severity" type="mapping" mapping="['1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:nsConversion>--}%
        %{--<rui:nsConversion property="lastClearedAt" type="date" format="d M H:i:s"></rui:nsConversion>--}%
    </rui:ntConversions>
</rui:notifications>

</body>
</html>
