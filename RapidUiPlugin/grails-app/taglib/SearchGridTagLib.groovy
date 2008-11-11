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
    static def fSearchGrid(attrs, bodyString) {
        def searchGridId = attrs["id"];
        def configXML = "<SearchGrid>${bodyString}</SearchGrid>";
        def onSaveQueryClick = attrs["onSaveQueryClick"];
        def saveQueryClickJs;
        if (onSaveQueryClick != null) {
            saveQueryClickJs = """
               ${searchGridId}sg.events['saveQueryClicked'].subscribe(function(query){
                   var params = {query:query};
                   YAHOO.rapidjs.Actions['${onSaveQueryClick}'].execute(params);
                }, this, true);
            """
        }
        def menuEvents = [:]
        def subMenuEvents = [:]
        def menuEventsJs;
        def configStr = getConfig(attrs, configXML, menuEvents, subMenuEvents);
        if (menuEvents.size() > 0 || subMenuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            menuEvents.each {id, action ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(menuId == '${id}'){
                   YAHOO.rapidjs.Actions['${action}'].execute(params);
                }
                """
                index++;
            }
            subMenuEvents.each {parentId, subMap ->
                subMap.each {id, action ->
                    innerJs += index == 0 ? "if" : "else if";
                    innerJs += """(parentId == '${parentId}' && menuId == '${id}'){
                       YAHOO.rapidjs.Actions['${action}'].execute(params);
                    }
                    """
                    index++;
                }
            }
            menuEventsJs = """
               ${searchGridId}sg.events['rowHeaderMenuClick'].subscribe(function(xmlData, menuId, parentId){
                   var params = {data:xmlData.getAttributes(), menuId:menuId, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${searchGridId}c = ${configStr};
               var ${searchGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${searchGridId}sg = new YAHOO.rapidjs.component.search.SearchGrid(${searchGridId}container, ${searchGridId}c);
               ${saveQueryClickJs ? saveQueryClickJs : ""}
               ${menuEventsJs ? menuEventsJs : ""}
               if(${searchGridId}sg.pollingInterval > 0){
                   ${searchGridId}sg.poll();
               }
           </script>
        """
    }
    def searchGrid = {attrs, body ->
        out << fSearchGrid(attrs, body());
    }

    static def getConfig(config, configXML, menuEvents, subMenuEvents) {
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
            menuItemArray.add(processMenuItem(menuItem, menuEvents, subMenuEvents));
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
                    width:${column.@width}
                }""")
        }
        cArray.add("columns:[${columnArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
    }

    static def processMenuItem(menuItem, eventMap, subMenuEvents) {
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
                    def subMap = subMenuEvents.get(id);
                    if (!subMap) {
                        subMap = [:]
                        subMenuEvents.put(id, subMap);
                    }
                    subMap.put(subMenuItem.@id, subAction)
                }
            }
            if (subMenuItemsArray.size() > 0) {
                menuItemArray.add("submenuItems:[${subMenuItemsArray.join(',\n')}]")
            }

        }
        return "{${menuItemArray.join(',\n')}}"
    }
    static def fSgMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("MenuItems", attrs, [], bodyString);
    }
    def sgMenuItems = {attrs, body ->
        out << fSgMenuItems(attrs, body())
    }
    static def fSgSubmenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], bodyString)
    }
    def sgSubmenuItems = {attrs, body ->
        out << fSgSubmenuItems(attrs, body())
    }
    static def fSgMenuItem(attrs, bodyString) {
        def validAttrs = ["id", "label", "visible", "action"];
        return TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, bodyString)
    }
    def sgMenuItem = {attrs, body ->
        out << fSgMenuItem(attrs, body())
    }
    static def fSgImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString)
    }
    def sgImages = {attrs, body ->
        out << fSgImages(attrs, body())
    }

    static def fSgImage(attrs, bodyString) {
        def validAttrs = ["src", "visible"];
        return TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }
    def sgImage = {attrs, body ->
        out << fSgImage(attrs, "")
    }

    static def fSgColumns(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Columns", attrs, [], bodyString)
    }
    def sgColumns = {attrs, body ->
        out << fSgColumns(attrs, body())
    }
    static def fSgColumn(attrs, bodyString) {
        def validAttrs = ["attributeName", "colLabel", "sortBy", "sortOrder", "width"];
        return TagLibUtils.getConfigAsXml("Column", attrs, validAttrs)
    }
    def sgColumn = {attrs, body ->
        out << fSgColumn(attrs, "")
    }
}