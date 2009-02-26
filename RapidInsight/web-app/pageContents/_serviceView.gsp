<script>
    function getEventsQuery(data){
        if(data.nodeType == 'Container'){
            return 'rsDatasource:"' + data.name + '"'
        }
        else{
            return 'rsDatasource:"' + data.rsDatasource + '" AND elementName:"' + data.name + '"';
        }
    }
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
</script>