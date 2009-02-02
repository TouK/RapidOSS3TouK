<html>
<head>
    <meta name="layout" content="${tab.url.url}Layout" />
</head>
<body>
<%
    print tabContent;
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
    })
</script>
</body>
</html>