<script>
    var classTree = YAHOO.rapidjs.Components["classTree"];
    classTree.poll();
    var objectList = YAHOO.rapidjs.Components["objectList"];
    var toolbarEl = objectList.toolbar.el;
    var tds = toolbarEl.nextSibling.getElementsByTagName('td');
    YAHOO.util.Dom.setStyle(tds[3], 'display', 'none')
    YAHOO.util.Dom.setStyle(tds[4], 'display', 'none')
    objectList.renderCellFunction = function(key, value, data, el){
        if(key == "id"){
           YAHOO.util.Dom.setStyle(el, 'color', 'blue');
        }
        return value;
    }
</script>