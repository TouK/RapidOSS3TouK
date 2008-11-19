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
    <rui:iMenus>
        <rui:iMenu id="browse" label="Browse" location="row" actionType="htmlDialog" width="850" height="700" x="85" y="50"
                url="'getObjectDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.className + ' ' + params.data.name"></rui:iMenu>
        <rui:iMenu id="showMap" label="Show Map" location="row" actionType="link" url="'redirectToMap.gsp?name='+params.data.name"></rui:iMenu>
    </rui:iMenus>
    <rui:iDefaultMenus>
        <rui:iDefaultMenu id="sortAsc" label="Sort Asc"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="sortDesc" label="Sort desc"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="except" label="Except"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="greaterThan" label="Greater Than"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="lessThan" label="Less Than"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:iDefaultMenu>
        <rui:iDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:iDefaultMenu>
    </rui:iDefaultMenus>
    <rui:iSearchResults>

        <rui:iSearchResult alias="RsComputerSystem" properties="${computerSystemFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="RsLink" properties="${linkFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
    </rui:iSearchResults>
</rui:inventory>
</body>
</html>
