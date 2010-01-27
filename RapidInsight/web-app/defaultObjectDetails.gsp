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
        def excludedPropNames=["id":"","rsDatasource":"","className":"","name":""];
        allProperties.each {
            def propName = it.name
            if (!excludedPropNames.containsKey(propName)) {
                propertyNames.add(propName)
            }
        }
        def relations = DomainClassUtils.getRelations(className);
    %>
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
         window.showRO = function(objName, className){
            YAHOO.rapidjs.Components['${componentId}'].show(createURL('getObjectDetails.gsp', {name:objName}), 'Details of ' + className + ' ' + objName);
         }

    </script>
    <div class="yui-navset yui-navset-top ri-object-details" style="margin-top:5px">
        <div style="display:block">
            <div>
                <table width="100%" cellspacing="1" cellpadding="1">
                    <tbody>
                        <g:each var="propertyName" status="i" in="${propertyNames}">
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${propertyName}</td>
                                <g:if test="${!relations.containsKey(propertyName)}">
                                    <%
                                        def propertyValue = domainObject[propertyName];
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
                                    <td width="100%">
                                        <ul style="margin-left: 10px;">
                                            <%
                                                def relatedObjects = domainObject.getRelatedModelPropertyValues(propertyName, ["className", "name"]);
                                                def sortedRelatedObjects = relatedObjects.sort {"${it.className}${it.name}"};
                                            %>
                                            <g:if test="${sortedRelatedObjects.size() > 10}">
                                                <div>
                                                    <g:each var="rObj" in="${sortedRelatedObjects[0..9]}">
                                                        <li><a onclick="window.showRO('${rObj.name}', '${rObj.className}')">${rObj.className} ${rObj.name}</a></li>
                                                    </g:each>
                                                </div>
                                                <div style="display:none" id="${propertyName}_hiddenObjects">
                                                    <g:each var="rObj" in="${sortedRelatedObjects[10..-1]}">
                                                        <li><a onclick="window.showRO('${rObj.name}', '${rObj.className}')">${rObj.className} ${rObj.name}</a></li>
                                                    </g:each>
                                                </div>
                                                <div class="ri-objectdetails-expand" id="${propertyName}_expandButton" onclick="window.expandRelations('${propertyName}', ${sortedRelatedObjects.size()});">Expand (${sortedRelatedObjects.size() - 10})</div>
                                            </g:if>
                                            <g:else>
                                                <g:each var="rObj" in="${sortedRelatedObjects}">
                                                    <li><a onclick="window.showRO('${rObj.name}', '${rObj.className}')">${rObj.className} ${rObj.name}</a></li>
                                                </g:each>
                                            </g:else>
                                        </ul>
                                    </td>
                                </g:else>
                            </tr>
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
