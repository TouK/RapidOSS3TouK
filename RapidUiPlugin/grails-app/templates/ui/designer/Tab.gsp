<html>
<head>
    <meta name="layout" content="${tab.url.url}Layout" />
</head>
<body>
<%
    print tabContent;
    def contentFiles = com.ifountain.rui.util.DesignerTemplateUtils.getLayoutContentFiles (tab.layout);
%>
<%
    contentFiles.each{contentFile->
%>
    <div id="${com.ifountain.rui.util.DesignerTemplateUtils.getContentDivId(contentFile)}">
        ${contentFile.getText()}    
    </div>
<%
    }
%>
<%
    if(tab.contentFile != null && tab.contentFile != "")
    {
        def contentFile = new File("web-app/"+tab.contentFile);
%>
        ${contentFile.getText()}
<%
    }
%>
<script type="text/javascript">
    YAHOO.util.Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:45},
                { position: 'center', resize: false, gutter: '1px' },
            ]
        });
        layout.on('render', function() {
            var el = layout.getUnitByPosition('center').get('wrap');
            ${layoutContent}
        });
        layout.render();
        window.layout = layout;
    });
</script>
</body>
</html>