import com.ifountain.rui.util.TagLibUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Oct 15, 2008
* Time: 1:47:39 PM
* To change this template use File | Settings | File Templates.
*/
class NotificationsTagLib {
    static namespace = "rui";
    def notifications = {attrs, body ->
        def configXML = "<Notifications>${body()}</Notifications>";
        def searchGridPollInterval = attrs["searchResultsPollingInterval"] ? attrs["searchResultsPollingInterval"] : "0";
        def treeGridPollInterval = attrs["queriesPollingInterval"] ? attrs["queriesPollingInterval"] : "0";
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
            rowMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
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
        def ntColumns = ntXML.Columns.Column;
        def columnsStr = "";
        ntColumns.each {
            columnsStr += SearchGridTagLib.fSgColumn(attributeName: it.@attributeName.toString(), width: it.@width.toString(),
                    colLabel: it.@colLabel.toString(), sortBy: it.@sortBy.toString(), sortOrder: it.@sortOrder.toString(), "")
        }


        out << TreeGridTagLib.fTreeGrid(id: "filterTree", url: "script/run/queryList?format=xml&type=notification", rootTag: "Filters",
                keyAttribute: "id", contentPath: "Filter", title: "SavedQueries", expanded: "true", onNodeClick: "setQueryAction", pollingInterval: treeGridPollInterval,
                TreeGridTagLib.fTgColumns([:],
                        TreeGridTagLib.fTgColumn(attributeName: "name", colLabel: "Name", width: "248", sortBy: "true", "")
                ) +
                        TreeGridTagLib.fTgMenuItems([:],
                                TreeGridTagLib.fTgMenuItem(id: "deleteQuery", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.nodeType == 'filter'", action: "deleteQueryAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "deleteQueryGroup", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'", action: "deleteQueryGroupAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryUpdate", label: "Update", visible: "params.data.nodeType == 'filter' && params.data.isPublic != 'true'", action: "queryUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "queryGroupUpdate", label: "Update", visible: "params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'", action: "queryGroupUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "copyQuery", label: "Copy Query", visible: "params.data.nodeType == 'filter'", action: "copyQueryAction", "")
                        ) +
                        TreeGridTagLib.fTgRootImages([:],
                                TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'group'", expanded: "images/rapidjs/component/tools/folder_open.gif", collapsed: "images/rapidjs/component/tools/folder.gif", "") +
                                        TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'filter'", expanded: "images/rapidjs/component/tools/filter.png", collapsed: "images/rapidjs/component/tools/filter.png", "")
                        )

        )

        out << RFormTagLib.fForm(id: "filterDialog", width: "35em", createUrl: "script/run/createQuery?queryType=notification", editUrl: "script/run/editQuery?queryType=notification",
                saveUrl: "searchQuery/save?format=xml&type=notification", updateUrl: "searchQuery/update?format=xml&type=notification", onSuccess: "refreshQueriesAction",
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
        out << RFormTagLib.fForm(id: "filterGroupDialog", width: "30em", saveUrl: "searchQueryGroup/save?format=xml&type=notification",
                updateUrl: "searchQueryGroup/update?format=xml&type=notification", onSuccess: "refreshQueriesAction",
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
        out << SearchGridTagLib.fSearchGrid(id: "searchGrid", url: "search?format=xml&searchIn=RsEvent", queryParameter: "query", rootTag: "Objects", contentPath: "Object",
                keyAttribute: "id", totalCountAttribute: "total", offsetAttribute: "offset", sortOrderAttribute: "sortOrder", title: "Events", onSaveQueryClick: "saveQueryAction",
                pollingInterval: searchGridPollInterval, fieldsUrl: "script/run/getViewFields?format=xml",
                SearchGridTagLib.fSgMenuItems([:],
                        SearchGridTagLib.fSgMenuItem(id: "browse", label: "Browse", "") +
                                getMenuXml(rowMenus)
                ) +
                        SearchGridTagLib.fSgImages([:],
                                SearchGridTagLib.fSgImage(visible: "params.data.severity == '1'", src: "images/rapidjs/component/searchlist/red.png", "") +
                                        SearchGridTagLib.fSgImage(visible: "params.data.severity == '2'", src: "images/rapidjs/component/searchlist/orange.png", "") +
                                        SearchGridTagLib.fSgImage(visible: "params.data.severity == '3'", src: "images/rapidjs/component/searchlist/yellow.png", "") +
                                        SearchGridTagLib.fSgImage(visible: "params.data.severity == '4'", src: "images/rapidjs/component/searchlist/blue.png", "") +
                                        SearchGridTagLib.fSgImage(visible: "params.data.severity == '5'", src: "images/rapidjs/component/searchlist/green.png", "")
                        ) +
                        SearchGridTagLib.fSgColumns([:], columnsStr)

        )
        out << HtmlTagLib.fHtml(id: "objectDetailsmenuHtml", width: "850", height: "700", iframe: "false", "");
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
                        ActionsTagLib.fFunctionArg([:], "{query:params.query, sortProperty:YAHOO.rapidjs.Components['searchGrid'].getSortAttribute(), sortProperty:YAHOO.rapidjs.Components['searchList'].getSortOrder()}")
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
                    searchGrid.events["rowHeaderMenuClick"].subscribe(function(xmlData, id, parentId) {
                        var notificationName = xmlData.getAttribute("name");
                        if(id == "browse"){
                            var key = 'elementName'
                            var value = xmlData.getAttribute(key);
                            if(!value || value == ''){
                                key = 'instanceName'
                                value = xmlData.getAttribute(key)
                            }
                            var url = "getObjectDetails.gsp?name="+ encodeURIComponent(value);
                            var title = key == "instanceName"? "Details of " + xmlData.getAttribute("className") + " " + value : "Details of " + xmlData.getAttribute("elementClassName") + " " + value
                            YAHOO.rapidjs.Components['objectDetailsmenuHtml'].show(url, title);
                        }
                    }, this, true);

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
                        var layoutLeft = layout.getUnitByPosition('left');
                        layoutLeft.on('resize', function(){
                            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
                        });

                        searchGrid.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        layout.on('resize', function() {
                            searchGrid.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
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

    def ntMenus = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("NtMenus", attrs, [], body());
    }
    def ntMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible"]
        out << TagLibUtils.getConfigAsXml("NtMenu", attrs, validAttrs);
    }

    def ntConversions = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("NtConversions", attrs, [], body());
    }
    def ntConversion = {attrs, body ->
        def validAttrs = ["type", "format", "property", "function", "mapping"]
        out << TagLibUtils.getConfigAsXml("NtConversion", attrs, validAttrs);
    }
    def ntColumns = {attrs, body ->
        out << SearchGridTagLib.fSgColumns(attrs, body())
    }
    def ntColumn = {attrs, body ->
        out << SearchGridTagLib.fSgColumn(attrs, "")
    }

    def getMenuXml(menus) {
        def output = "";
        menus.each {
            output += SearchGridTagLib.fSgMenuItem(it, "")
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
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, components: ["searchGrid"], paramString);
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