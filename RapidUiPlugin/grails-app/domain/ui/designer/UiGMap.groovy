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
        storageType "FileAndMemory"
    };

    String url
    String googleKey = "ABQIAAAA7ipkp7ZXyXVH2UHyOgqZhxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRnNbZP5arP3T53Mzg-yLZcEMRBew"
    Long pollingInterval= 0;
    Long timeout=60;
    String locationTagName = "Location";
    String lineTagName = "Line";
    String iconTagName = "Icon";
    Long lineSize = 5;
    Long defaultIconWidth = 32;
    Long defaultIconHeight = 32;
    org.springframework.validation.Errors errors ;
    static datasources = [:]
    static relations = [:]
    static constraints={
        locationTagName(nullable: true, blank: true)
        lineTagName(nullable: true, blank: true)
        iconTagName(nullable: true, blank: true)
        url(blank:false)
        googleKey(blank:false)
        pollingInterval(nullable:true)
        lineSize(nullable:true)
        timeout(nullable:true)
        defaultIconWidth(nullable:true)
        defaultIconHeight(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [:];
}