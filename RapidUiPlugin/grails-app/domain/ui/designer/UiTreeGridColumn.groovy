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
        storageType "File"
    };
    String type = "text";
    List images = [];
    static datasources = [:]
    static relations = [images:[type: UiImage, reverseName: "column", isMany: true]]
    static constraints={
        type(nullable:false, blank:false, inList:["text", "image"])
    }

    static propertyConfiguration= [:]
    static transients = ["images"];
}