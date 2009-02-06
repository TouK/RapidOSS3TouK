package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 9:19:02 AM
 * To change this template use File | Settings | File Templates.
 */
class UiObjectMap extends UiComponent{

    static searchable = {
        storageType "File"
    };

    String expandURL="../script/run/expandMap"
    String dataURL="../script/run/getMapData"
    Long pollingInterval= 0;
    Long nodeSize= 0;
    String edgeColors="";
    String dataTag="";
    String edgeColorDataKey="state";
    List nodeContents=[];
    List toolbarMenus=[];
    static datasources = [:]
    static relations = [
        nodeContents: [type: UiObjectMapContent, reverseName: "objectMap", isMany: true],
        toolbarMenus: [type: UiToolbarMenu, reverseName: "objectMap", isMany: true]
    ]
    static constraints={
        expandURL(nullable:false, blank:false)
        dataURL(nullable:false, blank:false)
        nodeSize(nullable:true)
        edgeColors(nullable:true)
        edgeColorDataKey(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}