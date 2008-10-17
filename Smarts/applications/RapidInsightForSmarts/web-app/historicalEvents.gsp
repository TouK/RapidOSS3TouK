<html>
<head>
    <meta name="layout" content="indexLayout"/>
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
<rui:historicalNotifications queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${['name']}">
    <rui:hnMenus>
        <rui:hnMenu id="eventDetails" label="Event Details" location="row" actionType="htmlDialog" width="850" height="500"
                url="'getHistoricalEventDetails.gsp?id=' + params.data.id" title="'Details of ' + params.data.name"></rui:hnMenu>
    </rui:hnMenus>
     <rui:hnDefaultMenus>
        <rui:hnDefaultMenu id="sortAsc" label="Sort Asc"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="sortDesc" label="Sort desc"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="except" label="Except"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="greaterThan" label="Greater Than"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="lessThan" label="Less Than"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:hnDefaultMenu>
        <rui:hnDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:hnDefaultMenu>
    </rui:hnDefaultMenus>
    <rui:hnSearchResults>
        <%
             def smartsEventFields = ["className", "instanceName", "eventName", "sourceDomainName","acknowledged","owner", "lastChangedAt",
                   "elementClassName", "elementName","isRoot", "severity"];
        %>
        <rui:hnSearchResult alias="RsSmartsHistoricalNotification" properties="${smartsEventFields}" emphasizeds="${['className', 'instanceName', 'eventName']}"></rui:hnSearchResult>
    </rui:hnSearchResults>
    <rui:hnConversions>
        <rui:hnConversion property="lastChangedAt" type="function" function="formatLastChangedAt"></rui:hnConversion>
        %{--<rui:nsConversion property="severity" type="mapping" mapping="['1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:nsConversion>--}%
        %{--<rui:nsConversion property="lastClearedAt" type="date" format="d M H:i:s"></rui:nsConversion>--}%
    </rui:hnConversions>
</rui:historicalNotifications>


</body>
</html>
