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
 * Date: Oct 14, 2008
 * Time: 9:47:43 AM
 */
class TopologySearchTagLib {
    static namespace = "rui";

    def topologySearch = {attrs, body ->
        def configXML = "<TopologySearch>${body()}</TopologySearch>";
        def searchListPollInterval = attrs["searchResultsPollingInterval"] ? attrs["searchResultsPollingInterval"] : "0";
        def treeGridPollInterval = attrs["queriesPollingInterval"] ? attrs["queriesPollingInterval"] : "0";
        def lineSize = attrs["numberOfLines"] ? attrs["numberOfLines"] : "3";
        def rowMenus = [];
        def propertyMenus = [];
        def actions = [];
        def htmlDialogs = [];
        def fields = [];
        def emphasizeds = [];

        def tsXML = new XmlSlurper().parseText(configXML);
        def tsMenus = tsXML.TsMenus.TsMenu;
        tsMenus.each {menuItem ->
            def location = menuItem.@location.toString().trim();
            def id = menuItem.@id;
            if (location == "row") {
                rowMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
            }
            else if (location == "property") {
                propertyMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
            }
            def actionType = menuItem.@actionType.toString().trim();
            if (actionType == "htmlDialog") {
                htmlDialogs.add([id: "${id}menuHtml", width: menuItem.@width.toString(), height: menuItem.@height.toString()])
                actions.add([id: "${id}menuAction", type: actionType, url: menuItem.@url.toString(), title: menuItem.@title.toString(), component: "${id}menuHtml"])
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
        def searchResults = tsXML.TsSearchResults.TsSearchResult;
        searchResults.each {searchResult ->
            def alias = searchResult.@alias.toString();
            def fieldsXml = searchResult.properties.Item;
            def fArray = [];
            fieldsXml.each {
                fArray.add(it.text());
            }
            def emphasizedXml = searchResult.emphasizeds.Item;
            def empArray = [];
            emphasizedXml.each {
                empArray.add("key == '${it.text()}'");
            }
            fields.add([alias: alias, fields: fArray]);
            emphasizeds.add([alias: alias, emphasizeds: empArray]);
        }

        out << TreeGridTagLib.fTreeGrid(id: "filterTree", url: "script/run/queryList?format=xml&type=topology", rootTag: "Filters",
                keyAttribute: "id", contentPath: "Filter", title: "SavedQueries", expanded: "true", onNodeClick: "setQueryAction", pollingInterval: treeGridPollInterval,
                TreeGridTagLib.fTgColumns([:],
                        TreeGridTagLib.fTgColumn(attributeName: "name", colLabel: "Name", width: "248", sortBy: "true", "")
                ) +
                        TreeGridTagLib.fTgMenuItems([:],
                                TreeGridTagLib.fTgMenuItem(id: "deleteQuery", label: "Delete", visible: "data.isPublic != 'true' && data.nodeType == 'filter'", action: "deleteQueryAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "deleteQueryGroup", label: "Delete", visible: "data.isPublic != 'true' && data.name != 'Default' && data.nodeType == 'group'", action: "deleteQueryGroupAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryUpdate", label: "Update", visible: "data.nodeType == 'filter' && data.isPublic != 'true'", action: "queryUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryGroupUpdate", label: "Update", visible: "data.isPublic != 'true' && data.name != 'Default' && data.nodeType == 'group'", action: "queryGroupUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "copyQuery", label: "Copy Query", visible: "data.nodeType == 'filter'", action: "copyQueryAction", "")
                        ) +
                        TreeGridTagLib.fTgRootImages([:],
                                TreeGridTagLib.fTgRootImage(visible: "data.nodeType == 'group'", expanded: "images/rapidjs/component/tools/folder_open.gif", collapsed: "images/rapidjs/component/tools/folder.gif", "") +
                                        TreeGridTagLib.fTgRootImage(visible: "data.nodeType == 'filter'", expanded: "images/rapidjs/component/tools/filter.png", collapsed: "images/rapidjs/component/tools/filter.png", "")
                        )
        )

        out << RFormTagLib.fForm(id: "filterDialog", width: "35em", createUrl: "script/run/createQuery?queryType=topology", editUrl: "script/run/editQuery?queryType=topology",
                saveUrl: "searchQuery/save?format=xml&type=topology", updateUrl: "searchQuery/update?format=xml&type=topology", onSuccess: "refreshQueriesAction",
                """
                  <div>
                    <div class="hd">Save query</div>
                    <div class="bd">
                    <form method="POST" action="javascript://nothing">
                        <table>
                        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="group" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Query Name:</label></td><td width="50%"><input type="textbox" name="name" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Query:</label></td><td width="50%"><input type="textbox" name="query" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Sort Property:</label></td><td width="50%"><select name="sortProperty" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Sort Order:</label></td><td width="50%">
                            <select name="sortOrder" style="width:175px"><option value="asc">asc</option><option value="desc">desc</option></select>
                        </td></tr>
                        </table>
                        <input type="hidden" name="id">
                    </form>

                    </div>
                </div>
                """
        )
        out << RFormTagLib.fForm(id: "filterGroupDialog", width: "30em", saveUrl: "searchQueryGroup/save?format=xml&type=topology",
                updateUrl: "searchQueryGroup/update?format=xml&type=topology", onSuccess: "refreshQueriesAction",
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
        out << SearchListTagLib.fSearchList(id: "searchList", url: "search?format=xml&searchIn=RsTopologyObject", queryParameter: "query", rootTag: "Objects", contentPath: "Object",
                keyAttribute: "id", totalCountAttribute: "total", offsetAttribute: "offset", sortOrderAttribute: "sortOrder", lineSize: "3", title: "Smarts Objects",
                defaultFields: ['creationClassName', 'name', 'description', 'displayName', 'isManaged'], onSaveQueryClick: "saveQueryAction",
                pollingInterval: searchListPollInterval, lineSize: lineSize,
                SearchListTagLib.fSlMenuItems([:],
                        SearchListTagLib.fSlMenuItem(id: "topMap", label: "Show Map", "") + getMenuXml(rowMenus)
                ) +
                        SearchListTagLib.fSlPropertyMenuItems([:],
                                SearchListTagLib.fSlMenuItem(id: "sortAsc", label: "Sort asc", action: "searchListSortAscAction", "") +
                                        SearchListTagLib.fSlMenuItem(id: "sortDesc", label: "Sort desc", action: "searchListSortDescAction", "") +
                                        SearchListTagLib.fSlMenuItem(id: "except", label: "Except", action: "exceptAction", "") +
                                        SearchListTagLib.fSlMenuItem(id: "greaterThan", label: "Greater than", action: "greaterThanAction", visible: "YAHOO.lang.isNumber(parseInt(value))", "") +
                                        SearchListTagLib.fSlMenuItem(id: "lessThan", label: "Less than", action: "lessThanAction", visible: "YAHOO.lang.isNumber(parseInt(value))", "") +
                                        SearchListTagLib.fSlMenuItem(id: "greaterThanOrEqualTo", label: "Greater than or equal to", action: "greaterThanOrEqualToAction", visible: "YAHOO.lang.isNumber(parseInt(value))", "") +
                                        SearchListTagLib.fSlMenuItem(id: "lessThanOrEqualTo", label: "Less than or equal to", action: "lessThanOrEqualToAction", visible: "YAHOO.lang.isNumber(parseInt(value))", "") +
                                        getMenuXml(propertyMenus)
                        ) +
                        SearchListTagLib.fSlFields([:], getFieldsXml(fields))

        )
        out << getHtmlDialogsXml(htmlDialogs)
        out << getActionXml(actions);

        out << ActionsTagLib.fAction(id: "searchListSortAscAction", type: "function", componentId: "searchList", function: "sort",
                ActionsTagLib.fFunctionArg([:], "params.key") +
                        ActionsTagLib.fFunctionArg([:], "'asc'")
        )
        out << ActionsTagLib.fAction(id: "searchListSortDescAction", type: "function", componentId: "searchList", function: "sort",
                ActionsTagLib.fFunctionArg([:], "params.key") +
                        ActionsTagLib.fFunctionArg([:], "'desc'")
        )
        out << ActionsTagLib.fAction(id: "exceptAction", type: "function", componentId: "searchList", function: "appendExceptQuery",
                ActionsTagLib.fFunctionArg([:], "params.key") +
                        ActionsTagLib.fFunctionArg([:], "params.value")
        )
        out << ActionsTagLib.fAction(id: "greaterThanAction", type: "function", componentId: "searchList", function: "appendToQuery",
                ActionsTagLib.fFunctionArg([:], "params.key + ':{' + params.value + ' TO *}'")
        )
        out << ActionsTagLib.fAction(id: "greaterThanOrEqualToAction", type: "function", componentId: "searchList", function: "appendToQuery",
                ActionsTagLib.fFunctionArg([:], "params.key + ':[' + params.value + ' TO *]'")
        )
        out << ActionsTagLib.fAction(id: "lessThanOrEqualToAction", type: "function", componentId: "searchList", function: "appendToQuery",
                ActionsTagLib.fFunctionArg([:], "params.key + ':[* TO ' + params.value + ']'")
        )
        out << ActionsTagLib.fAction(id: "lessThanAction", type: "function", componentId: "searchList", function: "appendToQuery",
                ActionsTagLib.fFunctionArg([:], "params.key + ':{* TO ' + params.value + '}'")
        )
        out << ActionsTagLib.fAction(id: "deleteQueryAction", type: "request", url: "searchQuery/delete?format=xml", onSuccess: "refreshQueriesAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "deleteQueryGroupAction", type: "request", url: "searchQueryGroup/delete?format=xml", onSuccess: "refreshQueriesAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "saveQueryAction", type: "function", componentId: "filterDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.CREATE_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{query:params.query, sortProperty:YAHOO.rapidjs.Components['searchList'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['searchList'].getSortOrder()}")
        )
        out << ActionsTagLib.fAction(id: "setQueryAction", type: "function", componentId: "searchList", function: "setQuery", condition: "params.data.nodeType == 'filter'",
                ActionsTagLib.fFunctionArg([:], "params.data.query") +
                        ActionsTagLib.fFunctionArg([:], "params.data.sortProperty") +
                        ActionsTagLib.fFunctionArg([:], "params.data.sortOrder")
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
                        ActionsTagLib.fFunctionArg([:], "{name:'', query:params.data.query, group:params.data.group, sortProperty:params.data.sortProperty, sortOrder:params.data.sortOrder}")
        )
        out << ActionsTagLib.fAction(id: "refreshQueriesAction", type: "function", function: "poll", componentId: "filterTree", "")
        out << """
                <script type="text/javascript">
                    var searchList = YAHOO.rapidjs.Components['searchList'];
                    var tree = YAHOO.rapidjs.Components['filterTree'];
                    searchList.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
                         var objectName = xmlData.getAttribute("name");
                         if( id == "topMap" )
                         {
                            var deviceName;
                            if( xmlData.getAttribute("computerSystemName") )
                            {
                               deviceName = xmlData.getAttribute("computerSystemName");
                            }
                            else if( xmlData.getAttribute("a_ComputerSystemName") )
                            {
                                deviceName = xmlData.getAttribute("a_ComputerSystemName");
                            }
                            else
                            {
                               deviceName = objectName;
                            }

                            var url = "topology.gsp?name="+deviceName;
                            window.location = url;
                         }

                    }, this, true);
                    ${getConvsersionJs(tsXML.TsConversions.TsConversion, emphasizeds)}
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

                    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

                    Event.onDOMReady(function() {
                        var layout = new YAHOO.widget.Layout({
                            units: [
                                { position: 'top', body: 'top', resize: false, height:45},
                                { position: 'center', body: searchList.container.id, resize: false, gutter: '1px' },
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
                        var layoutLeft = layout.getUnitByPosition('left');
                        layoutLeft.on('resize', function(){
                            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
                        });

                        searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        layout.on('resize', function() {
                            searchList.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        });
                        tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        layout.on('resize', function() {
                            tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        });
                        window.layout = layout;

                    })
                </script>
                """
    }

    def tsMenus = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("TsMenus", attrs, [], body());
    }
    def tsMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible"]
        out << TagLibUtils.getConfigAsXml("TsMenu", attrs, validAttrs);
    }

