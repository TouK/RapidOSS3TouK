<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>

<rui:javascript dir="yui/layout" file="layout-min.js"/>
<rui:javascript dir="yui/resize" file="resize-min.js"/>
<rui:javascript dir="rapidjs/component/pieChart" file="PieChart.js"></rui:javascript>
<rui:javascript dir="rapidjs/component/barChart" file="BarChart.js"></rui:javascript>

<rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
<rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
<rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
<rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
<rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
<rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

<rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>

<title>Pie Chart Example</title>

</head>

<body >

<div id="pieChartDiv"></div>


<script type="text/javascript">

     var pieChartConfig=
     {
        id:"chartDiv",
        title:"Sample Chart",
        url : "http://localhost:3333/RapidUi/test/pcxml.gsp",
	    dataType : "YAHOO.util.XHRDataSource.TYPE_XML",
	    swfURL: "../js/yui/charts/assets/charts.swf"
        //,  pollingInterval:0
     };

    alert("creating layout");



    //var pchart = new YAHOO.rapidjs.component.PieChart(document.getElementById("pieChartDiv"),pieChartConfig );
    //pchart.poll();

    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
            { position: 'center',body:"pieChartDiv" ,resize:true }
            ]
        });

        var phart=null;
        layout.on('render',function(){
            pchart = new YAHOO.rapidjs.component.PieChart(document.getElementById("pieChartDiv"),pieChartConfig );
            pchart.poll();
        },this,true);
        layout.render();


         
         //var pchart = new YAHOO.rapidjs.component.PieChart(document.getElementById("pieChartDiv"),pieChartConfig );
         pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);

         layout.on('resize', function() {
             pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
         });
         window.layout = layout;

    })
    


     
    function createLayout()
    {


            alert("creating layout");
            

            var layout = new YAHOO.widget.Layout({
                units: [
                        { position: 'center',body:pchart.container.id ,resize:true }
                ]
            });

            layout.render();

            pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);

            pchart.poll();
    
            layout.on('resize', function() {
                pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
            });

            //layout.on('click', function(e) {               alert(YAHOO.util.Event.getXY(e));           });

            window.layout = layout;




    }
    /*
    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                    { position: 'center',body:pchart.container.id ,resize:true }
            ]
        });

        
        layout.render();

        pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);

        layout.on('resize', function() {
            pchart.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });

        //layout.on('click', function(e) {               alert(YAHOO.util.Event.getXY(e));           });
        
        window.layout = layout;

    })
    */

</script>


</body>
</html>
