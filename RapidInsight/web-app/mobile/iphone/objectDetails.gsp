<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    name = params.name
    object = RsTopologyObject.get(name: name)
%>
<div id="objectDetails">

    <g:if test="${!object}">
        <div id="messageArea" class="error">
            Object with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <% objectProperties=object.retrieveVisibleProperties(); %>
        <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
        <rui:include template="mobile/iphone/actionMenu.gsp" model="${[actions:CONFIG.INVENTORY_ACTIONS, domainObject:object, title:'Actions', redirectUrl:'mobile/iphone/objectDetails.gsp']}"></rui:include>
        <rui:include template="mobile/contents/objectDetails.gsp" model="${binding.variables}"></rui:include>
    </g:else>
</div>