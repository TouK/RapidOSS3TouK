<%
   import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events;
   import org.codehaus.groovy.grails.commons.GrailsClassUtils
%>
<%=packageName%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create ${className}</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="\${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">${className} List</g:link></span>
</div>
<div class="body">
    <h1>Create ${className}</h1>
    <g:if test="\${flash.message}">
        <div class="message">\${flash.message}</div>
    </g:if>
    <g:hasErrors bean="\${${propertyName}}">
        <div class="errors">
            <g:renderErrors bean="\${${propertyName}}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="\${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="\${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
        <div class="dialog">
            <table>
                <tbody>
                    <%

                        excludedProps = ['version','errors', '__operation_class__', "__dynamic_property_storage__",
                                'id',
                                Events.ONLOAD_EVENT,
                                Events.BEFORE_DELETE_EVENT,
                                Events.BEFORE_INSERT_EVENT,
                                Events.BEFORE_UPDATE_EVENT]
                        def classHierarchy = [];
                        def datasourceMap = [:];
                        def masterDsName;
                        def tempClass = domainClass;
                        while (tempClass && tempClass != Object.class) {
                            classHierarchy.add(tempClass);
                            def realClass = tempClass.metaClass.getTheClass();
                            tempClass = realClass.getSuperclass();
                        }
                        for (i = classHierarchy.size() - 1; i >= 0; i--) {
                            def dClass = classHierarchy[i];
                            def realClass = dClass.metaClass.getTheClass();
                            if (dClass.metaClass.hasProperty(dClass, "datasources"))
                            {
                                def allDs = GrailsClassUtils.getStaticPropertyValue(realClass, "datasources");
                                if (allDs)
                                {
                                    datasourceMap.putAll(allDs);
                                }
                            }
                        }
                        datasourceMap.each {dsName, ds ->
                            if (dsName == "RCMDB")
                            {
                                masterDsName = dsName;
                            }
                        }
                        def masterDatasourceKeyPropertyNames = [];
                        def masterKeyProperties = [];
                        def otherProperties = [];
                        def props = [];
                        datasourceMap[masterDsName].keys.each {key, value ->
                            masterDatasourceKeyPropertyNames.add(key);
                        }
                        domainClass.properties.each {
                            if(it.name != "id")
                            {
                                if (masterDatasourceKeyPropertyNames.contains(it.name)) {
                                    masterKeyProperties.add(it);
                                }
                                else if (!excludedProps.contains(it.name)) {
                                    otherProperties.add(it);
                                }
                            }
                        }
                        masterKeyProperties.sort {it.name};
                        otherProperties.sort {it.name};
                        props.addAll(masterKeyProperties);
                        props.addAll(otherProperties);
                        props.each {p ->
                            if (p.type != List.class) {
                                cp = domainClass.constrainedProperties[p.name]
                                display = (cp ? cp.display : true)
                                if (display) { %>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="${p.name}">${p.name}:</label>
                        </td>
                        <td valign="top" class="value \${hasErrors(bean:${domainClass.propertyName},field:'${p.name}','errors')}">
                            ${renderEditor(p)}
                        </td>
                    </tr>
                    <% }}} %>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
