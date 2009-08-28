/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/

import com.ifountain.rui.util.TagLibUtils

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 28, 2008
* Time: 10:17:11 AM
*/
class TimelineTagLib {
    static namespace = "rui";

    static def fTimeline(attrs, bodyString) {
        def configXML = "<Timeline>${bodyString}</Timeline>"
        def configStr = getConfig(attrs, configXML);
        def onTooltipClick = attrs["onTooltipClicked"];
        def tooltipClickJs = "";
        if (onTooltipClick != null) {
            getActionsArray(onTooltipClick).each {actionName ->
                tooltipClickJs += """
                   timeline.events['tooltipClicked'].subscribe(function(buble, data){
                       var params = {data:data, buble:buble};
                       YAHOO.rapidjs.Actions['${actionName}'].execute(params);
                    }, this, true);
                """
            }

        }
        return """
           <script type="text/javascript">
               var timelineConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var timeline = new YAHOO.rapidjs.component.TimelineWindow(container, timelineConfig);
               ${tooltipClickJs}
               if(timeline.pollingInterval > 0){
                    YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, timeline, true)
               }
           </script>
        """
    }

    def timeline = {attrs, body ->
        out << fTimeline(attrs, body());
    }

    def tlBands = {attrs, body ->
        out << fTlBands(attrs, body());
    }

    def tlBand = {attrs, body ->
        out << fTlBand(attrs, "");
    }

    static def getConfig(attrs, configXML) {
        def xml = new XmlSlurper().parseText(configXML);
        def bands = xml.Bands.Band;
        def bandsArray = [];
        bands.each {
            def intervalUnit = getIntervalUnit(it.@intervalUnit.toString().trim())
            def showText = it.@showText.toString().trim()
            def trackHeight = it.@trackHeight.toString().trim()
            def trackGap = it.@trackGap.toString().trim()
            def syncWith = it.@syncWith.toString().trim()
            def layoutWith = it.@layoutWith.toString().trim()
            def date = it.@date.toString().trim()
            def highlight = it.@highlight.toString().trim()
            def textWidth = it.@textWidth.toString().trim()
            bandsArray.add("""{
                width:'${it.@width}',       
                intervalPixels:${it.@intervalPixels},
                ${showText != "" ? "showText:${showText}," : ""}       
                ${trackHeight != "" ? "trackHeight:${trackHeight}," : ""}       
                ${trackGap != "" ? "trackGap:${trackGap}," : ""}       
                ${syncWith != "" ? "syncWith:${syncWith}," : ""}       
                ${layoutWith != "" ? "layoutWith:${layoutWith}," : ""}       
                ${highlight != "" ? "highlight :${highlight}," : ""}
                ${date != "" ? "date:'${date}'," : ""}
                ${textWidth != "" ? "textWidth:${textWidth}," : ""}
                intervalUnit:${intervalUnit}
            }""")
        }
        return """{
            id:'${attrs["id"]}',
            url:'${attrs["url"]}',
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            Bands:[${bandsArray.join(',\n')}]
        }"""
    }

    static def getIntervalUnit(intervalUnit) {
        switch (intervalUnit) {
            case "millisecond":
                return "Timeline.DateTime.MILLISECOND"
            case "second":
                return "Timeline.DateTime.SECOND"
            case "minute":
                return "Timeline.DateTime.MINUTE"
            case "hour":
                return "Timeline.DateTime.HOUR"
            case "day":
                return "Timeline.DateTime.DAY"
            case "week":
                return "Timeline.DateTime.WEEK"
            case "month":
                return "Timeline.DateTime.MONTH"
            case "year":
                return "Timeline.DateTime.YEAR"
            case "decade":
                return "Timeline.DateTime.DECADE"
            case "century":
                return "Timeline.DateTime.CENTURY"
            case "millennium":
                return "Timeline.DateTime.MILLENNIUM"
            case "epoch":
                return "Timeline.DateTime.EPOCH"
            case "era":
                return "Timeline.DateTime.ERA"
            default:
                return "Timeline.DateTime.DAY"
        }
    }



    static def fTlBands(attrs, bodyString) {
        TagLibUtils.getConfigAsXml("Bands", attrs, [], bodyString);
    }

    static def fTlBand(attrs, bodyString) {
        def validAttrs = ["intervalUnit", "width", "intervalPixels", "showText", "trackHeight", "date",
                "trackGap", "textWidth", "highlight", "syncWith", "layoutWith"];
        TagLibUtils.getConfigAsXml("Band", attrs, validAttrs);
    }

    static def getActionsArray(actionAttribute) {
        def actions = [];
        if (actionAttribute instanceof List) {
            actions.addAll(actionAttribute);
        }
        else {
            actions.add(actionAttribute);
        }
        return actions;
    }

}