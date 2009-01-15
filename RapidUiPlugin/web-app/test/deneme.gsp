<html>
<head>
    <rui:javascript dir="yui/layout" file="layout-min.js"/>
    <rui:javascript dir="yui/resize" file="resize-min.js"/>
    <rui:javascript dir="rapidjs/component/treegrid" file="TreeGrid.js"></rui:javascript>

    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
</head>
<body class=" yui-skin-sam">
<script>
    function getFolders(name){
        return name == "Components" || name == "Layout" || name == "Forms" || name == "Dialogs" || name =="JavaScript" || name == "Actions"
    }

</script>
<rui:treeGrid id="topologyTree" url="auth.xml" rootTag="Objects" pollingInterval="60"
        keyAttribute="id" contentPath="Object" title="">
    <rui:tgColumns>
        <rui:tgColumn attributeName="name" colLabel="" width="248"></rui:tgColumn>
    </rui:tgColumns>
    <rui:tgRootImages>
        <rui:tgRootImage expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif" visible="getFolders(params.data.name)"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/gsp_logo.png" collapsed="images/gsp_logo.png" visible="params.data.type == 'url'"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/grid.png" collapsed="images/grid.png" visible="params.data.type == 'component'"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/testOutput.png" collapsed="images/testOutput.png" visible="params.data.type == 'tab'"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/run.gif" collapsed="images/run.gif" visible="params.data.type == 'action'"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/application_form.png" collapsed="images/application_form.png" visible="params.data.type == 'form'"></rui:tgRootImage>
        <rui:tgRootImage expanded="images/application_double.png" collapsed="images/application_double.png" visible="params.data.type == 'dialog'"></rui:tgRootImage>
    </rui:tgRootImages>
</rui:treeGrid>

<div id="center"></div>
<div id="top"><span style="font-size:14px; font-weight:bold;">Plain HTML</span></div>
<div id="innerCenter"><span style="font-size:14px; font-weight:bold;">eventsGrid</span></div>
<div id="left"><span style="font-size:14px; font-weight:bold;">filterTree</span></div>
<script type="text/javascript">
    YAHOO.util.Event.onDOMReady(function() {
        var topologyTree = YAHOO.rapidjs.Components['topologyTree'];
        var layout = new YAHOO.widget.Layout({
            units: [
            { position: 'left', body: topologyTree.container.id, resize: false, gutter: '1px', width:300},
            { position: 'center', body: "center", resize: false, gutter: '1px' }
            ]
        });
        layout.on('render', function() {
	            var el = layout.getUnitByPosition('center').get('wrap');
	            var layout2 = new YAHOO.widget.Layout(el, {
	                parent: layout,
	                minWidth: 400,
	                minHeight: 200,
	                units: [
	                    { position: 'top',  body:'top', height: 100, gutter: '1px' },
	                    { position: 'left', width: 200,  body: 'left', gutter: '1px'},
	                    { position: 'center', body: 'innerCenter', gutter: '1px'}
	                ]
	            });
	            layout2.render();
	        }); 
        layout.render();
        var layoutCenter = layout.getUnitByPosition('center');
        topologyTree.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
        layout.on('resize', function() {
            topologyTree.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
        });
        window.layout = layout;

})

</script>
</body>
</html>