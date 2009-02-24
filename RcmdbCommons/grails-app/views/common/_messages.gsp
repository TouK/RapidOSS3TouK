<g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
</g:if>
<g:hasErrors bean="${flash.errors}">
   <div class="errors">
        <g:renderErrors bean="${flash.errors}"/>
    </div>
</g:hasErrors>

<%
    beans.each{bean->
%>
<g:hasErrors bean="${bean}">
   <div class="errors">
        <g:renderErrors bean="${bean}" as="list"/>
    </div>
</g:hasErrors>
<%
    }
%>