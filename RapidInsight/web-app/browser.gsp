<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Jan 5, 2009
  Time: 10:40:41 AM
--%>


<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>
<rui:treeGrid id="classTree" url="rsBrowser/classes?format=xml" rootTag="Classes" pollingInterval="0"
        keyAttribute="name" contentPath="Class" title="Classes" onNodeClick="setQueryAction">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Class Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
</rui:treeGrid>
<rui:searchList id="searchList" url="rsBrowser/searchWithQuery?format=xml" rootTag="Objects" contentPath="Object" keyAttribute="id"
        lineSize="3" title="Objects" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
        pollingInterval="0" defaultFields="${['id', 'name']}">
    <rui:slMenuItems>
        <rui:slMenuItem id="browse" label="Browse" action="browseAction"></rui:slMenuItem>
    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
        <rui:slMenuItem id="sortAsc" label="Sort Asc" action="sortAscAction"></rui:slMenuItem>
        <rui:slMenuItem id="sortDesc" label="Sort Desc" action="sortDescAction"></rui:slMenuItem>
        <rui:slMenuItem id="greaterThan" label="Greater than" action="greaterThanAction"
                visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="lessThan" label="Less than" action="lessThanAction"
                visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="greaterThanOrEqualTo" label="Greater than or equal to" action="greaterThanEqualAction" visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="lessThanOrEqualTo" label="Less than or equal to" action="lessThanEqualAction" visible="YAHOO.lang.isNumber(parseInt(params.value))"></rui:slMenuItem>
        <rui:slMenuItem id="except" label="Except" action="exceptAction"></rui:slMenuItem>
    </rui:slPropertyMenuItems>
</rui:searchList>
<rui:html id="objectDetails" iframe="false"></rui:html>
<rui:popupWindow componentId="objectDetails" width="850" height="500"></rui:popupWindow>

<rui:action id="browseAction" type="function" componentId="objectDetails" function="show">
   <rui:functionArg>createURL('browserObjectDetails.gsp', {id:params.data.id, domain:params.data.rsAlias})</rui:functionArg>
   <rui:functionArg>'Details of ' + params.data.rsAlias + ' ' + params.data.id</rui:functionArg>
</rui:action>
<rui:action id="greaterThanAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':{' + params.value + ' TO *}'</rui:functionArg>
</rui:action>
<rui:action id="lessThanAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':{* TO ' + params.value + '}'</rui:functionArg>
</rui:action>
<rui:action id="greaterThanEqualAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':[' + params.value + ' TO *]'</rui:functionArg>
</rui:action>
<rui:action id="lessThanEqualAction" type="function" componentId="searchList" function="appendToQuery">
    <rui:functionArg>params.key + ':[* TO ' + params.value + ']'</rui:functionArg>
</rui:action>
<rui:action id="sortAscAction" type="function" componentId="searchList" function="sort">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>'asc'</rui:functionArg>
</rui:action>
<rui:action id="sortDescAction" type="function" componentId="searchList" function="sort">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>'desc'</rui:functionArg>
</rui:action>
<rui:action id="exceptAction" type="function" componentId="searchList" function="appendExceptQuery">
    <rui:functionArg>params.key</rui:functionArg>
    <rui:functionArg>params.value</rui:functionArg>
</rui:action>
<rui:action id="setQueryAction" type="function" componentId="searchList" function="setQuery" condition="params.data.name != 'System' && params.data.name != 'Application'">
    <rui:functionArg>''</rui:functionArg>
    <rui:functionArg>'id'</rui:functionArg>
    <rui:functionArg>'asc'</rui:functionArg>
    <rui:functionArg>{domain:params.data.logicalName}</rui:functionArg>
</rui:action>
<script type="text/javascript">
    var classTree = YAHOO.rapidjs.Components["classTree"];
    classTree.poll();
    var searchList = YAHOO.rapidjs.Components["searchList"];
    YAHOO.util.Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: searchList.container.id, resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: classTree.container.id, scroll: false}
            ]
        });

        layout.render();
        var leftUnit = layout.getUnitByPosition('left');
        var centerUnit = layout.getUnitByPosition('center');
        classTree.resize(leftUnit.getSizes().body.w, leftUnit.getSizes().body.h);
        layout.on('resize', function() {
            classTree.resize(leftUnit.getSizes().body.w, leftUnit.getSizes().body.h);
        });
        searchList.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        layout.on('resize', function() {
            searchList.resize(centerUnit.getSizes().body.w, centerUnit.getSizes().body.h);
        });
        window.layout = layout;
    })
</script>
</body>
</html>
