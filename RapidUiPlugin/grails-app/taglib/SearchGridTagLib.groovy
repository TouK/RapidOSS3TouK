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
 * Date: Oct 9, 2008
 * Time: 10:46:38 AM
 */
class SearchGridTagLib {
    static namespace = "rui"
    def searchGrid = {attrs, body ->
        validateAttributes(attrs);
        def searchGridId = attrs["id"];
        def configXML = "<SearchGrid>${body()}</SearchGrid>";
        def menuEvents = [:]
        def configStr = getConfig(attrs, configXML, menuEvents);
        out << """
           <script type="text/javascript">
               var ${searchGridId}c = ${configStr};
               var ${searchGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${searchGridId}sl = new YAHOO.rapidjs.component.search.SearchGrid(${searchGridId}container, ${searchGridId}c);
               if(${searchGridId}sl.pollingInterval > 0){
                   ${searchGridId}sl.poll();
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

    def getConfig(config, configXML, menuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
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

        def menuItems = xml.MenuItems?.MenuItem;
        def menuItemArray = [];
        menuItems.each {menuItem ->
            menuItemArray.add(processMenuItem(menuItem, menuEvents));
        }
        cArray.add("menuItems:[${menuItemArray.join(',\n')}]");

        def images = xml.Images?.Image;
        def imageArray = [];
        images.each {image ->
            imageArray.add("""{
                    src:'${image.@src}',
                    visible:\"${image.@visible}\"
                }""")
        }
        cArray.add("images:[${imageArray.join(',\n')}]")

        def columns = xml.Columns?.Column;
        def columnArray = [];
        columns.each {column ->
            def sortOrder = column.@sortOrder.toString().trim();
            def sortBy = column.@sortBy.toString().trim();
            columnArray.add("""{
                    attributeName:'${column.@attributeName}',
                    colLabel:'${column.@colLabel}',
                    ${sortBy != "" ? "sortBy:${sortBy}," : ""}
                    ${sortOrder != "" ? "sortOrder:'${sortOrder}'," : ""}
                    width:${column.@width},
                }""")
        }
        cArray.add("columns:[${columnArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
    }

    def processMenuItem(menuItem, eventMap) {
        def menuItemArray = [];
        def id = menuItem.@id;
        def label = menuItem.@label;
        def visible = menuItem.@visible.toString().trim();
        def action = menuItem.@action.toString().trim();
        if (action != "") {
            eventMap.put(id, action);
        }
        menuItemArray.add("id:'${id}'")
        menuItemArray.add("label:'${label}'")
        if (visible != "")
            menuItemArray.add("visible:\"${visible}\"")
        def subMenuItems = menuItem.SubmenuItems?.MenuItem;
        if (subMenuItems) {
            def subMenuItemsArray = [];
            subMenuItems.each {subMenuItem ->
                subMenuItemsArray.add("""{
                   id:'${subMenuItem.@id}',
                   ${subMenuItem.@visible.toString().trim() != "" ? "visible:\"${subMenuItem.@visible}\"," : ""}
                   label:'${subMenuItem.@label}'
               }""")
                def subAction = subMenuItem.@action.toString().trim();
                if (subAction != "") {
                    eventMap.put(subMenuItem.@id, action)
                }
            }
            if (subMenuItemsArray.size() > 0) {
                menuItemArray.add("submenuItems:[${subMenuItemsArray.join(',\n')}]")
            }

        }
        return "{${menuItemArray.join(',\n')}}"
    }

    def sgMenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("MenuItems", attrs, [], body());
    }
    def sgSubmenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], body())
    }
    def sgMenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, body());
    }

    def sgSubmenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("SubmenuItem", attrs, validAttrs)
    }
    def sgImages = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("Images", attrs, [], body())
    }
    def sgImage = {attrs, body ->
        def validAttrs = ["src", "visible"];
        out << TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }
    def sgColumns = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("Columns", attrs, [], body())
    }
    def sgColumn = {attrs, body ->
        def validAttrs = ["attributeName", "colLabel", "sortBy", "sortOrder", "width"];
        out << TagLibUtils.getConfigAsXml("Column", attrs, validAttrs)
    }
}