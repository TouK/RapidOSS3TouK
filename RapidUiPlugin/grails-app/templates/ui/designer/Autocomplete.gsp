<rui:autocomplete id="${uiElement.name}" url="../${uiElement.url}" contentPath="${uiElement.contentPath}" animated="${uiElement.animated}" submitButtonLabel="${uiElement.submitButtonLabel}"
        title="${uiElement.title}" suggestionAttribute="${uiElement.suggestionAttribute}" cacheSize="${uiElement.cacheSize}" timeout="${uiElement.timeout}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
</rui:autocomplete>