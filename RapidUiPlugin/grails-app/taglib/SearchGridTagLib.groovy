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
        def onSaveQueryClick = attrs["onSaveQueryClicked"];
        def onRowDoubleClick = attrs["onRowDoubleClicked"];
        def onRowClick = attrs["onRowClicked"];
        def onSelectionChange = attrs["onSelectionChanged"];
        def onPropertyClicked = attrs["onPropertyClicked"];
        def saveQueryClickJs = "";
        def rowDoubleClickJs = "";
        def rowClickJs = "";
        def selectionChangeJs = "";
        def propertyClickedJs = "";
        if (onSaveQueryClick != null) {
            getActionsArray(onSaveQueryClick).each {actionName ->
                saveQueryClickJs += """
               ${searchGridId}sg.events['saveQueryClicked'].subscribe(function(query){
                   var params = {query:query};
                   YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                }, this, true);
            """
            }

        }
        if (onRowDoubleClick != null) {
            getActionsArray(onRowDoubleClick).each {actionName ->
                rowDoubleClickJs += """
                   ${searchGridId}sg.events['rowDoubleClicked'].subscribe(function(xmlData, event){
                       var params = {data:xmlData.getAttributes(), event:event};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onRowClick != null) {
            getActionsArray(onRowClick).each {actionName ->
                rowClickJs += """
                   ${searchGridId}sg.events['rowClicked'].subscribe(function(xmlData, event){
                       var params = {data:xmlData.getAttributes(), event:event};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }

        }
        if (onSelectionChange != null) {
            getActionsArray(onSelectionChange).each {actionName ->
                selectionChangeJs += """
                   ${searchGridId}sg.events['selectionChanged'].subscribe(function(xmlDatas, event){
                       var params = {datas:ArrayUtils.collect(xmlDatas, function(xmlData){return xmlData.getAttributes()}), event:event};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onPropertyClicked != null) {
            getActionsArray(onPropertyClicked).each {actionName ->
                propertyClickedJs += """
                   ${searchGridId}sg.events['propertyClicked'].subscribe(function(key, value, xmlData){
                       var params = {data:xmlData.getAttributes(), key:key, value:value};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        def menuEvents = [:]
        def subMenuEvents = [:]
        def multipleSelectionMenuEvents = [:]
        def multipleSelectionSubMenuEvents = [:]
        def menuEventsJs;
        def configStr = getConfig(attrs, configXML, menuEvents, subMenuEvents, multipleSelectionMenuEvents, multipleSelectionSubMenuEvents);
        if (menuEvents.size() > 0 || subMenuEvents.size() > 0) {
            def innerJs = _getMenuEventJs(menuEvents, subMenuEvents)
            menuEventsJs = """
               ${searchGridId}sg.events['rowHeaderMenuClicked'].subscribe(function(xmlData, menuId, parentId){
                   var params = {data:xmlData.getAttributes(), menuId:menuId, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        def multiplerSelectionMenuEventJs;
        if (multipleSelectionMenuEvents.size() > 0 || multipleSelectionSubMenuEvents.size() > 0) {
            def innerJs = _getMenuEventJs(multipleSelectionMenuEvents, multipleSelectionSubMenuEvents)
            multiplerSelectionMenuEventJs = """
               ${searchGridId}sg.events['multiSelectionMenuClicked'].subscribe(function(xmlDatas, menuId, parentId){
                   var params = {datas:ArrayUtils.collect(xmlDatas, function(item){return item.getAttributes()}), menuId:menuId, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${searchGridId}c = ${configStr};
               var ${searchGridId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${searchGridId}sg = new YAHOO.rapidjs.component.search.SearchGrid(${searchGridId}container, ${searchGridId}c);
               ${saveQueryClickJs}
               ${rowDoubleClickJs}
               ${rowClickJs}
               ${selectionChangeJs}
               ${propertyClickedJs}
               ${menuEventsJs ? menuEventsJs : ""}
               ${multiplerSelectionMenuEventJs ? multiplerSelectionMenuEventJs : ""}
               if(${searchGridId}sg.pollingInterval > 0){
                   YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, ${searchGridId}sg, true)
               }
           </script>
        """
    }

    static def _getMenuEventJs(menuEvents, subMenuEvents) {
        def innerJs = "";
        def index = 0;
        menuEvents.each {id, actionArray ->
            innerJs += index == 0 ? "if" : "else if";
            innerJs += """(menuId == '${id}'){""";
            actionArray.each {actionName ->
                innerJs += """
                        YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    """
            }
            innerJs += """}""";
            index++;
        }
        subMenuEvents.each {parentId, subMap ->
            subMap.each {id, actionArray ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(parentId == '${parentId}' && menuId == '${id}'){"""
                actionArray.each {actionName ->
                    innerJs += """
                            YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        """
                }
                innerJs += """}""";
                index++;
            }
        }
        return innerJs;
    }
    def searchGrid = {attrs, body ->
        out << fSearchGrid(attrs, body());
    }

    static def getConfig(config, configXML, menuEvents, subMenuEvents, multipleSelectionMenuEvents, multipleSelectionSubMenuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
        def cArray = [];
        cArray.add("id: '${config["id"]}'")
        cArray.add("url: '${config["url"]}'")
        cArray.add("fieldsUrl: '${config["fieldsUrl"]}'")
        cArray.add("rootTag: '${config["rootTag"]}'")
        cArray.add("contentPath: '${config["contentPath"]}'")
        cArray.add("keyAttribute: '${config["keyAttribute"]}'")
        cArray.add("queryParameter: '${config["queryParameter"]}'")
        cArray.add("defaultSearchClass: '${config["defaultSearchClass"]}'")
        cArray.add("viewType: '${config["viewType"]}'")
        if (config["defaultView"])
            cArray.add("defaultView:'${config['defaultView']}'")
        if (config["title"])
            cArray.add("title:'${config['title']}'")
        if (config["queryEnabled"])
            cArray.add("queryEnabled:${config['queryEnabled']}")
        if (config["pollingInterval"])
            cArray.add("pollingInterval:${config['pollingInterval']}")
        if (config["timeout"])
            cArray.add("timeout:${config['timeout']}")
        if (config["maxRowsDisplayed"])
            cArray.add("maxRowsDisplayed:${config['maxRowsDisplayed']}")
        if (config["defaultQuery"])
            cArray.add("defaultFilter:'${config['defaultQuery']}'")
        if (config["searchClassesUrl"])
            cArray.add("searchClassesUrl:'${config['searchClassesUrl']}'")
        if (config["searchInEnabled"] != null)
            cArray.add("searchInEnabled:${config['searchInEnabled']}")
        if (config["bringAllProperties"] != null)
            cArray.add("bringAllProperties:${config['bringAllProperties']}")
        if (config["extraPropertiesToRequest"] != null)
            cArray.add("extraPropertiesToRequest:'${config['extraPropertiesToRequest']}'")
        if (config["multipleFieldSorting"] != null)
            cArray.add("multipleFieldSorting:${config['multipleFieldSorting']}")

        def menuItems = xml.MenuItems?.MenuItem;
        def menuItemArray = [];
        menuItems.each {menuItem ->
            menuItemArray.add(processMenuItem(menuItem, menuEvents, subMenuEvents));
        }
        cArray.add("menuItems:[${menuItemArray.join(',\n')}]");
        def multipleSelectionMenuItems = xml.MultiSelectionMenuItems?.MenuItem;
        def multipleSelectionMenuItemsArray = [];
        multipleSelectionMenuItems.each {menuItem ->
            multipleSelectionMenuItemsArray.add(processMenuItem(menuItem, multipleSelectionMenuEvents, multipleSelectionSubMenuEvents));
        }
        cArray.add("multiSelectionMenuItems:[${multipleSelectionMenuItemsArray.join(',\n')}]");
        def timeRangeSelector = xml.TimeRangeSelector;
        if (timeRangeSelector != null && timeRangeSelector.size() > 0)
        {
            timeRangeSelector = timeRangeSelector[0];
            cArray.add("timeRangeSelectorEnabled:true")
            cArray.add("""timeRangeConfig:{
                url:'${timeRangeSelector.@url.text()}',
                buttonConfigurationUrl:'${timeRangeSelector.@buttonConfigurationUrl.text()}',
                fromTimeProperty:'${timeRangeSelector.@fromTimeProperty.text()}',
                toTimeProperty:'${timeRangeSelector.@toTimeProperty.text()}',
                tooltipProperty:'${timeRangeSelector.@tooltipProperty.text()}',
                stringFromTimeProperty:'${timeRangeSelector.@stringFromTimeProperty.text()}',
                stringToTimeProperty:'${timeRangeSelector.@stringToTimeProperty.text()}',
                timeAxisLabelProperty:'${timeRangeSelector.@timeAxisLabelProperty.text()}',
                valueProperties:['${timeRangeSelector.@valueProperties.text().replaceAll(",", "','")}']
            }""")
        }
        def images = xml.Images?.Image;
        if (images.size() > 0) {
            def imageArray = [];
            images.each {image ->
                imageArray.add("""{
                    src:'${image.@src.text().encodeAsJavaScript()}',
                    visible:\"${image.@visible.text().encodeAsJavaScript()}\"
                }""")
            }
            cArray.add("images:[${imageArray.join(',\n')}]")
        }

        def columns = xml.Columns?.Column;
        def columnArray = [];
        columns.each {column ->
            def sortOrder = column.@sortOrder.toString().trim();
            def sortBy = column.@sortBy.toString().trim();
            def type = column.@type.toString().trim();
            def columnImages = null;
            if (type == 'image') {
                columnImages = getColumnImages(column);
            }
            columnArray.add("""{
                    attributeName:'${column.@attributeName}',
                    colLabel:'${column.@colLabel}',
                    ${sortBy != "" ? "sortBy:${sortBy}," : ""}
                    ${sortOrder != "" ? "sortOrder:'${sortOrder}'," : ""}
                    ${type != "" ? "type:'${type}'," : ""}
                    ${columnImages != null ? "images:[${columnImages.join(',\n')}]," : ""}
                    width:${column.@width}
                }""")
        }
        cArray.add("columns:[${columnArray.join(',\n')}]")

        def rowColors = xml.RowColors.RowColor;
        if (rowColors.size() > 0) {
            def rowColorsArray = [];
            rowColors.each {rowColor ->
                def textColor = rowColor.@textColor;
                rowColorsArray.add("""{
                    color:'${rowColor.@color}',
                    ${textColor != "" ? "textColor:'${textColor}'," : ""}
                    visible:\"${rowColor.@visible.text().encodeAsJavaScript()}\" 
                }""")
            }
            cArray.add("rowColors:[${rowColorsArray.join(',\n')}]")
        }
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

    static def processMenuItem(menuItem, eventMap, subMenuEvents) {
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
                def actionArray = [];
                actions.each {
                    actionArray.add(it.text());
                }
                eventMap.put(id, actionArray);
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
                    def subMap = subMenuEvents.get(id);
                    if (!subMap) {
                        subMap = [:]
                        subMenuEvents.put(id, subMap);
                    }
                    subMap.put(subMenuItem.@id, [subAction])
                }
                else {
                    def subActions = subMenuItem.action.Item;
                    if (subActions.size() > 0) {
                        def subMap = subMenuEvents.get(id);
                        if (!subMap) {
                            subMap = [:]
                            subMenuEvents.put(id, subMap);
                        }
                        def subActionsArray = [];
                        subActions.each {
                            subActionsArray.add(it.text())
                        }
                        subMap.put(subMenuItem.@id, subActionsArray)
                    }
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
    static def fSgMultiSelectionMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("MultiSelectionMenuItems", attrs, [], bodyString);
    }
    def sgMultiSelectionMenuItems = {attrs, body ->
        out << fSgMultiSelectionMenuItems(attrs, body())
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

    static def fTimeRangeSelector(attrs, bodyString) {
        def validAttrs = ["url", "buttonConfigurationUrl", "tooltipProperty", "fromTimeProperty", "toTimeProperty", "valueProperties", "stringFromTimeProperty", "stringToTimeProperty", "timeAxisLabelProperty"];
        return TagLibUtils.getConfigAsXml("TimeRangeSelector", attrs, validAttrs)
    }
    def timeRangeSelector = {attrs, body ->
        out << fTimeRangeSelector(attrs, body())
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
        def validAttrs = ["attributeName", "colLabel", "sortBy", "sortOrder", "width", "type"];
        return TagLibUtils.getConfigAsXml("Column", attrs, validAttrs, bodyString)
    }
    def sgColumn = {attrs, body ->
        out << fSgColumn(attrs, body())
    }
    static def fSgColumnImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString)
    }
    def sgColumnImages = {attrs, body ->
        out << fSgColumnImages(attrs, body());
    }
    static def fSgColumnImage(attrs, bodyString) {
        def validAttrs = ["visible", "src", "align"];
        return TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }

    def sgColumnImage = {attrs, body ->
        out << fSgColumnImage(attrs, "");
    }

    static def fSgRowColors(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("RowColors", attrs, [], bodyString)
    }
    def sgRowColors = {attrs, body ->
        out << fSgRowColors(attrs, body())
    }
    static def fSgRowColor(attrs, bodyString) {
        def validAttrs = ["color", "textColor", "visible"];
        return TagLibUtils.getConfigAsXml("RowColor", attrs, validAttrs)
    }
    def sgRowColor = {attrs, body ->
        out << fSgRowColor(attrs, "")
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