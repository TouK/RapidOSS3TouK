<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:treeGrid id="mapTree" url="script/run/mapList" rootTag="Maps" keyAttribute="id"
     contentPath="Map" title="Saved Maps" expanded="true" onNodeClick="requestMapAction">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="Name" width="248" sortBy="true"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgMenuItems>
        <rui:tgMenuItem id="deleteMap" label="Delete" visible="params.data.isPublic != 'true' && params.data.nodeType == 'map'" action="deleteMapAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="deleteGroup" label="Delete" visible="params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'" action="deleteMapGroupAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="updateMap" label="Update" visible="params.data.isPublic != 'true' && params.data.nodeType == 'map'" action="mapUpdateAction"></rui:tgMenuItem>
        <rui:tgMenuItem id="updateGroup" label="Update" visible="params.data.isPublic != 'true' && params.data.name != 'Default' && params.data.nodeType == 'group'" action="mapGroupUpdateAction"></rui:tgMenuItem>
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <rui:tgRootImage visible="params.data.nodeType == 'group'" expanded='images/rapidjs/component/tools/folder_open.gif' collapsed='images/rapidjs/component/tools/folder.gif'></rui:tgRootImage>
        <rui:tgRootImage visible="params.data.nodeType == 'map'" expanded='images/rapidjs/component/tools/filter.png' collapsed='images/rapidjs/component/tools/filter.png'></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>
<rui:form id="mapDialog" width="35em" createUrl="script/run/createMap" editUrl="script/run/editMap" updateUrl="topoMap/update?format=xml" saveUrl="script/run/saveMap"
        submitAction="POST" onSuccess="refreshMapsAction">
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
</rui:form>
<rui:form id="mapGroupDialog" width="30em" saveUrl="mapGroup/save?format=xml" updateUrl="mapGroup/update?format=xml" onSuccess="refreshMapsAction">
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
</rui:form>
   <%
       def edgeColors = ["1" : "0xffde2c26","2":"0xfff79229","3": "0xfffae500", "4" :  "0xff20b4e0","5": "0xff62b446", "default":"0xff62b446" ]
       def stateMapping = ["0":"green.png",
                    "1":"red.png",
                    "2":"orange.png",
                    "3":"yellow.png",
                    "4":"blue.png",
                    "5":"green.png",
                    "default":"green.png"]
        def typeMapping = ["Host":"server_icon.png",
                    "Router":"router_icon.png",
                    "Switch":"switch_icon.png"]                    
   %>
<rui:topologyMap id="topoMap" dataTag="device" expandURL="script/run/expandMap" dataURL="script/run/getMapData"
        pollingInterval="0" nodeSize="60" edgeColors="${edgeColors}">
     <rui:tmNodeContent>
        <rui:tmImages>
            <rui:tmImage id="status" x="70" y="40" width="30" height="30" dataKey="state" mapping="${stateMapping}"></rui:tmImage>
            <rui:tmImage id="icon" x="70" y="0" width="20" height="20" dataKey="type" mapping="${typeMapping}"></rui:tmImage>
        </rui:tmImages>
         <rui:tmTexts>
            <rui:tmText id="name" x="20" y="20" width="50" height="30" dataKey="id"></rui:tmText>
         </rui:tmTexts>
     </rui:tmNodeContent>
     <rui:tmToolbarMenus>
        <rui:tmToolbarMenu label="Map">
              <rui:tmMenuItem id="saveMap" label="Save Map" action="saveMapAction"></rui:tmMenuItem>
        </rui:tmToolbarMenu>
     </rui:tmToolbarMenus>
     <rui:tmMenuItems>
        <rui:tmMenuItem id="browse" label="Browse" action="browseAction"></rui:tmMenuItem>
     </rui:tmMenuItems>
</rui:topologyMap>

<rui:html id="objectDetails" width="850" height="700" iframe="false"></rui:html>

<rui:action id= "deleteMapAction" type= "request" url= "topoMap/delete?format=xml" onSuccess="refreshMapsAction">
   <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id= "deleteMapGroupAction" type= "request" url="mapGroup/delete?format=xml" onSuccess="refreshMapsAction">
   <rui:requestParam key="id" value="params.data.id"></rui:requestParam>
</rui:action>
<rui:action id= "mapUpdateAction" type= "fuction" function= "show" componentId= "mapDialog" onSuccess="refreshMapsAction">
   <rui:functionArg>YAHOO.rapidjs.component.Form.EDIT_MODE</rui:functionArg>
   <rui:functionArg>{mapId:params.data.id}</rui:functionArg>
</rui:action>
<rui:action id= "mapGroupUpdateAction" type= "fuction" function= "show" componentId="mapGroupDialog" onSuccess="refreshMapsAction">
   <rui:functionArg>YAHOO.rapidjs.component.Form.EDIT_MODE</rui:functionArg>
   <rui:functionArg>{}</rui:functionArg>
   <rui:functionArg>{groupName:params.data.name, id:params.data.id}</rui:functionArg>
</rui:action>
<rui:action id= "requestMapAction" type="request" url= "script/run/getMap?format=xml" onSuccess="loadMapAction">
   <rui:requestParam key="mapName" value="params.data.name"></rui:requestParam>
</rui:action>
<rui:action id="loadMapAction" type="function" function="loadMap" componentId="topoMap">
   <rui:functionArg>params.response</rui:functionArg>
</rui:action>        
<rui:action id= "saveMapAction" type="function" function= "show" componentId="mapDialog">
   <rui:functionArg>YAHOO.rapidjs.component.Form.CREATE_MODE</rui:functionArg>
   <rui:functionArg>{}</rui:functionArg>
   <rui:functionArg>{nodes:YAHOO.rapidjs.Components['topoMap'].getNodesString(), layout:YAHOO.rapidjs.Components['topoMap'].getLayout()}</rui:functionArg>
</rui:action>        
<rui:action id="refreshMapsAction" type="function" function="poll" componentId="mapTree"></rui:action>     
<rui:action id="browseAction" type="function" function="show" componentId="objectDetails">
    <rui:functionArg>'getObjectDetails.gsp?name=' + encodeURIComponent(params.data.id)</rui:functionArg>
    <rui:functionArg>'Details of ' + params.data.type + ' ' + params.data.id</rui:functionArg>
</rui:action>     

<script type="text/javascript">
    
    var tree = YAHOO.rapidjs.Components['mapTree'];
    var topMap = YAHOO.rapidjs.Components['topoMap'];
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

    topMap.events.mapInitialized.subscribe(function(){
        var deviceName = this.getURLParam( "name");
        if( deviceName )
        {
            this.topMap.loadMapForNode( deviceName);
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
            YAHOO.rapidjs.Components['mapDialog'].show(YAHOO.rapidjs.component.Form.CREATE_MODE, null, {nodes:topMap.getNodesString(), layout:topMap.getLayout()});
        }
    });
    tree.poll();


    var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45, useShim:true},
                { position: 'center', body: topMap.container.id, resize: false, gutter: '1px', useShim:true },
                { position: 'left', width: 250, resize: true, body: tree.container.id, scroll: false, useShim:true}
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

        topMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            topMap.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        layout.on('resize', function() {
            tree.resize(layout.getUnitByPosition('center').body.offsetWidth, layout.getUnitByPosition('center').body.offsetHeight);
        });
        window.layout = layout;



    })
</script>

</body>
</html>