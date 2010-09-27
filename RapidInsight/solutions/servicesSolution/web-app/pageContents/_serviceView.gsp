<script>
    function getEventsQuery(data){
        if(data.nodeType == 'Container'){
            return 'rsDatasource:' + data.name.toExactQuery()
        }
        if(data.nodeType == 'Service'){
            return 'serviceName:' + data.name.toQuery()
        }
        else{
            return 'elementName:' + data.name.toExactQuery();
        }
    }
    var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
</script>
<rui:include template="pageContents/renderer/_renderCommonEventsListCellFunction.gsp" model="[componentName:'eventsGrid']"></rui:include>