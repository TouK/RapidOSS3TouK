<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils" %>
<%
    def name = params.name;
    def componentId = params.componentId
    def domainObject = RsTopologyObject.get(name: name);
    if (domainObject != null) {
        String className = domainObject.getClass().getName();
        def allProperties = domainObject.getPropertiesList();
        def relations = DomainClassUtils.getRelations(className);
%>
<style>
.yui-navset a{
display:block;
color:#006DBA;
text-decoration:underline;
cursor:pointer;
}
</style>
<div class="yui-navset yui-navset-top smarts-object-details" style="margin-top:5px">
    <div style="display:block">
        <div>
            <table width="100%" cellspacing="1" cellpadding="1">
                <tbody>

                    <g:each var="property" status="i" in="${allProperties}">
                        <%
                            if(property.name != "id" && property.name != "rsDatasource")
                            {
                        %>
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${property.name}</td>
                                <%
                                        if (!relations.containsKey(property.name)) {

                                %>
                                <td>${domainObject[property.name]}&nbsp;</td>
                                <%
                                    }
                                    else {
                                        def relation = relations[property.name];
                                        if (relation.isOneToOne() || relation.isManyToOne()) {
                                            def sObj = domainObject[property.name]
                                            if (sObj != null) {
                                %>
                                <td>
                                    <a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show('getObjectDetails.gsp?name=' + encodeURIComponent('${sObj.name}'), 'Details of ${sObj.creationClassName} ${sObj.name}');">${sObj.creationClassName} ${sObj.name}<a>
                                </td>
                                <%
                                    }
                                    else {
                                %>
                                <td></td>
                                <%
                                        }
                                    }
                                    else {
                                %>
                                <td width="100%">
                                    <ul style="margin-left: 10px;">
                                        <%
                                                def relatedObjects = domainObject[property.name];
                                                def sortedRelatedObjects = relatedObjects.sort{"${it.creationClassName}${it.name}"};
                                                sortedRelatedObjects.each {
                                        %>
                                        <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show('getObjectDetails.gsp?name=' + encodeURIComponent('${it.name}'), 'Details of ${it.creationClassName} ${it.name}');">${it.creationClassName} ${it.name}<a></li>
                                        <%
                                                }
                                        %>
                                    </ul>
                                </td>
                                <%
                                            }
                                        }
                                %>
                            </tr>
                        <%
                            }
                        %>
                    </g:each>

                </tbody>
            </table>
        </div>
    </div>
</div>

<%
    }
    else {
%>
Object ${name} does not exist.
<%
    }
%>