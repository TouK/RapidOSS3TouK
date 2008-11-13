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
<rui:notificationSearch queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${['name']}">
    <rui:nsMenus>
        <rui:nsMenu id="eventDetails" label="Event Details" location="row" actionType="htmlDialog" width="850" height="500"
                url="'getEventDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.name"></rui:nsMenu>

        <rui:nsMenu id="objectDetails" label="Browse" location="property" actionType="htmlDialog" width="850" height="700" visible="params.key == 'instanceName' || params.key == 'elementName'"  x="85" y="50" 
                url="'getObjectDetails.gsp?name=' + params.value" title="'Details of ' + (params.key == 'instanceName'? params.data.className:params.data.elementClassName) +' ' + params.value"></rui:nsMenu>

        <rui:nsMenu id="acknowledge" label="Acknowledge" location="row" script="acknowledge" visible="params.data.acknowledged != 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'true']}"></rui:nsMenu>
        <rui:nsMenu id="unacknowledge" label="Unacknowledge" location="row" script="acknowledge" visible="params.data.acknowledged == 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'false']}"></rui:nsMenu>
        <rui:nsMenu id="takeOwnership" label="Take Ownership" location="row" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'true']}"></rui:nsMenu>
        <rui:nsMenu id="releaseOwnership" label="Release Ownership" location="row" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'false']}"></rui:nsMenu>
    </rui:nsMenus>
    <rui:nsDefaultMenus>
        <rui:nsDefaultMenu id="sortAsc" label="Sort Asc"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="sortDesc" label="Sort desc"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="except" label="Except"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="greaterThan" label="Greater Than"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="lessThan" label="Less Than"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:nsDefaultMenu>
        <rui:nsDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:nsDefaultMenu>
    </rui:nsDefaultMenus>
    <rui:nsSearchResults>
        <%
            def eventFields = ["name", "eventName", "node","active", "owner", "acknowledged", "severity", "source", "lastNotifiedAt",
                    "lastChangedAt", "visibility", "count"];
        %>
        <rui:nsSearchResult alias="RsRiEvent" properties="${eventFields}" emphasizeds="${['name']}"></rui:nsSearchResult>
    </rui:nsSearchResults>
    <rui:nsConversions>
        <rui:nsConversion property="lastChangedAt" type="function" function="formatLastChangedAt"></rui:nsConversion>
        <rui:nsConversion property="lastNotifiedAt" type="function" function="formatLastChangedAt"></rui:nsConversion>
        <rui:nsConversion property="severity" type="mapping" mapping="['0':'Clear', '1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:nsConversion>
        %{--<rui:nsConversion property="lastClearedAt" type="date" format="d M H:i:s"></rui:nsConversion>--}%
    </rui:nsConversions>
</rui:notificationSearch>
</body>
</html>
