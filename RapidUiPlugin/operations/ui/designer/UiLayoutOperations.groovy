package ui.designer;
import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.commons.ApplicationHolder

public class UiLayoutOperations extends AbstractDomainOperation
{
    public static Map metaData()
    {
        Map metaData = [
                designerType:"Layout",
                canBeDeleted: true,
                display: "Layout",
                propertyConfiguration: [:],
                childrenConfiguration:[
                    [designerType:"CenterUnit", propertyName:"units"],
                    [designerType:"TopUnit", propertyName:"units"],
                    [designerType:"BottomUnit", propertyName:"units"],
                    [designerType:"LeftUnit", propertyName:"units"],
                    [designerType:"RightUnit", propertyName:"units"]
                ]
        ];
        return metaData;
    }

}