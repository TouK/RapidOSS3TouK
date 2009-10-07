package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 3, 2009
 * Time: 9:51:24 AM
 * To change this template use File | Settings | File Templates.
 */
class UiTreeGrid extends UiComponent{
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["columns", "rootImages"];
        storageType "FileAndMemory"
    };
    static datasources = [:]
    String url = "";
    String rootTag = "";
    String contentPath = "";
    String keyAttribute = "";
    String expandAttribute = "";
    Long pollingInterval = 0;
    Long timeout= 30;
    Boolean expanded = false;
    Boolean tooltip = false;
    List columns = [];
    List rootImages = [];
    org.springframework.validation.Errors errors ;


    static relations = [
            columns: [type: UiTreeGridColumn, reverseName: "component", isMany: true],
            rootImages: [type: UiRootImage, reverseName: "component", isMany: true],
    ]

    static constraints = {
        url(blank:false)
        rootTag(blank:false)
        contentPath(blank:false)
        keyAttribute(blank:false)
        pollingInterval(nullable: true)
        expandAttribute(nullable: true)
        timeout(nullable: true)
        expanded(nullable: true)
        tooltip(nullable: true)
    }

    static propertyConfiguration = [:]
    static transients = ["columns", "rootImages"];
    //AUTO_GENERATED_CODE
}