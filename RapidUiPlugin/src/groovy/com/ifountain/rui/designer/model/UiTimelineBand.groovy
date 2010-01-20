package com.ifountain.rui.designer.model

import com.ifountain.rui.designer.UiElmnt
import groovy.util.slurpersupport.GPathResult

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Oct 22, 2009
* Time: 3:16:53 PM
*/
class UiTimelineBand extends UiElmnt {
    Long width = 50;
    Long intervalPixels = 0;
    String intervalUnit = "day";
    Boolean showText = "";
    Double trackHeight = 0;
    Long trackGap = 0;
    Long syncWith = -1;
    Long layoutWith = -1;
    Boolean highlight = false;
    Date date = new Date(0);
    Long textWidth = 200;
    String timelineId = "";

    public static java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
    public static Map metaData()
    {
        Map metaData = [
                help: "Timeline Band.html",
                designerType: "TimelineBand",
                canBeDeleted: true,
                display: "Band",
                imageExpanded: "images/rapidjs/designer/film.png",
                imageCollapsed: "images/rapidjs/designer/film.png",
                propertyConfiguration: [
                        timelineId:[isVisible:false, validators:[blank:false,nullable:false]],
                        width: [descr: "The percentage of the band in the whole timeline.", validators:[nullable:false]],
                        intervalPixels: [descr: "How many pixel each date unit will span.", validators:[nullable:false]],
                        intervalUnit: [descr: "Which date unit that the band will display. Possible values are millisecond, second, minute, hour, day, week, month, year, decade, century, millennium, epoch and era.",
                                validators:[blank:false, nullable:false, inList:["millisecond", "second", "minute", "hour", "day", "week", "month", "year", "decade", "century", "millennium", "epoch","era"]]],
                        showText: [descr: "Parameter whether to display event text or not.", validators:[nullable:false]],
                        trackHeight: [descr: "Track height in em unit. See SIMILE Timeline documentation for details."],
                        trackGap: [descr: "Track gap in em unit. See SIMILE Timeline documentation for details."],
                        syncWith: [descr: "By default all timeline bands scroll independently. The parameter specifies the band index that the current band scroll in synchrony."],
                        layoutWith: [descr: "The index of the band that the current band layout in synchrony (Events are at the same tracks)."],
                        highlight: [descr: "Parameter to display the band whether highlighted or not.", validators:[nullable:false]],
                        date: [descr: "Parameter to make sure the timeline starts out showing the events immediately without requiring the user to pan first. Band is positioned at the specified date.", defaultValue: "${formatDate(new Date(0))}"],
                        textWidth: [descr: "The width of the event text in pixels."]

                ],
                childrenConfiguration: []
        ];
        return metaData;
    }

    protected void addChildElements(GPathResult node, UiElmnt parent) {}

    protected void populateStringAttributes(GPathResult node, UiElmnt parent) {
        super.populateStringAttributes(node, parent);
        def attributes = node.attributes();
        if (attributes.date != null)
            attributesAsString["date"] = dateFormatter.parse(attributes.date);
        attributesAsString["timelineId"] = parent._designerKey;
    }
    

    public static String formatDate(Date date)
    {
        return dateFormatter.format(date);
    }
}