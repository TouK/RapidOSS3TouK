<rui:gmap id="${uiElement.name}" url="../${uiElement.url}" title="${uiElement.title}" contentPath="${uiElement.contentPath}" googleKey="${uiElement.googleKey}"
        timeout="${uiElement.timeout}" latitudeField="${uiElement.latitudeField}"
        longitudeField="${uiElement.longitudeField}" addressField="${uiElement.addressField}" markerField="${uiElement.markerField}"
        tooltipField="${uiElement.tooltipField}"
<%
        uiElement.getActionTrigers().each{eventName, actionTriggers->
    def actionString = uiElement.getActionsString(actionTriggers);
%>
    on${eventName.substring(0, 1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
        }
    %>

    ></rui:gmap>