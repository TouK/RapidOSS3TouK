<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Feb 19, 2009
  Time: 10:22:54 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<script type="text/javascript" src="../js/yui/utilities/utilities.js"></script>
<script type="text/javascript" src="../js/yui/json/json-min.js"></script>
<script type="text/javascript" src="../js/yui/container/container-min.js"></script>
<script type="text/javascript" src="../js/yui/menu/menu-min.js"></script>
<script type="text/javascript" src="../js/yui/resize/resize-min.js"></script>
<script type="text/javascript" src="../js/yui/layout/layout-min.js"></script>
<script type="text/javascript" src="../js/yui/history/history-min.js"></script>
<script type="text/javascript" src="../js/yui/datasource/datasource-min.js"></script>
<script type="text/javascript" src="../js/yui/datatable/datatable-min.js"></script>
<script type="text/javascript" src="../js/yui/charts/charts-experimental-min.js"></script>
<script type="text/javascript" src="../js/yui/autocomplete/autocomplete-min.js"></script>
<script type="text/javascript" src="../js/ext/ext.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/ComponentContainer.js"></script>
<script type="text/javascript" src="../js/rapidjs/RapidUtil.js"></script>
<script type="text/javascript" src="../js/rapidjs/data/NodeFactory.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/RapidElement.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/PollingComponentContainer.js"></script>

<script type="text/javascript" src="../js/yui/button/button-min.js"></script>
<script type="text/javascript" src="../js/rapidjs/data/RapidXmlDocument.js"></script>
<script type="text/javascript" src="../js/rapidjs/SelectUtils.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/form/Form.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/form/HtmlEmbeddableForm.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/simplewidgets/Button.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/BasicTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/SettingsTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/dialog/Dialog.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/PopupWindow.js"></script>


<script type="text/javascript" src="../js/rapidjs/component/search/SearchNode.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/ButtonToolBar.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/SearchListSettingsTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/LoadingTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/ErrorTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/search/ExportTool.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/search/AbstractSearchList.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/search/SearchList.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/search/ViewBuilder.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/search/SearchGrid.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/autocomplete/Autocomplete.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/pieChart/PieChart.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/pieChart/FABridge.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/pieChart/FlexPieChart.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/map/GMap.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/simplewidgets/split.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/tools/Tooltip.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/timeline/Timeline.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/timeline/Timelineext.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/timeline/TimelineWindow.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeNode.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeHeaderCell.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeGridView.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeGrid.js"></script>

<script type="text/javascript" src="../js/rapidjs/component/action/Action.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/html/Html.js"></script>
<script type="text/javascript" src="../js/rapidjs/component/topologyMap/TopologyMap.js"></script>


<link rel="stylesheet" type="text/css" href="../js/yui/assets/skins/sam/menu.css"/>
<link rel="stylesheet" type="text/css" href="../js/yui/assets/skins/sam/skin.css"/>
<link rel="stylesheet" type="text/css" href="../js/yui/button/assets/skins/sam/button.css"/>
<link rel="stylesheet" type="text/css" href="../js/yui/container/assets/skins/sam/container.css"/>
<link rel="stylesheet" type="text/css" href="../js/yui/datatable/assets/skins/sam/datatable.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/yuioverride.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/timeline/timeline.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/common.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/dialog.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/form.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/layout.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/search/search.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/search/searchlist.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/search/searchgrid.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/simplewidgets/button.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/tools/tools.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/treegrid/treegrid.css"/>
<link rel="stylesheet" type="text/css" href="../css/rapidjs/autocomplete/autocomplete.css"/>
<link rel="stylesheet" type="text/css" href="../riindex.css"/>

</head>
  <body>
    <rui:globalEvent onDOMReady="${['action1']}"></rui:globalEvent>

  </body>
</html>