<script type="text/javascript">
YAHOO.util.Event.onDOMReady(function() {
    var params = {};
    <%
        actions.each{actionName->
    %>
    YAHOO.rapidjs.Actions['${actionName}'].execute(params);
    <%
        }
    %>
});
</script>