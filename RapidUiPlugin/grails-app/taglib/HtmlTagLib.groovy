/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
 * Date: Oct 8, 2008
 * Time: 5:00:16 PM
 */
class HtmlTagLib {
    static namespace = "rui"
    static def fHtml(attrs, bodyString) {
        def configStr = getConfig(attrs);
        return """
           <script type="text/javascript">
               var htmlConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var html = new YAHOO.rapidjs.component.Html(container, htmlConfig);
               if(html.pollingInterval > 0){
                   YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, html, true)
               }
           </script>
        """;
    }
    def html = {attrs, body ->
         out << fHtml(attrs, "");
    }

    static def getConfig(attrs) {
        return """{
            ${attrs["url"] ? "url:'${attrs["url"]}'," : ""}
            ${attrs["iframe"] ? "iframe:${attrs["iframe"]}," : ""}
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            ${attrs["evaluateScripts"] ? "evaluateScripts:${attrs["evaluateScripts"]}," : ""}
            id:'${attrs["id"]}'
        }"""
    }
}