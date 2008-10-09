<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>
<rui:treeGrid id="filterTree" url="script/run/queryList?format=xml&type=historicalnotification" rootTag="Filters" keyAttribute="id"
     contentPath="Filter" title="Saved Queries" expanded="true">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="delete" label="Delete" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
        <rui:tgMenuItem id="update" label="Update" visible="data.isPublic != 'true' && !(data.name == 'Default' && data.nodeType == 'group')"></rui:tgMenuItem>
        <rui:tgMenuItem id="copyQuery" label="Copy Query" visible="data.nodeType == 'filter'"></rui:tgMenuItem>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <rui:tgRootImage visible="data.nodeType == 'group'" expanded='images/rapidjs/component/tools/folder_open.gif' collapsed='images/rapidjs/component/tools/folder.gif'></rui:tgRootImage>
        <rui:tgRootImage visible="data.nodeType == 'filter'" expanded='images/rapidjs/component/tools/filter.png' collapsed='images/rapidjs/component/tools/filter.png'></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>

<rui:searchList id="searchList" url="search?format=xml&searchIn=RsHistoricalEvent" queryParameter="query" rootTag="Objects" contentPath="Object"
         keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" lineSize="3" title="Events"
         defaultFields="['name']">
    <rui:slMenuItems>
        <rui:slMenuItem id="eventDetails" label="Event Details"></rui:slMenuItem>
    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
         <rui:slMenuItem id="sortAsc" label="Sort asc"></rui:slMenuItem>
         <rui:slMenuItem id="sortDesc" label="Sort desc"></rui:slMenuItem>
         <rui:slMenuItem id="except" label="Except"></rui:slMenuItem>
         <rui:slMenuItem id="greaterThan" label="Greater than" visible="(key == 'severity' && value != '1') || (key != 'severity' && YAHOO.lang.isNumber(parseInt(value)))"></rui:slMenuItem>
         <rui:slMenuItem id="lessThan" label="Less than" visible="(key == 'severity' && value != '5') || (key != 'severity' && YAHOO.lang.isNumber(parseInt(value)))"></rui:slMenuItem>
         <rui:slMenuItem id="greaterThanOrEqualTo" label="Greater than or equal to" visible="YAHOO.lang.isNumber(parseInt(value))"></rui:slMenuItem>
         <rui:slMenuItem id="lessThanOrEqualTo" label="Less than or equal to" visible="YAHOO.lang.isNumber(parseInt(value))"></rui:slMenuItem>
    </rui:slPropertyMenuItems>
    <rui:slImages>
        <rui:slImage visible="data.severity == '1'" src="images/rapidjs/component/searchlist/red.png"></rui:slImage>
        <rui:slImage visible="data.severity == '2'" src="images/rapidjs/component/searchlist/orange.png"></rui:slImage>
        <rui:slImage visible="data.severity == '3'" src="images/rapidjs/component/searchlist/yellow.png"></rui:slImage>
        <rui:slImage visible="data.severity == '4'" src="images/rapidjs/component/searchlist/blue.png"></rui:slImage>
        <rui:slImage visible="data.severity == '5'" src="images/rapidjs/component/searchlist/green.png"></rui:slImage>
    </rui:slImages>
    <rui:slFields>
        <%
           def smartsEventFields = ["className", "instanceName", "eventName", "sourceDomainName","acknowledged","owner", "lastChangedAt",
                   "elementClassName", "elementName","isRoot", "severity"];
        %>
        <rui:slField exp="data.rsAlias == 'RsSmartsHistoricalNotification'" fields="${smartsEventFields}"></rui:slField>
    </rui:slFields>
</rui:searchList>

<rui:form id="filterDialog" width="35em" createUrl="script/run/createQuery?queryType=historicalnotification"
        editUrl="script/run/editQuery?queryType=historicalnotification" saveUrl="searchQuery/save?format=xml&type=historicalnotification"
        updateUrl="searchQuery/update?format=xml&type=historicalnotification">
    <div>
        <div class="hd">Save query</div>
        <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
            <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Sort Property:</label></td><td width="50%"><select name="sortProperty" style="width:175px"/></td></tr>
            <tr><td width="50%"><label>Sort Order:</label></td><td width="50%">
                <select name="sortOrder" style="width:175px"><option value="asc">asc</option><option value="desc">desc</option></select>
            </td></tr>
            </table>
            <input type="hidden" name="id">
        </form>

        </div>
    </div>
</rui:form>
<rui:form id="filterGroupDialog" width="30em" saveUrl="searchQueryGroup/save?format=xml&type=historicalnotification"
        updateUrl="searchQueryGroup/update?format=xml&type=historicalnotification">
    <div>
    <div class="hd">Save group</div>
    <div class="bd">
        <form method="POST" action="javascript://nothing">
            <table>
                <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
            </table>
            <input type="hidden" name="id">
        </form>
    </div>
</div>
</rui:form>
<rui:html id="eventDetails" width="850" height="500" iframe="false"></rui:html>
<rui:html id="objectDetails" width="850" height="700" iframe="false"></rui:html>

