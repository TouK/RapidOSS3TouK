<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.mobile.MobileUtils;" %>
<%
    def format = new SimpleDateFormat("d MMM HH:mm:ss");

    //convert Date Properties
    CONFIG.INVENTORY_DATE_PROPERTIES.each{ propertyName ->
      if(objectProperties.containsKey(propertyName))
      {
          objectProperties[propertyName] = format.format(new Timestamp(objectProperties[propertyName]))
      }
    }

    //get _errors from objectProperties
    def _errors=objectProperties.remove("_errors");
    //prepare errored properties from federation
    def objectPropertyHasErrors=objectProperties.remove("_objectPropertyHasErrors");
    if(objectPropertyHasErrors == null) objectPropertyHasErrors = [:];

    //sort propertyNames first className, name then all others
    def propertyNames=["className","name"];
    propertyNames.addAll(objectProperties.keySet().findAll{it!="className" && it!="name"}.sort());

    def gspFolder = "simple"
    if (MobileUtils.isIphone(request)) {
        gspFolder = "iphone";             
    }
%>

<div class="ri-mobile-objectDetails">
    <table class="itable" width="100%" cellspacing="1" cellpadding="1">
        <tbody>
            <g:each var="propertyName" status="i" in="${propertyNames}">
                <g:if test="${propertyName != 'id' && propertyName != 'rsDatasource'}">
                    <g:set var="propertyValue" value=""/>
                    <tr class="${(i % 2) == 0 ? 'alt' : 'reg'}">
                        <td width="0%" style="font-weight:bold">${propertyName}</td>
                        <% def propertyValue=objectProperties[propertyName]; %>
                        <g:if test="${! (propertyValue instanceof List) }">
                            <g:if test="${!objectPropertyHasErrors[propertyName]}">
                                <td>${propertyValue}&nbsp;</td>
                            </g:if>
                            <g:else>
                                <td class="ri-field-error">InAccessible&nbsp;</td>
                            </g:else>
                        </g:if>
                        <g:else>
                            <td width="100%">
                                <ul style="margin-left: 10px;">
                                    <% def sortedRelatedObjects = propertyValue; %>
                                    <g:if test="${sortedRelatedObjects.size() > 10}">
                                        <div>
                                            <g:each var="rObj" in="${sortedRelatedObjects[0..9]}">
                                                <li><rui:link url="mobile/${gspFolder}/objectDetails.gsp" params="${[name:rObj.name]}">${rObj.className} ${rObj.name}</rui:link></li>
                                            </g:each>
                                        </div>
                                        <div style="display:none" id="${propertyName}_hiddenObjects">
                                            <g:each var="rObj" in="${sortedRelatedObjects[10..-1]}">
                                                <li><rui:link url="mobile/${gspFolder}/objectDetails.gsp" params="${[name:rObj.name]}">${rObj.className} ${rObj.name}</rui:link></li>
                                            </g:each>
                                        </div>
                                        <g:if test="${gspFolder=='iphone'}">
                                            <div class="ri-objectdetails-expand" id="${propertyName}_expandButton" onclick="window.expandRelations('${propertyName}', ${sortedRelatedObjects.size()});">Expand (${sortedRelatedObjects.size() - 10})</div>
                                        </g:if>
                                        <g:else>
                                            <rui:link url="mobile/simple/expandedRelations.gsp" params="${[name:name, relationName:propertyName]}">Expand (${sortedRelatedObjects.size() - 10})</rui:link>
                                        </g:else>
                                    </g:if>
                                    <g:else>
                                        <g:each var="rObj" in="${sortedRelatedObjects}">
                                            <li><rui:link url="mobile/${gspFolder}/objectDetails.gsp" params="${[name:rObj.name]}" >${rObj.className} ${rObj.name}</rui:link></li>
                                        </g:each>
                                    </g:else>
                                </ul>
                            </td>
                        </g:else>
                    </tr>
                </g:if>
            </g:each>
        </tbody>
    </table>
</div>