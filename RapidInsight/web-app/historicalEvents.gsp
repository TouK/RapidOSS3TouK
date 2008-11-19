<html>
<head>
    <meta name="layout" content="indexLayout"/>
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
<rui:historicalEvents queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${['name']}">
    <rui:heMenus>
        <rui:heMenu id="eventDetails" label="Event Details" location="row" actionType="htmlDialog" width="850" height="500"
                url="'getHistoricalEventDetails.gsp?id=' + params.data.id" title="'Details of ' + params.data.name"></rui:heMenu>
    </rui:heMenus>
     <rui:heDefaultMenus>
        <rui:heDefaultMenu id="sortAsc" label="Sort Asc"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="sortDesc" label="Sort desc"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="except" label="Except"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="greaterThan" label="Greater Than"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="lessThan" label="Less Than"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:heDefaultMenu>
        <rui:heDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:heDefaultMenu>
    </rui:heDefaultMenus>
    <rui:heSearchResults>
        <%
              def eventFields = ["name", "eventName", "node","active", "owner", "acknowledged", "severity", "source",
                    "changedAt", "visibility", "count"];
        %>
        <rui:heSearchResult alias="RsRiHistoricalEvent" properties="${eventFields}" emphasizeds="${['className', 'instanceName', 'eventName']}"></rui:heSearchResult>
    </rui:heSearchResults>
    <rui:heConversions>
        <rui:heConversion property="changedAt" type="function" function="formatChangedAt"></rui:heConversion>
        %{--<rui:heConversion property="severity" type="mapping" mapping="['1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:heConversion>--}%
        %{--<rui:heConversion property="clearedAt" type="date" format="d M H:i:s"></rui:heConversion>--}%
    </rui:heConversions>
</rui:historicalEvents>


</body>
</html>
