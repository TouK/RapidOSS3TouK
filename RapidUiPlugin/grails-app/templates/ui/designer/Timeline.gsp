<rui:timeline id="${uiElement.name}" url="../${uiElement.url}" title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}" timeout="${uiElement.timeout}"
<%
        uiElement.getActionTrigers().each{eventName, actionTriggers->
    def actionString = uiElement.getActionsString(actionTriggers);
%>
    on${eventName.substring(0, 1).toUpperCase()}${eventName.substring(1)}="${actionString}"
    <%
        }
    %>
>
    <rui:tlBands>
        <%
            uiElement.bands.each{band->
        %>
        <rui:tlBand width="${band.width}%" intervalUnit="${band.intervalUnit}" intervalPixels="${band.intervalPixels}" ${band.date > new Date(0)?"date=\""+band.formatDate(band.date)+"\"":""}
                ${band.syncWith > -1?"syncWith=\""+band.syncWith+"\"":""} highlight="${band.highlight}" showText="${band.showText}"
                ${band.textWidth> 0?"textWidth=\""+band.textWidth+"\"":""} ${band.trackHeight> 0?"trackHeight=\""+band.trackHeight+"\"":""} ${band.trackGap> 0?"trackGap=\""+band.trackGap+"\"":""}
                ${band.layoutWith> -1?"layoutWith=\""+band.layoutWith+"\"":""}></rui:tlBand>
        <%
            }
        %>
    </rui:tlBands>
</rui:timeline>