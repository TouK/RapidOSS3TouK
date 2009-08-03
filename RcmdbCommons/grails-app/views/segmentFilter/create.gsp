<%@ page import="auth.Group; auth.SegmentFilter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Create SegmentFilter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'group/show/' + params["groupId"])}">${Group.get(id:params["groupId"])?.name}</a></span>
</div>
<div class="body">
    <h1>Create SegmentFilter</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${segmentFilter}">
        <div class="errors">
            <g:renderErrors bean="${segmentFilter}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">Class Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: segmentFilter, field: 'className', 'errors')}">
                            <g:select class="inputtextfield" id="className" name="className" from="${grailsApplication.domainClasses.clazz.findAll{it.name.indexOf('.') < 0}.sort{it.name}.collect{it.name.encodeAsHTML()}}"
                                    value="${fieldValue(bean:segmentFilter,field:'className')}"></g:select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="filter">Filter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: segmentFilter, field: 'filter', 'errors')}">
                            <input type="text" class="inputtextfield" id="filter" name="filter" value="${fieldValue(bean: segmentFilter, field: 'filter')}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <input type="hidden" name="group.id" value="${fieldValue(bean: segmentFilter, field: 'groupId')}"/>
            <input type="hidden" name="groupId" value="${fieldValue(bean: segmentFilter, field: 'groupId')}"/>
            <input type="hidden" name="targetURI" value="${params['targetURI']}"/>
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
