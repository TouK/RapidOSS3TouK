<g:if test="${flash.message}">
    <div class="message" id="pageMessage">${flash.message}</div>
</g:if>
<g:hasErrors bean="${flash.errors}">
   <div class="errors" id="pageFlashErrors">
        <g:renderErrors bean="${flash.errors}"/>
    </div>
</g:hasErrors>

<%
    beans.each{bean->
%>
<g:hasErrors bean="${bean}">
   <div class="errors" id="pageBeanErrors">
        <g:renderErrors bean="${bean}" as="list"/>
    </div>
</g:hasErrors>
<%
    }
%>