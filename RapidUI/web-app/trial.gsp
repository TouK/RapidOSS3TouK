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
    <rui:javascript dir="yui/utilities" file="utilities.js"></rui:javascript>
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
<div id="mainDiv">
<div class="hd">Save query</div>
<div class="bd">
<form method="POST" action="javascript://nothing">
    <table width="100%">
    <tr><td width="50%"><label for="group">Group Name:</label></td><td width="50%"><input type="textbox" name="group" /></td></tr>
    <tr><td width="50%"><label for="filter">Filter Name:</label></td><td width="50%"><input type="textbox" name="query" /></td></tr>
    <tr><td width="50%"><label for="queryName">Query Name:</label></td><td width="50%"><label for="query" width="100%">Example Query</label></td></tr>
    </table>
</form>
</div>
    </div>
<div><button onclick="dialog.show(YAHOO.rapidjs.component.Form.EDIT_MODE)"> Edit</button></div>
<div><button onclick="dialog.show(YAHOO.rapidjs.component.Form.CREATE_MODE)"> Create</button></div>
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

    var config = {width:"30em",editUrl:"a3.xml", createUrl:"a3.xml", saveUrl:"", updateUrl:"",rootTag:"Filter"
    };
    var dialog = new YAHOO.rapidjs.component.Form(document.getElementById("mainDiv"), config);
    //dialog.show(1);



</script>

</body>
</html>