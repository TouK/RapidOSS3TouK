package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.commons.ApplicationHolder

public class UiLayoutOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                help:"Layout.html",
                designerType:"Layout",
                canBeDeleted: true,
                display: "Layout",
                imageExpanded: 'images/rapidjs/component/tools/folder_open.gif',
                imageCollapsed: 'images/rapidjs/component/tools/folder.gif',
                propertyConfiguration: [:],
                childrenConfiguration:[
                    [designerType:"CenterUnit", propertyName:"units", isVisible:{component-> return component.class.simpleName == "UiCenterUnit"}],
                    [designerType:"TopUnit", propertyName:"units", isVisible:{component-> return component.class.simpleName == "UiTopUnit"}],
                    [designerType:"BottomUnit", propertyName:"units",isVisible:{component-> return component.class.simpleName == "UiBottomUnit"}],
                    [designerType:"LeftUnit", propertyName:"units",isVisible:{component-> return component.class.simpleName == "UiLeftUnit"}],
                    [designerType:"RightUnit", propertyName:"units",isVisible:{component-> return component.class.simpleName == "UiRightUnit"}]
                ]
        ];
        return metaData;
    }

}