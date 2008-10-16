/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Oct 16, 2008
 * Time: 2:26:57 PM
 * To change this template use File | Settings | File Templates.
 */
class MapTagLib {
    static namespace = "rui";
    def map = {attrs, body ->
        def configXML = "<Map>${body()}</Map>";
        def topoMapPollInterval = attrs["mapPollingInterval"] ? attrs["mapPollingInterval"] : "0";
        def treeGridPollInterval = attrs["savedMapsPollingInterval"] ? attrs["savedMapsPollingInterval"] : "0";
        def lineSize = attrs["nodeSize"] ? attrs["nodeSize"] : "60";
        def nodeMenus = [];
        def toolbarMenus = [];
        def actions = [];
        def htmlDialogs = [];

        def nsXML = new XmlSlurper().parseText(configXML);
        def nsMenus = nsXML.NsMenus.NsMenu;
        nsMenus.each {menuItem ->
            def location = menuItem.@location.toString().trim();
            def id = menuItem.@id;
            if (location == "row") {
                nodeMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
            }
            else if (location == "property") {
                toolbarMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
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

        out << TreeGridTagLib.fTreeGrid(id: "mapTree", url: "script/run/mapList", rootTag: "Maps",
                keyAttribute: "id", contentPath: "Map", title: "Saved Maps", expanded: "true", onNodeClick: "requestMapAction", pollingInterval: treeGridPollInterval,
                TreeGridTagLib.fTgColumns([:],
                        TreeGridTagLib.fTgColumn(attributeName: "name", colLabel: "Name", width: "248", sortBy: "true", "")
                ) +
                        TreeGridTagLib.fTgMenuItems([:],
                                TreeGridTagLib.fTgMenuItem(id: "deleteMap", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.nodeType == 'map'", action: "deleteMapAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "deleteMapGroup", label: "Delete", visible: "params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'", action: "deleteMapGroupAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "mapUpdate", label: "Update", visible: "params.data.nodeType == 'map' && params.data.isPublic != 'true'", action: "mapUpdateAction", "") +
                                        TreeGridTagLib.fTgMenuItem(id: "mapGroupUpdate", label: "Update", visible: "params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'", action: "mapGroupUpdateAction", "") 
                        ) +
                        TreeGridTagLib.fTgRootImages([:],
                                TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'group'", expanded: "images/rapidjs/component/tools/folder_open.gif", collapsed: "images/rapidjs/component/tools/folder.gif", "") +
                                        TreeGridTagLib.fTgRootImage(visible: "params.data.nodeType == 'map'", expanded: "images/rapidjs/component/tools/filter.png", collapsed: "images/rapidjs/component/tools/filter.png", "")
                        )
        )

        out << RFormTagLib.fForm(id: "mapDialog", width: "35em", createUrl:"script/run/createMap", editUrl:"script/run/editMap",
                updateUrl:"topoMap/update?format=xml", saveUrl:"script/run/saveMap", submitAction="POST", onSuccess: "refreshMapsAction",
                """
                  <div >
                    <div class="hd">Save Map</div>
                    <div class="bd">
                    <form method="POST" action="javascript://nothing">
                        <table>
                        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><select name="groupName" style="width:175px"/></td></tr>
                        <tr><td width="50%"><label>Map Name:</label></td><td width="50%"><input type="textbox" name="mapName" style="width:175px"/></td></tr>
                        </table>
                        <input type="hidden" name="nodes"/>
                        <input type="hidden" name="edges"/>
                        <input type="hidden" name="id"/>
                        <input type="hidden" name="layout"/>
                    </form>
            
                    </div>
                </div>
                """
        )
        out << RFormTagLib.fForm(id: "mapGroupDialog", width: "30em", saveUrl: "mapGroup/save?format=xml",
                updateUrl: "mapGroup/update?format=xml", onSuccess: "refreshMapsAction",
                """
                 <div >
                    <div class="hd">Save group</div>
                    <div class="bd">
                    <form method="POST" action="javascript://nothing">
                        <table>
                        <tr><td width="50%"><label>Group Name:</label></td><td width="50%"><input type="textbox" name="groupName" style="width:175px"/></td></tr>
                        </table>
                        <input type="hidden" name="id">
                    </form>
                    </div>
                </div>
                """
        )
        out << getHtmlDialogsXml(htmlDialogs)
        out << getActionXml(actions);
       
        out << ActionsTagLib.fAction(id: "deleteMapAction", type: "request", url: "topoMap/delete?format=xml", onSuccess: "refreshMapsAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "deleteMapGroupAction", type: "request", url: "mapGroup/delete?format=xml", onSuccess: "refreshMapsAction",
                ActionsTagLib.fRequestParam(key: "id", value: "params.data.id", "")
        )
        out << ActionsTagLib.fAction(id: "mapUpdateAction", type: "function", componentId: "mapDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.EDIT_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{mapId:params.data.id}")
        )
        out << ActionsTagLib.fAction(id: "mapGroupUpdateAction", type: "function", componentId: "mapGroupDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.EDIT_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{groupName:params.data.name, id:params.data.id}")
        )
        out << ActionsTagLib.fAction(id: "requestMapAction", type: "request", url: "script/run/getMap?format=xml", onSuccess: "loadMapAction",
                ActionsTagLib.fRequestParam(key: "mapName", value: "params.data.name", "")
        )
        out << ActionsTagLib.fAction(id: "loadMapAction", type: "function", componentId: "topoMap", function: "loadMap",
                ActionsTagLib.fFunctionArg([:], "params.response")
        )
        out << ActionsTagLib.fAction(id: "saveMapAction", type: "function", componentId: "mapDialog", function: "show",
                ActionsTagLib.fFunctionArg([:], "YAHOO.rapidjs.component.Form.CREATE_MODE") +
                        ActionsTagLib.fFunctionArg([:], "{}") +
                        ActionsTagLib.fFunctionArg([:], "{nodes:YAHOO.rapidjs.Components['topoMap'].getNodesString(), layout:YAHOO.rapidjs.Components['topoMap'].getLayout()}")
        )
        
        out << ActionsTagLib.fAction(id: "refreshMapsAction", type: "function", function: "poll", componentId: "mapTree", "")
        out << """
                <script type="text/javascript">
                    function getURLParam(strParamName){
                        var strReturn = "";
                        var strHref = window.location.href;
                        if ( strHref.indexOf("?") > -1 ){
                            var strQueryString = strHref.substr(strHref.indexOf("?")).toLowerCase();
                            var aQueryString = strQueryString.split("&");
                            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                                if (
                                    aQueryString[iParam].indexOf(strParamName.toLowerCase() + "=") > -1 ){
                                    var aParam = aQueryString[iParam].split("=");
                                    strReturn = aParam[1];
                                    break;
                                }
                            }
                        }
                        return unescape(strReturn);
                    }
                    var topoMap = YAHOO.rapidjs.Components['topoMap'];
                    var tree = YAHOO.rapidjs.Components['mapTree'];
                    topoMap.events['mapInitialized'].subscribe(function(){
                        var deviceName = this.getURLParam( "name");
                        if( deviceName )
                        {
                            this.topoMap.loadMapForNode( deviceName);
                        }
                    }, this, true);
                    tree.addToolbarButton({
                        className:'r-filterTree-groupAdd',
                        scope:this,
                        tooltip: 'Add group',
                        click:function() {
                            YAHOO.rapidjs.Components['mapGroupDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE);
                        }
                    });
                    tree.addToolbarButton({
                        className:'r-filterTree-queryAdd',
                        scope:this,
                        tooltip: 'Save Map',
                        click:function() {
                            YAHOO.rapidjs.Components['mapDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE, null, {nodes:topoMap.getNodesString(), layout:topoMap.getLayout()});
                        }
                    });
                    tree.poll();

                    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

                    Event.onDOMReady(function() {
                        var layout = new YAHOO.widget.Layout({
                            units: [
                                { position: 'top', body: 'top', resize: false, height:45},
                                { position: 'center', body: topoMap.container.id, resize: false, gutter: '1px' },
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

                        topoMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
                        layout.on('resize', function() {
                            topoMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
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

    def nsMenus = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("NsMenus", attrs, [], body());
    }
    def nsMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible"]
        out << TagLibUtils.getConfigAsXml("NsMenu", attrs, validAttrs);
    }

    def nsSearchResults = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("NsSearchResults", attrs, [], body());
    }
    def nsSearchResult = {attrs, body ->
        def validAttrs = ["alias", "properties", "emphasizeds"]
        out << TagLibUtils.getConfigAsXml("NsSearchResult", attrs, validAttrs);
    }

    def nsConversions = {attrs, body ->
        out << TagLibUtils.getConfigAsXml("NsConversions", attrs, [], body());
    }
    def nsConversion = {attrs, body ->
        def validAttrs = ["type", "format", "property", "function", "mapping"]
        out << TagLibUtils.getConfigAsXml("NsConversion", attrs, validAttrs);
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
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, components:["searchList"], paramString);
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
}