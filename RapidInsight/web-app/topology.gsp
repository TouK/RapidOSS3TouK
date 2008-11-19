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

<rui:topologySearch queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${defaultFields}">
    <rui:tsMenus>
        <rui:tsMenu id="browse" label="Browse" location="row" actionType="htmlDialog" width="850" height="700" x="85" y="50"
                url="'getObjectDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.className + ' ' + params.data.name"></rui:tsMenu>
        <rui:tsMenu id="showMap" label="Show Map" location="row" actionType="link" url="'redirectToMap.gsp?name='+params.data.name"></rui:tsMenu>
    </rui:tsMenus>
    <rui:tsDefaultMenus>
        <rui:tsDefaultMenu id="sortAsc" label="Sort Asc"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="sortDesc" label="Sort desc"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="except" label="Except"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="greaterThan" label="Greater Than"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="lessThan" label="Less Than"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="greaterThanOrEqualTo" label="Greater than or equal to"></rui:tsDefaultMenu>
        <rui:tsDefaultMenu id="lessThanOrEqualTo" label="Less than or equal to"></rui:tsDefaultMenu>
    </rui:tsDefaultMenus>
    <rui:tsSearchResults>

        <rui:tsSearchResult alias="RsComputerSystem" properties="${computerSystemFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsLink" properties="${linkFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
    </rui:tsSearchResults>
</rui:topologySearch>
</body>
</html>
