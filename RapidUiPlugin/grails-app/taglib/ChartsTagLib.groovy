class ChartsTagLib {

    static namespace = "rui"

    static def fPieChart(attrs, bodyString) {
        def configStr = getPieConfig(attrs);
        return """
           <script type="text/javascript">
               var chartConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var pieChart = new YAHOO.rapidjs.component.PieChart(container, chartConfig);
               if(pieChart.pollingInterval > 0){
                   pieChart.poll();
               }
           </script>
        """;
    }

    def pieChart = {attrs, body ->
        out << fPieChart(attrs, "");
    }

    static def getPieConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            url:'${attrs["url"]}',
            contentPath:'${attrs["contentPath"]}',
            swfURL:'${attrs["swfURL"]}',
            categoryField:'${attrs["categoryField"]}',
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["legend"] ? "legend:'${attrs["legend"]}'," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            ${attrs["colors"] ? "colors:${attrs["colors"]}," : ""}
            dataField:'${attrs["dataField"]}'
        }"""
    }

}