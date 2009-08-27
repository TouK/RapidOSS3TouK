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
class AutocompleteTagLib {

    static namespace = "rui"

    static def fAutocomplete(attrs, bodyString) {
        def configString = getConfig(attrs);
        def onSubmit = attrs["onSubmit"];
        def submitJs = "";
        if (onSubmit != null) {
            getActionsArray(onSubmit).each {actionName ->
                submitJs += """
                   autocomplete.events['submit'].subscribe(function(query){
                       var params = {query:query};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }

        }
        return """
           <script type="text/javascript">
               var aConfig = ${configString};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var autocomplete = new YAHOO.rapidjs.component.Autocomplete(container, aConfig);
               ${submitJs ? submitJs : ""}
           </script>
        """;
    }
    def autocomplete = {attrs, body ->
        out << fAutocomplete(attrs, "")
    }

    static def getConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            title:'${attrs["title"]}',
            url:'${attrs["url"]}',
            contentPath:'${attrs["contentPath"]}',
            ${attrs["cacheSize"] ? "cacheSize:${attrs["cacheSize"]}," : ""}
            ${attrs["submitButtonLabel"] ? "submitButtonLabel:'${attrs["submitButtonLabel"]}'," : ""}
            ${attrs["animated"] ? "animated:${attrs["animated"]}," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            suggestionAttribute:'${attrs["suggestionAttribute"]}'
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