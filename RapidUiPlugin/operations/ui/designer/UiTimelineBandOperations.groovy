package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 5, 2009
* Time: 5:23:40 PM
* To change this template use File | Settings | File Templates.
*/
class UiTimelineBandOperations extends AbstractDomainOperation{
    public static java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MMM dd yyyy HH:mm:ss z");
    public static Map metaData()
    {
        Map metaData = [
                designerType: "TimelineBand",
                canBeDeleted: true,
                display: "Band",
                imageExpanded: "images/rapidjs/designer/layout_content.png",
                imageCollapsed: "images/rapidjs/designer/layout_content.png",
                propertyConfiguration: [
                        width:[descr:"The percentage of the band in the whole timeline.", required:true],
                        intervalPixels:[descr:"How many pixel each date unit will span.", required:true],
                        intervalUnit:[descr:"Which date unit that the band will display. Possible values are millisecond, second, minute, hour, day, week, month, year, decade, century, millennium, epoch and era."],
                        showText:[descr:"Parameter whether to display event text or not."],
                        trackHeight:[descr:"Track height in em unit. See SIMILE Timeline documentation for details."],
                        trackGap:[descr:"Track gap in em unit. See SIMILE Timeline documentation for details."],
                        syncWith:[descr:"By default all timeline bands scroll independently. The parameter specifies the band index that the current band scroll in synchrony."],
                        layoutWith:[descr:"The index of the band that the current band layout in synchrony (Events are at the same tracks)."],
                        highlight:[descr:"Parameter to display the band whether highlighted or not."],
                        date:[descr:"Parameter to make sure the timeline starts out showing the events immediately without requiring the user to pan first. Band is positioned at the specified date.", defaultValue:"${formatDate(new Date(0))}"],
                        textWidth:[descr:"The width of the event text in pixels."]

                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }

    def static addUiElement(xmlNode, parentElement)
    {
        def attributes = xmlNode.attributes();
        if(attributes.date != null)
        attributes.date = dateFormatter.parse (attributes.date);
        attributes.timeline= parentElement;
        return DesignerUtils.addUiObject(UiTimelineBand, attributes, xmlNode);
    }

    def static formatDate(Date date)
    {
        return dateFormatter.format(date);
    }
}