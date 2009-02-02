package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 3:31:29 PM
 * To change this template use File | Settings | File Templates.
 */
class UiFlexPieChart extends UiComponent{
     static searchable = {
        storageType "File"
    };
    String rootTag = "";
    String url = "";
    Long pollingInterval= 0;
    static datasources = [:]
    static relations = [:]
    static constraints={
        rootTag(nullable:false, blank:false)
        url(nullable:false, blank:false)
        pollingInterval(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}