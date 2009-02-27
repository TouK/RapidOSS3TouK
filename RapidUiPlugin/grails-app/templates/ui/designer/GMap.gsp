<rui:gmap id="${uiElement.name.encodeAsHTML()}" url="../${uiElement.url.encodeAsHTML()}" title="${uiElement.title.encodeAsHTML()}" contentPath="${uiElement.contentPath.encodeAsHTML()}" googleKey="${uiElement.googleKey.encodeAsHTML()}"
        timeout="${uiElement.timeout}" latitudeField="${uiElement.latitudeField.encodeAsHTML()}"
        longitudeField="${uiElement.longitudeField.encodeAsHTML()}" addressField="${uiElement.addressField.encodeAsHTML()}" markerField="${uiElement.markerField.encodeAsHTML()}"
        tooltipField="${uiElement.tooltipField.encodeAsHTML()}"
<%
        uiElement.getActionTrigers().each{eventName, actionTriggers->
    def actionString = uiElement.getActionsString(actionTriggers);
%>
    on${eventName.substring(0, 1).toUpperCase().encodeAsHTML()}${eventName.substring(1).encodeAsHTML()}="${actionString}"
    <%
        }
    %>

    ></rui:gmap>