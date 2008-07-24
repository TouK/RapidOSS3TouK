<html>
<head>
    <rui:javascript dir="yui/layout" file="layout-beta-min.js"/>
    <rui:javascript dir="ext" file="ext.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/form" file="Form.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/search" file="SearchList.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/tree" file="Tree.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/action" file="Action.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/html" file="Html.js"></rui:javascript>

    <rui:stylesheet dir="js/yui/treeview" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
</head>
<body class=" yui-skin-sam">
<div id="filterDialog">
    <div class="hd">Save query</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="group" /></td></tr>
        <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" /></td></tr>
        </table>
        <input name="query" type="hidden"/>
    </form>

    </div>
</div>
<div id="left">
    <div id="treeDiv1"></div>
</div>
<div id="right">
    <div id="searchDiv"></div>
</div>
  <style>
    .dragging, .drag-hint {
      border: 1px solid gray;
      background-color: blue;
      color: white;
      opacity: 0.76;
      filter: "alpha(opacity=76)";
    }
    </style>

<script type="text/javascript">
    function searchListPropertyMenuConditionFunction(key, value, data)
    {
        return key == "severity" || key == "lastoccurrence" || key=="statechange"
    }

    function searchListHeaderMenuConditionFunctionAcknowledge(data)
    {
        return data.getAttribute("acknowledged") == 0;
    }

    function searchListHeaderMenuConditionFunctionDeacknowledge(data)
    {
        return data.getAttribute("acknowledged") == 1;
    }

    var conf =  {width:400, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf);
    html.hide();
    var actionConfig = {url:'searchQuery/delete.xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);

    var actionGroupConfig = {url:'searchQueryGroup/delete.xml'}
    var deleteQueryGroupAction = new YAHOO.rapidjs.component.action.RequestAction(actionGroupConfig);


    var searchConfig = {
        id:'searchList',
        url:'search',
        searchQueryParamName:'query',
        rootTag:'Objects',
        contentPath:'Object',
        indexAtt:'id',
        totalCountAttribute:'total',
        offsetAttribute:'offset',
        sortOrderAttribute:'sortOrder',
        titleAttribute:"serverserial",
        lineSize:3,
        fields:['node', 'owneruid', 'ownergid', 'acknowledged','agent','manager', 'summary','tally','severity','suppressescl','tasklist','lastoccurrence','statechange','alertgroup','alertkey'],
        menuItems:{
            item1 : { id : 'eventDetails', label : 'Event Details' },
            item2 : { id : 'acknowledge', label : 'Acknowledge', condition: searchListHeaderMenuConditionFunctionAcknowledge },
            item3 : { id : 'deacknowledge', label : 'Deacknowledge', condition: searchListHeaderMenuConditionFunctionDeacknowledge },
            item4 : { id : 'takeOwnership', label : 'Take Ownership' },
            item5 : { id : 'severity', label : 'Change Severity', submenuItems : {
                            subItem1 : { id: 'critical', label : 'Critical' },
                            subItem2 : { id: 'major', label : 'Major' },
                            subItem3 : { id: 'minor', label : 'Minor' },
                            subItem4 : { id: 'warning', label : 'Warning' },
                            subItem5 : { id: 'indeterminate', label : 'Indeterminate' },
                            subItem6 : { id: 'clear', label : 'Clear' }
                        }
                    }
        } ,
        propertyMenuItems:{
            item1 : { id : 'sortAsc', label : 'Sort asc' },
            item2 : { id : 'sortDesc', label : 'Sort desc' },
            item3 : { id : 'greaterThan', label : 'Greater than',  condition: searchListPropertyMenuConditionFunction},
            item4 : { id : 'lessThan', label : 'Less than' , condition: searchListPropertyMenuConditionFunction}
        } ,
        saveQueryFunction: function(query){
                    dialog.dialog.form.query.value = query;
                    dialog.show(dialog.CREATE_MODE);
        }
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);


    var acknowledgeConfig = { url: 'script/run/acknowledge' };
	var acknowledgeAction = new YAHOO.rapidjs.component.action.RequestAction(acknowledgeConfig);
	acknowledgeAction.events.success.subscribe(searchList.refreshAndPoll, searchList, true);
	acknowledgeAction.events.failure.subscribe(function(){alert("Error occurred");}, this, true);

    var deacknowledgeConfig = { url: 'script/run/acknowledge' };
	var deacknowledgeAction = new YAHOO.rapidjs.component.action.RequestAction(deacknowledgeConfig);
	deacknowledgeAction.events.success.subscribe(searchList.refreshAndPoll, searchList, true);
	deacknowledgeAction.events.failure.subscribe(function(){alert("Error occurred");}, this, true);

    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id) {
        if( id == "eventDetails"){
            var type = xmlData.getAttribute("alias");
            var eventId = xmlData.getAttribute("id");
            var url = "getDetails.gsp?type="+type + "&id="+eventId;
            html.show(url);

        }
        else if( id == 'acknowledge' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
            acknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"true"});
        }
        else if( id == 'deacknowledge' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
    		deacknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"false"});
        }
    }, this, true);

    searchList.events["cellMenuClick"].subscribe(function(key, value, xmlData, id) {
        if( id == "sortAsc"){
            searchList.setSortDirection(key, true);
        }
        else if( id == "sortDesc"){
            searchList.setSortDirection(key, false);
        }
        else if( id == "greaterThan"){
            searchList.appendToQuery(key+":{"+value+" TO *}");
        }
        else if( id == "lessThan"){
            searchList.appendToQuery(key+":{* TO "+value+"}");
        }
    }, this, true);


    var treeDisplayAttribute = "name";
    function treeNodesDeleteConditionFunction(data)
    {
        return data.getAttribute(treeDisplayAttribute) != "Default";
    }

    var config = {  id:"filterTree", "url":"script/run/queryList", "rootTag":"Filters", "nodeId":"id", "nodeTag":"Filter",
                    "displayAttribute":treeDisplayAttribute, "nodeTypeAttribute":"nodeType", "queryAttribute":"query",
                    menuItems:{
                        Delete : { id: 'delete', label : 'Delete',  condition : treeNodesDeleteConditionFunction }
                    }
    };
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();
    deleteQueryAction.events.success.subscribe(tree.poll, tree, true);
    deleteQueryAction.events.failure.subscribe(function(){alert("Error occurred");}, this, true);

    deleteQueryGroupAction.events.success.subscribe(tree.poll, tree, true);
    deleteQueryGroupAction.events.failure.subscribe(function(){alert("Error occurred");}, this, true);

    tree.events["treeClick"].subscribe(function(data) {
            if(  data.getAttribute("nodeType") == "filter")
            {
               searchList.setQuery( data.getAttribute("query") );
            }
    }, this, true);

    tree.events["treeMenuItemClick"].subscribe(function(id, data) {
            if( id == "delete")
            {
                if( data.getAttribute("nodeType") == "filter" )
                    deleteQueryAction.execute({id:data.getAttribute("id")});
                else if( data.getAttribute("nodeType") == "group" )
                    deleteQueryGroupAction.execute({id:data.getAttribute("id")});
            }
    }, this, true);

    var filterDefinitionDialogConfig = {width:"30em", saveUrl:"searchQuery/save.xml", updateUrl:"searchQuery/update.xml",rootTag:"Filter", successfulyExecuted: function () { tree.poll() }};
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'center', header: 'Netcool Events', body: 'right', resize: false, gutter: '1px' },
                { position: 'left', header: 'Saved Queries', width: 200, gutter: '1px', resize: true, body: 'left', collapse: false, close: false, collapseSize: 50, scroll: true, animate: true }
            ]
        });

        layout.render();
    searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
    layout.on('resize', function() {
            searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });

    })

</script>

</body>
</html>