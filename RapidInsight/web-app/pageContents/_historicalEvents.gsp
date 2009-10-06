<script>
	var filterTree = YAHOO.rapidjs.Components['filterTree'];
	var eventList = YAHOO.rapidjs.Components['eventList'];
</script>
<rui:include template="pageContents/renderer/_renderCommonEventsListCellFunction.gsp" model="[componentName:'eventList']"></rui:include>
<rui:include template="pageContents/renderer/_renderQueryButtons.gsp" model="[componentName:'filterTree', queryType:'historicalEvent', searchComponentType:'list']"></rui:include>
<script>
    filterTree.poll();
    eventList.poll();
</script>