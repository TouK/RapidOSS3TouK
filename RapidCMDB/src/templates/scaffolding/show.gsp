<%
   import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor as Events;
   import org.codehaus.groovy.grails.commons.GrailsClassUtils
%>
<%=packageName%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ${className}</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="\${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">${className} List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New ${className}</g:link></span>
</div>
<div class="body">
    <h1>Show ${className}</h1>
    <g:if test="\${flash.message}">
        <div class="message">\${flash.message}</div>
    </g:if>
    <g:if test="\${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="\${flash?.errors}">
                    <li>\${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

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
                    while(tempClass && tempClass != Object.class){
                        classHierarchy.add(tempClass);
                        def realClass = tempClass.metaClass.getTheClass();
                        tempClass = realClass.getSuperclass();
                    }
                    for(i=classHierarchy.size() -1; i >= 0 ; i--){
                        def dClass = classHierarchy[i];
                        def realClass = dClass.metaClass.getTheClass();
                        if(dClass.metaClass.hasProperty(dClass, "datasources"))
                        {
                            def allDs = GrailsClassUtils.getStaticPropertyValue (realClass, "datasources");
                            if(allDs)
                            {
                                datasourceMap.putAll(allDs);
                            }
                        }
                    }
                    datasourceMap.each{dsName, ds->
                        if(ds.master)
                        {
                            masterDsName = dsName;
                        }
                    }
                    def masterDatasourceKeyPropertyNames = [];
                    def masterKeyProperties = [];
                    def otherProperties = [];
                    def props = [];
                    datasourceMap[masterDsName].keys.each{key, value ->
                        masterDatasourceKeyPropertyNames.add(key);
                    }
                    domainClass.properties.each {
                        if(it.name == "id"){
                            props.add(it);
                        }
                        else if(masterDatasourceKeyPropertyNames.contains(it.name)){
                            masterKeyProperties.add(it);
                        }
                        else if(!excludedProps.contains(it.name)){
                            otherProperties.add(it);
                        }
                    }
                    masterKeyProperties.sort{it.name};
                    otherProperties.sort{it.name};
                    props.addAll(masterKeyProperties);
                    props.addAll(otherProperties);
                    props.each {p -> %>
                <tr class="prop">
                    <td valign="top" class="name">${p.name}:</td>
                    <% if (p.oneToMany || p.manyToMany) { %>
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="${p.name[0]}" in="\${${propertyName}.${p.name}}">
                                <li><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${p.name[0]}.id}">\${${p.name[0]}}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    <% } else if (p.manyToOne || p.oneToOne) { %>
                    <td valign="top" class="value"><g:link controller="${p.referencedDomainClass?.propertyName}" action="show" id="\${${propertyName}?.${p.name}?.id}">\${${propertyName}?.${p.name}}</g:link></td>
                    <% } else { %>
                    <td valign="top" class="value">\${${propertyName}.${p.name}}</td>
                    <% } %>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="\${${propertyName}?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
