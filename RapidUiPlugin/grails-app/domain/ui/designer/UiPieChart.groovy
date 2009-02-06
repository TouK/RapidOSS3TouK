package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 5, 2009
 * Time: 6:27:56 PM
 * To change this template use File | Settings | File Templates.
 */
class UiPieChart extends UiComponent{
    static searchable = {
        storageType "File"
    };
    String contentPath = "";
    String categoryField = "";
    String dataField = "";
    String legend = "none";
    String colors = "";
    String url = "";
    Long pollingInterval= 0;
    static datasources = [:]
    static relations = [:]
    static constraints={
        legend(nullable:false, blank:false, inList:["top", "right", "bottom", "left", "none"])
        pollingInterval(nullable:true)
        colors(blank:true, nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}