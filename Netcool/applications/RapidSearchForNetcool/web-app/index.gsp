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
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
    <jsec:isNotLoggedIn>
	  <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
	</jsec:isNotLoggedIn>
    <style>
		.r-filterTree-groupAdd{
			background-image: url( images/rapidjs/component/tools/filter_group.png);
		}
		.r-filterTree-queryAdd{
			background-image: url( images/rapidjs/component/tools/filteradd.png);
		}
        #errors{
            padding: 10px;
            font-style: italic;
            background:yellow;
            width:600px;
            position:absolute;
            top:-100;
            background:#FFF3F3 none repeat scroll 0%;
            border:1px solid red;
            color:#CC0000;
        }
    </style>
</head>
<body class=" yui-skin-sam">
<div id="filterDialog">
    <div class="hd">Save query</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select type="textbox" name="group" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
        </table>
        <input type="hidden" name="id">
    </form>

    </div>
</div>
<div id="filterGroup">
    <div class="hd">Save group</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" /></td></tr>
        </table>
        <input type="hidden" name="id">
    </form>

    </div>
</div>
<div id="left">
    <div id="treeDiv1"></div>
</div>
<div id="top">
    IFOUNTAIN
</div>
<div id="right">
    <div id="searchDiv"></div>
</div>
<div id="errors">
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

    function searchListHeaderMenuConditionFunctionAddTaskToList(data)
    {
        return data.getAttribute("tasklist") == 0;
    }

    function searchListHeaderMenuConditionFunctionRemoveTaskFromList(data)
    {
        return data.getAttribute("tasklist") == 1;
    }
    function searchListHeaderMenuConditionFunctionSeveritySubmenu( data, label )
    {
        var severity = data.getAttribute("severity");
        return !(severity == label);
    }
    var errorAnim = null;
    var errorFadeAnim = null;
    YAHOO.rapidjs.ErrorManager.errorOccurredEvent.subscribe(function(obj, errors){
        if(errorAnim){
            errorAnim.stop();
        }
        if(errorFadeAnim){
            errorFadeAnim.stop();
        }
        var errorsElement = YAHOO.ext.Element.get('errors');
        var xCoord = (YAHOO.util.Dom.getViewportWidth()/2) - (errorsElement.getWidth()/2);
        YAHOO.util.Dom.setStyle(errorsElement.dom, 'opacity', '1');
        errorsElement.dom.innerHTML = errors.join("<br>");
        errorsElement.setHeight(window.layout.getUnitByPosition('top').get('height'));
        errorAnim = new YAHOO.util.Motion('errors', {points:{ from:[xCoord,-100], to: [xCoord, 1] }},1, YAHOO.util.Easing.elasticBoth);
        errorFadeAnim = new YAHOO.util.Anim("errors", {
	        opacity: {to: 0}
        }, 5);
        errorAnim.animate();
        errorAnim.onComplete.subscribe(function() {
	        errorFadeAnim.animate();
	    });
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(){
        if(errorAnim){
            errorAnim.stop();
        }
        if(errorFadeAnim){
            errorFadeAnim.stop();
        }
        var errorsElement = YAHOO.ext.Element.get('errors');
        var xCoord = (YAHOO.util.Dom.getViewportWidth()/2) - (errorsElement.getWidth()/2);
        YAHOO.util.Dom.setStyle(errorsElement.dom, 'opacity', '1');
        errorsElement.dom.innerHTML = 'Server is not available.';
        errorsElement.setHeight(window.layout.getUnitByPosition('top').get('height'));
        errorAnim = new YAHOO.util.Motion('errors', {points:{ from:[xCoord,-100], to: [xCoord, 1] }},1, YAHOO.util.Easing.elasticBoth);
        errorAnim.animate();
    }, this, true);
    YAHOO.rapidjs.ErrorManager.serverUpEvent.subscribe(function(){
        YAHOO.util.Dom.setStyle(YAHOO.ext.Element.get('errors').dom, 'opacity', '0');
    }, this, true);


    var conf = {width:400, height:400, iframe:false};
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
        keyAttribute:'id',
        totalCountAttribute:'total',
        offsetAttribute:'offset',
        sortOrderAttribute:'sortOrder',
        titleAttribute:"serverserial",
        lineSize:3,
        title:'Netcool Events',
        fields:['node', 'owneruid', 'ownergid', 'acknowledged','agent','manager', 'summary','tally','severity','suppressescl','tasklist','lastoccurrence','statechange','alertgroup','alertkey'],
        menuItems:{
            item1 : { id : 'eventDetails', label : 'Event Details' },
            item2 : { id : 'acknowledge', label : 'Acknowledge', condition: searchListHeaderMenuConditionFunctionAcknowledge },
            item3 : { id : 'deacknowledge', label : 'Deacknowledge', condition: searchListHeaderMenuConditionFunctionDeacknowledge },
            item4 : { id : 'takeOwnership', label : 'Take Ownership' },
            item5 : { id : 'addTaskToList', label : 'Add Task To List', condition: searchListHeaderMenuConditionFunctionAddTaskToList },
            item6 : { id : 'removeTaskFromList', label : 'Remove Task From List', condition: searchListHeaderMenuConditionFunctionRemoveTaskFromList },
            item7 : { id : 'severity', label : 'Change Severity', submenuItems : {
                            subItem1 : { id: 'critical', label : 'Critical', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu},
                            subItem2 : { id: 'major', label : 'Major', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu},
                            subItem3 : { id: 'minor', label : 'Minor', condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem4 : { id: 'warning', label : 'Warning',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem5 : { id: 'indeterminate', label : 'Indeterminate',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu },
                            subItem6 : { id: 'clear', label : 'Clear',condition: searchListHeaderMenuConditionFunctionSeveritySubmenu }
                        }
                    }
        } ,
        propertyMenuItems:{
            item1 : { id : 'sortAsc', label : 'Sort asc' },
            item2 : { id : 'sortDesc', label : 'Sort desc' },
            item3 : { id : 'greaterThan', label : 'Greater than',  condition: searchListPropertyMenuConditionFunction},
            item4 : { id : 'lessThan', label : 'Less than' , condition: searchListPropertyMenuConditionFunction}
        } ,
        saveQueryFunction: function(query) {
            dialog.dialog.form.query.value = query;
            dialog.show(dialog.CREATE_MODE);
        }
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);


    var acknowledgeConfig = { url: 'script/run/acknowledge' };
	var acknowledgeAction = new YAHOO.rapidjs.component.action.MergeAction(acknowledgeConfig);

	var taskListConfig = { url: 'script/run/taskList' };
	var taskListAction = new YAHOO.rapidjs.component.action.MergeAction(taskListConfig);

	var severityConfig = { url: 'script/run/severity' };
	var severityAction = new YAHOO.rapidjs.component.action.MergeAction(severityConfig);

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
            acknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"true"}, [searchList]);
        }
        else if( id == 'deacknowledge' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
    		acknowledgeAction.execute({servername:serverName, serverserial : serverSerial, acknowledged:"false"}, [searchList]);
        }
        else if( id == 'addTaskToList' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
    		taskListAction.execute({servername:serverName, serverserial : serverSerial, taskList:"true"}, [searchList]);
        }
        else if( id == 'removeTaskFromList' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
    		taskListAction.execute({servername:serverName, serverserial : serverSerial, taskList:"false"}, [searchList]);
        }
        else if( id == 'critical' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:5}, [searchList]);
        }
        else if( id == 'major' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:4}, [searchList]);
        }
        else if( id == 'minor' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:3}, [searchList]);
        }
        else if( id == 'warning' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:2}, [searchList]);
        }
        else if( id == 'indeterminate' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:1}, [searchList]);
        }
        else if( id == 'clear' )
        {
        	var serverName = xmlData.getAttribute("servername");
        	var serverSerial = xmlData.getAttribute("serverserial");
        	severityAction.execute({servername:serverName, serverserial : serverSerial, severity:0}, [searchList]);
        }
    }, this, true);

    searchList.events["cellMenuClick"].subscribe(function(key, value, xmlData, id) {

	        if (id == "sortAsc") {
	            searchList.setSortDirection(key, true);
	        }
	        else if (id == "sortDesc") {
	            searchList.setSortDirection(key, false);
	        }
	        else if (id == "greaterThan") {
	        	if( key == "severity")
 	  			{
	 	  			if( value == 'Critical' )
			           	searchList.appendToQuery("");
			        else if( value == 'Major' )
			        	searchList.appendToQuery("severity: Critical");
			        else if( value == 'Minor' )
			        	searchList.appendToQuery("severity: Critical and Major");
			        else if( value == 'Warning' )
			        	searchList.appendToQuery("severity: Critical OR Major ");
			        else if( value == 'Indeterminate' )
			        	searchList.appendToQuery("severity: Critical OR Major OR Warning");
			        else if( value == 'Clear' )
			        	searchList.appendToQuery("severity: Critical OR Major OR Warning OR Indeterminate");
	 	  		}
 	  			else
	            	searchList.appendToQuery(key + ":{" + value + " TO *}");
	        }
	        else if (id == "lessThan") {
	        	if( key == "severity")
 	  			{
	 	  			if( value == 'Critical' )
			        	searchList.appendToQuery("severity: Major OR Warning OR Indeterminate OR Clear");
			        else if( value == 'Major' )
			        	searchList.appendToQuery("severity: Minor OR Warning OR Indeterminate OR Clear");
			        else if( value == 'Minor' )
			        	searchList.appendToQuery("severity: Warning OR Indeterminate OR Clear");
			        else if( value == 'Warning' )
			        	searchList.appendToQuery("severity: Indeterminate OR Clear ");
			        else if( value == 'Indeterminate' )
			        	searchList.appendToQuery("severity: Clear");
			        else if( value == 'Clear' )
			        	searchList.appendToQuery("");
	 	  		}
	        	else
	            	searchList.appendToQuery(key + ":{* TO " + value + "}");
	        }

    }, this, true);


    var treeDisplayAttribute = "name";
    function treeNodesConditionFunction(data)
    {
        return data.getAttribute(treeDisplayAttribute) != "Default";
    }
    var groupDefinitionDialogConfig = {
        width:"30em",
        saveUrl:"searchQueryGroup/save.xml",
        updateUrl:"searchQueryGroup/update.xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var groupDialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterGroup"), groupDefinitionDialogConfig);

    var config = {  id:"filterTree", "url":"script/run/queryList", "rootTag":"Filters", "nodeId":"id", "nodeTag":"Filter",
        "displayAttribute":treeDisplayAttribute, "nodeTypeAttribute":"nodeType", "queryAttribute":"query", title:'Saved Queries',
        menuItems:{
            Delete : { id: 'delete', label : 'Delete',  condition : treeNodesConditionFunction },
            Update : { id: 'update', label : 'Update',  condition : treeNodesConditionFunction }
        }
    };
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
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

    tree.events["treeClick"].subscribe(function(data) {
        if (data.getAttribute("nodeType") == "filter")
        {
            searchList.setQuery(data.getAttribute("query"));
        }
    }, this, true);

    tree.events["treeMenuItemClick"].subscribe(function(id, data) {
        if (id == "delete")
        {
            if (data.getAttribute("nodeType") == "filter")
                deleteQueryAction.execute({id:data.getAttribute("id")});
            else if (data.getAttribute("nodeType") == "group")
                deleteQueryGroupAction.execute({id:data.getAttribute("id")});
        }
        else if(id == "update"){
            if (data.getAttribute("nodeType") == "filter")
                dialog.show(dialog.EDIT_MODE, {id:data.getAttribute("id")})
            else if(data.getAttribute("nodeType") == "group"){
                groupDialog.dialog.form.name.value = data.getAttribute("name")
                groupDialog.dialog.form.id.value = data.getAttribute("id")
                groupDialog.show(groupDialog.EDIT_MODE)
            }

        }
    }, this, true);

    var filterDefinitionDialogConfig = {
        width:"35em",
        createUrl:"searchQuery/create.xml",
        editUrl:"searchQuery/edit.xml",
        saveUrl:"searchQuery/save.xml",
        updateUrl:"searchQuery/update.xml",
        successfulyExecuted: function () {
            tree.poll()
        }
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: 'right', resize: false, gutter: '1px' },
                { position: 'left', width: 200, resize: true, body: 'left', scroll: true}
            ]
        });
        layout.on('render', function(){
            var header = layout.getUnitByPosition('top').body;
            YAHOO.util.Dom.setStyle(header, 'border', 'none');
            var left = layout.getUnitByPosition('left').body;
            YAHOO.util.Dom.setStyle(left, 'top', '1px');
        });
        layout.render();
        var layoutLeft = layout.getUnitByPosition('left');
        layoutLeft.on('resize', function(){
            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
        });
        searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        window.layout = layout;

    })
</script>

</body>
</html>