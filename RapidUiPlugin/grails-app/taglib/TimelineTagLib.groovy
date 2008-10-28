/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Oct 28, 2008
 * Time: 10:17:11 AM
 */
class TimelineTagLib {
    static namespace = "rui";

    static def fTimeline(attrs, bodyString){
        
    }

    def timeline = {attrs, body ->
        out << fTimeline(attrs, body());
    }
}