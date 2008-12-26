<html>
<head>
    <rui:javascript dir="yui/layout" file="layout-min.js"/>
    <rui:javascript dir="yui/resize" file="resize-min.js"/>
    <rui:javascript dir="rapidjs/component/search" file="SearchGrid.js"></rui:javascript>

    <rui:stylesheet dir="js/yui/resize" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/layout" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="menu.css"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/assets/skins/sam" file="skin.css"></rui:stylesheet>
    <rui:stylesheet dir="css/rapidjs" includeType="recursive"></rui:stylesheet>
    <rui:stylesheet dir="js/yui/button/assets/skins/sam" file="button.css"></rui:stylesheet>

    <rui:stylesheet dir="js/yui/container/assets/skins/sam" file="container.css"></rui:stylesheet>
</head>
<body class=" yui-skin-sam">
     <rui:searchGrid id="searchGrid" url="res.xml?searchIn=RsEvent" queryParameter="query" rootTag="Results" contentPath="Result" queryEnabled="false"
                keyAttribute="id" totalCountAttribute="Total" offsetAttribute="Offset" sortOrderAttribute="sortOrder" title="Events"
                fieldsUrl="script/run/getViewFields?format=xml">
        <rui:sgColumns>
            <rui:sgColumn attributeName="creationClassName" colLabel="Class" width="100"></rui:sgColumn>
            <rui:sgColumn attributeName="name" colLabel="Name" width="100"></rui:sgColumn>
        </rui:sgColumns>
     </rui:searchGrid>

     <script type="text/javascript">
        YAHOO.util.Event.onDOMReady(function() {
            var searchGrid = YAHOO.rapidjs.Components['searchGrid'];
            var layout = new YAHOO.widget.Layout({
                units: [
                    { position: 'center', body: searchGrid.container.id, resize: false, gutter: '1px' }
                ]
            });
            layout.render();
            var layoutCenter = layout.getUnitByPosition('center');
            searchGrid.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
            layout.on('resize', function() {
                searchGrid.resize(layoutCenter.getSizes().body.w, layoutCenter.getSizes().body.h);
            });
            window.layout = layout;

        })

     </script>
</body>
</html>