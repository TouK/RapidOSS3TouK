<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>
<%
    def defaultFields = ["className", "name", "description", "displayName", "isManaged"];
    def computerSystemFields = ["className", "name", "vendor", "model", "managementServer", "location", "snmpAddress"];
    def computerSystemComponentFields = ['className', 'name', 'description', 'displayName', 'isManaged', 'computerSystemName', 'tag']
    def networkAdapterFields = ["className", "name", "computerSystemName", "adminStatus", "operStatus", "description", "type", "mode", "isManaged", "maxSpeed", "interfaceAlias"];
    def ipFields = ["className", "name", "computerSystemName", "address", "ipStatus", "interfaceOperStatus", "netmask", "networkNumber", "responsive", "status"];
    def cardFields = ["className", "name", "computerSystemName", "standbyStatus", "status"];
    def linkFields = ["className", "name", "a_ComputerSystemName", "a_Name", "a_AdminStatus", "a_OperStatus", "connectedSystemsUnresponsive", "z_ComputerSystemName",
            "z_Name", "z_AdminStatus", "z_OperStatus"];
    def ipNetworkFields = ["className", "name", "netmask", "networkNumber"];
    def hsrGroupFields = ["className", "name", "activeInterfaceName", "activeSystemName", "numberOfFaultyComponents", "virtualIP", "virtualMAC"];
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

        <rui:iSearchResult alias="SmartsComputerSystem" properties="${computerSystemFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsComputerSystemComponent" properties="${computerSystemComponentFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsPort" properties="${networkAdapterFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsInterface" properties="${networkAdapterFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsIp" properties="${ipFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsCard" properties="${cardFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsLink" properties="${linkFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsIpNetwork" properties="${ipNetworkFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
        <rui:iSearchResult alias="SmartsHsrGroup" properties="${hsrGroupFields}" emphasizeds="${emphasized}"></rui:iSearchResult>
    </rui:iSearchResults>
</rui:inventory>
</body>
</html>
