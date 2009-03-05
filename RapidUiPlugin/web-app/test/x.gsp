<html>
<head>
    <g:render template="/layouts/layoutHeader"></g:render>
</head>
<body>
      <div id="top">top</div>
      <div id="center"></div>
<script>
    YAHOO.util.Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: true, height:45},
                { position: 'center', resize: false, gutter: '1px' },
                { position: 'left', width: 250, resize: true, scroll: false}
            ]
        });
        layout.on('render', function(){
            var layout2 = new YAHOO.widget.Layout(layout.getUnitByPosition("center").get("wrap"), {
                parent:layout,
               units: [{ position: 'left', resize: true, height:45},
                { position: 'center', resize: false, gutter: '1px' }]
               }
            )
            layout2.render();

            var layout3 = new YAHOO.widget.Layout(layout.getUnitByPosition("left").get("wrap"), {
                parent:layout,
               units: [{ position: 'left', resize: true, height:45},
                { position: 'center', resize: false, gutter: '1px' }]
               }
            )
            layout3.render();
        });
        layout.render();

    })
</script>
</body>
</html>