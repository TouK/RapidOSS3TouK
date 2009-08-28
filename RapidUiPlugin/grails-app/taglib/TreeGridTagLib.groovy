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
        def onNodeClick = attrs["onNodeClicked"];
        def onSelectionChanged = attrs["onSelectionChanged"];
        def nodeClickJs = "";
        def selectionChangedJs = "";
        def menuEventsJs;
        if (onNodeClick != null) {
            getActionsArray(onNodeClick).each {actionName ->
                nodeClickJs += """
               ${treeGridId}tg.events['nodeClicked'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                }, this, true);
            """
            }
        }
        if (onSelectionChanged != null) {
           getActionsArray(onSelectionChanged).each {actionName ->
                selectionChangedJs += """
               ${treeGridId}tg.events['selectionChanged'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                }, this, true);
            """
            }
        }
        def configXML = "<TreeGrid>${bodyString}</TreeGrid>";
        def menuEvents = [:]
        def configStr = getConfig(attrs, configXML, menuEvents);
        if (menuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            menuEvents.each {id, actionArray ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(id == '${id}'){""";
                actionArray.each {actionName ->
                    innerJs += """
                          YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    """
                }
                innerJs += """}""";
                index++;
            }
            menuEventsJs = """
               ${treeGridId}tg.events['rowMenuClicked'].subscribe(function(xmlData, id, parentId){
                   var params = {data:xmlData.getAttributes(), menuId:id, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${treeGridId}c = ${configStr};
               var ${treeGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${treeGridId}tg = new YAHOO.rapidjs.component.TreeGrid(${treeGridId}container, ${treeGridId}c);
               ${nodeClickJs}
               ${selectionChangedJs}
               ${menuEventsJs ? menuEventsJs : ""}
               if(${treeGridId}tg.pollingInterval > 0){
                    YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, ${treeGridId}tg, true)
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
        if (config["timeout"])
            cArray.add("timeout:${config['timeout']}")
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
                    visible:\"${rootImage.@visible.text().encodeAsJavaScript()}\",
                    expanded:'${rootImage.@expanded}',
                    collapsed:'${rootImage.@collapsed}'
                }""")
        }
        cArray.add("rootImages:[${imageArray.join(',\n')}]")

        def columns = xml.Columns?.Column;
        def columnArray = [];
        columns.each {column ->
            def sortBy = column.@sortBy.toString().trim();
            def sortOrder = column.@sortOrder.toString().trim();
            def sortType = column.@sortType.toString().trim();
            def sortTypeInJs;
            if(sortType != ""){
                switch(sortType){
                    case 'string':sortTypeInJs = 'YAHOO.rapidjs.component.treegrid.sortTypes.none'; break;
                    case 'int':sortTypeInJs = 'YAHOO.rapidjs.component.treegrid.sortTypes.asInt'; break;
                    case 'date':sortTypeInJs = 'YAHOO.rapidjs.component.treegrid.sortTypes.asDate'; break;
                    case 'float':sortTypeInJs = 'YAHOO.rapidjs.component.treegrid.sortTypes.asFloat'; break;
                    case 'ucString':sortTypeInJs = 'YAHOO.rapidjs.component.treegrid.sortTypes.asUCString'; break;
                }
            }
            def type = column.@type.toString().trim();
            def images = [];
            if (type == 'image' || type == "Image") {
                images = getColumnImages(column);
            }
            columnArray.add("""{
                    attributeName:'${column.@attributeName}',
                    colLabel:'${column.@colLabel}',
                    ${sortBy != "" ? "sortBy:${sortBy}," : ""}
                    ${sortTypeInJs != "" ? "sortType:${sortTypeInJs}," : ""}
                    ${sortOrder != "" ? "sortOrder:'${sortOrder}'," : ""}
                    ${type != "" ? "type:'${type}'," : ""}
                    ${images.size() > 0 ? "images:[${images.join(',\n')}]," : ""}
                    width:${column.@width}
                }""")
        }
        cArray.add("columns:[${columnArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
    }

    static def getColumnImages(columnNode) {
        def imagesArray = [];
        def images = columnNode.Images?.Image;
        images.each {image ->
            def imageArray = [];
            def src = image.@src.toString().trim();
            def visible = image.@visible.toString().trim();
            def align = image.@align.toString().trim();
            imageArray.add("src:'${src}'");
            if (visible != "") {
                imageArray.add("visible:\"${visible.encodeAsJavaScript()}\"")
            }
            if (align != "") {
                imageArray.add("align:'${align}'")
            }
            imagesArray.add("{${imageArray.join(',')}}")
        }

        return imagesArray;
    }

    static def processMenuItem(menuItem, eventMap) {
        def menuItemArray = [];
        def id = menuItem.@id;
        def label = menuItem.@label;
        def visible = menuItem.@visible.toString().trim();
        def action = menuItem.@action.toString().trim();
        if (action != "") {
            eventMap.put(id, [action]);
        }
        else {
            def actions = menuItem.action.Item;
            if (actions.size() > 0) {
                def actionsArray = [];
                actions.each {
                    actionsArray.add(it.text())
                }
                eventMap.put(id, actionsArray);
            }
        }
        menuItemArray.add("id:'${id}'")
        menuItemArray.add("label:'${label}'")
        if (visible != "")
            menuItemArray.add("visible:\"${visible.encodeAsJavaScript()}\"")
        def subMenuItems = menuItem.SubmenuItems?.MenuItem;
        if (subMenuItems) {
            def subMenuItemsArray = [];
            subMenuItems.each {subMenuItem ->
                subMenuItemsArray.add("""{
                   id:'${subMenuItem.@id}',
                   ${subMenuItem.@visible.toString().trim() != "" ? "visible:\"${subMenuItem.@visible.text().encodeAsJavaScript()}\"," : ""}
                   label:'${subMenuItem.@label}'
               }""")
                def subAction = subMenuItem.@action.toString().trim();
                if (subAction != "") {
                    eventMap.put(subMenuItem.@id, [subAction])
                }
                else {
                    def subActions = subMenuItem.action.Item;
                    if (subActions.size() > 0) {
                        def subActionsArray = [];
                        subActions.each {
                            subActionsArray.add(it.text())
                        }
                        eventMap.put(subMenuItem.@id, subActionsArray);
                    }
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
        def validAttrs = ["attributeName", "colLabel", "sortBy", "type", "width", "sortOrder", "sortType"];
        return TagLibUtils.getConfigAsXml("Column", attrs, validAttrs, bodyString)
    }
    def tgColumn = {attrs, body ->
        out << fTgColumn(attrs, body());
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

    static def fTgImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString)
    }
    def tgImages = {attrs, body ->
        out << fTgImages(attrs, body());
    }
    static def fTgImage(attrs, bodyString) {
        def validAttrs = ["visible", "src", "align"];
        return TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }

    def tgImage = {attrs, body ->
        out << fTgImage(attrs, "");
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