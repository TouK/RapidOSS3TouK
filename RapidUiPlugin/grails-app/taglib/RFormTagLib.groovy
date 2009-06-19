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
 * Time: 3:33:43 PM
 */
class RFormTagLib {
    static namespace = "rui"
    static def nextId = 0;
    static def fForm(attrs, bodyString) {
        def formId = attrs["id"];
        def configStr = getFormConfig(attrs);
        def onSuccess = attrs["onSuccess"];
        def successJs;
        if (onSuccess != null) {
            successJs = """
               ${formId}form.events['submitSuccessful'].subscribe(function(response){
                   YAHOO.rapidjs.Actions['${onSuccess}'].execute({});
                }, this, true);
            """
        }
        def containerId = "rform_${attrs["id"]}"
        return """
           <div id="${containerId}">${bodyString.trim()}</div>
           <script type="text/javascript">
                YAHOO.util.Event.onDOMReady(function(){
                    var ${formId}conf = ${configStr};
                   var ${formId}parentContainer = document.getElementById('${containerId}');
                   var ${formId}container =${formId}parentContainer.firstChild;
                   document.body.appendChild(${formId}container);
                   var ${formId}form = new YAHOO.rapidjs.component.Form(${formId}container, ${formId}conf);
                    ${successJs ? successJs : ""}
                })

           </script>
        """;
    }
    static def fFormRemote(attrs, bodyString) {
        def onSuccess = attrs.remove("onSuccess");
        def componentId = attrs.remove("componentId");
        def formId=attrs.remove("formId");
        def useDefaultButtons=attrs.remove("useDefaultButtons");
        def submitConfirmation=attrs.remove("submitConfirmation");
        def configAttrs=[:];
        if(formId)
        {
            configAttrs["id"]="\"${formId}\"";
        }
        if(useDefaultButtons)
        {
            if(useDefaultButtons=="false")
            {
                configAttrs["useDefaultButtons"]="false";
            }
            else
            {
                configAttrs["useDefaultButtons"]="true";
            }
        }
        if(submitConfirmation){
             configAttrs["submitConfirmation"]="\"${submitConfirmation.encodeAsJavaScript()}\"";
        }

        def configJs="";
        configAttrs.each{ key , value ->
            configJs+="${key}:${value},";
        }
        if(configAttrs.size()>0)
        {
            configJs=configJs.substring(0,configJs.size()-1);
        }


        def successJs;
        if (onSuccess != null) {
            successJs = """
               formRemote.events['submitSuccessful'].subscribe(${onSuccess}, this, true);
            """
        }
        def containerId = "rformRemote_${nextId++}"
        def attrsArray = [];
        attrs.each{key, value ->
            attrsArray.add("${key}=\"${value}\"")
        }
        return """
           <div id="${containerId}">
                <form ${attrsArray.join(' ')}>
                    ${bodyString}
                </form>
           </div>
           <script type="text/javascript">
               var config={ ${configJs} }
               var htmlComponent = YAHOO.rapidjs.Components['${componentId}'];
               var formContainer = document.getElementById('${containerId}');
               var formRemote = new YAHOO.rapidjs.component.HtmlEmbeddableForm(formContainer,config, htmlComponent);
                ${successJs ? successJs : ""}
           </script>
        """;
    }

    def formRemote = {attrs, body ->
        out << fFormRemote(attrs, body());
    }
    def form = {attrs, body ->
        out << fForm(attrs, body());
    }

    static def getFormConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            ${attrs["createUrl"] ? "createUrl:'${attrs["createUrl"]}'," : ""}
            ${attrs["editUrl"] ? "editUrl:'${attrs["editUrl"]}'," : ""}
            ${attrs["saveUrl"] ? "saveUrl:'${attrs["saveUrl"]}'," : ""}
            ${attrs["updateUrl"] ? "updateUrl:'${attrs["updateUrl"]}'," : ""}
            ${attrs["submitAction"] ? "submitAction:'${attrs["submitAction"]}'," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            width:'${attrs["width"]}'
         }"""
    }
}