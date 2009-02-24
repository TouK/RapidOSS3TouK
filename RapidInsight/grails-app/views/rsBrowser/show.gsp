<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ${domainObject.class.name}</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="home" action="classes">Home</g:link></span>
    <span class="menuButton"><g:link class="list" action="${params.domain}">${domainObject.class.name} List</g:link></span>
</div>
<div class="body">
    <h1>Show ${domainObject.class.name}</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>
                <tr class="prop">
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${domainObject?.id}</td>

                </tr>
                <g:each in="${keys}" var="p">
                    <g:if test="${p.name != 'id'}">
                        <tr class="prop">
                            <td valign="top" class="name">${p.name}:</td>
                            <td valign="top" class="value">${domainObject[p.name]}</td>
                        </tr>
                    </g:if>
                </g:each>
                <g:each in="${propertyList}" var="p">
                    <tr class="prop">
                        <td valign="top" class="name">${p.name}:</td>
                        <g:if test="${p.name != 'id' && !p.isKey}">
                            <g:if test="${!p.isRelation}">
                                <td valign="top" class="value">${domainObject[p.name]?.encodeAsHTML()}</td>
                            </g:if>
                            <g:else>
                                <g:if test="${p.isOneToMany() || p.isManyToMany()}">
                                    <td valign="top" style="text-align:left;" class="value">
                                        <ul>

                                            <g:each var="relatedObject" in="${domainObject[p.name]}">
                                                <%
                                                    def domainClass = grailsApplication.getDomainClass(relatedObject.class.name);
                                                %>
                                                <li><g:link action="${domainClass.logicalPropertyName}" id="${relatedObject.id}">${relatedObject}</g:link></li>
                                            </g:each>
                                        </ul>
                                    </td>
                                </g:if>
                                <g:else>
                                    <g:set var="relatedObject" value="${domainObject[p.name]}"></g:set>
                                    <g:if test="${relatedObject}">
                                         <g:set var="relatedClass" value="${grailsApplication.getDomainClass(relatedObject.class.name)}"></g:set>
                                         <td valign="top" class="value"><g:link action="${relatedClass.logicalPropertyName}" id="${relatedObject.id}">${relatedObject}</g:link></td>
                                    </g:if>
                                    <g:else>
                                        <td></td>
                                    </g:else>
                                </g:else>
                            </g:else>
                        </g:if>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="buttons">
    </div>
</div>
</body>
</html>
