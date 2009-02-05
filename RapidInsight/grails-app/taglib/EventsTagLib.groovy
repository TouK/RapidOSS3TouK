
/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
* User: Administrator
* Date: Oct 15, 2008
* Time: 1:47:39 PM
* To change this template use File | Settings | File Templates.
*/
class EventsTagLib {
    static namespace = "rui";
    def events = {attrs, body ->
        def configXML = "<Notifications>${body()}</Notifications>";
        def searchGridPollInterval = attrs["searchResultsPollingInterval"] ? attrs["searchResultsPollingInterval"] : "0";
        def treeGridPollInterval = attrs["queriesPollingInterval"] ? attrs["queriesPollingInterval"] : "0";
        def searchIn = attrs["searchIn"] ? attrs["searchIn"] : "RsEvent";
        def rowMenus = [];
        def actions = [];
        def htmlDialogs = [];
        def fields = [];
        def columns = [];
        
        def ntXML = new XmlSlurper().parseText(configXML);
        def ntMenus = ntXML.NtMenus.NtMenu;
        ntMenus.each {menuItem ->
            def location = menuItem.@location.toString().trim();
            def id = menuItem.@id;

            def subMenuItemsArray = [];
            def subMenuItems = menuItem.NtSubMenus?.NtMenu;            
            if (subMenuItems) {
                  subMenuItems.each {subMenuItem ->
                        def subMenuItemId = subMenuItem.@id;                        
                        subMenuItemsArray.add([id: subMenuItemId, label: subMenuItem.@label, visible: subMenuItem.@visible, action: "${subMenuItemId}menuAction"]);
                        processMenuItem(subMenuItem,actions,htmlDialogs);

                  }
            }
            
            rowMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction",subMenuItems:subMenuItemsArray])
            processMenuItem(menuItem,actions,htmlDialogs);
        }
        def ntColumns = ntXML.Columns.Column;
        def columnsStr = "";
        ntColumns.each {
            columnsStr += SearchGridTagLib.fSgColumn(attributeName: it.@attributeName.toString(), width: it.@width.toString(),
                    colLabel: it.@colLabel.toString(), sortBy: it.@sortBy.toString(), sortOrder: it.@sortOrder.toString(), "")
        }



        
        out << TreeGridTagLib.fTreeGrid(id: "filterTree", url: "script/run/queryList?format=xml&type=event", rootTag: "Filters",
                keyAttribute: "id", contentPath: "Filter", title: "Saved Queries", expanded: "true", onNodeClicked: "setQueryAction", pollingInterval: treeGridPollInterval,
                TreeGridTagLib.fTgColumns([:],
                        TreeGridTagLib.fTgColumn(attributeName: "name", colLabel: "Name", width: "248", sortBy: "true", "")
                ) +
                        TreeGridTagLib.fTgMenuItems([:],
                                TreeGridTagLib.fTgMenuItem(id: "deleteQuery", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.nodeType == 'filter'", action: "deleteQueryAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "deleteQueryGroup", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'", action: "deleteQueryGroupAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryUpdate", label: "Update", visible: "params.data.nodeType == 'filter' && params.data.isPublic != 'true'", action: "queryUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryGroupUpdate", label: "Update", visible: "params.data.isPublic != 'true' && params.data.name != 'My Queries' && params.data.nodeType == 'group'", action: "queryGroupUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "copyQuery", label: "Copy Query", visible: "params.data.nodeType == 'filter'", action: "copyQueryAction", "")
                        ) +
                        TreeGridTagLib.fTgRootImages([:],
                                TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'group'", expanded: "images/rapidjs/component/tools/folder_open.gif", collapsed: "images/rapidjs/component/tools/folder.gif", "") +
                                        TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'filter'", expanded: "images/rapidjs/component/tools/filter.png", collapsed: "images/rapidjs/component/tools/filter.png", "")
                        )

        )

        out << RFormTagLib.fForm(id: "filterDialog", width: "35em", createUrl: "script/run/createQuery?queryType=event", editUrl: "script/run/editQuery?queryType=event",
                saveUrl: "searchQuery/save?format=xml&type=event", updateUrl: "searchQuery/update?format=xml&type=event", onSuccess: "refreshQueriesAction",
                """
                  <div>
                    <div class="hd">Save query</div>
                    <div class="bd">
                    <form method="POST" action="javascript://nothing">
                        <table>
                        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>View Name:</label></td><td width="50%"><select name="viewName" style="width:175px"/></td></tr>
                        </table>
                        <input type="hidden" name="id">
                        <input type="hidden" name="sortProperty">
                    </form>

                    </div>
                </div>
                """
        )
        out << RFormTagLib.fForm(id: "filterGroupDialog", width: "30em", saveUrl: "searchQueryGroup/save?format=xml&type=event",
                updateUrl: "searchQueryGroup/update?format=xml&type=event", onSuccess: "refreshQueriesAction",
                """
                  <div>
                    <div class="hd">Save group</div>
                    <div class="bd">
                    <form method="POST" action="javascript://nothing">
                        <table>
                        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
                        </table>
                        <input type="hidden" name="id">
                    </form>
                    </div>
                </div>
                """
        )

        def searchGridImagesTagList=""
        ntXML.Images.Image.each{
            searchGridImagesTagList+=SearchGridTagLib.fSgImage(visible: it.@visible.toString(), src: it.@src.toString(), "");
        }
        out << SearchGridTagLib.fSearchGrid(id: "searchGrid", url: "search?format=xml&searchIn=${searchIn}", queryParameter: "query", rootTag: "Objects", contentPath: "Object",
                keyAttribute: "id", totalCountAttribute: "total", offsetAttribute: "offset", sortOrderAttribute: "sortOrder", title: "Events", onSaveQueryClicked: "saveQueryAction",
                pollingInterval: searchGridPollInterval, fieldsUrl: "script/run/getViewFields?format=xml",
                SearchGridTagLib.fSgMenuItems([:],
                        getMenuXml(rowMenus)
                ) +
                  SearchGridTagLib.fSgImages([:],searchGridImagesTagList)+
                  SearchGridTagLib.fSgColumns([:], columnsStr)

        )
        out << getHtmlDialogsXml(htmlDialogs)
        out << getActionXml(actions);

        out << ActionsTagLib.fAction(id: "deleteQueryAction", type: "request", url: "searchQuery/delete?format=xml", onSuccess: "refreshQueriesAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "deleteQueryGroupAction", type: "request", url: "searchQueryGroup/delete?format=xml", onSuccess: "refreshQueriesAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "saveQueryAction", type: "function", componentId: "filterDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.CREATE_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{query:params.query, sortProperty:YAHOO.rapidjs.Components['searchGrid'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['searchGrid'].getSortOrder()}")
        )
        out << ActionsTagLib.fAction(id: "setQueryAction", type: "function", componentId: "searchGrid", function: "setQueryWithView", condition: "params.data.nodeType == 'filter'",
                ActionsTagLib.fFunctionArg([:], "params.data.query") +
                        ActionsTagLib.fFunctionArg([:], "params.data.viewName")
        )
        out << ActionsTagLib.fAction(id: "queryUpdateAction", type: "function", componentId: "filterDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.EDIT_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{queryId:params.data.id}")
        )
        out << ActionsTagLib.fAction(id: "queryGroupUpdateAction", type: "function", componentId: "filterGroupDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.EDIT_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{name:params.data.name, id:params.data.id}")
        )
        out << ActionsTagLib.fAction(id: "copyQueryAction", type: "function", componentId: "filterDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.CREATE_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{name:'', query:params.data.query, group:params.data.group, viewName:params.data.viewName}")
        )
        out << ActionsTagLib.fAction(id: "refreshQueriesAction", type: "function", function: "poll", componentId: "filterTree", "")
        out << """
                <script type="text/javascript">
                    var searchGrid = YAHOO.rapidjs.Components['searchGrid'];
                    var tree = YAHOO.rapidjs.Components['filterTree'];
                    ${getConvsersionJs(ntXML.NtConversions.NtConversion)}
                    tree.addToolbarButton({
                        className:'r-filterTree-groupAdd',
                        scope:this,
                        tooltip: 'Add group',
                        click:function() {
                            YAHOO.rapidjs.Components['filterGroupDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE);
                        }
                    });
                    tree.addToolbarButton({
                        className:'r-filterTree-queryAdd',
                        scope:this,
                        tooltip: 'Add query',
                        click:function() {
                            YAHOO.rapidjs.Components['filterDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE);
                        }
                    });
                    tree.poll();
                    searchGrid.poll();
                    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

                    Event.onDOMReady(function() {
                        var layout = new YAHOO.widget.Layout({
                            units: [
                                { position: 'top', body: 'top', resize: false, height:45},
                                { position: 'center', body: searchGrid.container.id, resize: false, gutter: '1px' },
                                { position: 'left', width: 250, resize: true, body: tree.container.id, scroll: false}
                            ]
                        });
                        layout.on('render', function(){
                            var topUnit = layout.getUnitByPosition('top');
                            YAHOO.util.Dom.setStyle(topUnit.get('wrap'), 'background-color', '#BBD4F6')
                            var header = topUnit.body;
                            YAHOO.util.Dom.setStyle(header, 'border', 'none');
                            var left = layout.getUnitByPosition('left').body;
                            YAHOO.util.Dom.setStyle(left, 'top', '1px');
                        });
                        layout.render();
                        var layoutCenter = layout.getUnitByPosition('center');
                        var layoutLeft = layout.getUnitByPosition('left');
                        layoutLeft.on('resize', function(){
                            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
                        });

                        searchGrid.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
                        layout.on('resize', function() {
                            searchGrid.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
                        });
                        tree.resize(layoutLeft.getSizes().body.w, layoutLeft.getSizes().body.h);
                        layout.on('resize', function() {
                            tree.resize(layoutLeft.getSizes().body.w, layoutLeft.getSizes().body.h);
                        });
                        window.layout = layout;

                    })
                </script>
                """
    }
    def processMenuItem(menuItem,actions,htmlDialogs)
    {
        def id = menuItem.@id;
        def actionType = menuItem.@actionType.toString().trim();
        if (actionType == "htmlDialog") {
            htmlDialogs.add([id: "${id}menuHtml", width: menuItem.@width.toString(), height: menuItem.@height.toString(), x:menuItem.@x.toString(), y:menuItem.@y.toString()])
            actions.add([id: "${id}menuAction", type: actionType, url: menuItem.@url.toString(), title: menuItem.@title.toString(), component: "${id}menuHtml"])
        }
        else if(actionType == "link"){
            actions.add([id: "${id}menuAction", type: actionType, url: menuItem.@url.toString()])
        }
        else if (actionType == "update" || actionType == "execute") {
            def params = menuItem.parameters.Item;
            def pMap = [:]
            params.each {
                pMap.put(it.@key.toString(), it.@value.toString())
            }
            actions.add([id: "${id}menuAction", type: actionType, script: menuItem.@script.toString(), parameters: pMap]);
        }
    }
    def evMenus = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("NtMenus", attrs, [], body());
    }
    def evMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible", "x", "y"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("NtMenu", attrs, validAttrs,body());
    }
    def evSubMenus = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("NtSubMenus", attrs, [], body());
    }    
    def evConversions = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("NtConversions", attrs, [], body());
    }
    def evConversion = {attrs, body ->
        def validAttrs = ["type", "format", "property", "function", "mapping"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("NtConversion", attrs, validAttrs);
    }
    def evColumns = {attrs, body ->
        out << SearchGridTagLib.fSgColumns(attrs, body())
    }
    def evColumn = {attrs, body ->
        out << SearchGridTagLib.fSgColumn(attrs, "")
    }

    def evImages = {attrs, body ->
        out << fEvImages(attrs, body())
    }
    static def fEvImages(attrs, bodyString) {
        return com.ifountain.rui.util.TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString)
    }
    
    def evImage = {attrs, body ->
        out << fEvImage(attrs, "")
    }

    static def fEvImage(attrs, bodyString) {
        def validAttrs = ["src", "visible"];
        return com.ifountain.rui.util.TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }


    def getMenuXml(menus) {
        def output = "";

        menus.each {

            def subMenuItemsBody=""
            if(it.subMenuItems?.size()>0)
            {
               subMenuItemsBody+=SearchGridTagLib.fSgSubmenuItems([:],getMenuXml(it.subMenuItems)) 
            }

            output += SearchGridTagLib.fSgMenuItem(it, subMenuItemsBody)
        }
        return output;
    }

    def getActionXml(actions) {
        def output = ""
        actions.each {
            def type = it.type;
            if (type == "htmlDialog") {
                output += ActionsTagLib.fAction(id: it.id, type: "function", componentId: it.component, function: "show",
                        ActionsTagLib.fFunctionArg([:], it.url) +
                                ActionsTagLib.fFunctionArg([:], it.title)
                )
            }
            else if(type == "link"){
                output += ActionsTagLib.fAction(id: it.id, type: "link", url: it.url, "");
            }
            else if (type == "execute" || type == "update") {
                def paramString = "";
                it.parameters.each {k, v ->
                    paramString += ActionsTagLib.fRequestParam(key: k, value: v, "")
                }
                def url = "script/run/${it.script}?format=xml"
                def actionType = type == "execute" ? "request" : "merge"
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, components: ["searchGrid"], paramString);
            }
        }
        return output;
    }

    def getHtmlDialogsXml(htmlDialogs) {
        def output = "";
        htmlDialogs.each {
            output += HtmlTagLib.fHtml(id: it.id, iframe: "false", "")
            output += PopupWindowTagLib.fPopupWindow(componentId: it.id, width: it.width, height: it.height, x:it.x, y:it.y, "")
        }
        return output;
    }
    def getConvsersionJs(conversionsXml) {
        def convString = "";
        def convIndex = 0;
        conversionsXml.each {
            def type = it.@type.toString().trim();
            if (type == "date") {
                convString += convIndex == 0 ? "if" : "else if"
                def format = it.@format.toString();
                convString += """(key == '${it.@property}'){
                try{
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("${format}");
                }
                catch(e){}
               }
               """
                convIndex++;
            }
            else if (type == "function") {
                convString += convIndex == 0 ? "if" : "else if"
                convString += """(key == '${it.@property}'){
                try{
                    return ${it.@function}(key, value, data, el);
                }
                catch(e){}
               }
               """
                convIndex++;
            }
            else if (type == "mapping") {
                def mArray = [];
                def mappings = it.mapping.Item;
                mappings.each {
                    mArray.add("'${it.@key}':'${it.@value}'")
                }
                convString += convIndex == 0 ? "if" : "else if"
                convString += """(key == '${it.@property}'){
                    var mapping = {${mArray.join(',')}}
                    return mapping[value] || value;
               }
               """
                convIndex++;
            }

        }
        return """
           searchGrid.renderCellFunction = function(key, value, xmlData, el){
              var data = xmlData.getAttributes();
              ${convString}
              return value;
           }
        """
    }
}