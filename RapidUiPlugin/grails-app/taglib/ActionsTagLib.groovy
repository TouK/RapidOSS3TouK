import com.ifountain.rui.util.TagLibUtils

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
 * Date: Oct 10, 2008
 * Time: 6:03:52 PM
 */
class ActionsTagLib {
    static namespace = "rui"

    def action = {attrs, body ->
        validateAttributes(attrs);
        def actionId = attrs["id"];
        def actionType = attrs["type"];
        if(actionType == "function"){
            def configXml = "<Action>${body()}</Action>"
            def arguments = [];
            def args = new XmlSlurper().parseText(configXml).FunctionArg;
            args.each{
                arguments.add("\"${it.text()}\"");
            }
            out <<"""
               <script type="text/javascript">
               var comp = YAHOO.rapidjs.Components['${attrs["componentId"]}'];
               var func = comp.${attrs["function"]};
               new YAHOO.rapidjs.component.action.FunctionAction('${attrs["id"]}',comp ,func, ${attrs["condition"] ? "\"${attrs["condition"]}\"":"null"}, [${arguments.join(",")}] )
               </script>
            """

        }
    }

    def functionArg = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("FunctionArg", attrs, [], body()); 
    }

    def validateAttributes(config) {
        def tagName = "action";
        if (!config['id']) {
            throwTagError("Tag [${tagName}] is missing required attribute [id]")
            return;
        }
        if (!config['type']) {
            throwTagError("Tag [${tagName}] is missing required attribute [type]")
            return;
        }
        if (config['type'] == "function") {
            if (!config['componentId']) {
                throwTagError("Tag [${tagName}] is missing required attribute [componentId]")
                return;
            }
            if (!config['function']) {
                throwTagError("Tag [${tagName}] is missing required attribute [function]")
                return;
            }
        }
    }
}