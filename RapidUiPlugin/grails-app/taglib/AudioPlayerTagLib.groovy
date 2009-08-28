/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 16, 2009
 * Time: 5:39:00 PM
 * To change this template use File | Settings | File Templates.
 */
class AudioPlayerTagLib {
  static namespace = "rui"

    static def fAudioPlayer(attrs, bodyString) {
        def configString = getConfig(attrs);
        return """
           <script type="text/javascript">
               var aConfig = ${configString};
               var audioPlayer = new YAHOO.rapidjs.component.AudioPlayer(aConfig);
               if(audioPlayer.pollingInterval > 0){
                    YAHOO.util.Event.onDOMReady(function(){
                        this.poll();
                   }, audioPlayer, true)
               }
           </script>
        """;
    }
    def audioPlayer = {attrs, body ->
        out << fAudioPlayer(attrs, "")
    }

    static def getConfig(attrs) {
        return """{
            id:'${attrs["id"]}',
            ${attrs["pollingInterval"] ? "pollingInterval:${attrs["pollingInterval"]}," : ""}
            ${attrs["url"] ? "url:'${attrs["url"]}'," : ""}
            ${attrs["playCondition"] ? "playCondition:\"${attrs["playCondition"].encodeAsJavaScript()}\"," : ""}
            ${attrs["volume"] ? "volume:${attrs["volume"]}," : ""}
            ${attrs["timeout"] ? "timeout:${attrs["timeout"]}," : ""}
            soundFile:'${attrs["soundFile"]}'
        }"""
    }
}