<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Jan 15, 2009
  Time: 4:55:43 PM
--%>

<%
    def className = params.className;
    def domainClass = grailsApplication.getDomainClass(className);
%>
<g:if test="domainClass">
    <%
        def operations = domainClass.clazz.getOperations();
        def keys = domainClass.clazz.keySet();
        def pureProps = domainClass.clazz.getNonFederatedPropertyList().findAll{return !it.isKey}
        def federatedProps = domainClass.clazz.getFederatedPropertyList()
        def relations = domainClass.clazz.getRelationPropertyList();
    %>
    <div class="yui-navset yui-navset-top ri-object-details" style="margin-top:5px">
        <div style="display:block">
            <div>
                <div><h3 style="color:#083772">Properties:</h3></div>
                <table width="100%" cellspacing="1" cellpadding="1" style="border-bottom:1px solid #083772">
                    <tbody>
                        <tr class="odd"><td  width="0%"><h4 style="color:#083772">Keys:</h4></td><td></td></tr>
                        <g:set var="status" value="${1}"></g:set>
                        <g:each var="key" in="${keys}">
                            <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${key.name}</td>
                                <g:if test="${key.isRelation}">
                                   <td>${key.relatedModel.name}</td>
                                </g:if>
                                <g:else>
                                    <td>${key.type.name}</td>
                                </g:else>
                            </tr>
                            <% status++ %>
                        </g:each>
                        <tr class="${(status % 2) == 0 ? 'odd' : 'even'}"><td width="0%"><h4 style="color:#083772">Simple Properties:</h4></td><td></td></tr>
                        <% status++ %>
                        <g:each var="prop" in="${pureProps}">
                            <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${prop.name}</td>
                                <td>${prop.type.name}</td>
                            </tr>
                            <% status++ %>
                        </g:each>
                        <tr class="${(status % 2) == 0 ? 'odd' : 'even'}"><td width="0%"><h4 style="color:#083772">Relations:</h4></td><td></td></tr>
                        <% status++ %>
                        <g:each var="rel" in="${relations}">
                            <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${rel.name}</td>
                                <td>${rel.relatedModel.name}</td>
                            </tr>
                            <% status++ %>
                        </g:each>
                    </tbody>
                </table>
                <div><h3 style="color:#083772">Federated Properties:</h3></div>
                <table width="100%" cellspacing="1" cellpadding="1" style="border-bottom:1px solid #083772">
                    <tbody>
                        <g:each var="prop" in="${federatedProps}">
                            <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${prop.name}</td>
                                <td>${prop.type.name}</td>
                            </tr>
                            <% status++ %>
                        </g:each>

                </table>
                <div style="margin-top:10px;"><h3 style="color:#083772">Operations:</h3></div>
                <table width="100%" cellspacing="1" cellpadding="1">
                    <tbody>
                        <g:each var="op" in="${operations}" status="i">
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td>
                                    <div style="font-weight:bold">${op.name} ( ${op.parameters.name.join(', ')} )</div>
                                    <div style="padding-left:20px">
                                        <div>${op.description}</div>
                                        <div style="padding:3px 0px 0px 0px"><span style="font-style:italic">Returns: </span><span>${op.returnType}</span></div>
                                    </div>
                                </td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</g:if>
<g:else>
    Class with name ${className} could not be found.
</g:else>
