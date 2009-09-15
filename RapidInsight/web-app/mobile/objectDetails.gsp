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
<div id="objectDetails" title="Details of ${object.name}:Details">

    <g:if test="${!object}">
        <div id="messageArea" class="error">
            Object with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
    <%----------------------------------------------------------------
                        <Event Action Menu>
    ----------------------------------------------------------------%>
        <g:if test="${CONFIG.INVENTORY_ACTIONS.size() > 0}">
            <div id="event${event.id}-menu" style="position: static; ">
                <div id="menu${event.id}-header">
                    <div id="menu${event.id}-link" class="menu-closed"><a href="home.gsp#_${event.id}" onclick="expandEventActionMenu('menu${event.id}-link', 'menu${event.id}-list'); return false">Event Actions</a></div>
                </div>
                <div id="menu${event.id}-list" style="display: none; ">
                    <ul class="items">

                        <g:each var="actionConf" in="${CONFIG.INVENTORY_ACTIONS}">
                            <g:if test="${actionConf.type && actionConf.type == 'group'}">
                                <li>
                                    <div id="menu-${actionGroupIdIndex++}-header">
                                        <div id="menu-${actionGroupIdIndex}-link" class="menu-closed"><a href="home.gsp#_${event.id}" onclick="expandEventActionMenu('menu-${actionGroupIdIndex}-link', 'menu-${actionGroupIdIndex}-list'); return false">${actionConf.title.encodeAsHTML()}</a></div>
                                    </div>
                                    <div id="menu-${actionGroupIdIndex}-list" style="display: none; ">
                                        <ul class="items">
                                            <g:each var="subActionConf" in="${actionConf.actions}">
                                                <g:if test="${!subActionConf.visible || subActionConf.visible(event)}">
                                                    <%
                                                        def subScriptParams = subActionConf.parameters ? subActionConf.parameters(event) : [:]
                                                        subScriptParams["scriptName"] = subActionConf.scriptName;
                                                    %>
                                                    <li><rui:link url="mobile/scriptExecuter.gsp" params="${subScriptParams}" target="_open">${subActionConf.title.encodeAsHTML()}</rui:link></li>
                                                </g:if>

                                            </g:each>
                                        </ul>
                                    </div>
                                </li>
                            </g:if>
                            <g:else>
                                <g:if test="${!actionConf.visible || actionConf.visible(event)}">
                                    <%
                                        def scriptParams = actionConf.parameters ? actionConf.parameters(event) : [:]
                                        scriptParams["scriptName"] = actionConf.scriptName;
                                    %>
                                    <li><rui:link url="mobile/scriptExecuter.gsp" params="${scriptParams}" target="_open">${actionConf.title.encodeAsHTML()}</rui:link></li>
                                </g:if>
                            </g:else>
                        </g:each>
                    </ul>
                </div>
            </div>
        </g:if>
    <%----------------------------------------------------------------
                        </Event Action Menu>
    ----------------------------------------------------------------%>
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
                                                <rui:link url="mobile/objectDetails.gsp" params="${[name:sObj.name]}" target="_open">${sObj.className} ${sObj.name}</rui:link>
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
                                                        <li><rui:link url="mobile/objectDetails.gsp" params="${[name:rObj.name]}" target="_open">${rObj.className} ${rObj.name}</rui:link></li>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                    <div style="display:none" id="${propertyName}_hiddenObjects">
                                                        <%
                                                            for (int j = 10; j < sortedRelatedObjects.size(); j++) {
                                                        %>
                                                        <g:set var="rObj" value="${sortedRelatedObjects[j]}"></g:set>
                                                        <li><rui:link url="mobile/objectDetails.gsp" params="${[name:rObj.name]}" target="_open">${rObj.className} ${rObj.name}</rui:link></li>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                    <div class="ri-objectdetails-expand" id="${propertyName}_expandButton" onclick="window.expandRelations('${propertyName}', ${sortedRelatedObjects.size()});">Expand (${sortedRelatedObjects.size() - 10})</div>
                                                </g:if>
                                                <g:else>
                                                    <g:each var="rObj" in="${sortedRelatedObjects}">
                                                        <li><rui:link url="mobile/objectDetails.gsp" params="${[name:rObj.name]}" target="_open">${rObj.className} ${rObj.name}</rui:link></li>
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