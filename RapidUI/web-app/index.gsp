<html>
<head>
    <rui:javascript dir="yui/yahoo" file="yahoo-min.js"></rui:javascript>
    <rui:javascript dir="yui/event" file="event-min.js"></rui:javascript>
    <rui:javascript dir="yui/datasource" file="datasource-beta-min.js"></rui:javascript>
    <rui:javascript dir="yui/connection" file="connection-min.js"></rui:javascript>
    <rui:javascript dir="yui/utilities" file="utilities.js"></rui:javascript>
    <rui:javascript dir="yui/container" file="container_core-min.js"></rui:javascript>
    <rui:javascript dir="yui/treeview" file="treeview-min.js"></rui:javascript>
    <rui:javascript dir="yui/resize" file="resize-beta-min.js"></rui:javascript>
    <rui:javascript dir="yui/layout" file="layout-beta-min.js"></rui:javascript>
    <rui:javascript dir="yui/menu" file="menu-min.js"></rui:javascript>
    <rui:javascript dir="yui/dom" file="dom-min.js"></rui:javascript>
    <rui:javascript dir="yui/button" file="button-min.js"></rui:javascript>
    <rui:javascript dir="yui/dragdrop" file="dragdrop-min.js"></rui:javascript>   
    <rui:javascript dir="yui/container" file="container-min.js"></rui:javascript>


    <rui:javascript dir="ext" file="yutil.js"></rui:javascript>
    <rui:javascript dir="ext" file="Element.js"></rui:javascript>
    <rui:javascript dir="ext" file="DomHelper.js"></rui:javascript>
    <rui:javascript dir="ext" file="CSS.js"></rui:javascript>

    <rui:javascript dir="rapidjs" file="rapidjs.js"></rui:javascript>
    <rui:javascript dir="rapidjs" includeType="recursive"></rui:javascript>
    <rui:javascript dir="rapidjs/component/form" file="Form.js"></rui:javascript>

    <rui:stylesheet dir="js/yui/treeview" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="yui-ext.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="common.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="layout.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="searchlist.css"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>
  
    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
</head>
<body class=" yui-skin-sam">
<div id="filterDialog">
    <div class="hd">Save query</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label for="group">Group Name:</label></td><td width="50%"><input type="textbox" name="group" /></td></tr>
        <tr><td width="50%"><label for="filter">Filter Name:</label></td><td width="50%"><input type="textbox" name="name" /></td></tr>
        <tr><td width="50%"><label for="queryName">Query Name:</label></td><td width="50%"><label for="query" width="100%">Example Query</label></td></tr>
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


    var searchConfig = {
        id:'searchList',
        url:'res.xml',
        searchQueryParamName:'query',
        rootTag:'Results',
        contentPath:'Result',
        indexAtt:'id',
        totalCountAttribute:'Total',
        offsetAttribute:'Offset',
        sortOrderAttribute:'sortOrder',
        fields:['id', 'name', 'creationClassName', 'vendor', 'description', 'location'],
        menuItems:{ item1 : { url: "url1" }, item2 : { url: "url2", condition : function(data) {return data == "3001"} }, item3 : { url: "url3" } } ,
        menuItemUrlParamName: 'id',
        saveQueryFunction: function(query){
                    dialog.dialog.form.query.value = query;
                    dialog.show(dialog.CREATE_MODE);
        }
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);




    var config = {  id:"filterTree","pollingInterval":1, "url":"a2.xml", "rootTag":"Filters", "nodeId":"id", "nodeTag":"Filter",
                    "displayAttribute":"name", "nodeTypeAttribute":"nodeType", "queryAttribute":"query"
    };
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();

     tree.events["treenodeclick"].subscribe(function(nodeType, query) {
            if( nodeType == "group")
                alert( "Look kamil this is group!" );
            else if(  nodeType == "filter")
            {
               searchList.setQuery( query );
            }
    }, this, true);

    var filterDefinitionDialogConfig = {width:"30em",editUrl:"a3.xml", saveUrl:"a3.xml", updateUrl:"",rootTag:"Filter", successfulyExecuted: function () { tree.poll() }};
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        console.log("rendered layout");
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'center', header: 'Netcool Events', body: 'right', resize: false, gutter: '1px' },
                { position: 'left', header: 'Filters', width: 200, gutter: '1px', resize: true, body: 'left', collapse: false, close: false, collapseSize: 50, scroll: true, animate: true }
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