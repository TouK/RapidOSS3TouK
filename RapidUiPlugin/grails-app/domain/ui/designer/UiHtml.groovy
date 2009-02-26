package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 9:04:52 AM
 * To change this template use File | Settings | File Templates.
 */
class UiHtml extends UiComponent{

    static searchable = {
        storageType "FileAndMemory"
    };

    Long pollingInterval= 0;
    Boolean iframe=false;
    static datasources = [:]
    static relations = [:]
    static constraints={
        pollingInterval(nullable:true)
        iframe(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}