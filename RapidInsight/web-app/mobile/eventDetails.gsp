<%@ page import="search.SearchQuery; auth.RsUser; java.text.SimpleDateFormat" %>
<%
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    PROPERTIES = ["name", "owner", "acknowledged", "inMaintenance", "severity", "source", "createdAt", "changedAt", "clearedAt",
            "willExpireAt", "rsDatasource", "state", "elementName", "elementDisplayName"];

    DATE_PROPERTIES = ["createdAt", "changedAt", "clearedAt", "willExpireAt"]

    ACTIONS = [
        [
                scriptName:"acknowledge",
                title:"Acknowledge",
                visible:{event ->
                    return event.acknowledged == false;
                },
                parameters:{event ->
                     return [name:event.name, acknowledged:"true"]
                }
        ],
        [
                scriptName:"acknowledge",
                title:"Unacknowledge",
                visible:{event ->
                    return event.acknowledged == true;
                },
                parameters:{event ->
                     return [name:event.name, acknowledged:"false"]
                }
        ],
        [
                scriptName:"setOwnership",
                title:"Take Ownership",
                parameters:{event ->
                     return [name:event.name, act:"true"]
                }
        ],
        [
                scriptName:"setOwnership",
                title:"Release Ownership",
                parameters:{event ->
                     return [name:event.name, act:"false"]
                }
        ]

    ];

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    def name = params.name
    def event = RsEvent.get(name: name)
    def format = new SimpleDateFormat("d MMM HH:mm:ss");
%>

<div id="eventdetails" title="Details of ${name}:Details">

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
                    <g:each var="actionConf" in="${ACTIONS}">
                        <g:if test="${!actionConf.visible || actionConf.visible(event)}">
                            <%
                                def scriptParams = actionConf.parameters ? actionConf.parameters(event) : [:]
                                scriptParams["scriptName"] = actionConf.scriptName;
                            %>
                             <li><rui:link url="mobile/scriptExecuter.gsp" params="${scriptParams}" target="_open">${actionConf.title.encodeAsHTML()}</rui:link></li>
                        </g:if>
                    </g:each>
                </ul>
            </div>
        </div>
    <%----------------------------------------------------------------
                        </Event Action Menu>
    ----------------------------------------------------------------%>

        <table class="itable" width="100%" border="0" cellspacing="0" cellpadding="3">
            <g:each var="prop" in="${PROPERTIES}" status="i">
                <g:set var="propertyValue" value="${event[prop]}"></g:set>
                <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                    <td><b>${prop}</b></td>
                    <g:if test="${DATE_PROPERTIES.contains(prop)}">
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