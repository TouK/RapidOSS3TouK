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
    static def fTreeGrid(attrs, bodyString) {
        def treeGridId = attrs["id"];
        def onNodeClick = attrs["onNodeClick"];
        def nodeClickJs;
        def menuEventsJs;
        if (onNodeClick != null) {
            nodeClickJs = """
               ${treeGridId}tg.events['treeNodeClick'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${onNodeClick}'].execute(params);
                }, this, true);
            """
        }
        def configXML = "<TreeGrid>${bodyString}</TreeGrid>";
        def menuEvents = [:]
        def configStr = getConfig(attrs, configXML, menuEvents);
        if (menuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            menuEvents.each {id, action ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(id == '${id}'){
                   YAHOO.rapidjs.Actions['${action}'].execute(params);
                }
                """
                index++;
            }
            menuEventsJs = """
               ${treeGridId}tg.events['rowMenuClick'].subscribe(function(xmlData, id, parentId){
                   var params = {data:xmlData.getAttributes(), id:id, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${treeGridId}c = ${configStr};
               var ${treeGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${treeGridId}tg = new YAHOO.rapidjs.component.TreeGrid(${treeGridId}container, ${treeGridId}c);
               ${nodeClickJs ? nodeClickJs : ""}
               ${menuEventsJs ? menuEventsJs : ""}
               if(${treeGridId}tg.pollingInterval > 0){
                   ${treeGridId}tg.poll();
               }
           </script>
        """
    }
    def treeGrid = {attrs, body ->
        out << fTreeGrid(attrs, body());
    }


    static def getConfig(config, configXML, menuEvents) {
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
                    width:${column.@width}
                }""")
        }
        cArray.add("columns:[${columnArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
    }

    static def processMenuItem(menuItem, eventMap) {
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
    static def fTgColumns(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Columns", attrs, [], bodyString)
    }
    def tgColumns = {attrs, body ->
        out << fTgColumns(attrs, body())
    }
    static def fTgColumn(attrs, bodyString) {
        def validAttrs = ["attributeName", "colLabel", "sortBy", "type", "width"];
        return TagLibUtils.getConfigAsXml("Column", attrs, validAttrs)
    }
    def tgColumn = {attrs, body ->
        out << fTgColumn(attrs, "");
    }
    static def fTgRootImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("RootImages", attrs, [], bodyString)
    }
    def tgRootImages = {attrs, body ->
        out << fTgRootImages(attrs, body());
    }
    static def fTgRootImage(attrs, bodyString) {
        def validAttrs = ["visible", "expanded", "collapsed"];
        return TagLibUtils.getConfigAsXml("RootImage", attrs, validAttrs)
    }

    def tgRootImage = {attrs, body ->
        out << fTgRootImage(attrs, "");
    }

    static def fTgMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("MenuItems", attrs, [], bodyString)
    }
    def tgMenuItems = {attrs, body ->
        out << fTgMenuItems(attrs, body());
    }
    static def fTgMenuItem(attrs, bodyString) {
        def validAttrs = ["id", "label", "visible", "action"];
        return TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, bodyString)
    }
    def tgMenuItem = {attrs, body ->
        out << fTgMenuItem(attrs, body());
    }

    static def fTgSubmenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], bodyString)
    }
    def tgSubmenuItems = {attrs, body ->
        out << fTgSubmenuItems(attrs, body());
    }
    static def fTgSubmenuItem(attrs, bodyString) {
        def validAttrs = ["id", "label", "visible", "action"];
        return TagLibUtils.getConfigAsXml("SubmenuItem", attrs, validAttrs)
    }
    def tgSubmenuItem = {attrs, body ->
        out << fTgSubmenuItem(attrs, "");
    }
}