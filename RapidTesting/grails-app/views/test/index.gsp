
<html>
	<head>
        <link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/yui-ext.css"/>
		<link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/treegrid.css"/>
		<link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/common.css"/>
		<link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/layout.css"/>
		<link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/overlay.css"/>
		<link rel="stylesheet" type="text/css" href="${createLinkTo(dir:'')}/css/mgrid.css"/>

		<script src="${createLinkTo(dir:'')}/jslib/YAHOO.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/RSHelp.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/yui/build/animation/animation.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/yui/build/container/container.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/rapidjs.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/ServerStatusTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/RapidUtil.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/SelectUtils.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/Login.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/RapidPanel.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/menu/MenuAction.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/menu/MenuItem.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/menu/ContextMenu.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/menu/Tooltip.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/split.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/data/NodeFactory.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/data/RapidXmlDocument.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/data/NodeFactory.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/data/xml2json.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/RapidElement.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/grid/GridRow.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/grid/RapidSelectionModel.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/grid/RapidGridView.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/grid/RapidGrid.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/grid/RapidXmlDataModel.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/tree/TreeCell.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/tree/TreeNode.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/tree/TreeHeaderCell.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/tree/TreeGrid.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/ComponentContainer.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/PollingComponentContainer.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/windows/TreeWindow.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/windows/GridWindow.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/windows/ConsoleWindow.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/windows/HtmlWindow.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/dialogs/ComponentConfigurationDialog.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/dialogs/ErrorDialog.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/RapidTabPanel.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/LayoutTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/LoadingTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/PollingTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/LogoutTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/UserTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/ErrorTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/layout/HelpTool.js"></script>
		<script src="${createLinkTo(dir:'')}/jslib/rapidjs/component/PopUpWindow.js"></script>
		<script src="${createLinkTo(dir:'')}/ricomp/newconfiguration.js"></script>

		<script>                  
			CONF_URL = "${createLinkTo(dir:'')}/testUIConfiguration.xml";
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
