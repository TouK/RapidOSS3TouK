<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>
<%
    def defaultFields = ["className", "name", "description", "displayName", "isManaged"];
    def computerSystemFields = ["className", "name", "vendor", "model", "managementServer", "location", "snmpAddress"];
    def linkFields = ["className", "name", "a_ComputerSystemName", "a_Name", "z_ComputerSystemName","z_Name"];
    def emphasized = ["className", "name"]
%>

<rui:inventory queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${defaultFields}">
    <rui:inMenus>
        <rui:inMenu id="browse" label="Browse" location="row" actionType="htmlDialog" width="850" height="700" x="85" y="50"
                url="'getObjectDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.className + ' ' + params.data.name"></rui:inMenu>
        <rui:inMenu id="showMap" label="Show Map" location="row" actionType="link" url="'redirectToMap.gsp?name='+params.data.name"></rui:inMenu>
    </rui:inMenus>
    <rui:inDefaultMenus>
        <rui:inDefaultMenu id="sortAsc" label="Sort Asc"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="sortDesc" label="Sort Desc"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="except" label="Except"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="greaterThan" label="Greater Than"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="lessThan" label="Less Than"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:inDefaultMenu>
        <rui:inDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:inDefaultMenu>
    </rui:inDefaultMenus>
    <rui:inSearchResults>

        <rui:inSearchResult alias="RsComputerSystem" properties="${computerSystemFields}" emphasizeds="${emphasized}"></rui:inSearchResult>
        <rui:inSearchResult alias="RsLink" properties="${linkFields}" emphasizeds="${emphasized}"></rui:inSearchResult>
    </rui:inSearchResults>
</rui:inventory>
</body>
</html>
