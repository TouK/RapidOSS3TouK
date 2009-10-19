<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils; java.sql.Timestamp; search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def name = params.name
    def object = RsTopologyObject.get(name: name)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>
<html>
<head>
    <title>ROSSMobile</title>
    <link rel="stylesheet" type="text/css" href="${createLinkTo(dir: 'css/mobile', file: 'simple.css')}"/>
</head>
<body>
<div class="toolbar">
    <h1>ROSS</h1>
    <div class="toolbarLinks">
        <a href="${createLinkTo(dir: 'mobile/simple', file: 'home.gsp')}">Home</a>|
    <rui:link url="mobile/simple/queries.gsp" params="${[filterType:'topology', listURI:'mobile/simple/inventory.gsp']}">Queries</rui:link>|
    <rui:link url="mobile/simple/search.gsp" params="${[rootClass:'RsTopologyObject']}">Search</rui:link>
    </div>
    <g:if test="${CONFIG.INVENTORY_ACTIONS.size() > 0}">
        <div class="toolbarLinks">
            <rui:link url="mobile/simple/actions.gsp" params="${[name:params.name, type:'topology']}">Object Actions</rui:link>
        </div>
    </g:if>
</div>
<div>

    <g:if test="${!object}">
        <div class="error">
            Object with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
        <%
            String className = object.getClass().getName();
            def allProperties = object.getPropertiesList();
            def propertyNames = ["className", "name"];
            allProperties.each {
                def propName = it.name
                if (propName != "className" && propName != "name") {
                    propertyNames.add(propName)
                }
            }
            def relations = DomainClassUtils.getRelations(className);
        %>
        <div class="ri-mobile-objectDetails">
            <table class="itable" width="100%" cellspacing="1" cellpadding="1">
                <tbody>
                    <g:each var="propertyName" status="i" in="${propertyNames}">
                        <g:if test="${propertyName != 'id' && propertyName != 'rsDatasource'}">
                            <g:set var="propertyValue" value=""/>
                            <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                                <td width="0%" style="font-weight:bold">${propertyName}</td>
                                <g:if test="${!relations.containsKey(propertyName)}">
                                    <%
                                        propertyValue = object[propertyName];
                                        if (CONFIG.INVENTORY_DATE_PROPERTIES.contains(propertyName))
                                        {
                                            propertyValue = format.format(new Timestamp(propertyValue))
                                        }
                                        def fieldHasError = object.hasErrors(propertyName)
                                    %>
                                    <td ${fieldHasError ? 'class="ri-field-error"' : ""}>
                                        ${fieldHasError ? "InAccessible" : propertyValue}&nbsp;
                                    </td>
                                </g:if>
                                <g:else>
                                    <g:set var="relation" value="${relations[propertyName]}"></g:set>
                                    <g:if test="${relation.isOneToOne() || relation.isManyToOne()}">
                                        <g:set var="sObj" value="${object[propertyName]}"></g:set>
                                        <g:if test="${sObj != null}">
                                            <td>
                                                <rui:link url="mobile/simple/objectDetails.gsp" params="${[name:sObj.name]}">${sObj.className} ${sObj.name}</rui:link>
                                            </td>
                                        </g:if>
                                        <g:else>
                                            <td></td>
                                        </g:else>
                                    </g:if>
                                    <g:else>
                                        <td width="100%">
                                            <ul style="margin-left: 10px;">
                                                <%
                                                    def relatedObjects = object.getRelatedModelPropertyValues(propertyName, ["name", "className"]);
                                                    def sortedRelatedObjects = relatedObjects.sort {"${it.className}${it.name}"};
                                                %>
                                                <g:if test="${sortedRelatedObjects.size() > 10}">
                                                    <div>
                                                        <%
                                                            for (j in 0..9) {
                                                        %>
                                                        <g:set var="rObj" value="${sortedRelatedObjects[j]}"></g:set>
                                                        <li><rui:link url="mobile/simple/objectDetails.gsp" params="${[name:rObj.name]}">${rObj.className} ${rObj.name}</rui:link></li>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                    <rui:link url="mobile/simple/expandedRelations.gsp" params="${[name:name, relationName:propertyName]}">Expand (${sortedRelatedObjects.size() - 10})</rui:link>
                                                </g:if>
                                                <g:else>
                                                    <g:each var="rObj" in="${sortedRelatedObjects}">
                                                        <li><rui:link url="mobile/simple/objectDetails.gsp" params="${[name:rObj.name]}">${rObj.className} ${rObj.name}</rui:link></li>
                                                    </g:each>
                                                </g:else>
                                            </ul>
                                        </td>
                                    </g:else>

                                </g:else>
                            </tr>
                        </g:if>
                    </g:each>
                </tbody>
            </table>
        </div>
    </g:else>
</div>
<div class="footer">
    <g:link controller="auth" action="logout">Logout</g:link>
</div>
</body>
</html>