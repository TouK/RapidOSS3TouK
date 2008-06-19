
<html>
	<head>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'yui-ext.css')}"/>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'treegrid.css')}"/>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'common.css')}"/>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'layout.css')}"/>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'overlay.css')}"/>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'test/css', file:'mgrid.css')}"/>

		<script src="${createLinkTo(dir:'test/jslib', file:'YAHOO.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib', file:'RSHelp.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib', file:'RSHelp.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/yui/build/animation', file:'animation.js')}')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/yui/build/container', file:'container.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs', file:'rapidjs.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'ServerStatusTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs', file:'RapidUtil.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs', file:'SelectUtils.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs', file:'Login.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'RapidPanel.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/menu', file:'MenuAction.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/menu', file:'MenuItem.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/menu', file:'ContextMenu.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/menu', file:'Tooltip.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component', file:'split.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/data', file:'NodeFactory.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/data', file:'RapidXmlDocument.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/data', file:'NodeFactory.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/data', file:'xml2json.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component', file:'RapidElement.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/grid', file:'GridRow.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/grid', file:'RapidSelectionModel.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/grid', file:'RapidGridView.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/grid', file:'RapidGrid.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/grid', file:'RapidXmlDataModel.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/tree', file:'TreeCell.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/tree', file:'TreeNode.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/tree', file:'TreeHeaderCell.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/tree', file:'TreeGrid.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component', file:'ComponentContainer.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component', file:'PollingComponentContainer.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/windows', file:'TreeWindow.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/windows', file:'GridWindow.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/windows', file:'ConsoleWindow.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/windows', file:'HtmlWindow.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/dialogs', file:'ComponentConfigurationDialog.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/dialogs', file:'ErrorDialog.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'RapidTabPanel.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'LayoutTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'LoadingTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'PollingTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'LogoutTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'UserTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'ErrorTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component/layout', file:'HelpTool.js')}"></script>
		<script src="${createLinkTo(dir:'test/jslib/rapidjs/component', file:'PopUpWindow.js')}"></script>
		<script src="${createLinkTo(dir:'test/ricomp', file:'newconfiguration.js')}"></script>

		<script>                  
			CONF_URL = "${createLinkTo(dir:'test', file:'testUIConfiguration.xml')}";
		</script>
		<title> Test UI </title>
	</head>

	<body onload="renderAll()">
		<div id="loadingwrapper" style="width: 100%;height: 100%;">
			<div id="loading">
			    <div class="loading-indicator">Loading...</div>
			</div>
		</div>
	</body>
</html>
