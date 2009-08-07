<%@ page import="message.RsMessage;" %>----------------------------------
<g:if  test="${message.action==RsMessage.ACTION_CREATE}">"Event Created"</g:if><g:else>"Event Cleared"</g:else>
Event Properties
<g:each var="entry" in="${event.asMap().entrySet()}">${entry.key} : ${entry.value}
</g:each>
