<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var inventoryList = YAHOO.rapidjs.Components['inventoryList'];
    inventoryList.renderCellFunction = function(key, value, data, el){
        if(key == "className" || key == "name"){
            YAHOO.util.Dom.setStyle(el, 'color','blue')
        }
        return value;
     }
    filterTree.poll();
</script>

<rui:include template="pageContents/renderer/_renderQueryButtons.gsp" model="[componentName:'filterTree', queryType:'topology', searchComponentType:'list',searchComponentName:'inventoryList']"></rui:include>