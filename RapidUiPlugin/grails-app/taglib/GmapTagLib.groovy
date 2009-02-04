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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 30, 2008
 * Time: 5:29:00 PM
 */
class GmapTagLib {
    static namespace = "rui"
    static def fGmap(attrs, bodyString) {
        def configStr = getConfig(attrs);
        def onMarkerClick = attrs["onMarkerClick"];
        def markerClickJs;
        if (onMarkerClick != null) {
            markerClickJs = """
               gmap.events['markerClicked'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${onMarkerClick}'].execute(params);
                }, this, true);
            """
        }
        return """
           <script type="text/javascript">
               var gmapConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var gmap = new YAHOO.rapidjs.component.GMap(container, gmapConfig);
               ${markerClickJs ? markerClickJs : ""}
               if(gmap.pollingInterval > 0){
                   gmap.poll();
               }
           </script>
        """;
    }
    def gmap = {attrs, body ->
         out << fGmap(attrs, "");
    }

    static def getConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            url:'${attrs["url"]}',
            contentPath:'${attrs["contentPath"]}',
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            latitudeAttributeName:'${attrs["latitudeField"]}',
            longitudeAttributeName:'${attrs["longitudeField"]}',
            addressAttributeName:'${attrs["addressField"]}',
            markerAttributeName:'${attrs["markerField"]}',
            tooltipAttributeName:'${attrs["tooltipField"]}'
        }"""
    }
}