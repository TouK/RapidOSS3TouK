<%@ page import="message.RsMessage;" %>
----------------------------------
<g:if  test="${message.action==RsMessage.ACTION_CREATE}">"Event Created"</g:if><g:else>"Event Cleared"</g:else>
Event Properties
<g:each in="${event.asMap().entrySet()}">${it.key} : ${it.value}
</g:each>
