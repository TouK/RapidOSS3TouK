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
        storageType "FileAndMemory"
    };
    String contentPath = "";
    String categoryField = "";
    String dataField = "";
    String legend = "none";
    String colors = "";
    String url = "";
    Long pollingInterval= 0;
    Long timeout= 30;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        contentPath(blank:false)
        dataField(blank:false)
        url(blank:false)
        legend(nullable:false, blank:false, inList:["top", "right", "bottom", "left", "none"])
        pollingInterval(nullable:true)
        timeout(nullable:true)
        colors(blank:true, nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}