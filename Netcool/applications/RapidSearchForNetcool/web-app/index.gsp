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
    var conf =  {width:400, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf);
    html.hide();
    var actionConfig = {url:'searchQuery/delete.xml'}
    var deleteQueryAction = new YAHOO.rapidjs.component.action.RequestAction(actionConfig);
    
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
        fields:['id', 'serverserial'],
        menuItems:{
            item1 : { id : 'eventDetails', label : 'Event Details' }
        } ,
        menuItemUrlParamName: 'id',
        saveQueryFunction: function(query){
                    dialog.dialog.form.query.value = query;
                    dialog.show(dialog.CREATE_MODE);
        }
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);
    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id) {
        if( id == "eventDetails"){
             var type = xmlData.getAttribute("alias");
            var eventId = xmlData.getAttribute("id");
            var url = "getDetails.gsp?type="+type + "&id="+eventId;
            html.show(url);

        }
    }, this, true);



    var config = {  id:"filterTree","pollingInterval":1, "url":"script/run/queryList", "rootTag":"Filters", "nodeId":"id", "nodeTag":"Filter",
                    "displayAttribute":"name", "nodeTypeAttribute":"nodeType", "queryAttribute":"query",
                    menuItems:{
                        Delete : { id: 'delete', label : 'Delete',  condition : function(data) {return data.getAttribute("nodeType") != "group"} }
                    }
    };
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();
    deleteQueryAction.events.success.subscribe(tree.poll, tree, true);
    deleteQueryAction.events.failure.subscribe(function(){alert("Error occurred");}, this, true);

    tree.events["treeClick"].subscribe(function(data) {
            if(  data.getAttribute("nodeType") == "filter")
            {
               searchList.setQuery( data.getAttribute("query") );
            }
    }, this, true);

    tree.events["treeMenuItemClick"].subscribe(function(id, data) {
            if( id == "delete")
            {
                deleteQueryAction.execute({id:data.getAttribute("id")});
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