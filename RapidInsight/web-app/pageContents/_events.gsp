<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
    eventsGrid.renderCellFunction = function(key, value, data, el){
        if(key == "changedAt"){
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
        	queryGroupForm.show(createURL('queryGroupForm.gsp', {mode:'create', type:'event'}));
        	queryGroupForm.popupWindow.show();

        }
    });
    filterTree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Add query',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveQueryForm'];
        	queryForm.show(createURL('queryForm.gsp', {mode:'create', type:'event', searchComponentType:'grid'}));
        	queryForm.popupWindow.show();
        }
    });
    filterTree.poll();
    eventsGrid.poll();
</script>