/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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
    def flexPieChart = {attrs, body ->
        out << fFlexPieChart(attrs, "");
    }


    static def fFlexPieChart(attrs, bodyString) {
        def configStr = getFlexPieConfig(attrs);
        def onItemClick = attrs["onItemClick"];
        def itemClickJs;
        if (onItemClick != null) {
            itemClickJs = """
               pieChart.events['itemClicked'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${onItemClick}'].execute(params);
                }, this, true);
            """
        }
        return """
           <script type="text/javascript">
               var chartConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var pieChart = new YAHOO.rapidjs.component.FlexPieChart(container, chartConfig);
               ${itemClickJs ? itemClickJs : ""}
               if(pieChart.pollingInterval > 0){
                   pieChart.poll();
               }
           </script>
        """;
    }

    static def getFlexPieConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            url:'${attrs["url"]}',
            rootTag:'${attrs["rootTag"]}',
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            swfURL:'${attrs["swfURL"]}'
        }"""
    }

}