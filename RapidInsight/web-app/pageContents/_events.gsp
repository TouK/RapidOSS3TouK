<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
    eventsGrid.renderCellFunction = function(key, value, data, el){
        if(key == "changedAt" || key == 'createdAt' || key == 'clearedAt' || key == 'willExpireAt'){
            if(value == "0" || value == "")
            {
                return "never"
            }
            else
            {
                try
                {
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("d M H:i:s");
                }
                catch(e)
                {}
            }
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
            queryGroupForm.show(createURL('queryGroupForm.gsp', {mode:'create', type:'event'}));

        }
    });
    filterTree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveQueryForm'];
            queryForm.popupWindow.show();
            queryForm.show(createURL('queryForm.gsp', {mode:'create', type:'event', searchComponentType:'grid'}));
        }
    });
    filterTree.poll();
    eventsGrid.poll();
</script>