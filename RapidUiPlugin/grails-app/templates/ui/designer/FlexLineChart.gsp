
<rui:flexLineChart id="${uiElement.name}" url="../${uiElement.url}" rootTag="${uiElement.rootTag}"  timeout="${uiElement.timeout}"
        dataRootTag="${uiElement.dataRootTag}"  dataTag="${uiElement.dataTag}"  annotationTag="${uiElement.annotationTag}"  dateAttribute="${uiElement.dateAttribute}"
        valueAttribute="${uiElement.valueAttribute}"  annLabelAttr="${uiElement.annLabelAttr}"  annTimeAttr="${uiElement.annTimeAttr}"
        swfURL="../images/rapidjs/component/chart/FlexLineChart.swf" title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}"
        durations="${uiElement.durations}"
<%
    uiElement.getActionTrigers().each{eventName, actionTriggers->
         def actionString = uiElement.getActionsString(actionTriggers);
    %>
        on${eventName.substring(0,1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
    }
    %>
>
</rui:flexLineChart>