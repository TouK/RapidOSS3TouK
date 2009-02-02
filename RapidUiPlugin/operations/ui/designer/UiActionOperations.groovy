package ui.designer

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import com.ifountain.rui.util.DesignerUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder;
public class UiActionOperations extends AbstractDomainOperation
{

    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration: [
                        name: [descr: "Unique name of the action"],
                        condition: [descr: "The JavaScript expression that will be evaluated to determine whether to execute the action or not."]
                ],
                childrenConfiguration: [
                        [
                                designerType: "Events",
                                metaData: [
                                        designerType: "Events",
                                        display: "Events",
                                        canBeDeleted: false,
                                        propertyConfiguration: [
                                                designerHidden: [descr: "", type: "String", defaultValue: "true"]
                                        ],
                                        childrenConfiguration: [
                                                [designerType: "Event", isMultiple: true, propertyName: "events"]
                                        ]
                                ],
                                isMultiple: false
                        ]
                ]
        ];
        return metaData;
    }

}