<html>
<head>
    <rui:javascript dir="yui/layout" file="layout-min.js"/>
    <rui:javascript dir="yui/resize" file="resize-min.js"/>
    <rui:javascript dir="rapidjs/component/pieChart" file="FlexPieChart.js"></rui:javascript>

    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
</head>
<body class=" yui-skin-sam">
     <rui:flexPieChart id="pieChart" rootTag="chart" pollingInterval="30" title="Chart"
             swfURL="/RapidUi/images/rapidjs/component/chart/PieChart.swf" url="chart.xml">

     </rui:flexPieChart>

     <script type="text/javascript">
        YAHOO.util.Event.onDOMReady(function() {
            var pieChart = YAHOO.rapidjs.Components['pieChart'];
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', body: pieChart.container.id, resize: false, gutter: '1px' }
                ]
            });
            layout.render();
            var layoutCenter = layout.getUnitByPosition('center');
            pieChart.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
            layout.on('resize', function() {
                pieChart.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
            });
            window.layout = layout;

        })

     </script>
</body>
</html>