<%@ page import="com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def name = params.name;

    def allProperties = [];
    def domainObject = RsSmartsObject.get(name: name);
    def excludedProps = ["id", "version", RapidCMDBConstants.ERRORS_PROPERTY_NAME,
            RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED, RapidCMDBConstants.OPERATION_PROPERTY_NAME]
    if (domainObject != null) {
        GrailsDomainClass domainClass = ApplicationHolder.application.getDomainClass(domainObject.getClass().getName());
        allProperties = domainClass.getProperties().findAll {!excludedProps.contains(it.name)};
        %>
        <div class="yui-navset yui-navset-top">
            <div style="display:block">
                <table cellspacing="2" cellpadding="2">
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
        Object ${name} does not exist.
        <%
    }
%>