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
        <div class="yui-navset yui-navset-top smarts-object-details" >
            <div style="display:block">
                <table width="100%" cellspacing="1" cellpadding="1">
                    <tbody>

                        <g:each var="property"  status="i" in="${allProperties}">
                             <tr  class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td width="0%" style="font-weight:bold">${property.name}</td>
                                <%
                                   if(!relations.containsKey(property.name)){
                                       %>
                                            <td >${domainObject[property.name]}&nbsp;</td>
                                       <%
                                   }
                                   else{
                                       def relation = relations[property.name];
                                       if(relation.isOneToOne() || relation.isManyToOne()){
                                           def sObj = domainObject[property.name]
                                           if(sObj != null){
                                                %>
                                                    <td>
                                                        <a onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${sObj.name}', 'Details of ${sObj.creationClassName} ${sObj.name}');">${sObj.creationClassName} ${sObj.name}<a>
                                                    </td>
                                               <%
                                            }
                                            else{
                                                %>
                                                    <td></td>
                                                <%
                                            }
                                       }
                                       else{
                                           %>
                                                <td>
                                                <ul>
                                            <%
                                            domainObject[property.name].each{
                                              %>
                                                   <li><a onclick="YAHOO.rapidjs.Components['objectDetails'].show('getObjectDetails.gsp?name=${it.name}', 'Details of ${it.creationClassName} ${it.name}');">${it.creationClassName} ${it.name}<a></li>
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
        </div>

        <%
    }
    else {
        %>
        Object ${name} does not exist.
        <%
    }
%>