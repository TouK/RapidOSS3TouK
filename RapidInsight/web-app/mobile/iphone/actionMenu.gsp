<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Apr 29, 2010
  Time: 3:05:25 PM
--%>
<g:if test="${actions.size() > 0}">
    <div id="event${domainObject.id}-menu" style="position: static; ">
        <div id="menu${domainObject.id}-header">
            <div id="menu${domainObject.id}-link" class="menu-closed"><a href="home.gsp#_${domainObject.id}" onclick="expandEventActionMenu('menu${domainObject.id}-link', 'menu${domainObject.id}-list'); return false">${title}</a></div>
        </div>
        <div id="menu${domainObject.id}-list" style="display: none; ">
            <ul class="items">

                <g:each var="actionConf" in="${actions}">
                    <g:if test="${actionConf.type && actionConf.type == 'group'}">
                        <li>
                            <div id="menu-${actionGroupIdIndex++}-header">
                                <div id="menu-${actionGroupIdIndex}-link" class="menu-closed"><a href="home.gsp#_${domainObject.id}" onclick="expandEventActionMenu('menu-${actionGroupIdIndex}-link', 'menu-${actionGroupIdIndex}-list'); return false">${actionConf.title.encodeAsHTML()}</a></div>
                            </div>
                            <div id="menu-${actionGroupIdIndex}-list" style="display: none; ">
                                <ul class="items">
                                    <g:each var="subActionConf" in="${actionConf.actions}">
                                        <g:if test="${!subActionConf.visible || subActionConf.visible(domainObject)}">
                                            <%
                                                def subUrlParams = subActionConf.parameters ? subActionConf.parameters(domainObject) : [:]
                                            %>
                                            <g:if test="${subActionConf.type == 'gsp'}">
                                                <li><rui:link url="${subActionConf.url}" params="${subUrlParams}">${subActionConf.title.encodeAsHTML()}</rui:link></li>
                                            </g:if>
                                            <g:else>
                                                <%
                                                    subUrlParams["scriptName"] = subActionConf.scriptName;
                                                    subUrlParams["redirectUrl"] = rui.createLink(url: redirectUrl, params: [name: params.name]);
                                                %>
                                                <li><rui:link url="mobile/scriptExecuter.gsp" params="${subUrlParams}">${subActionConf.title.encodeAsHTML()}</rui:link></li>
                                            </g:else>
                                        </g:if>
                                    </g:each>
                                </ul>
                            </div>
                        </li>
                    </g:if>
                    <g:else>
                        <g:if test="${!actionConf.visible || actionConf.visible(domainObject)}">
                            <%
                                def urlParams = actionConf.parameters ? actionConf.parameters(domainObject) : [:]
                            %>
                            <g:if test="${actionConf.type == 'gsp'}">
                                <li><rui:link url="${actionConf.url}" params="${urlParams}">${actionConf.title.encodeAsHTML()}</rui:link></li>
                            </g:if>
                            <g:else>
                                <%
                                    urlParams["scriptName"] = actionConf.scriptName;
                                    urlParams["redirectUrl"] = rui.createLink(url: redirectUrl, params: [name: params.name]);
                                %>
                                <li><rui:link url="mobile/scriptExecuter.gsp" params="${urlParams}">${actionConf.title.encodeAsHTML()}</rui:link></li>
                            </g:else>
                        </g:if>
                    </g:else>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>