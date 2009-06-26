<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Dec 2, 2008
  Time: 10:41:44 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.domain.util.DomainClassUtils" %>
<%
    def dateProperties = ["lastChangedAt", "consideredDownAt"];
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss")
    def name = params.name;
    def componentId = params.componentId
    def domainObject = RsTopologyObject.get(name: name);
%>
<g:if test="${domainObject != null}">
    <%
        String className = domainObject.getClass().getName();
        def allProperties = domainObject.getPropertiesList();
        def propertyNames = ["className", "name"];
        allProperties.each {
            def propName = it.name
            if (propName != "className" && propName != "name") {
                propertyNames.add(propName)
            }
        }
        def relations = DomainClassUtils.getRelations(className);
    %>
    <style>
    .yui-navset a{
    display:block;
    color:#006DBA;
    text-decoration:underline;
    cursor:pointer;
    }
    </style>
    <script type="text/javascript">
         window.expandRelations = function(propertyName, relCount){
            var divEl = document.getElementById(propertyName + '_hiddenObjects');
            var buttonEl = document.getElementById(propertyName + '_expandButton');
            if(divEl.style.display == 'none'){
                divEl.style.display = '';
                buttonEl.innerHTML = 'Collapse'
            }
            else{
                divEl.style.display = 'none';
                buttonEl.innerHTML = 'Expand (' + (relCount - 10) + ')';
            }
         }

    </script>
    <div class="yui-navset yui-navset-top ri-object-details" style="margin-top:5px">
        <div style="display:block">
            <div>
                <table width="100%" cellspacing="1" cellpadding="1">
                    <tbody>
                        <g:each var="propertyName" status="i" in="${propertyNames}">
                            <g:if test="${propertyName != 'id' && propertyName != 'rsDatasource'}">
                                <g:set var="propertyValue" value=""/>
                                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                    <td width="0%" style="font-weight:bold">${propertyName}</td>
                                    <g:if test="${!relations.containsKey(propertyName)}">
                                        <%
                                            propertyValue = domainObject[propertyName];
                                            if (dateProperties.contains(propertyName))
                                            {
                                                propertyValue = format.format(new Timestamp(propertyValue))
                                            }
                                            def fieldHasError = domainObject.hasErrors(propertyName)
                                        %>
                                        <td ${fieldHasError ? 'class="ri-field-error"' : ""}>
                                            ${fieldHasError ? "InAccessible" : propertyValue}&nbsp;
                                        </td>
                                    </g:if>
                                    <g:else>
                                        <g:set var="relation" value="${relations[propertyName]}"></g:set>
                                        <g:if test="${relation.isOneToOne() || relation.isManyToOne()}">
                                            <g:set var="sObj" value="${domainObject[propertyName]}"></g:set>
                                            <g:if test="${sObj != null}">
                                                <td>
                                                    <a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getObjectDetails.gsp', {name:'${sObj.name}'}), 'Details of ${sObj.className} ${sObj.name}');">${sObj.className} ${sObj.name}</a>
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
                                                        def relatedObjects = domainObject[propertyName];
                                                        def sortedRelatedObjects = relatedObjects.sort {"${it.className}${it.name}"};
                                                    %>
                                                    <g:if test="${sortedRelatedObjects.size() > 10}">
                                                        <div>
                                                            <%
                                                                for (j in 0..9) {
                                                            %>
                                                            <g:set var="rObj" value="${sortedRelatedObjects[j]}"></g:set>
                                                            <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getObjectDetails.gsp', {name:'${rObj.name}'}), 'Details of ${rObj.className} ${rObj.name}');">${rObj.className} ${rObj.name}</a></li>
                                                            <%
                                                                }
                                                            %>
                                                        </div>
                                                        <div style="display:none" id="${propertyName}_hiddenObjects">
                                                            <%
                                                                for (int j=10; j< sortedRelatedObjects.size(); j++) {
                                                            %>
                                                            <g:set var="rObj" value="${sortedRelatedObjects[j]}"></g:set>
                                                            <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getObjectDetails.gsp', {name:'${rObj.name}'}), 'Details of ${rObj.className} ${rObj.name}');">${rObj.className} ${rObj.name}</a></li>
                                                            <%
                                                                }
                                                            %>
                                                        </div>
                                                        <div class="ri-objectdetails-expand" id="${propertyName}_expandButton" onclick="window.expandRelations('${propertyName}', ${sortedRelatedObjects.size()});">Expand (${sortedRelatedObjects.size() - 10})</div>
                                                    </g:if>
                                                    <g:else>
                                                        <g:each var="rObj" in="${sortedRelatedObjects}">
                                                            <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('getObjectDetails.gsp', {name:'${rObj.name}'}), 'Details of ${rObj.className} ${rObj.name}');">${rObj.className} ${rObj.name}</a></li>
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
        </div>
    </div>
</g:if>
<g:else>
    Object ${name} does not exist.
</g:else>
