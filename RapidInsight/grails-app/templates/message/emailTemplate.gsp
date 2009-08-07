<%@ page import="message.RsMessage;" %>
<html>
<body>
<g:if  test="${message.action==RsMessage.ACTION_CREATE}"><b>"Event Created"</b></g:if>
<g:else><b>"Event Cleared"</b></g:else>

<br><br>Event Properties
<ul>
<g:each in="${event.asMap().entrySet()}">
<li>${it.key} : ${it.value}</li>
</g:each>
</ul>
</body>
</html>