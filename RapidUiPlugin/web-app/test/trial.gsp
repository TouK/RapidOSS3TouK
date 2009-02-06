<html>
<head>
    <script type="text/javascript" src="../js/yui/utilities/utilities.js"></script>
    <script type="text/javascript" src="../js/yui/element/element-beta.js"></script>
    <script type="text/javascript" src="../js/yui/json/json-min.js"></script>
    <script type="text/javascript" src="../js/yui/container/container.js"></script>
    <script type="text/javascript" src="../js/yui/menu/menu.js"></script>
    <script type="text/javascript" src="../js/yui/resize/resize-min.js"></script>
    <script type="text/javascript" src="../js/yui/layout/layout.js"></script>
    <script type="text/javascript" src="../js/yui/history/history.js"></script>
    <script type="text/javascript" src="../js/yui/datasource/datasource-min.js"></script>
    <script type="text/javascript" src="../js/yui/datatable/datatable.js"></script>
     <script type="text/javascript" src="../js/yui/button/button.js"></script>
    <script type="text/javascript" src="../js/ext/ext.js"></script>
    <script type="text/javascript" src="../js/rapidjs/yuioverride.js"></script>

    <script type="text/javascript" src="../js/rapidjs/component/ComponentContainer.js"></script>
    <script type="text/javascript" src="../js/rapidjs/RapidUtil.js"></script>
    <script type="text/javascript" src="../js/rapidjs/data/NodeFactory.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/RapidElement.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/PollingComponentContainer.js"></script>


    <script type="text/javascript" src="../js/rapidjs/data/RapidXmlDocument.js"></script>
    <script type="text/javascript" src="../js/rapidjs/SelectUtils.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/form/Form.js"></script>

    <script type="text/javascript" src="../js/rapidjs/component/simplewidgets/Button.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/BasicTool.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/SettingsTool.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/dialog/Dialog.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/PopupWindow.js"></script>


    <script type="text/javascript" src="../js/rapidjs/component/tools/ButtonToolBar.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/SearchListSettingsTool.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/LoadingTool.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/ErrorTool.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/search/ExportTool.js"></script>

    <script type="text/javascript" src="../js/rapidjs/component/simplewidgets/split.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/tools/Tooltip.js"></script>


    <script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeNode.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeHeaderCell.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeGridView.js"></script>
    <script type="text/javascript" src="../js/rapidjs/component/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" src="../js/rapidjs/designer/Config.js"></script>
    <script type="text/javascript" src="../js/rapidjs/designer/DesignerUtils.js"></script>
    <script type="text/javascript" src="../js/rapidjs/designer/ActionDefinitionDialog.js"></script>
    <script type="text/javascript" src="../js/rapidjs/designer/UIDesigner.js"></script>



    <link rel="stylesheet" type="text/css" href="../js/yui/assets/skins/sam/menu.css"/>
    <link rel="stylesheet" type="text/css" href="../js/yui/assets/skins/sam/skin.css"/>
    <link rel="stylesheet" type="text/css" href="../js/yui/button/assets/skins/sam/button.css"/>
    <link rel="stylesheet" type="text/css" href="../js/yui/container/assets/skins/sam/container.css"/>
    <link rel="stylesheet" type="text/css" href="../js/yui/datatable/assets/skins/sam/datatable.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/yuioverride.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/common.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/dialog.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/form.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/layout.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/simplewidgets/button.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/tools/tools.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/treegrid/treegrid.css"/>
    <link rel="stylesheet" type="text/css" href="../css/rapidjs/designer/designer.css"/>
</head>
<body class="yui-skin-sam">
    <div id="innerTop">innerTop</div>
    <div id="innerCenter">innerCenter</div>
    <div id="outerCenter">center</div>
    <script>
           var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', body:'outerCenter'},
                    { position: 'top', height:400}
                ]
            });
           layout.on('render', function(){
              var topUnit = layout.getUnitByPosition('top');
              var layout2 = new YAHOO.widget.Layout(topUnit.get('wrap'),{
                parent: layout,
                units: [
                    { position: 'center', body:'innerCenter'},
                    { position: 'top', body:'innerTop', height:10}
                ]
            });
            layout2.render();
           }, this,true)
            layout.render();

    </script>
</body>
</html>