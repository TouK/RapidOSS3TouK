package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:47:12 AM
 * To change this template use File | Settings | File Templates.
 */
class UiTreeGridColumn extends UiColumn{
    static searchable = {
        except = ["images"]
        storageType "FileAndMemory"
    };
    String type = "text";
    String sortType = "string";
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        type(nullable:false, blank:false, inList:["text", "image"])
        sortType(nullable:false, blank:false, inList:["string", "ucString", "int", "date", "float"])
    }

    static propertyConfiguration= [:]
    static transients = ["images"];
}