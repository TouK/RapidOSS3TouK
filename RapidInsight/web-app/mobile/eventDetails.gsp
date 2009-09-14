<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def name = params.name
    def event = CONFIG.EVENT_CLASS.get(name: name)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
    def actionGroupIdIndex = 0;
%>

<div id="eventDetails" title="Details of ${name}:Details">

    <g:if test="${!event}">
        <div id="messageArea" class="error">
            Event with name: ${name} does not exist
        </div>
    </g:if>
    <g:else>
    <%----------------------------------------------------------------
                        <Event Action Menu>
    ----------------------------------------------------------------%>
        <div id="event${event.id}-menu" style="position: static; ">
            <div id="menu${event.id}-header">
                <div id="menu${event.id}-link" class="menu-closed"><a href="home.gsp#_${event.id}" onclick="expandEventActionMenu('menu${event.id}-link', 'menu${event.id}-list'); return false">Event Actions</a></div>
            </div>
            <div id="menu${event.id}-list" style="display: none; ">
                <ul class="items">

                    <g:each var="actionConf" in="${CONFIG.EVENT_ACTIONS}">
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
    <%----------------------------------------------------------------
                        </Event Action Menu>
    ----------------------------------------------------------------%>
        <g:set var="props" value="${event.asMap()}"></g:set>
        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <g:each var="propEntry" in="${props}" status="i">
                <g:set var="propertyName" value="${propEntry.key}"></g:set>
                <g:set var="propertyValue" value="${propEntry.value}"></g:set>
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    <td><b>${propertyName}</b></td>
                    <g:if test="${CONFIG.EVENT_DATE_PROPERTIES.contains(propertyName)}">
                        <%
                            propertyValue = (propertyValue == 0) ? 'never' : format.format(new Date(propertyValue))
                        %>
                    </g:if>
                    <td>${propertyValue}</td>
                </tr>
            </g:each>
        </table>
    </g:else>
</div>