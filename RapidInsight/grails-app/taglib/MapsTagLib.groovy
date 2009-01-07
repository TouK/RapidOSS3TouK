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
* Date: Oct 16, 2008
* Time: 2:26:57 PM
* To change this template use File | Settings | File Templates.
*/
class MapsTagLib {
    static namespace = "rui";
    def maps = {attrs, body ->
        def configXML = "<Map>${body()}</Map>";
        def topoMapPollInterval = attrs["mapPollingInterval"] ? attrs["mapPollingInterval"] : "0";
        def treeGridPollInterval = attrs["savedMapsPollingInterval"] ? attrs["savedMapsPollingInterval"] : "0";
        def nodeSize = attrs["nodeSize"] ? attrs["nodeSize"] : "60";
        def edgeColors = ["1" : "0xffde2c26","2":"0xfff79229","3": "0xfffae500", "4" :  "0xff20b4e0","5": "0xff62b446", "0":"0xff62b446", "default":"0xff62b446" ]
        def nodeMenus = [];
        def toolbarMenus = ["Map":[[id:"saveMap", label:"Save Map", action:"saveMapAction"]]];
        def actions = [];
        def htmlDialogs = [];

        def tmXML = new XmlSlurper().parseText(configXML);
        def tmMenus = tmXML.Menus.Menu;
        tmMenus.each {menuItem ->
            def location = menuItem.@location.toString().trim();
            def id = menuItem.@id;
            if (location == "node") {
                nodeMenus.add([id: id, label: menuItem.@label, visible: menuItem.@visible, action: "${id}menuAction"])
            }
            else if (location == "toolbar") {
                def parentMenu = menuItem.@parentMenu.toString();
                def parentMenuArray = toolbarMenus.get(parentMenu);
                if(parentMenuArray == null){
                    parentMenuArray = [];
                    toolbarMenus.put(parentMenu, parentMenuArray)
                }
                parentMenuArray.add([id: id, label: menuItem.@label.toString(), action: "${id}menuAction"])
            }
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

        def images = tmXML.Images.Image;
        def imageString = "";
        images.each{
            def mappings = [:]
            def mappingXml = it.mapping.Item;
            mappingXml.each{mapping ->
               mappings.put(mapping.@key.toString(), mapping.@value.toString())
            }
            imageString += ObjectMapTagLib.fOmImage(id:it.@id, x:it.@x, y:it.@y, width:it.@width, height:it.@height, dataKey:it.@dataKey, mapping:mappings, "")
        }
        def texts = tmXML.Texts.Text;
        def textString = "";
        texts.each{
            textString += ObjectMapTagLib.fOmText(id:it.@id, x:it.@x, y:it.@y, width:it.@width, height:it.@height, dataKey:it.@dataKey, "")
        }
        def gauges = tmXML.Gauges.Gauge;
        def gaugeString = "";
        gauges.each{
            gaugeString += ObjectMapTagLib.fOmGauge(id:it.@id, x:it.@x, y:it.@y, width:it.@width, height:it.@height, dataKey:it.@dataKey, "")
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

        out << RFormTagLib.fForm(id: "mapDialog", width: "35em", createUrl:"script/run/createMap?format=xml", editUrl:"script/run/editMap?format=xml",
                updateUrl:"script/run/saveMap?format=xml", saveUrl:"script/run/saveMap?format=xml", submitAction:"POST", onSuccess: "refreshMapsAction",
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

        out << ObjectMapTagLib.fObjectMap(id:"topoMap", dataTag:"device", expandURL:"script/run/expandMap", dataURL:"script/run/getMapData",
            nodeSize:nodeSize, edgeColors:edgeColors, pollingInterval:topoMapPollInterval, 
            ObjectMapTagLib.fOmMenuItems([:], getMenuXml(nodeMenus)) +
                    ObjectMapTagLib.fOmToolbarMenus([:], getToolbarMenuXml(toolbarMenus)) +
                    ObjectMapTagLib.fOmNodeContent([:],
                        ObjectMapTagLib.fOmImages([:], imageString) +
                                ObjectMapTagLib.fOmGauges([:], gaugeString)+
                                ObjectMapTagLib.fOmTexts([:], textString)
                    )

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
                ActionsTagLib.fRequestParam(key: "mapName", value: "params.data.name", "") +
                ActionsTagLib.fRequestParam(key: "isPublic", value:"params.data.isPublic", "")
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
                            var strQueryString = strHref.substr(strHref.indexOf("?"));
                            var aQueryString = strQueryString.split("&");
                            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                                if (
                                    aQueryString[iParam].toLowerCase().indexOf(strParamName.toLowerCase() + "=") > -1 ){
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
                                { position: 'center', body: topoMap.container.id, resize: false, gutter: '1px', useShim:true},
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

    def mpMenus = {attrs, body ->
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("Menus", attrs, [], body());
    }
    def mpMenu = {attrs, body ->
        def validAttrs = ["id", "label", "actionType", "script", "width", "height", "url", "title", "location", "parameters", "visible", "parentMenu", "x", "y"]
        out << com.ifountain.rui.util.TagLibUtils.getConfigAsXml("Menu", attrs, validAttrs);
    }

    def mpImages = {attrs, body ->
        out << ObjectMapTagLib.fOmImages(attrs, body())
    }
    def mpImage = {attrs, body ->
        out << ObjectMapTagLib.fOmImage(attrs, "")
    }

    def mpTexts = {attrs, body ->
       out << ObjectMapTagLib.fOmTexts(attrs, body())
    }

    def mpText = {attrs, body->
        out << ObjectMapTagLib.fOmText(attrs, "")
    }

    def mpGauges = {attrs, body ->
        out << ObjectMapTagLib.fOmGauges(attrs, body())
    }

    def mpGauge = {attrs, body ->
        out << ObjectMapTagLib.fOmGauge(attrs, "")
    }

    def getMenuXml(menus) {
        def output = "";
        menus.each {
            output += ObjectMapTagLib.fOmMenuItem(it, "")
        }
        return output;
    }
    def getToolbarMenuXml(toolbarMenus){
        def output = "";
        toolbarMenus.each{menuLabel, menuItems ->
            def innerXml = "";
            menuItems.each{
                innerXml += ObjectMapTagLib.fOmMenuItem(it, "")
            }
            output += ObjectMapTagLib.fOmToolbarMenu(label:menuLabel, innerXml);
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
                output += ActionsTagLib.fAction(id: it.id, type: actionType, url: url, components:["topoMap"], paramString);
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
}