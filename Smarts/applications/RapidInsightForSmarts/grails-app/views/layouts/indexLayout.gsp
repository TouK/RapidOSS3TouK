<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 2, 2008
  Time: 11:42:22 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>iFountain - RapidInsight for Smarts</title>
    <script type="text/javascript" src="js/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="js/yui/resize/resize-beta-min.js"></script>
    <script type="text/javascript" src="js/yui/layout/layout-beta-min.js"></script>
    <script type="text/javascript" src="js/yui/history/history-min.js"></script>
    <script type="text/javascript" src="js/yui/datasource/datasource-beta-min.js"></script>
    <script type="text/javascript" src="js/yui/datatable/datatable-beta-min.js"></script>
    <script type="text/javascript" src="js/ext/ext.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/ComponentContainer.js"></script>
    <script type="text/javascript" src="js/rapidjs/RapidUtil.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/NodeFactory.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/RapidElement.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/PollingComponentContainer.js"></script>
    <script type="text/javascript" src="js/yui/container/container-min.js"></script>
    <script type="text/javascript" src="js/yui/button/button-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/data/RapidXmlDocument.js"></script>

    <script type="text/javascript" src="js/rapidjs/SelectUtils.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/form/Form.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/Button.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/BasicTool.js"></script>
    <script type="text/javascript" src="js/yui/container/container_core-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/dialog/Dialog.js"></script>
    <script type="text/javascript" src="js/yui/menu/menu-min.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/search/SearchNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ButtonToolBar.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/SearchListSettingsTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/LoadingTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/ErrorTool.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/AbstractSearchList.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/SearchList.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/ViewBuilder.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/search/SearchGrid.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/split.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/tools/Tooltip.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeNode.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeHeaderCell.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGridView.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/treegrid/TreeGrid.js"></script>

    <script type="text/javascript" src="js/rapidjs/component/action/Action.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/html/Html.js"></script>
    <script type="text/javascript" src="js/yui/charts/charts-experimental-min.js"></script>
    <script type="text/javascript" src="js/rapidjs/component/topologyMap/TopologyMap.js"></script>


    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/menu.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/assets/skins/sam/skin.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/button/assets/skins/sam/button.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/container/assets/skins/sam/container.css"/>
    <link rel="stylesheet" type="text/css" href="js/yui/datatable/assets/skins/sam/datatable.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/common.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/dialog.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/form.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/mgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/overlay.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/layout.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/ryuitree.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/search.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/searchlist.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/search/searchgrid.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/simplewidgets/button.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/tools/tools.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/treegrid/treegrid.css"/>
    <link rel="stylesheet" type="text/css" href="smartsindex.css"/>

    <jsec:isNotLoggedIn>
        <g:javascript>window.location='auth/login?targetUri=/index.gsp'</g:javascript>
    </jsec:isNotLoggedIn>
    <g:layoutHead/>
</head>
<body class=" yui-skin-sam">
    <g:layoutBody/>
</body>
</html>