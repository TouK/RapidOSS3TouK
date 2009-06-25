<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jan 7, 2009
  Time: 2:45:31 PM
  To change this template use File | Settings | File Templates.
--%>
<style>
.yui-navset a{
display:block;
color:#006DBA;
text-decoration:underline;
cursor:pointer;
}
</style>
<g:set var="id" value="${params.id}"></g:set>
<g:set var="componentId" value="${params.componentId}"></g:set>
<g:set var="domain" value="${params.domain}"></g:set>
<%
    def domainClass = grailsApplication.getDomainClass(domain);
%>
<g:if test="${domainClass}">
    <%
        def domainObject = domainClass.clazz."get"(id: id);
    %>
    <g:if test="${domainObject}">
        <%
            def properties = domainObject.getPropertiesList().findAll{!it.isKey};
            def keySet = domainObject.keySet();
        %>
        <div class="yui-navset yui-navset-top ri-object-details" style="margin-top:5px">
            <div style="display:block">
                <div>
                    <table width="100%" cellspacing="1" cellpadding="1">
                        <tbody>
                            <tr class="odd">
                                <td width="0%" style="font-weight:bold">id</td>
                                <td>${domainObject.id}&nbsp;</td>
                            </tr>
                            <g:set var="status" value="${1}"></g:set>
                            <g:each var="key" in="${keySet}">
                                <g:if test="${key.name != 'id'}">
                                    <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                        <td width="0%" style="font-weight:bold">${key.name}</td>
                                        <td>${domainObject[key.name]}&nbsp;</td>
                                    </tr>
                                    <%
                                        status++;
                                    %>
                                </g:if>
                            </g:each>
                            <g:each var="p" in="${properties}">
                                <g:if test="${p.name != 'id'}">
                                    <tr class="${(status % 2) == 0 ? 'odd' : 'even'}">
                                        <td width="0%" style="font-weight:bold">${p.name}</td>
                                        <g:if test="${!p.isRelation}">
                                            <%         
                                                def propValue = domainObject[p.name];
                                                def fieldHasError = domainObject.hasErrors(p.name)
                                            %>
                                            <td ${fieldHasError?'class="ri-field-error"':""}>
                                                ${fieldHasError?"InAccessible":(propValue)}&nbsp;
                                            </td>
                                        </g:if>
                                        <g:elseif test="${p.isOneToMany() || p.isManyToMany()}">
                                            <td width="100%">
                                                <ul style="margin-left: 10px;">
                                                    <g:each var="relatedObject" in="${domainObject[p.name]}">
                                                       <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;"
                                                    onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('browser/browserObjectDetails.gsp', {id:'${relatedObject.id}', domain:'${relatedObject.class.name}'}), 'Details of ${relatedObject.class.name} ${relatedObject.id}');">${relatedObject}<a></li>
                                                    </g:each>
                                                </ul>
                                            </td>
                                        </g:elseif>
                                        <g:else>
                                            <td>
                                                <g:set var="relatedObject" value="${domainObject[p.name]}"></g:set>
                                                <g:if test="${relatedObject}">
                                                    <a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;"
                                                    onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('browser/browserObjectDetails.gsp', {id:'${relatedObject.id}', domain:'${relatedObject.class.name}'}), 'Details of ${relatedObject.class.name} ${relatedObject.id}');">${relatedObject}<a>
                                                </g:if>
                                            </td>
                                        </g:else>
                                    </tr>
                                    <%
                                        status++;
                                    %>
                                </g:if>
                            </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </g:if>
    <g:else>
        ${domainClass} with id ${id} does not exist
    </g:else>
</g:if>
<g:else>
    Domain Class ${domainClass} does not exist
</g:else>