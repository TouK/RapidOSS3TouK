<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var inventoryList = YAHOO.rapidjs.Components['inventoryList'];
    inventoryList.renderCellFunction = function(key, value, data, el){
        if(key == "className" || key == "name"){
            YAHOO.util.Dom.setStyle(el, 'color','blue')
        }
        return value;
     }
    filterTree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
        	var queryGroupForm = YAHOO.rapidjs.Components['saveQueryGroupForm'];
            queryGroupForm.popupWindow.show();
            queryGroupForm.show(createURL('queryGroupForm.gsp', {mode:'create', type:'topology'}));

        }
    });
    filterTree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveQueryForm'];
            queryForm.popupWindow.show();
            queryForm.show(createURL('queryForm.gsp', {mode:'create', type:'topology', searchComponentType:'list'}));
        }
    });
    filterTree.poll();
</script>