package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 6, 2009
 * Time: 8:54:03 AM
 * To change this template use File | Settings | File Templates.
 */
class UiGMap extends UiComponent{

    static searchable = {
        storageType "File"
    };

    String contentPath
    String url
    Long pollingInterval= 0;
    String latitudeField
    String longitudeField
    String addressField
    String markerField
    String tooltipField
    Long timeout=60;
    static datasources = [:]
    static relations = [:]
    static constraints={
        contentPath(nullable:false, blank:false)
        url(nullable:false, blank:false)
        latitudeField(nullable:false, blank:false)
        longitudeField(nullable:false, blank:false)
        addressField(nullable:false, blank:false)
        markerField(nullable:false, blank:false)
        tooltipField(nullable:false, blank:false)
        pollingInterval(nullable:true)
        timeout(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}