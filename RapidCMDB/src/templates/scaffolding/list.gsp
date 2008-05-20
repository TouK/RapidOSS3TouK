<%
   import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events;
   import org.codehaus.groovy.grails.commons.GrailsClassUtils
%>
<%=packageName%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${className} List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="\${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="create" action="create">New ${className}</g:link></span>
</div>
<div class="body">
    <h1>${className} List</h1>
    <g:if test="\${flash.message}">
        <div class="message">\${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
                <tr>
                    <%
                        excludedProps = ['version',
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
                            if (ds.master)
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
                            if (it.name == "id") {
                                props.add(it);
                            }
                            else if (masterDatasourceKeyPropertyNames.contains(it.name)) {
                                masterKeyProperties.add(it);
                            }
                            else if (!excludedProps.contains(it.name)) {
                                otherProperties.add(it);
                            }
                        }
                        masterKeyProperties.sort {it.name};
                        props.addAll(masterKeyProperties);
                        def propertiesWhichCanBeListed = otherProperties.findAll{!it.oneToMany && !it.manyToMany};
                        if (masterKeyProperties.size() + propertiesWhichCanBeListed.size() < 5) {
                            otherProperties.sort {it.name};
                            props.addAll(otherProperties);
                        }
                        props.eachWithIndex {p, i ->
                                if (p.isAssociation()) { %>
                    <th>${p.name}</th>
                    <% } else { %>
                    <g:sortableColumn property="${p.name}" title="${p.name}"/>
                    <% }} %>
                </tr>
            </thead>
            <tbody>
                <g:each in="\${${propertyName}List}" status="i" var="${propertyName}">
                    <tr class="\${(i % 2) == 0 ? 'odd' : 'even'}">
                        <% props.eachWithIndex {p, i ->
                            if (i == 0) { %>
                        <td><g:link action="show" id="\${${propertyName}.id}">\${${propertyName}.${p.name}?.encodeAsHTML()}</g:link></td>
                        <% } else {
                            if (p.oneToOne || p.manyToOne) {
                        %>
                        <td><g:link action="show" controller="${p.referencedDomainClass.propertyName}" id="\${${propertyName}.${p.name}?.id}">\${${propertyName}.${p.name}?.encodeAsHTML()}</g:link></td>
                        <%
                            }
                            else {
                        %>
                        <td>\${${propertyName}.${p.name}?.encodeAsHTML()}</td>
                        <% }}} %>
                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="\${${className}.countHits('id:*')}"/>
    </div>
</div>
</body>
</html>
