<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Code Editor</title>
       <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="${pluginContextPath}/js/edit_area" file="edit_area.css" />"/>
       <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="js/yui/fonts" file="fonts-min.css" />"/>
       <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="js/yui/button/assets/skins/sam" file="button.css" />"/>
       <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="js/yui/container/assets/skins/sam" file="container.css" />"/>
       <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="js/yui/datatable/assets/skins/sam" file="datatable.css" />"/>

        <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/edit_area" file="edit_area_loader.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/edit_area/reg_syntax" file="groovy.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/yahoo-dom-event" file="yahoo-dom-event.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/utilities" file="utilities.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/element" file="element-min.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/button" file="button-min.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/datasource" file="datasource-min.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/datatable" file="datatable-min.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/container" file="container-min.js" />"></script>
        <script type="text/javascript" src="<g:createLinkTo dir="js/yui/connection" file="connection-min.js" />"></script>

        <style type="text/css">
            .fileDetails{
                cursor:pointer;
                white-space:nowrap
            }
            .fileDetailsDir{
                font-weight:bold;
                cursor:pointer;
                white-space:nowrap;
            }
            .fileList{
                overflow:auto;
                height:250px;
                width:400px;
                border-width:1px;
                border-style:solid
            }
            .fileDetailsSelected{
                background-color:blue;
            }
        </style>
    </head>
    <body class=" yui-skin-sam">
        <g:editArea id="editArea" width="100%" start_highlight="true" height="100%"
                font_size="10"
                allow_resize="n"
                allow_toggle="false"
                is_multi_files="true"
                syntax="groovy"
                toolbar="search, go_to_line, |, undo, redo, |, help"
                plugins="fileoperations,autocomplete,action,actiontabs"
                begin_toolbar="newfile,savefile,deletefile,openfile"
                end_toolbar="executeAction"
        >

        </g:editArea>
        <textarea id="editArea" width="100%" height="100%" style="width:100%;height:100%;white-space:pre-wrap"></textarea>
    </body>
</html>
