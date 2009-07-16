<%@ page import="auth.Group" %><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show Group</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Group List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Group</g:link></span>
</div>
<div class="body">
    <h1>Show Group</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name" id="nameLabel">Name:</td>

                    <td valign="top" class="value" id="name">${group.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="roleLabel">Role:</td>

                    <td valign="top" class="value" id="role">${group?.role}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="segmentFilterTypeLabel">Segment Filter Type:</td>

                    <td valign="top" class="value" id="segmentFilterType">${group.segmentFilterType}</td>

                </tr>
                <g:if test="${group.segmentFilterType == Group.GLOBAL_FILTER}">
                    <tr class="prop">
                        <td valign="top" class="name" id="segmentFilterLabel">Segment Filter:</td>

                        <td valign="top" class="value" id="segmentFilter">${group.segmentFilter}</td>

                    </tr>
                </g:if>
                <tr class="prop">
                    <td valign="top" class="name">Users:</td>

                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${group.users}">
                                <li><g:link controller="rsUser" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>

                </tr>

            </tbody>
        </table>
    </div>
    <%
        def filterDisplay = group.segmentFilterType == Group.GLOBAL_FILTER ? 'none' : ''
    %>
    <div style="margin-top:20px;display:${filterDisplay}">
        <%
            def currentUrl = request.request.uri.toString().replace("/RapidSuite", "");
            def startIndex = currentUrl.indexOf("group")
            if (script >= 0)
            {
                currentUrl = currentUrl.substring(startIndex)
            }

        %>
        <table>
            <tr>
                <td>
                    <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">SegmentFilter List</span>
                    <span class="menuButton"><g:link class="create" controller="segmentFilter" params="['groupId':group?.id, 'targetURI':currentUrl]" action="create">New SegmentFilter</g:link></span>
                    <div class="list">
                        <table><br>
                            <thead>
                                <tr>
                                    <th>Class Name</th>
                                    <th>Filter</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${group.filters}" status="i" var="segmentFilter">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <td><g:link action="show" controller="segmentFilter" id="${segmentFilter.id}" params="${[groupId:group.id]}">${segmentFilter.className?.encodeAsHTML()}</g:link></td>
                                        <td>${segmentFilter.filter.encodeAsHTML()}</td>
                                        <td><g:link action="edit" controller="segmentFilter" id="${segmentFilter.id}" class="edit" params="${[groupId:group.id, targetURI:currentUrl]}">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${group?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>


