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
   def html = {attrs, body ->
        validateAttributes(attrs);
        def configStr = getConfig(attrs, body);
        out << """
           <script type="text/javascript">
               var htmlConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var html = new YAHOO.rapidjs.component.Html(container, htmlConfig);
               if(html.pollingInterval > 0){
                   html.poll();
               }
           </script>
        """
    }

    def validateAttributes(config) {
        def tagName = "html";
        if (!config['id']) {
            throwTagError("Tag [${tagName}] is missing required attribute [id]")
            return;
        }
        if (!config['width']) {
            throwTagError("Tag [${tagName}] is missing required attribute [width]")
            return;
        }
        if (!config['height']) {
            throwTagError("Tag [${tagName}] is missing required attribute [height]")
            return;
        }
    }

    def getConfig(attrs, body){
        return """{
            id:'${attrs["id"]}',
            ${attrs["iframe"] ? "iframe:${attrs["iframe"]}," : ""}
            width:${attrs["width"]},
            height:${attrs["height"]}
        }"""
    }
}