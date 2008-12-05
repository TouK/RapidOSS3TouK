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
<rui:eventSearch queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${['name']}">
    <rui:esMenus>
        <rui:esMenu id="eventDetails" label="Event Details" location="row" actionType="htmlDialog" width="850" height="500"
                url="'getEventDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.name"></rui:esMenu>
        <rui:esMenu id="acknowledge" label="Acknowledge" location="row" script="acknowledge" visible="params.data.acknowledged != 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'true']}"></rui:esMenu>
        <rui:esMenu id="unacknowledge" label="Unacknowledge" location="row" script="acknowledge" visible="params.data.acknowledged == 'true'" actionType="update" parameters="${[name:'params.data.name', acknowledged:'false']}"></rui:esMenu>
        <rui:esMenu id="takeOwnership" label="Take Ownership" location="row" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'true']}"></rui:esMenu>
        <rui:esMenu id="releaseOwnership" label="Release Ownership" location="row" script="setOwnership" actionType="update" parameters="${[name:'params.data.name', act:'false']}"></rui:esMenu>
    </rui:esMenus>
    <rui:esDefaultMenus>
        <rui:esDefaultMenu id="sortAsc" label="Sort Asc"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="sortDesc" label="Sort Desc"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="except" label="Except"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="greaterThan" label="Greater Than"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="lessThan" label="Less Than"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:esDefaultMenu>
        <rui:esDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:esDefaultMenu>
    </rui:esDefaultMenus>
    <rui:esSearchResults>
        <%
            def eventFields = ["name", "identifier", "node","active", "owner", "acknowledged", "severity", "source",
                    "changedAt", "visibility", "count"];
        %>
        <rui:esSearchResult alias="RsRiEvent" properties="${eventFields}" emphasizeds="${['name']}"></rui:esSearchResult>
    </rui:esSearchResults>
    <rui:esConversions>
        <rui:esConversion property="changedAt" type="function" function="formatChangedAt"></rui:esConversion>
        <rui:esConversion property="severity" type="mapping" mapping="['0':'Clear', '1':'Critical', '2':'Major', '3':'Minor', '4':'Unknown', '5':'Normal']"></rui:esConversion>
        %{--<rui:esConversion property="clearedAt" type="date" format="d M H:i:s"></rui:esConversion>--}%
    </rui:esConversions>
    <rui:esImages>
        <rui:esImage visible="params.data.severity == '5'" src="images/rapidjs/component/searchlist/red.png"></rui:esImage>
        <rui:esImage visible="params.data.severity == '4'" src="images/rapidjs/component/searchlist/orange.png"></rui:esImage>
        <rui:esImage visible="params.data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:esImage>
        <rui:esImage visible="params.data.severity == '2'" src="images/rapidjs/component/searchlist/blue.png"></rui:esImage>
        <rui:esImage visible="params.data.severity == '1'" src="images/rapidjs/component/searchlist/purple.png"></rui:esImage>
        <rui:esImage visible="params.data.severity == '0'" src="images/rapidjs/component/searchlist/green.png"></rui:esImage>
    </rui:esImages>
</rui:eventSearch>

<rui:html id="objectDetailsmenuHtml" iframe="false"></rui:html>
<rui:popupWindow componentId="objectDetailsmenuHtml" width="850" height="700" x="85" y="50" ></rui:popupWindow>


</body>
</html>
