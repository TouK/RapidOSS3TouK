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
 * Date: Oct 8, 2008
 * Time: 1:25:41 PM
 */
class SearchListTagLib {
    static namespace = "rui"
    static def fSearchList(attrs, bodyString) {
        def searchListId = attrs["id"];
        def configXML = "<SearchList>${bodyString}</SearchList>";
        def onSaveQueryClick = attrs["onSaveQueryClicked"];
        def onRowDoubleClick = attrs["onRowDoubleClicked"];
        def onPropertyClicked = attrs["onPropertyClicked"];
        def onRowClick = attrs["onRowClicked"];
        def onSelectionChanged = attrs["onSelectionChanged"];
        def onRowHeaderClick = attrs["onRowHeaderClicked"];
        def saveQueryClickJs = "";
        def rowDoubleClickJs = "";
        def rowClickJs = "";
        def propertyClickedJs = "";
        def rowHeaderClickedJs = "";
        def selectionChangedJs = "";
        if (onSaveQueryClick != null) {
            getActionsArray(onSaveQueryClick).each {actionName ->
                saveQueryClickJs += """
                   ${searchListId}sl.events['saveQueryClicked'].subscribe(function(query){
                       var params = {query:query};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }

        }
        if (onRowDoubleClick != null) {
            getActionsArray(onRowDoubleClick).each {actionName ->
                rowDoubleClickJs += """
                   ${searchListId}sl.events['rowDoubleClicked'].subscribe(function(xmlData, event){
                       var params = {data:xmlData.getAttributes(), event:event};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }

        }
        if (onRowClick != null) {
            getActionsArray(onRowClick).each {actionName ->
                rowClickJs += """
                   ${searchListId}sl.events['rowClicked'].subscribe(function(xmlData, event){
                       var params = {data:xmlData.getAttributes(), event:event};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onPropertyClicked != null) {
            getActionsArray(onPropertyClicked).each {actionName ->
                propertyClickedJs += """
                   ${searchListId}sl.events['propertyClicked'].subscribe(function(key, value, xmlData){
                       var params = {data:xmlData.getAttributes(), key:key, value:value};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onSelectionChanged != null) {
            getActionsArray(onSelectionChanged).each {actionName ->
                selectionChangedJs += """
                   ${searchListId}sl.events['selectionChanged'].subscribe(function(xmlData){
                       var params = {data:xmlData.getAttributes()};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        if (onRowHeaderClick != null) {
            getActionsArray(onRowHeaderClick).each {actionName ->
                rowHeaderClickedJs += """
                   ${searchListId}sl.events['rowHeaderClicked'].subscribe(function(xmlData){
                       var params = {data:xmlData.getAttributes()};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }
        }
        def menuEvents = [:]
        def subMenuEvents = [:]
        def propertyMenuEvents = [:]
        def menuEventsJs;
        def propMenuEventsJs;
        def configStr = getConfig(attrs, configXML, menuEvents, subMenuEvents, propertyMenuEvents);
        if (menuEvents.size() > 0 || subMenuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            menuEvents.each {id, actionArray ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(menuId == '${id}'){"""
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
            menuEventsJs = """
               ${searchListId}sl.events['rowHeaderMenuClicked'].subscribe(function(xmlData, menuId, parentId){
                   var params = {data:xmlData.getAttributes(), menuId:menuId, parentId:parentId};
                   ${innerJs}
                }, this, true);
            """

        }
        if (propertyMenuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            propertyMenuEvents.each {id, actionArray ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(menuId == '${id}'){"""
                actionArray.each {actionName ->
                    innerJs += """
                           YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                        """
                }
                innerJs += """}""";
                index++;
            }
            propMenuEventsJs = """
               ${searchListId}sl.events['cellMenuClicked'].subscribe(function(key, value, xmlData, menuId){
                   var params = {data:xmlData.getAttributes(), menuId:menuId, key:key, value:value};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${searchListId}c = ${configStr};
               var ${searchListId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${searchListId}sl = new YAHOO.rapidjs.component.search.SearchList(${searchListId}container, ${searchListId}c);
               ${saveQueryClickJs}
               ${rowDoubleClickJs}
               ${rowClickJs}
               ${propertyClickedJs}
               ${rowHeaderClickedJs}
               ${selectionChangedJs}
               ${menuEventsJs ? menuEventsJs : ""}
               ${propMenuEventsJs ? propMenuEventsJs : ""}
               if(${searchListId}sl.pollingInterval > 0){
                   YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, ${searchListId}sl, true)
               }
           </script>
        """
    }
    def searchList = {attrs, body ->
        out << fSearchList(attrs, body());
    }

    static def getConfig(config, configXML, menuEvents, subMenuEvents, propertyMenuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
        def cArray = [];
        cArray.add("id: '${config["id"]}'")
        cArray.add("url: '${config["url"]}'")
        cArray.add("rootTag: '${config["rootTag"]}'")
        cArray.add("contentPath: '${config["contentPath"]}'")
        cArray.add("keyAttribute: '${config["keyAttribute"]}'")
        cArray.add("defaultSearchClass: '${config["defaultSearchClass"]}'")
        cArray.add("queryParameter: '${config["queryParameter"]}'")
        cArray.add("totalCountAttribute: '${config["totalCountAttribute"]}'")
        cArray.add("offsetAttribute: '${config["offsetAttribute"]}'")
        cArray.add("sortOrderAttribute: '${config["sortOrderAttribute"]}'")
        if (config["title"])
            cArray.add("title:'${config['title']}'")
        if (config["pollingInterval"])
            cArray.add("pollingInterval:${config['pollingInterval']}")
        if (config["timeout"])
            cArray.add("timeout:${config['timeout']}")
        if (config["defaultFields"]) {
            def fArray = [];
            config['defaultFields'].each {
                fArray.add("'${it.trim()}'");
            }
            cArray.add("defaultFields:[${fArray.join(",")}]")
        }
        if (config["maxRowsDisplayed"])
            cArray.add("maxRowsDisplayed:${config['maxRowsDisplayed']}")
        if (config["showMax"])
            cArray.add("showMax:${config['showMax']}")
        if (config["defaultFilter"])
            cArray.add("defaultFilter:'${config['defaultFilter']}'")
        if (config["lineSize"])
            cArray.add("lineSize:${config['lineSize']}")
        if (config["rowHeaderAttribute"])
            cArray.add("rowHeaderAttribute:'${config['rowHeaderAttribute']}'")
        if (config["searchInEnabled"] != null)
            cArray.add("searchInEnabled:${config['searchInEnabled']}")
        if (config["bringAllProperties"] != null)
            cArray.add("bringAllProperties:${config['bringAllProperties']}")
        if (config["searchClassesUrl"] != null)
            cArray.add("searchClassesUrl:'${config['searchClassesUrl']}'")
        if (config["extraPropertiesToRequest"] != null)
            cArray.add("extraPropertiesToRequest:'${config['extraPropertiesToRequest']}'")

        def menuItems = xml.MenuItems?.MenuItem;
        def menuItemArray = [];
        menuItems.each {menuItem ->
            menuItemArray.add(processMenuItem(menuItem, menuEvents, subMenuEvents));
        }
        cArray.add("menuItems:[${menuItemArray.join(',\n')}]");
        def pmenuItems = xml.PropertyMenuItems?.MenuItem;

        def pmenuItemArray = [];
        pmenuItems.each {menuItem ->
            pmenuItemArray.add(processMenuItem(menuItem, propertyMenuEvents, [:]));
        }
        cArray.add("propertyMenuItems:[${pmenuItemArray.join(',\n')}]");
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
                stringFromTimeProperty:'${timeRangeSelector.@stringFromTimeProperty.text()}',
                tooltipProperty:'${timeRangeSelector.@tooltipProperty.text()}',
                stringToTimeProperty:'${timeRangeSelector.@stringToTimeProperty.text()}',
                timeAxisLabelProperty:'${timeRangeSelector.@timeAxisLabelProperty.text()}',
                valueProperties:['${timeRangeSelector.@valueProperties.text().replaceAll(",", "','")}']
            }""")
        }
        def images = xml.Images?.Image;
        def imageArray = [];
        images.each {image ->
            imageArray.add("""{
                    src:'${image.@src.text().encodeAsJavaScript()}',
                    visible:\"${image.@visible.text().encodeAsJavaScript()}\"
                }""")
        }
        cArray.add("images:[${imageArray.join(',\n')}]")
        def fields = xml.Fields?.Field;
        def fieldArray = [];
        fields.each {field ->
            def exp = field.@exp;
            def flds = field.fields.Item;
            def fldsArray = [];
            flds.each {
                fldsArray.add("'${it.text().trim()}'");
            }
            fieldArray.add("""{
                    exp:"${exp.encodeAsJavaScript()}",
                    fields:[${fldsArray.join(',')}]
                }""")
        }
        cArray.add("fields:[${fieldArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
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
                        def subActionsArray = [];
                        subActions.each {
                            subActionsArray.add(it.text())
                        }
                        def subMap = subMenuEvents.get(id);
                        if (!subMap) {
                            subMap = [:]
                            subMenuEvents.put(id, subMap);
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
    static def fSlMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("MenuItems", attrs, [], bodyString);
    }
    def slMenuItems = {attrs, body ->
        out << fSlMenuItems(attrs, body());
    }
    static def fSlPropertyMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("PropertyMenuItems", attrs, [], bodyString);
    }
    def slPropertyMenuItems = {attrs, body ->
        out << fSlPropertyMenuItems(attrs, body());
    }
    static def fSlSubmenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], bodyString);
    }
    def slSubmenuItems = {attrs, body ->
        out << fSlSubmenuItems(attrs, body());
    }

    static def fSlMenuItem(attrs, bodyString) {
        def validAttrs = ["id", "label", "visible", "action"];
        return TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, bodyString);
    }
    def slMenuItem = {attrs, body ->
        out << fSlMenuItem(attrs, body())
    }
    static def fSlImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString);
    }
    def slImages = {attrs, body ->
        out << fSlImages(attrs, body())
    }
    static def fSlImage(attrs, bodyString) {
        def validAttrs = ["src", "visible"];
        return TagLibUtils.getConfigAsXml("Image", attrs, validAttrs);
    }
    def slImage = {attrs, body ->
        out << fSlImage(attrs, "")
    }
    static def fSlFields(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Fields", attrs, [], bodyString);
    }
    def slFields = {attrs, body ->
        out << fSlFields(attrs, body());
    }
    static def fSlField(attrs, bodyString) {
        def validAttrs = ["exp", "fields"];
        return TagLibUtils.getConfigAsXml("Field", attrs, validAttrs)
    }
    def slField = {attrs, body ->
        out << fSlField(attrs, "")
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