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
    def form = {attrs, body ->
        validateAttributes(attrs);
        def formId = attrs["id"];
        def configStr = getConfig(attrs, body);
        def onSuccess = attrs["onSuccess"];
        def successJs;
        if(onSuccess != null){
            successJs = """
               ${formId}form.events['submitSuccessful'].subscribe(function(response){
                   YAHOO.rapidjs.Actions['${onSuccess}'].execute({});
                }, this, true);
            """
        }
        def formBody = body();
        def containerId = "rform_${attrs["id"]}"
        out << """
           <div id="${containerId}">${formBody.trim()}</div>
           <script type="text/javascript">
               var ${formId}conf = ${configStr};
               var ${formId}parentContainer = document.getElementById('${containerId}');
               var ${formId}container =${formId}parentContainer.firstChild;
               document.body.appendChild(${formId}container);
               var ${formId}form = new YAHOO.rapidjs.component.Form(${formId}container, ${formId}conf);
                ${successJs ? successJs:""}
           </script>
        """
    }

    def validateAttributes(config) {
        def tagName = "form";
        if (!config['id']) {
            throwTagError("Tag [${tagName}] is missing required attribute [id]")
            return;
        }
        if (!config['width']) {
            throwTagError("Tag [${tagName}] is missing required attribute [width]")
            return;
        }
    }

    def getConfig(attrs, body){
         return """{
            id:'${attrs["id"]}',
            ${attrs["createUrl"] ? "createUrl:'${attrs["createUrl"]}'," : ""}
            ${attrs["editUrl"] ? "editUrl:'${attrs["editUrl"]}'," : ""}
            ${attrs["saveUrl"] ? "saveUrl:'${attrs["saveUrl"]}'," : ""}
            ${attrs["updateUrl"] ? "updateUrl:'${attrs["updateUrl"]}'," : ""}
            ${attrs["submitAction"] ? "submitAction:'${attrs["submitAction"]}'," : ""}
            width:'${attrs["width"]}'
         }"""
    }
}