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
</script>
<rui:include template="pageContents/renderer/_renderCommonEventsListCellFunction.gsp" model="[componentName:'eventsGrid']"></rui:include>