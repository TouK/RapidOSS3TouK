package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 27, 2009
 * Time: 8:47:04 AM
 * To change this template use File | Settings | File Templates.
 */
class UiAutocomplete extends UiComponent{
   static searchable = {
        storageType "FileAndMemory"
    };
    String contentPath = "";
    String url = "";
    String suggestionAttribute = "";
    String submitButtonLabel = "Search";
    Long cacheSize = 0;
    Long timeout = 0;
    Boolean animated = false;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        contentPath(nullable:false, blank:false)
        url(nullable:false, blank:false)
        suggestionAttribute(nullable:false, blank:false)
        cacheSize(nullable:true)
        animated(nullable:true)
        timeout(nullable:true)
        submitButtonLabel(blank:true,nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}