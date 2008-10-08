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
 * Time: 1:25:41 PM
 */
class SearchListTagLib {
    static namespace = "rui"
    def searchList = {attrs, body ->
        validateAttributes(attrs);
        def configStr = getConfig(attrs, body);
        out << """
           <script type="text/javascript">
               var searchConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var searchList = new YAHOO.rapidjs.component.search.SearchList(container, searchConfig);
               if(searchList.pollingInterval > 0){
                   searchList.poll();
               }
           </script>
        """
    }

    def validateAttributes(config) {
        def tagName = "searchList";
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
        if (config["defaultFields"]){
            def fArray = [];
            config['defaultFields'].each{
                fArray.add("'${it}'");
            }
            cArray.add("defaultFields:[${fArray.join(",")}]")
        }

        if (config["maxRowsDisplayed"])
            cArray.add("maxRowsDisplayed:${config['maxRowsDisplayed']}")
        if (config["defaultFilter"])
            cArray.add("defaultFilter:'${config['defaultFilter']}'")
        if (config["lineSize"])
            cArray.add("lineSize:${config['lineSize']}")
        if (config["rowHeaderAttribute"])
            cArray.add("rowHeaderAttribute:'${config['rowHeaderAttribute']}'")
        cArray.add(getInnerConfig(body));
        return "{${cArray.join(',\n')}}"
    }

    def slMenuItems = {attrs, body ->
        out << "menuItems:[${getInnerConfig(body)}],\n";
    }

    def slPropertyMenuItems = {attrs, body ->
        out << "propertyMenuItems:[${getInnerConfig(body)}],\n";
    }
    def slSubmenuItems = {attrs, body ->
        out << "submenuItems:[${getInnerConfig(body)}],\n";
    }

    def slMenuItem = {attrs, body ->
        def mArray = [];
        mArray.add("id:'${attrs["id"]}'");
        mArray.add("label:'${attrs["label"]}'");
        if (attrs["visible"])
            mArray.add("visible:\"${attrs["visible"]}\"");
        mArray.add(getInnerConfig(body));
        out << "{${mArray.join(',\n')}},\n"
    }

    def slSubmenuItem = {attrs, body ->
        out << """{
            ${attrs["visible"] ? "visible:\"${attrs["visible"]}\"," : ""}
            id:'${attrs["id"]}',
            label:'${attrs["label"]}'
         },\n"""
    }

    def slImages = {attrs, body ->
        out << "images:[${getInnerConfig(body)}],\n";
    }
    def slImage = {attrs, body ->
        out << """{
            visible:"${attrs["visible"]}",
            src:'${attrs["src"]}'
         },\n"""
    }

    def slFields = {attrs, body ->
        out << "fields:[${getInnerConfig(body)}],\n";
    }
    def slField = {attrs, body ->
        def fArray = []; 
        attrs['fields'].each{
            fArray.add("'${it}'")
        }
        out << """{
            exp:"${attrs["exp"]}",
            fields:[${fArray.join(",")}]
         },\n"""
    }

    def getInnerConfig(body){
        def innerConfig = body();
        println "InnerConfig : ${innerConfig}"
        if (innerConfig.length() > 0) {
            def lastIndex = innerConfig.lastIndexOf(',');
            innerConfig = innerConfig.substring(0, lastIndex) + innerConfig.substring(lastIndex + 1, innerConfig.length());
        }
        return innerConfig;
    }

}