package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 5, 2009
 * Time: 4:50:59 PM
 * To change this template use File | Settings | File Templates.
 */
class UiTimeline extends UiComponent{
    static searchable = {
        storageType "FileAndMemory"
    };
    String url = "";
    Long pollingInterval= 0;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    List bands=[];
    static relations = [
            bands: [type: UiTimelineBand, reverseName: "timeline", isMany: true]
    ]
    static constraints={
        url(blank:false)
        pollingInterval(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}