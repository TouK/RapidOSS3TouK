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
    def searchList = {attrs, body ->
        validateAttributes(attrs);
        def searchListId = attrs["id"];
        def configXML = "<SearchList>${body()}</SearchList>";
        def menuEvents = [:]
        def propertyMenuEvents = [:]
        def configStr = getConfig(attrs, configXML, menuEvents, propertyMenuEvents);
        out << """
           <script type="text/javascript">
               var ${searchListId}c = ${configStr};
               var ${searchListId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${searchListId}sl = new YAHOO.rapidjs.component.search.SearchList(${searchListId}container, ${searchListId}c);
               ${searchListId}sl.events["cellMenuClick"].subscribe(function(key, value, data, id) {

               }, this, true);
               if(${searchListId}sl.pollingInterval > 0){
                   ${searchListId}sl.poll();
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

    def getConfig(config, configXML, menuEvents, propertyMenuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
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
        if (config["defaultFields"]) {
            def fArray = [];
            config['defaultFields'].each {
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

        def menuItems = xml.MenuItems?.MenuItem;
        def menuItemArray = [];
        menuItems.each {menuItem ->
            menuItemArray.add(processMenuItem(menuItem, menuEvents));
        }
        cArray.add("menuItems:[${menuItemArray.join(',\n')}]");
        def pmenuItems = xml.PropertyMenuItems?.MenuItem;

        def pmenuItemArray = [];
        pmenuItems.each {menuItem ->
            pmenuItemArray.add(processMenuItem(menuItem, propertyMenuEvents));
        }
        cArray.add("propertyMenuItems:[${pmenuItemArray.join(',\n')}]");

        def images = xml.Images?.Image;
        def imageArray = [];
        images.each {image ->
            imageArray.add("""{
                    src:'${image.@src}',
                    visible:\"${image.@visible}\"
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
                fldsArray.add("'${it.text()}'");
            }
            fieldArray.add("""{
                    exp:"${exp}",
                    fields:[${fldsArray.join(',')}]
                }""")
        }
        cArray.add("fields:[${fieldArray.join(',\n')}]")
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

    def slMenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("MenuItems", attrs, [], body());
    }

    def slPropertyMenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("PropertyMenuItems", attrs, [], body());
    }
    def slSubmenuItems = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("SubmenuItems", attrs, [], body())
    }

    def slMenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs, body());
    }

    def slSubmenuItem = {attrs, body ->
        def validAttrs = ["id", "label", "visible", "action"];
        out << TagLibUtils.getConfigAsXml("SubmenuItem", attrs, validAttrs)
    }

    def slImages = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("Images", attrs, [], body())
    }
    def slImage = {attrs, body ->
        def validAttrs = ["src", "visible"];
        out << TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }

    def slFields = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("Fields", attrs, [], body())
    }
    def slField = {attrs, body ->
        def validAttrs = ["exp", "fields"];
        out << TagLibUtils.getConfigAsXml("Field", attrs, validAttrs)
    }

}