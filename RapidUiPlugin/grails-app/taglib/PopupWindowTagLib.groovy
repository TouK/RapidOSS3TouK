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
 * Date: Oct 23, 2008
 * Time: 10:20:23 AM
 */
class PopupWindowTagLib {
    static namespace = "rui"

    static def fPopupWindow(attrs, bodyString) {
        def configString = getConfig(attrs);
        def componentId = attrs["componentId"];
        return """
           <script type="text/javascript">
               var pConfig = ${configString};
               new YAHOO.rapidjs.component.PopupWindow(YAHOO.rapidjs.Components['${componentId}'], pConfig);
           </script>
        """;
    }
    def popupWindow = {attrs, body ->
        out << fPopupWindow(attrs, "")
    }

    static def getConfig(attrs) {
        return """{
            width:${attrs["width"]},
            ${attrs["x"] ? "x:${attrs["x"]}," : ""}
            ${attrs["y"] ? "y:${attrs["y"]}," : ""}
            ${attrs["minHeight"] ? "minHeight:${attrs["minHeight"]}," : ""}
            ${attrs["maxHeight"] ? "maxHeight:${attrs["maxHeight"]}," : ""}
            ${attrs["minWidth"] ? "minWidth:${attrs["minWidth"]}," : ""}
            ${attrs["maxWidth"] ? "maxWidth:${attrs["maxWidth"]}," : ""}
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            height:${attrs["height"]}
        }"""
    }
}