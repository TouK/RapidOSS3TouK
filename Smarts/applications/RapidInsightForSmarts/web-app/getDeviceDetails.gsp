<%@ page import="com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def name = params.elementName;

    def allProperties = [];
    def domainObject = null;
    GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass("RsSmartsObject");
    if (domainClass != null)
    {
        domainClass.getProperties().each{
            if(it.name != RapidCMDBConstants.ERRORS_PROPERTY_NAME && it.name != RapidCMDBConstants.OPERATION_PROPERTY_NAME && it.name !="__is_federated_properties_loaded__")
            {
                allProperties += it;
            }
        }

        domainObject =RsSmartsObject.get(name:name);
    }
    if (domainObject != null) {
%>
<div class="yui-navset yui-navset-top">
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