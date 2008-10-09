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
 * Date: Oct 9, 2008
 * Time: 10:46:38 AM
 */
class SearchGridTagLib {
    static namespace = "rui"
    def searchGrid = {attrs, body ->
        validateAttributes(attrs);
        def configStr = getConfig(attrs, body);
        out << """
           <script type="text/javascript">
               var searchConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var searchGrid = new YAHOO.rapidjs.component.search.SearchGrid(container, searchConfig);
               if(searchGrid.pollingInterval > 0){
                   searchGrid.poll();
               }
           </script>
        """
    }

    def validateAttributes(config) {
        def tagName = "searchGrid";
        if (!config['id']) {
            throwTagError("Tag [${tagName}] is missing required attribute [id]")
            return;
        }
        if (!config['url']) {
            throwTagError("Tag [${tagName}] is missing required attribute [url]")
            return;
        }
        if (!config['fieldsUrl']) {
            throwTagError("Tag [${tagName}] is missing required attribute [fieldsUrl]")
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
        if (!config['queryParameter']) {
            throwTagError("Tag [${tagName}] is missing required attribute [queryParameter]")
            return;
        }
        if (!config['totalCountAttribute']) {
            throwTagError("Tag [${tagName}] is missing required attribute [totalCountAttribute]")
            return;
        }
        if (!config['offsetAttribute']) {
            throwTagError("Tag [${tagName}] is missing required attribute [offsetAttribute]")
            return;
        }
        if (!config['sortOrderAttribute']) {
            throwTagError("Tag [${tagName}] is missing required attribute [sortOrderAttribute]")
            return;
        }
    }

    def getConfig(config, body) {
        def cArray = [];
        cArray.add("id: '${config["id"]}'")
        cArray.add("url: '${config["url"]}'")
        cArray.add("fieldsUrl: '${config["fieldsUrl"]}'")
        cArray.add("rootTag: '${config["rootTag"]}'")
        cArray.add("contentPath: '${config["contentPath"]}'")
        cArray.add("keyAttribute: '${config["keyAttribute"]}'")
        cArray.add("queryParameter: '${config["queryParameter"]}'")
        cArray.add("totalCountAttribute: '${config["totalCountAttribute"]}'")
        cArray.add("offsetAttribute: '${config["offsetAttribute"]}'")
        cArray.add("sortOrderAttribute: '${config["sortOrderAttribute"]}'")
        if (config["title"])
            cArray.add("title:'${config['title']}'")
        if (config["pollingInterval"])
            cArray.add("pollingInterval:${config['pollingInterval']}")
        if (config["maxRowsDisplayed"])
            cArray.add("maxRowsDisplayed:${config['maxRowsDisplayed']}")
        if (config["defaultFilter"])
            cArray.add("defaultFilter:'${config['defaultFilter']}'")
        cArray.add(getInnerConfig(body));
        return "{${cArray.join(',\n')}}"
    }

    def sgMenuItems = {attrs, body ->
        out << "menuItems:[${getInnerConfig(body)}],\n";
    }
    def sgSubmenuItems = {attrs, body ->
        out << "submenuItems:[${getInnerConfig(body)}],\n";
    }
    def sgMenuItem = {attrs, body ->
        def mArray = [];
        mArray.add("id:'${attrs["id"]}'");
        mArray.add("label:'${attrs["label"]}'");
        if (attrs["visible"])
            mArray.add("visible:\"${attrs["visible"]}\"");
        mArray.add(getInnerConfig(body));
        out << "{${mArray.join(',\n')}},\n"
    }

    def sgSubmenuItem = {attrs, body ->
        out << """{
            ${attrs["visible"] ? "visible:\"${attrs["visible"]}\"," : ""}
            id:'${attrs["id"]}',
            label:'${attrs["label"]}'
         },\n"""
    }
    def sgImages = {attrs, body ->
        out << "images:[${getInnerConfig(body)}],\n";
    }
    def sgImage = {attrs, body ->
        out << """{
            visible:"${attrs["visible"]}",
            src:'${attrs["src"]}'
         },\n"""
    }
    def sgColumns = {attrs, body ->
        out << "columns:[${getInnerConfig(body)}],\n";
    }
    def sgColumn = {attrs, body ->
        out << """{
            attributeName:'${attrs["attributeName"]}',
            colLabel:'${attrs["colLabel"]}',
            ${attrs["sortBy"] ? "sortBy:${attrs["sortBy"]}," : ""}
            ${attrs["sortOrder"] ? "sortOrder:\"${attrs["sortOrder"]}\"," : ""}
            width:${attrs["width"]}
         },\n"""
    }

    def getInnerConfig(body) {
        def innerConfig = body();
        if (innerConfig.length() > 0) {
            def lastIndex = innerConfig.lastIndexOf(',');
            innerConfig = innerConfig.substring(0, lastIndex) + innerConfig.substring(lastIndex + 1, innerConfig.length());
        }
        return innerConfig;
    }
}