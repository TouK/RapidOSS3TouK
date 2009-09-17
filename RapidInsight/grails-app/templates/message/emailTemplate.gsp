<%@ page import="message.RsMessage;" %>
<html>
<body>
<g:if  test="${message.eventType==RsMessage.EVENT_TYPE_CREATE}"><b>"Event Created"</b></g:if>
<g:else><b>"Event Cleared"</b></g:else>

<br><br>Event Properties
<ul>
<g:each var="entry" in="${event.asMap().entrySet()}">
<li>${entry.key} : ${entry.value}</li>
</g:each>
</ul>
</body>
</html>