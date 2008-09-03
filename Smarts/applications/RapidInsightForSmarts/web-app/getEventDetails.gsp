<%@ page import="com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def eventName = params.eventName;
    def instanceName = params.instanceName;
    def className = params.className;
    def allProperties = ["className", "instanceName", "eventName", "sourceDomainName", "acknowledged", "owner","elementClassName", "elementName", 
                        "severity", "eventText", "isRoot", "isProblem", "certainty", "eventType", "category", "impact", "inMaintenance"];
    def domainObject = RsEvent.get(eventName:eventName, instanceName:instanceName, className:className);
    if(domainObject != null){
         %>
           <div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li class="selected"><a href="#"><em>Properties</em></a></li>
        <li><a href="#" onclick="YAHOO.rapidjs.Components['eventDetails'].show('getAuditLog.gsp?id=${domainObject?.id}');"><em>Audit Log</em></a></li>
    </ul>
    <div style="display:block">
        <table>
            <tbody>

                <g:each var="propertyName" in="${allProperties}">
                    <tr>
                        <td>${propertyName}</td>
                        <%
                            if(propertyName == "instanceName"){
                            %>
                               <td><a onclick="YAHOO.rapidjs.Components['objectDetails'].show('getDeviceDetails.gsp?name=${domainObject[propertyName]}');">${domainObject[propertyName]}</a></td>
                            <%
                            }
                            else{
                            %>
                               <td>${domainObject[propertyName]}</td>
                            <%
                            }
                        %>

                    </tr>
                </g:each>

            </tbody>
        </table>
    </div>
</div>
        <%   
    }
    else{
        %>
            Event ${className} ${instanceName} ${eventName} does not exist.
        <%
    }

%>
