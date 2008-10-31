/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 30, 2008
 * Time: 5:29:00 PM
 */
class GmapTagLib {
    static namespace = "rui"
    static def fGmap(attrs, bodyString) {
        def configStr = getConfig(attrs);
        def onMarkerClick = attrs["onMarkerClick"];
        def markerClickJs;
        if (onMarkerClick != null) {
            markerClickJs = """
               gmap.events['markerClick'].subscribe(function(xmlData){
                   var params = {data:xmlData.getAttributes()};
                   YAHOO.rapidjs.Actions['${onMarkerClick}'].execute(params);
                }, this, true);
            """
        }
        return """
           <script type="text/javascript">
               var gmapConfig = ${configStr};
               var container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var gmap = new YAHOO.rapidjs.component.GMap(container, gmapConfig);
               ${markerClickJs ? markerClickJs : ""}
               if(gmap.pollingInterval > 0){
                   gmap.poll();
               }
           </script>
        """;
    }
    def gmap = {attrs, body ->
         out << fGmap(attrs, "");
    }

    static def getConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            url:'${attrs["url"]}',
            contentPath:'${attrs["contentPath"]}',
            ${attrs["title"] ? "title:'${attrs["title"]}'," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            latitudeAttributeName:'${attrs["latitudeField"]}',
            longitudeAttributeName:'${attrs["longitudeField"]}',
            addressAttributeName:'${attrs["addressField"]}',
            markerAttributeName:'${attrs["markerField"]}'
        }"""
    }
}