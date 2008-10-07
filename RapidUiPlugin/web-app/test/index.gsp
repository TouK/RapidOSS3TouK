<html>
<head>


    <rui:javascript dir="yui/layout" file="layout-beta-min.js"/>
    <rui:javascript dir="rapidjs/component/form" file="Form.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/search" file="SearchList.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/tree" file="Tree.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/action" file="Action.js"></rui:javascript>
    <rui:javascript dir="rapidjs/component/html" file="Html.js"></rui:javascript>

  <rui:stylesheet dir="css/rapidjs" file="yui-ext.css"></rui:stylesheet>
  <rui:stylesheet dir="css/rapidjs" file="common.css"></rui:stylesheet>
  <rui:stylesheet dir="css/rapidjs" file="dialog.css"></rui:stylesheet>
  <rui:stylesheet dir="js/yui/layout/assets/skins/sam" file="layout.css"></rui:stylesheet>
  <rui:stylesheet dir="css/rapidjs" file="searchlist.css"></rui:stylesheet>
  <rui:stylesheet dir="css/rapidjs" file="treenode.css"></rui:stylesheet>

  <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

  <rui:stylesheet dir="js/yui/treeview" includeType="recursive"></rui:stylesheet>
  <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
  <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
  <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
  <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>







  

  
</head>
<body class=" yui-skin-sam">

<div id="filterDialog">
   
    <div class="hd">Save query</div>
    <div class="bd">
    <form method="POST" action="javascript://nothing">
        <table width="100%">
        <tr><td width="50%"><label for="group">Group Name:</label></td><td width="50%"><input type="textbox" name="group" /></td></tr>
        <tr><td width="50%"><label for="query">Query Name:</label></td><td width="50%"><input type="textbox" name="name" /></td></tr>
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
        titleAttribute:"name",
        offsetAttribute:'Offset',
        sortOrderAttribute:'sortOrder',
        fields:['id', 'name', 'creationClassName', 'vendor', 'description', 'location'],
        menuItems:{  } ,
        menuItemUrlParamName: 'id',
        saveQueryFunction: function(query){
                    dialog.dialog.form.query.value = query;
                    dialog.show(dialog.CREATE_MODE);
        },
        rowHeaderAttribute : "vendor"
    }
    var searchList = new YAHOO.rapidjs.component.search.SearchList(document.getElementById("searchDiv"), searchConfig);


    var config = {  id:"filterTree","pollingInterval":1, "url":"a2.xml", "rootTag":"Filters", "keyAttribute":"id", "nodeTag":"Filter",
                    "displayAttribute":"name", "nodeTypeAttribute":"nodeType", "queryAttribute":"query",
                    menuItems:{   }
    };
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();

    var filterDefinitionDialogConfig = {width:"30em",editUrl:"a2.xml", saveUrl:"a2.xml", updateUrl:"",rootTag:"Filter", successfulyExecuted: function () { tree.poll() }};
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("filterDialog"), filterDefinitionDialogConfig);

    searchList.events["rowHeaderMenuClick"].subscribe(function(data, id) {
            alert(data);
           
    }, this,true);

    searchList.events["rowHeaderClick"].subscribe(function(data) {
                alert(data);

        }, this,true);


    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
       // console.log("rendered layout");
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

    var conf =  {width:400, height:400, iframe:false};
    var html = new YAHOO.rapidjs.component.Html(conf)
    html.show("x.gsp");


</script>

</body>
</html>