<%
    CONFIG = [:]
%>
<rui:include template="mobile/config.gsp" model="${['CONFIG':CONFIG]}"></rui:include>
<%
    def name = params.name
    def object = RsTopologyObject.get(name: name)    
%>
<div id="objectDetails">

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
                                                        subScriptParams["redirectUrl"] = rui.createLink(url:"mobile/iphone/objectDetails.gsp", params:[name:params.name]);
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
                                        scriptParams["redirectUrl"] = rui.createLink(url:"mobile/iphone/objectDetails.gsp", params:[name:params.name]);
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
           def objectDetailsProps=[:];
           objectDetailsProps.putAll(binding.variables);
           objectDetailsProps.object=object;
        %>
        <rui:include template="mobile/contents/objectDetails.gsp" model="${objectDetailsProps}"></rui:include>
    </g:else>
</div>