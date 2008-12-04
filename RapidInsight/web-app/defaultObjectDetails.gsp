<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Dec 2, 2008
  Time: 10:41:44 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils" %>
<%
    def name = params.name;
    def componentId = params.componentId
    def domainObject = RsTopologyObject.get(name: name);
    if (domainObject != null) {
        String className = domainObject.getClass().getName();
        def allProperties = domainObject.getPropertiesList();
        def propertyNames = ["className", "name"];
        allProperties.each{
            def propName = it.name
            if(propName != "className" && propName != "name"){
                propertyNames.add(propName)
            }
        }
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
<div class="yui-navset yui-navset-top ri-object-details" style="margin-top:5px">
    <div style="display:block">
        <div>
            <table width="100%" cellspacing="1" cellpadding="1">
                <tbody>

                    <g:each var="propertyName" status="i" in="${propertyNames}">
                        <%
                            if(propertyName != "id" && propertyName != "rsDatasource")
                            {
                        %>
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${propertyName}</td>
                                <%
                                        if (!relations.containsKey(propertyName)) {

                                %>
                                <td>${domainObject[propertyName]}&nbsp;</td>
                                <%
                                    }
                                    else {
                                        def relation = relations[propertyName];
                                        if (relation.isOneToOne() || relation.isManyToOne()) {
                                            def sObj = domainObject[propertyName]
                                            if (sObj != null) {
                                %>
                                <td>
                                    <a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show('getObjectDetails.gsp?name=' + encodeURIComponent('${sObj.name}'), 'Details of ${sObj.className} ${sObj.name}');">${sObj.className} ${sObj.name}<a>
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
                                                def relatedObjects = domainObject[propertyName];
                                                def sortedRelatedObjects = relatedObjects.sort{"${it.className}${it.name}"};
                                                sortedRelatedObjects.each {
                                        %>
                                        <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['${componentId}'].show('getObjectDetails.gsp?name=' + encodeURIComponent('${it.name}'), 'Details of ${it.className} ${it.name}');">${it.className} ${it.name}<a></li>
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