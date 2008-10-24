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

<script type="text/javascript" src="/RapidUi/js/yui/utilities/utilities.js"></script>

<script type="text/javascript" src="/RapidUi/js/yui/layout/layout-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/resize/resize-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/ext/ext.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/yahoo/yahoo-min.js"></script>

<script type="text/javascript" src="/RapidUi/js/yui/history/history-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/simplewidgets/Button.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/container/container-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/button/button-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/json/json-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/datasource/datasource-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/ComponentContainer.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/RapidUtil.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/tools/BasicTool.js"></script>

<script type="text/javascript" src="/RapidUi/js/rapidjs/component/dialog/Dialog.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/element/element-beta-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/datasource/datasource-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/charts/charts-experimental-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/PollingComponentContainer.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/tools/ButtonToolBar.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/tools/SettingsTool.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/tools/LoadingTool.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/tools/ErrorTool.js"></script>

<script type="text/javascript" src="/RapidUi/js/rapidjs/component/pieChart/PieChart.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/container/container_core-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/data/NodeFactory.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/RapidElement.js"></script>
<script type="text/javascript" src="/RapidUi/js/yui/menu/menu-min.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/data/RapidXmlDocument.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/SelectUtils.js"></script>
<script type="text/javascript" src="/RapidUi/js/rapidjs/component/barChart/BarChart.js"></script>

<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/resize/assets/resize-core.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/resize/assets/skins/sam/resize-skin.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/resize/assets/skins/sam/resize.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/layout/assets/layout-core.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/layout/assets/skins/sam/layout-skin.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/layout/assets/skins/sam/layout.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/assets/skins/sam/skin.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/autocomplete/autocomplete.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/common.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/dialog.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/form.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/layout.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/mgrid.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/overlay.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/ryuitree.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/search/search.css" />

<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/search/searchgrid.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/search/searchlist.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/simplewidgets/button.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/timeline/timeline.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/tools/tools.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/treegrid/treegrid.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/css/rapidjs/yuioverride.css" />
<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/button/assets/skins/sam/button.css" />

<link rel="stylesheet" type="text/css" href="/RapidUi/js/yui/container/assets/skins/sam/container.css" />




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
		}
		//only needed for flash player express install
		//expressInstall: "assets/expressinstall.swf"
	});

    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                    { position: 'center',body:"chart",resize:true }
            ]
        });


        layout.render();

        //pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);

        layout.on('resize', function() {
            alert(layout.getUnitByPosition('center').body.offsetWidth);
            //pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });

        //layout.on('click', function(e) {               alert(YAHOO.util.Event.getXY(e));           });

        window.layout = layout;

    })
</script>
<!--END SOURCE CODE FOR EXAMPLE =============================== -->




</body>
</html>
