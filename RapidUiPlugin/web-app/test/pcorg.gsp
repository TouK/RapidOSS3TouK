<%--
  Created by IntelliJ IDEA.
  User: deneme
  Date: Oct 13, 2008
  Time: 11:02:09 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    



<link rel="stylesheet" type="text/css" href="../js/yui/fonts/fonts-min.css" />
<script type="text/javascript" src="../js/yui/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../js/yui/json/json-min.js"></script>
<script type="text/javascript" src="../js/yui/element/element-beta-min.js"></script>
<script type="text/javascript" src="../js/yui/datasource/datasource-min.js"></script>
<script type="text/javascript" src="../js/yui/charts/charts-experimental-min.js"></script>




</head>

<body >




<div id="chart">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can install the latest version at the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>

<script type="text/javascript">

	YAHOO.widget.Chart.SWFURL = "../js/yui/charts/assets/charts.swf";

//--- data

	YAHOO.example.publicOpinion =
	[
		{ response: "Summer", count: 2 },
		{ response: "Fall", count: 2 },
		{ response: "Spring", count: 2 },
		{ response: "Winter", count: 2 },
		{ response: "Undecided", count: 2 }
	]

	var opinionData = new YAHOO.util.DataSource( YAHOO.example.publicOpinion );
	opinionData.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
	opinionData.responseSchema = { fields: [ "response", "count" ] };

//--- chart

	var mychart = new YAHOO.widget.PieChart( "chart", opinionData,
	{
		dataField: "count",
		categoryField: "response",
		style:
		{
			padding: 20,
			legend:
			{
				display: "right",
				padding: 10,
				spacing: 5,
				font:
				{
					family: "Arial",
					size: 13
				}
			}
		},
		//only needed for flash player express install
		expressInstall: "assets/expressinstall.swf"
	});


</script>
<!--END SOURCE CODE FOR EXAMPLE =============================== -->




</body>
</html>
