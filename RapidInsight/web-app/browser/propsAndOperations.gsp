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
        def operations = domainClass.clazz."getOperations"();
        def keys = domainClass.clazz."keySet"();
        def properties = domainClass.clazz."getPropertiesList"();
        def pureProps = [];
        def relations = [];
        properties.each {p ->
            if (p.isRelation) {
                relations.add(p)
            }
            else if (!p.isKey && !p.isOperationProperty) {
                pureProps.add(p)
            }
        }
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
                                <td>${key.type.name}</td>
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
                                <td>${rel.type.name}</td>
                            </tr>
                            <% status++ %>
                        </g:each>
                    </tbody>
                </table>
                <div><h3 style="color:#083772">Operations:</h3></div>
                <table width="100%" cellspacing="1" cellpadding="1">
                    <tbody>
                        <g:each var="op" in="${operations}" status="i">
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td>
                                    <div style="font-weight:bold">${op.name} ( ${op.parameters.name.join(', ')} )</div>
                                    <div style="padding-left:20px">
                                        <div>${op.description}</div>
                                        <div><span style="font-style:italic">Returns: </span><span>${op.returnType}</span></div>
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
