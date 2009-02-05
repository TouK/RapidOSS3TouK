<rui:timeline id="${uiElement.name}" url="${uiElement.url}" title="${uiElement.title}" pollingInterval="${uiElement.pollingInterval}">
    <rui:tlBands>
        <%
            def dateFormatter = new java.text.SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
            uiElement.bands.each{band->
        %>
        <rui:tlBand width="${band.width}%" intervalUnit="${band.intervalUnit}" intervalPixels="${band.intervalPixels}"
                intervalPixels="${band.intervalPixels}" ${band.date > new Date(0)?"date=\""+dateFormatter.format(band.date)+"\"":""}
                ${band.syncWith > -1?"syncWith=\""+band.syncWith+"\"":""} highlight="${band.highlight}" showText="${band.showText}"
                ${band.textWidth> 0?"textWidth=\""+band.textWidth+"\"":""} ${band.trackHeight> 0?"trackHeight=\""+band.trackHeight+"\"":""} ${band.trackGap> 0?"trackGap=\""+band.trackGap+"\"":""}
                ${band.layoutWith> 0?"layoutWith=\""+band.layoutWith+"\"":""}></rui:tlBand>
        <%
            }
        %>
    </rui:tlBands>
</rui:timeline>