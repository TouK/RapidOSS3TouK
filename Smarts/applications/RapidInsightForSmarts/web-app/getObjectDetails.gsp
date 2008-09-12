<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils" %>
<%
    def name = params.name;

    def domainObject = RsSmartsObject.get(name: name);
    if (domainObject != null) {
        String className = domainObject.getClass().getName();
        def allProperties = DomainClassUtils.getFilteredProperties(className, ["id", "rsDatasource"], false)
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
<div class="yui-navset yui-navset-top smarts-object-details">
    <div style="display:block">
        <table><tbody><tr>
            <td>
                <div>
                    <table width="100%" cellspacing="1" cellpadding="1">
                        <tbody>

                            <g:each var="property" status="i" in="${allProperties}">
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
                                        <a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${sObj.name}', 'Details of ${sObj.creationClassName} ${sObj.name}');">${sObj.creationClassName} ${sObj.name}<a>
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
                                    <td>
                                        <ul style="margin-left: 10px;">
                                            <%
                                                    domainObject[property.name].each {
                                            %>
                                            <li><a style="color:#006DBA;cursor:pointer;display:block;text-decoration:underline;" onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${it.name}', 'Details of ${it.creationClassName} ${it.name}');">${it.creationClassName} ${it.name}<a></li>
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
                            </g:each>

                        </tbody>
                    </table>
                </div>
            </td>
            <td width="0%">
                <div style="float:right;background:#DBEAFF;width:200px;padding:10px 0px 0px 10px">
                    <h3 style="color:#14264F">Reports</h3>
                    <ul style="list-style-image:none;list-style-position:outside;list-style-type:none;line-height:18px;margin-left:15px;">
                        <li><a onclick="YAHOO.rapidjs.Components['reportDialog'].show()">Availability</a></li>
                        <li><a onclick="YAHOO.rapidjs.Components['reportDialog'].show()">Cpu Utilization</a></li>
                        <li><a onclick="YAHOO.rapidjs.Components['reportDialog'].show()">Availability</a></li>
                    </ul>
                </div>
            </td>
        </tr></tbody></table>
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