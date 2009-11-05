<html>
<head>
    <g:render template="/layouts/layoutHeader"></g:render>
    <script type="text/javascript" src="js/rapidjs/component/simplewidgets/ConfirmBox.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/UIDesigner.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/DesignerUtils.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/DesignerRenderUtils.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/WizardScenario.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/Config.js"></script>
    <script type="text/javascript" src="js/rapidjs/designer/DesignerWizard.js"></script>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/designer/designer.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/designer/help.css"/>
    <link rel="stylesheet" type="text/css" href="css/rapidjs/designer/wizard.css"/>
</head>
<body class="yui-skin-sam">
    <script>
        var config = {
            rootTag : "UiConfig",
            contentPath : "UiElement",
            keyAttribute : 'id',
            treeTypeAttribute : 'designerType',
            treeHideAttribute : 'designerHidden',
            url : "uiDesigner/view?format=xml",
            saveUrl : "uiDesigner/save?format=xml",
            generateUrl : "uiDesigner/generate?format=xml",
            helpUrl : "uiDesigner/help?format=xml",
            metaDataUrl : "uiDesigner/metaData?format=xml"
        }
        window.designer = new YAHOO.rapidjs.designer.UIDesigner(config);
    </script>
</body>
</html>