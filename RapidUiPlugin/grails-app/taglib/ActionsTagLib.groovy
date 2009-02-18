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

    static def fAction(attrs, bodyString) {
        def actionId = attrs["id"];
        def actionType = attrs["type"];
        def configXml = "<Action>${bodyString}</Action>"
        if (actionType == "function") {
            def arguments = [];
            def args = new XmlSlurper().parseText(configXml).FunctionArg;
            args.each {
                arguments.add("\"${it.text()}\"");
            }
            return """
               <script type="text/javascript">
               var ${actionId}comp = YAHOO.rapidjs.Components['${attrs["componentId"]}'];
               var ${actionId}func = ${actionId}comp.${attrs["function"]};
               new YAHOO.rapidjs.component.action.FunctionAction('${attrs["id"]}',${actionId}comp ,${actionId}func, ${attrs["condition"] ? "\"${attrs["condition"]}\"" : "null"}, [${arguments.join(",")}] )
               </script>
            """;

        }
        else if (actionType == "request") {

            def successJs;
            def onSuccess = attrs["onSuccess"];
            if (onSuccess) {
                successJs = """
               ${actionId}action.events['success'].subscribe(function(response){
                   params = {response:response}
                   YAHOO.rapidjs.Actions['${onSuccess}'].execute(params);
                }, this, true);
            """
            }
            def requestParams = [];
            def params = new XmlSlurper().parseText(configXml).RequestParam;
            params.each {
                requestParams.add("${it.@key}:\"${it.@value}\"");
            }
            def compnentList = attrs["components"];
            def cList = [];
            if (compnentList) {
                compnentList.each {
                    cList.add("YAHOO.rapidjs.Components['${it}']");
                }
            }
            return """
               <script type="text/javascript">
               var ${actionId}config = {
                 id:'${actionId}',
                 ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
                 ${attrs["condition"] ? "condition:\"${attrs["condition"]}\"," : ""}
                 url:'${attrs["url"]}'
               }
               var ${actionId}action = new YAHOO.rapidjs.component.action.RequestAction( ${actionId}config, {${requestParams.join(",")}}, [${cList.join(",")}]);
               ${successJs ? successJs : ""}
               </script>
            """ ;
        }
        else if (actionType == "merge") {
            def requestParams = [];
            def params = new XmlSlurper().parseText(configXml).RequestParam;
            params.each {
                requestParams.add("${it.@key}:\"${it.@value}\"");
            }
            def compnentList = attrs["components"];
            def cList = [];
            if (compnentList) {
                compnentList.each {
                    cList.add("YAHOO.rapidjs.Components['${it}']");
                }
            }
            return """
               <script type="text/javascript">
               var ${actionId}config = {
                 id:'${actionId}',
                 ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
                 ${attrs["condition"] ? "condition:\"${attrs["condition"]}\"," : ""}
                 ${attrs["removeAttribute"] ? "removeAttribute:'${attrs["removeAttribute"]}'," : ""}
                 url:'${attrs["url"]}'
               }
               var ${actionId}action = new YAHOO.rapidjs.component.action.MergeAction( ${actionId}config, {${requestParams.join(",")}}, [${cList.join(",")}]);
               </script>
            """;
        }
        else if(actionType == "link"){
            return """
               <script type="text/javascript">
               var ${actionId}action = new YAHOO.rapidjs.component.action.LinkAction( '${actionId}', \"${attrs["url"]}\", ${attrs["condition"] ? "\"${attrs["condition"]}\"" : "null"});
               </script>
            """;
        }
    }
    def action = {attrs, body ->
         out << fAction(attrs, body());
    }
    static def fFunctionArg(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("FunctionArg", attrs, [], bodyString);
    }
    def functionArg = {attrs, body ->
        out << fFunctionArg(attrs, body())
    }
    static def fRequestParam(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("RequestParam", attrs, ["key", "value"], bodyString);
    }
    def requestParam = {attrs, body ->
        out << fRequestParam(attrs, "");
    }

}