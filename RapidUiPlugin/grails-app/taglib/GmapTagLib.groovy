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
        def onMarkerClick = attrs["onMarkerClicked"];
        def onLineClick = attrs["onLineClicked"];
        def onIconClick = attrs["onIconClicked"];
        def markerClickJs = "";
        def linerClickJs = "";
        def iconClickJs = "";
        if (onMarkerClick != null) {
            getActionsArray(onMarkerClick).each {actionName ->
                markerClickJs += """
                   gmap.events['markerClicked'].subscribe(function(xmlData){
                       var params = {data:xmlData.getAttributes()};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
         if (onLineClick != null) {
            getActionsArray(onLineClick).each {actionName ->
                linerClickJs += """
                   gmap.events['lineClicked'].subscribe(function(xmlData){
                       var params = {data:xmlData.getAttributes()};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
         if (onIconClick != null) {
            getActionsArray(onIconClick).each {actionName ->
                iconClickJs += """
                   gmap.events['iconClicked'].subscribe(function(xmlData){
                       var params = {data:xmlData.getAttributes()};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        return """
           <script type="text/javascript">
               var gmapConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var gmap = new YAHOO.rapidjs.component.GMap(container, gmapConfig);
               ${markerClickJs}
               ${linerClickJs}
               ${iconClickJs}
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
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            ${attrs["locationTagName"] ? "locationTagName:'${attrs["locationTagName"]}'," : ""}
            ${attrs["lineTagName"] ? "lineTagName:'${attrs["lineTagName"]}'," : ""}
            ${attrs["iconTagName"] ? "iconTagName:'${attrs["iconTagName"]}'," : ""}
            ${attrs["lineSize"] ? "lineSize:${attrs["lineSize"]}," : ""}
            ${attrs["defaultIconWidth"] ? "defaultIconWidth:${attrs["defaultIconWidth"]}," : ""}
            ${attrs["defaultIconHeight"] ? "defaultIconHeight:${attrs["defaultIconHeight"]}," : ""}
            googleKey:'${attrs["googleKey"]}'
        }"""
    }

    static def getActionsArray(actionAttribute) {
        def actions = [];
        if (actionAttribute instanceof List) {
            actions.addAll(actionAttribute);
        }
        else {
            actions.add(actionAttribute);
        }
        return actions;
    }
}