package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 23, 2009
 * Time: 5:49:01 PM
 * To change this template use File | Settings | File Templates.
 */
class UiSearchGridColumn extends UiColumn{
   static searchable = {
        except = []
        storageType "FileAndMemory"
    };
    String type = "text";
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        type(nullable:false, blank:false, inList:["text", "link", "image"])
    }

    static propertyConfiguration= [:]
    static transients = [];
}