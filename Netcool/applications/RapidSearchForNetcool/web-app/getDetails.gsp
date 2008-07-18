<%@ page import="org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def type = params.type;
    def id = params.id;
    def allProperties = [];
    def domainObject = null;
    if(type != null)
    {
        GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass(type);
        if(domainClass != null && id != null)
        {
            allProperties = domainClass.getProperties();
            domainObject = domainClass?.metaClass.invokeStaticMethod(domainClass.clazz, "get", [[id:id]] as Object[]);
        }
    }
%>
<g:if test="${domainObject != null}">
    <table>
        <tbody>

        <g:each var="property" in="${allProperties}">
            <tr>
                <td>${property.name}</td>
                <td>${domainObject[property.name]}</td>
            </tr>
        </g:each>

        </tbody>
    </table>
</g:if>
<g:else>
    No data found
</g:else>
