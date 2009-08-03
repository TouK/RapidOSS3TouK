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
        def successJs = "";
        def errorJs = "";
        def onSuccess = attrs["onSuccess"];
        def onError = attrs["onError"];
        if (onSuccess) {
            getActionsArray(onSuccess).each {actionName ->
                successJs += """
                   ${actionId}action.events['success'].subscribe(function(response){
                       var params = {response:response}
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onError) {
            getActionsArray(onError).each {actionName ->
                errorJs += """
                   ${actionId}action.events['error'].subscribe(function(messages){
                       var params = {messages:messages}
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (actionType == "function") {
            def arguments = [];
            def args = new XmlSlurper().parseText(configXml).FunctionArg;
            args.each {
                arguments.add("\"${it.text().encodeAsJavaScript()}\"");
            }
            return """
               <script type="text/javascript">
               var ${actionId}comp = YAHOO.rapidjs.Components['${attrs["componentId"]}'];
               var ${actionId}func = ${actionId}comp.${attrs["function"]};
               var ${actionId}action = new YAHOO.rapidjs.component.action.FunctionAction('${attrs["id"]}',${actionId}comp ,${actionId}func, ${attrs["condition"] ? "\"${attrs["condition"].encodeAsJavaScript()}\"" : "null"}, [${arguments.join(",")}] )
               ${successJs}
               ${errorJs}
               </script>
            """;

        }
        else if (actionType == "request" || actionType == "merge") {
            def timeoutJs = "";
            def unknownUrlJs = "";
            def internalServerErrorJs = "";
            def serverDownJs = "";
            def onTimeout = attrs["onTimeout"];
            def onUnknownUrl = attrs["onUnknownUrl"];
            def onServerDown = attrs["onServerDown"];
            def onInternalServerError = attrs["onInternalServerError"];
            if (onTimeout) {
                getActionsArray(onTimeout).each {actionName ->
                    timeoutJs += """
                       ${actionId}action.events['timeout'].subscribe(function(){
                           var params = {}
                           YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        }, this, true);
                    """
                }
            }
            if (onUnknownUrl) {
                getActionsArray(onUnknownUrl).each {actionName ->
                    unknownUrlJs += """
                       ${actionId}action.events['unknownUrl'].subscribe(function(){
                           var params = {}
                           YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        }, this, true);
                    """
                }
            }
            if (onUnknownUrl) {
                getActionsArray(onInternalServerError).each {actionName ->
                    internalServerErrorJs += """
                       ${actionId}action.events['internalServerError'].subscribe(function(){
                           var params = {}
                           YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        }, this, true);
                    """
                }
            }
            if (onServerDown) {
                getActionsArray(onServerDown).each {actionName ->
                    serverDownJs += """
                       ${actionId}action.events['serverDown'].subscribe(function(){
                           var params = {}
                           YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        }, this, true);
                    """
                }
            }
            def requestParams = [];
            def params = new XmlSlurper().parseText(configXml).RequestParam;
            params.each {
                requestParams.add("${it.@key}:\"${it.@value.text().encodeAsJavaScript()}\"");
            }
            def compnentList = attrs["components"];
            def cList = [];
            if (compnentList) {
                compnentList.each {
                    cList.add("YAHOO.rapidjs.Components['${it}']");
                }
            }
            if (actionType == "request") {
                return """
                   <script type="text/javascript">
                       var ${actionId}config = {
                         id:'${actionId}',
                         ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
                         ${attrs["submitType"] ? "submitType:'${attrs["submitType"]}'," : ""}
                         ${attrs["condition"] ? "condition:\"${attrs["condition"].encodeAsJavaScript()}\"," : ""}
                         url:'${attrs["url"]}'
                       }
                       var ${actionId}action = new YAHOO.rapidjs.component.action.RequestAction( ${actionId}config, {${requestParams.join(",")}}, [${cList.join(",")}]);
                       ${successJs}
                       ${errorJs}
                       ${serverDownJs}
                       ${timeoutJs}
                       ${unknownUrlJs}
                       ${internalServerErrorJs}
                   </script>
                """;
            }
            else {
               return """
                   <script type="text/javascript">
                       var ${actionId}config = {
                         id:'${actionId}',
                         ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
                         ${attrs["condition"] ? "condition:\"${attrs["condition"].encodeAsJavaScript()}\"," : ""}
                         ${attrs["removeAttribute"] ? "removeAttribute:'${attrs["removeAttribute"]}'," : ""}
                         url:'${attrs["url"]}'
                       }
                       var ${actionId}action = new YAHOO.rapidjs.component.action.MergeAction( ${actionId}config, {${requestParams.join(",")}}, [${cList.join(",")}]);
                       ${successJs}
                       ${errorJs}
                       ${serverDownJs}
                       ${timeoutJs}
                       ${unknownUrlJs}
                   </script>
                """;
            }

        }
        else if (actionType == "link") {
            return """
               <script type="text/javascript">
               var ${actionId}action = new YAHOO.rapidjs.component.action.LinkAction( '${actionId}', \"${attrs["url"].encodeAsJavaScript()}\", ${attrs["condition"] ? "\"${attrs["condition"].encodeAsJavaScript()}\"" : "null"}, ${attrs["target"] ? "\"${attrs["target"]}\"":"null"});
               ${errorJs} 
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