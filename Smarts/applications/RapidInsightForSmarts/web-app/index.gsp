<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>
<%
    def defaultFields = ["creationClassName", "name", "description", "displayName", "isManaged"];
    def computerSystemFields = ["creationClassName", "name", "vendor", "model", "managementServer", "location", "snmpAddress"];
    def computerSystemComponentFields = ['creationClassName', 'name', 'description', 'displayName', 'isManaged', 'computerSystemName', 'tag']
    def networkAdapterFields = ["creationClassName", "name", "computerSystemName", "adminStatus", "operStatus", "description", "type", "mode", "isManaged", "maxSpeed", "interfaceAlias"];
    def ipFields = ["creationClassName", "name", "computerSystemName", "address", "ipStatus", "interfaceOperStatus", "netmask", "networkNumber", "responsive", "status"];
    def cardFields = ["creationClassName", "name", "computerSystemName", "standbyStatus", "status"];
    def linkFields = ["creationClassName", "name", "a_ComputerSystemName", "a_Name", "a_AdminStatus", "a_OperStatus", "connectedSystemsUnresponsive", "z_ComputerSystemName",
            "z_Name", "z_AdminStatus", "z_OperStatus"];
    def ipNetworkFields = ["creationClassName", "name", "netmask", "networkNumber"];
    def hsrGroupFields = ["creationClassName", "name", "activeInterfaceName", "activeSystemName", "numberOfFaultyComponents", "virtualIP", "virtualMAC"];
    def emphasized = ["creationClassName", "name"]
%>

<rui:topologySearch queriesPollingInterval="0" searchResultsPollingInterval="0" numberOfLines="3" defaultFields="${defaultFields}">
    <rui:tsMenus>
        <rui:tsMenu id="browse" label="Browse" location="row" actionType="htmlDialog" width="850" height="700" x="85" y="50" 
                url="'getObjectDetails.gsp?name=' + params.data.name" title="'Details of ' + params.data.creationClassName + ' ' + params.data.name"></rui:tsMenu>
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
        <rui:tsSearchResult alias="RsComputerSystemComponent" properties="${computerSystemComponentFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsPort" properties="${networkAdapterFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsInterface" properties="${networkAdapterFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsIp" properties="${ipFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsCard" properties="${cardFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsLink" properties="${linkFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsIpNetwork" properties="${ipNetworkFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
        <rui:tsSearchResult alias="RsHsrGroup" properties="${hsrGroupFields}" emphasizeds="${emphasized}"></rui:tsSearchResult>
    </rui:tsSearchResults>
</rui:topologySearch>
</body>
</html>
