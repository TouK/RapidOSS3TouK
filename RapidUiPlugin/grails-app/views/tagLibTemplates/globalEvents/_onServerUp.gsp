<script type="text/javascript">
YAHOO.rapidjs.ErrorManager.serverDownEvent.subscribe(function(obj, errorMessages) {
    var params = {};
    <%
        actions.each{actionName->
    %>
    YAHOO.rapidjs.Actions['${actionName}'].execute(params);
    <%
        }
    %>
}, this, true);
</script>