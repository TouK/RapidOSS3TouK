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

    String expandURL="script/run/expandMap"
    String dataURL="script/run/getMapData"
    Long pollingInterval= 0;
    Long nodeSize= 60;
    String edgeColors="5:0xffde2c26,4:0xfff79229,3:0xfffae500,2:0xff20b4e0,1:0xffac6bac,0:0xff62b446,default:0xff62b446"
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
        nodeSize(nullable:true)
        edgeColors(nullable:true)
        edgeColorDataKey(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}