<%@ page import="message.RsMessage;" %><% def propsToSend=["name","elementName","severity"] %>
<g:if  test="${message.action==RsMessage.ACTION_CREATE}">"Event Created"</g:if><g:else>"Event Cleared"</g:else>
Event Properties
<g:each var="propName" in="${propsToSend}">${propName} : ${event.getProperty(propName)}
</g:each>
