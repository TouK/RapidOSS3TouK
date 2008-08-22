<%@ page import="com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def domainClassName = params.domainClassName;
    def id = params.id;
    def allProperties = [];
    def domainObject = null;
    GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass(domainClassName);
    if (domainClass != null && id != null)
    {
        domainClass.getProperties().each{
            if(it.name != RapidCMDBConstants.ERRORS_PROPERTY_NAME && it.name != RapidCMDBConstants.OPERATION_PROPERTY_NAME)
            {
                allProperties += it;
            }
        }
        domainObject = domainClass?.metaClass.invokeStaticMethod(domainClass.clazz, "get", [[id:id]] as Object[]);
    }
    if (domainObject != null) {
%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li class="selected"><a href="#"><em>Smarts Object</em></a></li>
    </ul>
    <div style="display:block">
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
    </div>
</div>
<%
    }
    else {
%>
No data found
<%
    }
%>


