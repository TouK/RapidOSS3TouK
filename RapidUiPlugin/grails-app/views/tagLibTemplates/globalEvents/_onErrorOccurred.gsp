<script type="text/javascript">
YAHOO.rapidjs.ErrorManager.errorOccurredEvent.subscribe(function(obj, errorMessages) {
    var params = {messages:errorMessages};
    <%
        actions.each{actionName->
    %>
    YAHOO.rapidjs.Actions['${actionName}'].execute(params);
    <%
        }
    %>
}, this, true);
</script>