    def tsSearchResults = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("TsSearchResults", attrs, [], body());
    }
    def tsSearchResult = {attrs, body ->
        def validAttrs = ["alias", "properties", "emphasizeds"]
        out << TagLibUtils.getConfigAsXml("TsSearchResult", attrs, validAttrs);
    }

    def tsConversions = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("TsConversions", attrs, [], body());
    }
    def tsConversion = {attrs, body ->
        def validAttrs = ["type", "format", "property", "function", "mapping"]
        out << TagLibUtils.getConfigAsXml("TsConversion", attrs, validAttrs);
    }

    def getMenuXml(menus) {
        def output = "";
        menus.each {
            output += SearchListTagLib.fSlMenuItem(it, "")
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
            else if (type == "execute" || type == "update") {
                def paramString = "";
                it.parameters.each {k, v ->
                    paramString += ActionsTagLib.fRequestParam(key: k, value: v)
                }
                def url = "script/run/${it.script}?format=xml"
                def actionType = type == "execute" ? "request" : "merge"
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, paramString);
            }
        }
        return output;
    }

    def getHtmlDialogsXml(htmlDialogs) {
        def output = "";
        htmlDialogs.each {
            output += HtmlTagLib.fHtml(id: it.id, width: it.width, height: it.height, iframe: "false", "")
        }
        return output;
    }

    def getFieldsXml(fields) {
        def output = ""
        fields.each {
            output += SearchListTagLib.fSlField(exp: "data.rsAlias == '${it.alias}'", fields: it.fields, "");
        }
        return output;
    }
    def getConvsersionJs(conversionsXml, emphasizeds) {
        def emphasizedString = "";
        def empIndex = 0;
        emphasizeds.each {
            def empJs = it.emphasizeds.size() > 0 ? " && (${it.emphasizeds.join(' || ')})" : "";
            emphasizedString += empIndex == 0 ? "if" : "else if"
            emphasizedString += """(data.rsAlias == '${it.alias}' ${empJs}){
                YAHOO.util.Dom.setStyle(el, 'color', 'blue');
            }
            """
            empIndex++;
        }
        def convString = "";
        def convIndex = 0;
        conversionsXml.each {
            def type = it.@type.toString().trim();
            if (type == "date") {
                convString += convIndex == 0 ? "if" : "else if"
                def format = it.@format.toString();
                convString += """(data.rsAlias == '${it.alias}' && key == '${it.property}'){
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
                convString += """(data.rsAlias == '${it.alias}' && key == '${it.property}'){
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

                convString += """(data.rsAlias == '${it.alias}' && key == '${it.property}'){
                    var mapping = {${mArray.join(',')}}
                    return mapping['${it.property}'] || value;
               }
               """
                convIndex++;
            }

        }
        return """
           searchList.renderCellFunction = function(key, value, xmlData, el){
              var data = xmlData.getAttributes();
              ${emphasizedString}
              ${convString}
              return value;
           }
        """
    }
}

