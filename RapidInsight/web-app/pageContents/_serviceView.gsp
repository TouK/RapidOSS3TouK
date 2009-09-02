<script>
    function getEventsQuery(data){
        if(data.nodeType == 'Container'){
            return 'rsDatasource:' + data.name.toExactQuery()
        }
        else{
            return 'rsDatasource:' + data.rsDatasource.toExactQuery() + ' AND elementName:' + data.name.toExactQuery();
        }
    }
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
</script>