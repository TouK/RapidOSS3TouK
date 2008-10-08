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
 * Date: Oct 7, 2008
 * Time: 11:26:22 AM
 */
class TreeGridTagLib {
    static namespace = "rui"
    def treeGrid = {attrs, body ->
        validateAttributes(attrs);
        def configStr = getConfig(attrs, body);
        out << """
           <script type="text/javascript">
               var treeConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var treeGrid = new YAHOO.rapidjs.component.TreeGrid(container, treeConfig);
               if(treeGrid.pollingInterval > 0){
                   treeGrid.poll();
               }
           </script>
        """
    }

    def validateAttributes(config) {
        def tagName = "treeGrid";
        if (!config['id']) {
            throwTagError("Tag [${tagName}] is missing required attribute [id]")
            return;
        }
        if (!config['url']) {
            throwTagError("Tag [${tagName}] is missing required attribute [url]")
            return;
        }
        if (!config['rootTag']) {
            throwTagError("Tag [${tagName}] is missing required attribute [rootTag]")
            return;
        }
        if (!config['contentPath']) {
            throwTagError("Tag [${tagName}] is missing required attribute [contentPath]")
            return;
        }
        if (!config['keyAttribute']) {
            throwTagError("Tag [${tagName}] is missing required attribute [keyAttribute]")
            return;
        }
    }

    def getConfig(config, body) {
        def cArray = [];
        cArray.add("id: '${config["id"]}'")
        cArray.add("url: '${config["url"]}'")
        cArray.add("rootTag: '${config["rootTag"]}'")
        cArray.add("contentPath: '${config["contentPath"]}'")
        cArray.add("keyAttribute: '${config["keyAttribute"]}'")
        if (config["title"])
            cArray.add("title:'${config['title']}'")
        if (config["expanded"])
            cArray.add("expanded:${config['expanded']}")
        if (config["tooltip"])
            cArray.add("tooltip:${config['tooltip']}")
        if (config["pollingInterval"])
            cArray.add("pollingInterval:${config['pollingInterval']}")
        String innerConfig = body();
        if (innerConfig.length() > 0) {
            def lastIndex =  innerConfig.lastIndexOf(',');
            innerConfig = innerConfig.substring(0, lastIndex) + innerConfig.substring(lastIndex + 1, innerConfig.length());
        }
        cArray.add(innerConfig);
        return "{${cArray.join(',\n')}}"
    }

    def tgColumns = {attrs, body ->
        String config = body();
        if (config.length() > 0) {
            def lastIndex =  config.lastIndexOf(',');
            config = config.substring(0, lastIndex) + config.substring(lastIndex + 1, config.length());
        }
        out << "columns:[${config}],\n";
    }

    def tgColumn = {attrs, body ->
        out << """{
            attributeName:'${attrs["attributeName"]}',
            width:${attrs["width"]},
            colLabel:'${attrs["colLabel"]}',
            ${attrs["sortBy"] ? "sortBy:'${attrs["sortBy"]}'," : ""}
            ${attrs["type"] ? "type:'${attrs["type"]}'," : ""}
         },\n"""
    }

    def tgRootImages = {attrs, body ->
        String config = body();
        if (config.length() > 0) {
            def lastIndex =  config.lastIndexOf(',');
            config = config.substring(0, lastIndex) + config.substring(lastIndex + 1, config.length());
        }
        out << "rootImages:[${config}],\n";
    }

    def tgRootImage = {attrs, body ->
        out << """{
            visible:"${attrs["visible"]}",
            expanded:'${attrs["expanded"]}',
            collapsed:'${attrs["collapsed"]}',
         },\n"""
    }
}