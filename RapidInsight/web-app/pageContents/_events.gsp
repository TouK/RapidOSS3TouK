<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
</script>
<rui:include template="pageContents/renderer/_renderCommonEventsListCellFunction.gsp" model="[componentName:'eventsGrid']"></rui:include>
<rui:include template="pageContents/renderer/_renderQueryButtons.gsp" model="[componentName:'filterTree', queryType:'event', searchComponentType:'grid']"></rui:include>
<script>
    filterTree.poll();
    eventsGrid.poll();
</script>