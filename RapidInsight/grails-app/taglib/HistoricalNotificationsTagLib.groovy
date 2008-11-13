
/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Oct 15, 2008
* Time: 4:15:26 PM
* To change this template use File | Settings | File Templates.
*/
class HistoricalNotificationsTagLib {
    static namespace = "rui";
    def historicalNotifications = {attrs, body ->
        def configXML = "<HistoricalNotifications>${body()}</HistoricalNotifications>";
        def searchListPollInterval = attrs["searchResultsPollingInterval"] ? attrs["searchResultsPollingInterval"] : "0";
        def treeGridPollInterval = attrs["queriesPollingInterval"] ? attrs["queriesPollingInterval"] : "0";
        def lineSize = attrs["numberOfLines"] ? attrs["numberOfLines"] : "3";
        def defaultFields = attrs["defaultFields"] ? attrs["defaultFields"] : [];
        def rowMenus = [];
        def propertyMenus = [];
        def actions = [];
        def htmlDialogs = [];
        def fields = [];
        def emphasizeds = [];
        def defaultMenus = ["sortAsc": [label: "Sort asc"],
                "sortDesc": [label: "Sort desc"],
                "except": [label: "Except"],
                "lessThan": [label: "Less Than"],
                "greaterThan": [label: "Greater Than"],
                "greaterThanOrEqualTo": [label: "Greater than or equal to"],
                "lessThanOrEqualTo": [label: "Less than or equal to"]
        ]

        def hnXML = new XmlSlurper().parseText(configXML);
        def tsDefaultMenus = hnXML.DefaultMenus.DefaultMenu;
        tsDefaultMenus.each {
            def id = it.@id.toString().trim();
            if (defaultMenus.containsKey(id)) {
                def defaultMenuConfig = defaultMenus.get(id);
                def label = it.@label.toString();
                defaultMenuConfig.label = label;
                def properties = it.properties.Item;
                if (properties.size() > 0) {
                    def propArray = [];
                    properties.each {p ->
                        propArray.add("params.key == '${p.text()}'")
                    }
                    defaultMenuConfig.put("properties", propArray)
                }
                else {
                    def excepts = it.except.Item;
                    if (excepts.size() > 0) {
                        def exceptArray = [];
                        excepts.each {p ->
                            exceptArray.add("params.key != '${p.text()}'")
                        }
                        defaultMenuConfig.put("except", exceptArray)
                    }
                }
            }
        }

        def hnMenus = hnXML.HnMenus.HnMenu;
        hnMenus.each {menuItem ->
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
                htmlDialogs.add([id: "${id}menuHtml", width: menuItem.@width.toString(), height: menuItem.@height.toString(), x:menuItem.@x.toString(), y:menuItem.@y.toString()])
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
        def searchResults = hnXML.HnSearchResults.HnSearchResult;
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

        out << TreeGridTagLib.fTreeGrid(id: "filterTree", url: "script/run/queryList?format=xml&type=historicalEvent", rootTag: "Filters",
                keyAttribute: "id", contentPath: "Filter", title: "Saved Queries", expanded: "true", onNodeClick: "setQueryAction", pollingInterval: treeGridPollInterval,
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

        out << RFormTagLib.fForm(id: "filterDialog", width: "35em", createUrl: "script/run/createQuery?queryType=historicalEvent", editUrl: "script/run/editQuery?queryType=historicalEvent",
                saveUrl: "searchQuery/save?format=xml&type=historicalEvent", updateUrl: "searchQuery/update?format=xml&type=historicalEvent", onSuccess: "refreshQueriesAction",
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
        out << RFormTagLib.fForm(id: "filterGroupDialog", width: "30em", saveUrl: "searchQueryGroup/save?format=xml&type=historicalEvent",
                updateUrl: "searchQueryGroup/update?format=xml&type=historicalEvent", onSuccess: "refreshQueriesAction",
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
        out << SearchListTagLib.fSearchList(id: "searchList", url: "search?format=xml&searchIn=RsHistoricalEvent", queryParameter: "query", rootTag: "Objects", contentPath: "Object",
                keyAttribute: "id", totalCountAttribute: "total", offsetAttribute: "offset", sortOrderAttribute: "sortOrder", lineSize: "3", title: "Events",
                defaultFields: defaultFields, onSaveQueryClick: "saveQueryAction",
                pollingInterval: searchListPollInterval, lineSize: lineSize,
                SearchListTagLib.fSlMenuItems([:],
                        getMenuXml(rowMenus)
                ) +
                        SearchListTagLib.fSlPropertyMenuItems([:],
                                SearchListTagLib.fSlMenuItem(id: "sortAsc", label: defaultMenus.sortAsc.label, action: "searchListSortAscAction", visible:getSortAscVisibility(defaultMenus), "") +
                                        SearchListTagLib.fSlMenuItem(id: "sortDesc", label: defaultMenus.sortDesc.label, action: "searchListSortDescAction", visible:getSortDescVisibility(defaultMenus),"") +
                                        SearchListTagLib.fSlMenuItem(id: "except", label: defaultMenus.except.label, action: "exceptAction", visible:getExceptVisibility(defaultMenus),"") +
                                        SearchListTagLib.fSlMenuItem(id: "greaterThan", label: defaultMenus.greaterThan.label, action: "greaterThanAction", visible: getGreaterThanVisibility(defaultMenus), "") +
                                        SearchListTagLib.fSlMenuItem(id: "lessThan", label: defaultMenus.lessThan.label, action: "lessThanAction", visible: getLessThanVisibility(defaultMenus), "") +
                                        SearchListTagLib.fSlMenuItem(id: "greaterThanOrEqualTo", label: defaultMenus.greaterThanOrEqualTo.label, action: "greaterThanOrEqualToAction", visible: getGreaterThanOrEqualVisibility(defaultMenus), "") +
                                        SearchListTagLib.fSlMenuItem(id: "lessThanOrEqualTo", label: defaultMenus.lessThanOrEqualTo.label, action: "lessThanOrEqualToAction", visible: getLessThanOrEqualVisibility(defaultMenus), "") +
                                        getMenuXml(propertyMenus)
                        ) +
                        SearchListTagLib.fSlImages([:],
                                SearchListTagLib.fSlImage(visible: "params.data.severity == '1'", src: "images/rapidjs/component/searchlist/red.png", "") +
                                        SearchListTagLib.fSlImage(visible: "params.data.severity == '2'", src: "images/rapidjs/component/searchlist/orange.png", "") +
                                        SearchListTagLib.fSlImage(visible: "params.data.severity == '3'", src: "images/rapidjs/component/searchlist/yellow.png", "") +
                                        SearchListTagLib.fSlImage(visible: "params.data.severity == '4'", src: "images/rapidjs/component/searchlist/blue.png", "") +
                                        SearchListTagLib.fSlImage(visible: "params.data.severity == '5'", src: "images/rapidjs/component/searchlist/green.png", "")
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
                    ${getConvsersionJs(hnXML.HnConversions.HnConversion, emphasizeds)}
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
                    searchList.poll();

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

    def hnMenus = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnMenus", attrs, [], body());
    }
    def hnMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible", "x", "y"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnMenu", attrs, validAttrs);
    }

    def hnDefaultMenus = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("DefaultMenus", attrs, [], body());
    }
    def hnDefaultMenu = {attrs, body ->
        def validAttrs = ["id", "label", "properties", "except"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("DefaultMenu", attrs, validAttrs);
    }


    def hnSearchResults = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnSearchResults", attrs, [], body());
    }
    def hnSearchResult = {attrs, body ->
        def validAttrs = ["alias", "properties", "emphasizeds"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnSearchResult", attrs, validAttrs);
    }

    def hnConversions = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnConversions", attrs, [], body());
    }
    def hnConversion = {attrs, body ->
        def validAttrs = ["type", "format", "property", "function", "mapping"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("HnConversion", attrs, validAttrs);
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
                    paramString += ActionsTagLib.fRequestParam(key: k, value: v, "")
                }
                def url = "script/run/${it.script}?format=xml"
                def actionType = type == "execute" ? "request" : "merge"
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, components: ["searchList"], paramString);
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

    def getFieldsXml(fields) {
        def output = ""
        fields.each {
            output += SearchListTagLib.fSlField(exp: "params.data.rsAlias == '${it.alias}'", fields: it.fields, "");
        }
        return output;
    }
    def getConvsersionJs(conversionsXml, emphasizeds) {
        def emphasizedString = "";
        def empIndex = 0;
        emphasizeds.each {
            if (it.emphasizeds.size() > 0) {
                emphasizedString += empIndex == 0 ? "if" : "else if"
                emphasizedString += """(data.rsAlias == '${it.alias}' && (${it.emphasizeds.join(' || ')})){
                YAHOO.util.Dom.setStyle(el, 'color', 'blue');
            }
            """
                empIndex++;
            }
        }
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
           searchList.renderCellFunction = function(key, value, xmlData, el){
              var data = xmlData.getAttributes();
              ${emphasizedString}
              ${convString}
              return value;
           }
        """
    }
    def getSortAscVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "sortAsc", "")
    }
    def getSortDescVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "sortDesc", "")
    }
    def getExceptVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "except", "")
    }
    def getGreaterThanVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "greaterThan", "(params.key == 'severity' && params.value != '1') || (params.key != 'severity' && YAHOO.lang.isNumber(parseInt(params.value)))")
    }
    def getLessThanVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "lessThan", "(params.key == 'severity' && params.value != '5') || (params.key != 'severity' && YAHOO.lang.isNumber(parseInt(params.value)))")
    }
    def getLessThanOrEqualVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "lessThanOrEqualTo", "YAHOO.lang.isNumber(parseInt(params.value))")
    }
    def getGreaterThanOrEqualVisibility(defaultMenus) {
        return getDefaultMenuVisibility(defaultMenus, "greaterThanOrEqualTo", "YAHOO.lang.isNumber(parseInt(params.value))")
    }

    def getDefaultMenuVisibility(defaultMenus, id, originalExp) {
        def properties = defaultMenus.get(id).get("properties");
        def except = defaultMenus.get(id).get("except");
        if (properties != null && properties.size() > 0) {
            return originalExp == "" ? "${properties.join(" || ")}" : "${originalExp} && (${properties.join(' || ')})"
        }
        else if (except != null && except.size() > 0) {
            return originalExp == "" ? "${except.join(" && ")}" : "${originalExp} && (${except.join(' && ')})"
        }
        return originalExp;
    }
}