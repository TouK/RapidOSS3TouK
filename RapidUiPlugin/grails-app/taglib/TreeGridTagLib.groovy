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
 * Date: Oct 7, 2008
 * Time: 11:26:22 AM
 */
class TreeGridTagLib {
    static namespace = "rui"
    def treeGrid = {attrs, body ->
        validateAttributes(attrs);
        def treeGridId = attrs["id"];
        def configXML = "<TreeGrid>${body()}</TreeGrid>";
        def menuEvents = [:]
        def configStr = getConfig(attrs, configXML, menuEvents);
        out << """
           <script type="text/javascript">
               var ${treeGridId}c = ${configStr};
               var ${treeGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${treeGridId}tg = new YAHOO.rapidjs.component.TreeGrid(${treeGridId}container, ${treeGridId}c);
               if(${treeGridId}tg.pollingInterval > 0){
                   ${treeGridId}tg.poll();
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

    def getConfig(config, configXML, menuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
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

        def menuItems = xml.MenuItems?.MenuItem;
        def menuItemArray = [];
        menuItems.each {menuItem ->
            menuItemArray.add(processMenuItem(menuItem, menuEvents));
        }
        cArray.add("menuItems:[${menuItemArray.join(',\n')}]");

        def rootImages = xml.RootImages?.RootImage;
        def imageArray = [];
        rootImages.each {rootImage ->
            imageArray.add("""{
                    visible:\"${rootImage.@visible}\",
                    expanded:'${rootImage.@expanded}',
                    collapsed:'${rootImage.@collapsed}'
                }""")
        }
        cArray.add("rootImages:[${imageArray.join(',\n')}]")

        def columns = xml.Columns?.Column;
        def columnArray = [];
        columns.each {column ->
            def sortBy = column.@sortBy.toString().trim();
            def type = column.@type.toString().trim();
            columnArray.add("""{
                    attributeName:'${column.@attributeName}',
                    colLabel:'${column.@colLabel}',
                    ${sortBy != "" ? "sortBy:${sortBy}," : ""}
                    ${type != "" ? "type:'${type}'," : ""}
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

    def tgColumns = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("Columns", attrs, [], body())
    }

    def tgColumn = {attrs, body ->
        def validAttrs = ["attributeName", "colLabel", "sortBy", "type", "width"];
        out << TagLibUtils.getConfigAsXml("Column", attrs, validAttrs)
    }

    def tgRootImages = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("RootImages", attrs, [], body())
    }

    def tgRootImage = {attrs, body ->
        def validAttrs = ["visible", "expanded", "collapsed"];
        out << TagLibUtils.getConfigAsXml("RootImage", attrs, validAttrs)
    }

    def tgMenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("MenuItems", attrs, [], body());
    }

    def tgMenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, body());
    }

    def tgSubmenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], body())
    }
    def tgSubmenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("SubmenuItem", attrs, validAttrs)
    }
}