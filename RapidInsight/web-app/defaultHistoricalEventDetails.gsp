<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
    def dateProperties = [];
    def componentId = params.componentId
    def notificationId = params.id;
    def domainObject = RsHistoricalEvent.get(id: notificationId);

    if (domainObject != null) {
        def excludedProps = ["createdAt", "changedAt", "clearedAt"]
        def allProperties = domainObject.getPropertiesList();
        def filteredProps = allProperties.findAll{!excludedProps.contains(it.name)}
        def propertiesLinkedToObject=["elementName":"elementId"];
%>
<script type="text/javascript">
window.showTopologyObject = function (url, title){
var objectDialog = YAHOO.rapidjs.Components['objectDetailsmenuHtml'];
if(objectDialog.popupWindow){
objectDialog.popupWindow.show();
}
objectDialog.show(url, title);
}
</script>

<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getHistoricalEventDetails.gsp', {id:'${domainObject.id}'}));">
                <em>Properties</em>
            </a>
        </li>
        <li>
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getEventJournals.gsp', {id:'${domainObject.id}', isHistorical:true}));">
                <em>Journal</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <%

                def createdAt = domainObject.createdAt == 0 ? "never" : format.format(new Timestamp(domainObject.createdAt));
                def changedAt = domainObject.changedAt == 0 ? "never" : format.format(new Timestamp(domainObject.changedAt));
                def clearedAt = domainObject.clearedAt == 0 ? "never" : format.format(new Timestamp(domainObject.clearedAt));
                def severityClass;
                def severity = domainObject.severity;
                def severityClassMap=[:];
                severityClassMap["5"]="event-details-severity-critical";
                severityClassMap["4"]="event-details-severity-major";
                severityClassMap["3"]="event-details-severity-minor";
                severityClassMap["2"]="event-details-severity-warning";
                severityClassMap["1"]="event-details-severity-unknown";
                severityClassMap["0"]="event-details-severity-normal";

                severityClass=severityClassMap[String.valueOf(severity)];
                if(!severityClass)
                {
                    severityClass="event-details-severity-invalid";
                }

        %>
        <table>
            <tr>
                <td width="100%">
                    <div class="ri-object-details" style="width:100%">
                        <table cellspacing="2" cellpadding="2" width="100%">
                            <tbody>
                                <g:each var="property" status="i" in="${filteredProps}">
                                    <g:set var="propertyName" value="${property.name}" />
                                    <g:set var="propertyValue" value=""/>
                                    <g:if test="${dateProperties.contains(propertyName)}">
                                        <%
                                            propertyValue = format.format(new Timestamp(domainObject[propertyName]))
                                        %>
                                    </g:if>
                                    <g:else>
                                        <%
                                            propertyValue = domainObject[propertyName]
                                        %>
                                    </g:else>
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td style="font-weight:bold">${propertyName}</td>
                                        <td>
                                            <g:if test="${propertiesLinkedToObject[propertyName]!=null}">
                                               <g:set var="targetObject" value="${RsTopologyObject.get(id:domainObject[propertiesLinkedToObject[propertyName]])}" />
                                               <g:if test="${targetObject!=null}">
                                                    <a style="cursor:pointer;text-decoration:underline;color:#006DBA" onclick="showTopologyObject(createURL('getObjectDetails.gsp', {name:'${targetObject.name}'}),' Details of ${targetObject.className} ${targetObject.name} ') ">${propertyValue}</a>
                                               </g:if>
                                                <g:else>
                                                    ${propertyValue}
                                                </g:else>
                                            </g:if>
                                            <g:else>
                                                ${propertyValue}
                                            </g:else>
                                        </td>
                                    </tr>
                                </g:each>

                            </tbody>
                        </table>

                    </div>
                </td>
                <td width="0%" style="vertical-align:top;">
                    <div style="padding:0px 10px;">
                        <div style="background-color:#EDF5FF;padding:3px;border:#2647A0 1px solid">
                            <div class="event-details-severity ${severityClass}"></div>
                            <div style="padding:3px 20px 20px 20px;">
                                <div style="width:190px;">
                                    <b>Created At:</b> ${createdAt}<br>
                                    <b>Changed At:</b> ${changedAt}<br>
                                    <b>Cleared At:</b> ${clearedAt}<br>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<%
    }
    else {
%>
Event with id ${notificationId} does not exist.
<%
    }
%>
