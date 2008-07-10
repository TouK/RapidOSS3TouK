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


    <rui:javascript dir="ext" file="yutil.js"></rui:javascript>
    <rui:javascript dir="ext" file="Element.js"></rui:javascript>
    <rui:javascript dir="ext" file="DomHelper.js"></rui:javascript>
    <rui:javascript dir="ext" file="CSS.js"></rui:javascript>

    <rui:javascript dir="rapidjs" file="rapidjs.js"></rui:javascript>
    <rui:javascript dir="rapidjs" includeType="recursive"></rui:javascript>

    <rui:stylesheet dir="js/yui/treeview" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="yui-ext.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="common.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="layout.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" file="searchlist.css"></rui:stylesheet>

</head>
<body class=" yui-skin-sam">

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

    var config = {id:"filterTree","pollingInterval":1, "url":"a2.xml", "rootTag":"SalesDatabase", "nodeId":"id", "nodeTag":"Employee"};
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();
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
        menuItemUrlParamName: 'id'
    }

    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig)

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