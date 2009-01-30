package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 30, 2009
 * Time: 8:54:45 AM
 * To change this template use File | Settings | File Templates.
 */
class UiComponentOperations extends UiLayoutUnitOperations{
    public static Map metaData()
    {
        Map metaData = [
                propertyConfiguration:
                [
                    name:[descr:"Name of component"],
                    title:[descr:"Title of component"]
                ],
                childrenConfiguration:[]
        ];
        return metaData;
    }
}