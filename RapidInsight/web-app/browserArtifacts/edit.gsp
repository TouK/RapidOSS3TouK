<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Jun 3, 2009
  Time: 5:25:44 PM
--%>

<%
    def className = params.__rsBrowserClassName;
    def domainClass = grailsApplication.getDomainClass(className)
    def logicalName = domainClass.logicalPropertyName;
    def domainObject = domainClass.clazz."get"(id: params.id);
%>
<g:if test="${domainObject}">
    <%
        def properties = domainClass.clazz.getNonFederatedPropertyList();
        def keys = domainClass.clazz."keySet"();
        def propsToBeExcluded = ["id", "rsInsertedAt", "rsUpdatedAt"]
    %>
    <script type="text/javascript">
        window.refreshObjectList = function(){
            var objectListComp = YAHOO.rapidjs.Components['objectList'];
            objectListComp.setQuery(objectListComp.searchInput.value, 'id', 'asc', '${domainClass.fullName}', {domain:'${logicalName}'});
        }
    </script>
    <div class="ri-browser-form">
        <rui:formRemote componentId="${params.componentId}" method="POST" action="rsBrowserCrud/update" onSuccess="window.refreshObjectList">
            <table><tbody>
                <input type="hidden" name="__rsBrowserClassName" value="${className}">
                <input type="hidden" name="id" value="${domainObject.id}">
                <g:each var="keyProp" in="${keys}">
                    <g:if test="${!keyProp.isRelation && !propsToBeExcluded.contains(keyProp.name)}">
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label>${keyProp.name}:</label>
                            </td>
                            <td valign="top">
                                <rui:include template="browserArtifacts/browserEditor.gsp" model="${[property:keyProp, domainClass:domainClass, cp:domainClass.constrainedProperties[keyProp.name], domainObject:domainObject]}"></rui:include>
                            </td>
                        </tr>
                    </g:if>
                </g:each>
                <g:each var="prop" in="${properties}">
                    <g:if test="${!prop.isKey && !propsToBeExcluded.contains(prop.name)}">
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label>${prop.name}:</label>
                            </td>
                            <td valign="top">
                                <rui:include template="browserArtifacts/browserEditor.gsp" model="${[property:prop, domainClass:domainClass, cp:domainClass.constrainedProperties[prop.name], domainObject:domainObject]}"></rui:include>
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


