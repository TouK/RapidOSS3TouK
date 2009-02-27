<script>
    var classTree = YAHOO.rapidjs.Components["classTree"];
    classTree.poll();
    var objectList = YAHOO.rapidjs.Components["objectList"];
    objectList.renderCellFunction = function(key, value, data, el){
        if(key == "id"){
           YAHOO.util.Dom.setStyle(el, 'color', 'blue');
        }
        return value;
    }
</script>