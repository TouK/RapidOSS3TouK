<%@ page import="auth.Group; auth.SegmentFilter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show SegmentFilter</title>
</head>
<body>
<div class="nav">
   <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'group/show/' + segmentFilter.groupId)}">${Group.get(id:segmentFilter.groupId)?.name}</a></span>
</div>
<div class="body">
    <h1>Show SegmentFilter</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Class Name:</td>

                    <td valign="top" class="value">${segmentFilter.className}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Filter:</td>

                    <td valign="top" class="value">${segmentFilter.filter}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${segmentFilter?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
