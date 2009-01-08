</div>

<script type="text/javascript">
var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: "ruleBody", resize: false, gutter: '1px' }

            ]
        });
        layout.on('render', function(){
        	var topUnit = layout.getUnitByPosition('top');
        	YAHOO.util.Dom.setStyle(topUnit.get('wrap'), 'background-color', '#BBD4F6')
            var header = topUnit.body;
            YAHOO.util.Dom.setStyle(header, 'border', 'none');
            var left = layout.getUnitByPosition('left').body;
            YAHOO.util.Dom.setStyle(left, 'top', '1px');
        });
        layout.render();
        var layoutLeft = layout.getUnitByPosition('left');
        layoutLeft.on('resize', function(){
            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
        });

        window.layout = layout;

    })
</script>

</body>
</html>