<script type="text/javascript">
    var tree = YAHOO.rapidjs.Components['filterTree'];
    var groupDialog = YAHOO.rapidjs.Components['filterGroupDialog'];
    groupDialog.successful = function(){tree.poll()};
    var dialog = YAHOO.rapidjs.Components['filterDialog'];
     dialog.successful = function(){tree.poll()};
    var eventDetailsDialog = YAHOO.rapidjs.Components['eventDetails'];
    var objectDetailsDialog = YAHOO.rapidjs.Components['objectDetails'];
    eventDetailsDialog.hide();
    objectDetailsDialog.hide();
    var searchList = YAHOO.rapidjs.Components['searchList'];
    searchList.renderCellFunction = function(key, value, data) {
        if (key == "lastChangedAt") {
            if (value == "0" || value == "")
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
                }
            }
        }
        return value;
    }

    var actionConfig = {url:'searchQuery/delete?format=xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'searchQueryGroup/delete?format=xml'}
    var deleteQueryGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);

    searchList.events["saveQueryClicked"].subscribe(function(query) {
        dialog.show(dialog.CREATE_MODE, null, {query:query, sortProperty:searchList.getSortAttribute(), sortOrder: searchList.getSortOrder()});
    });
    searchList.events["cellMenuClick"].subscribe(function(key, value, xmlData, id) {
        if (id == "except") {
            if (searchList.searchInput.value != "")
                searchList.appendToQuery("NOT " + key + ": \"" + value + "\"");
            else
                searchList.appendToQuery(key + ":[0 TO *] NOT " + key + ": \"" + value + "\"");
        }
        else if (id == "sortAsc") {
            searchList.sort(key, 'asc');
        }
        else if (id == "sortDesc") {
            searchList.sort(key, 'desc');
        }
        else if (id == "greaterThan") {
            searchList.appendToQuery(key + ":{" + value + " TO *}");
        }
        else if (id == "greaterThanOrEqualTo") {
            searchList.appendToQuery(key + ":[" + value + " TO *]");
        }
        else if (id == "lessThanOrEqualTo") {
            searchList.appendToQuery(key + ":[* TO " + value + "]");
        }
        else if (id == "lessThan") {
            searchList.appendToQuery(key + ":{* TO " + value + "}");
        }
    }, this, true);


    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
        var notificationName = xmlData.getAttribute("name");
        var notificationId = xmlData.getAttribute("id");

        if (id == "eventDetails") {
            var url = "getHistoricalEventDetails.gsp?id=" + encodeURIComponent(notificationId);
            eventDetailsDialog.show(url, "Details of " + notificationName);
        }
    }, this, true);
    searchList.events["rowDoubleClicked"].subscribe(function(xmlData, event) {
        if (YAHOO.util.Event.getTarget(event).className != 'rcmdb-search-cell-key')
        {
            var notificationName = xmlData.getAttribute("name");
            var notificationId = xmlData.getAttribute("id");
            var url = "getHistoricalEventDetails.gsp?id=" + encodeURIComponent(notificationId);
            eventDetailsDialog.show(url, "Details of " + notificationName);
        }

    }, true, true);


    tree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            groupDialog.show(groupDialog.CREATE_MODE);
        }
    });
    tree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            dialog.show(dialog.CREATE_MODE);
        }
    });
    tree.poll();
    deleteQueryAction.events.success.subscribe(tree.poll, tree, true);

    deleteQueryGroupAction.events.success.subscribe(tree.poll, tree, true);

    tree.events["treeNodeClick"].subscribe(function(data) {
        if (data.getAttribute("nodeType") == "filter")
        {
            searchList.setQuery(data.getAttribute("query"), data.getAttribute('sortProperty'), data.getAttribute('sortOrder'));
        }
    }, this, true);

    tree.events["rowMenuClick"].subscribe(function(data, id, parentId) {
        if (id == "delete")
        {
            if (data.getAttribute("nodeType") == "filter")
                deleteQueryAction.execute({id:data.getAttribute("id")});
            else if (data.getAttribute("nodeType") == "group")
                deleteQueryGroupAction.execute({id:data.getAttribute("id")});
        }
        else if (id == "update") {
            if (data.getAttribute("nodeType") == "filter")
                dialog.show(dialog.EDIT_MODE, {queryId:data.getAttribute("id")})
            else if (data.getAttribute("nodeType") == "group") {
                groupDialog.show(groupDialog.EDIT_MODE)
                groupDialog.dialog.form.name.value = data.getAttribute("name");
                groupDialog.dialog.form.id.value = data.getAttribute("id")
            }
        }
        else if (id == "copyQuery") {
            dialog.show(dialog.CREATE_MODE, null, {name:'', group:data.parentNode().getAttribute('name'),
                query:data.getAttribute('query')});

        }
    }, this, true);


    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', body: searchList.container.id, resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, body: tree.container.id, scroll: false}
            ]
        });
        layout.on('render', function() {
            var topUnit = layout.getUnitByPosition('top');
            YAHOO.util.Dom.setStyle(topUnit.get('wrap'), 'background-color', '#BBD4F6')
            var header = topUnit.body;
            YAHOO.util.Dom.setStyle(header, 'border', 'none');
            var left = layout.getUnitByPosition('left').body;
            YAHOO.util.Dom.setStyle(left, 'top', '1px');
        });
        layout.render();
        var layoutLeft = layout.getUnitByPosition('left');
        layoutLeft.on('resize', function() {
            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
        });

        searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        window.layout = layout;

    })
</script>

</body>
</html>
