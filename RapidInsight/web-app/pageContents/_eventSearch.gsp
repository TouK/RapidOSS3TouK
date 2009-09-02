<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var eventList = YAHOO.rapidjs.Components['eventList'];
    eventList.renderCellFunction = function(key, value, data, el){
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
        else if(key == "severity")
        {
            switch(value)
            {
                case '5' : return "Critical";
                case '4' : return "Major";
                case '3' : return "Minor";
                case '2' : return "Warning";
                case '1' : return "Indeterminate";
                case '0' : return "Clear";
                default  : return "";
            }
        }
        else if(data.rsAlias == "RsRiEvent" && (key == "elementName" || key=="identifier")){
            YAHOO.util.Dom.setStyle(el, 'color', 'blue')
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
            queryForm.show(createURL('queryForm.gsp', {mode:'create', type:'event', searchComponentType:'list'}));
        }
    });
    filterTree.poll();
    eventList.poll();
</script>