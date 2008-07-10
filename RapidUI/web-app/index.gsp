<html>
<head>
    <rui:javascript dir="yui/yahoo" file="yahoo-min.js"></rui:javascript>
    <rui:javascript dir="yui/event" file="event-min.js"></rui:javascript>
    <rui:javascript dir="yui/datasource" file="datasource-beta-min.js"></rui:javascript>
    <rui:javascript dir="yui/connection" file="connection-min.js"></rui:javascript>
    <rui:javascript dir="yui/treeview" file="treeview-min.js"></rui:javascript>
    <rui:javascript dir="yui/dom" file="dom-min.js"></rui:javascript>
    <rui:stylesheet dir="js/yui/treeview" includeType="recursive"></rui:stylesheet>
    <rui:javascript dir="rapidjs" file="rapidjs.js"></rui:javascript>
    <rui:javascript dir="rapidjs" includeType="recursive"></rui:javascript>
</head>
<body>
<div id="treeDiv1"></div>
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

    var config = {"pollingInterval":1, "url":"a2.xml", "rootTag":"SalesDatabase", "nodeId":"id", "nodeTag":"Employee"};
    var tree = new YAHOO.rapidjs.component.Tree(document.getElementById("treeDiv1"), config);
    tree.poll();



</script>
<tr>
	<td>
		<textarea id="text1" rows="2" cols="40"></textarea>
			<script type="text/javascript">
			document.getElementById("text1").value = "";

			</script>

	</td>
	<td>
		<textarea id="delete" rows="1" cols="4"></textarea>
			<script type="text/javascript">
			document.getElementById("delete").value = "";

			</script>

	</td>
	<td>

		<button onclick="javascript:remove();">Remove Node</button>
		<button onclick="javascript:removeSelected();">Remove Selected Node</button>
		<button onclick="javascript:reDraw();">ReDraw</button>
	</td>

</tr>
</body>
</html>