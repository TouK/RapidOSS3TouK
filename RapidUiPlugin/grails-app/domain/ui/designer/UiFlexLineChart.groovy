package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 3:31:29 PM
 * To change this template use File | Settings | File Templates.
 */
class UiFlexLineChart extends UiComponent{
     static searchable = {
        storageType "FileAndMemory"
    };

    String rootTag = "RootTag";
    String url = "";
    String dataTag = "Data";
    String dataRootTag = "Variable";
    String annotationTag = "Annotation";
    String annTimeAttr = "time";
    String annLabelAttr = "label";
    String dateAttribute = "time";
    String valueAttribute = "value";
    String durations = "";
    Long pollingInterval= 0;
    Long timeout= 30;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        rootTag(nullable:false, blank:false)
        url(nullable:false, blank:false)
        dataRootTag(nullable:true, blank:true)
        dataTag(nullable:false, blank:false)
        dateAttribute(nullable:false, blank:false)
        valueAttribute(nullable:false, blank:false)
        annotationTag(nullable:true, blank:true)
        annTimeAttr(nullable:true, blank:true)
        annLabelAttr(nullable:true, blank:true)
        durations(nullable:true, blank:true)
        pollingInterval(nullable:true)
        timeout(nullable:true)
    }
    static propertyConfiguration= [:]
    static transients = [:];
}