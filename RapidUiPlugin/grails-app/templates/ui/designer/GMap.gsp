<rui:gmap id="${uiElement.name}" url="../${uiElement.url}" title="${uiElement.title}" googleKey="${uiElement.googleKey}"
        timeout="${uiElement.timeout}" locationTagName="${uiElement.locationTagName}" lineTagName="${uiElement.lineTagName}"
         iconTagName="${uiElement.iconTagName}" lineSize="${uiElement.lineSize}" defaultIconWidth="${uiElement.defaultIconWidth}" defaultIconHeight="${uiElement.defaultIconHeight}"
<%
        uiElement.getActionTrigers().each{eventName, actionTriggers->
    def actionString = uiElement.getActionsString(actionTriggers);
%>
    on${eventName.substring(0, 1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
        }
    %>

    ></rui:gmap>