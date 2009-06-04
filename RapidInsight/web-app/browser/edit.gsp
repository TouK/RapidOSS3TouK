<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Jun 3, 2009
  Time: 5:25:44 PM
--%>

<%
    def className = params.className;
    def domainClass = grailsApplication.getDomainClass(className)
    def domainObject = domainClass.clazz."get"(id: params.id);
%>
<g:if test="${domainObject}">
    <%
        def properties = domainClass.clazz."getPropertiesList"();
        def keys = domainClass.clazz."keySet"();
    %>
    <script type="text/javascript">
        window.refreshObjectList = function(){
            YAHOO.rapidjs.Components['objectList'].poll();
        }
    </script>
    <div class="ri-browser-form">
        <rui:formRemote componentId="${params.componentId}" method="POST" action="rsBrowserCrud/update" onSuccess="window.refreshObjectList">
            <table><tbody>
                <input type="hidden" name="className" value="${className}">
                <input type="hidden" name="id" value="${domainObject.id}">
                <g:each var="keyProp" in="${keys}">
                    <g:if test="${!keyProp.isRelation && keyProp.name != 'id'}">
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label>${keyProp.name}:</label>
                            </td>
                            <td valign="top">
                                <rui:include template="browser/browserEditor.gsp" model="${[property:keyProp, domainClass:domainClass, cp:domainClass.constrainedProperties[keyProp.name], domainObject:domainObject]}"></rui:include>
                            </td>
                        </tr>
                    </g:if>
                </g:each>
                <g:each var="prop" in="${properties}">
                    <g:if test="${!prop.isRelation && !prop.isOperationProperty && !prop.isKey && prop.name != 'id'}">
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label>${prop.name}:</label>
                            </td>
                            <td valign="top">
                                <rui:include template="browser/browserEditor.gsp" model="${[property:prop, domainClass:domainClass, cp:domainClass.constrainedProperties[prop.name], domainObject:domainObject]}"></rui:include>
                            </td>
                        </tr>
                    </g:if>
                </g:each>
            </tbody></table>
        </rui:formRemote>
    </div>
</g:if>
<g:else>
    ${domainClass} with id ${params.id} does not exist
</g:else>


