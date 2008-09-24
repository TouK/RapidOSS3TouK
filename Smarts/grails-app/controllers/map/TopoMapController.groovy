package map
import com.ifountain.rcmdb.domain.util.ControllerUtils;
import grails.converters.XML

class TopoMapController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = {}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET']]


    def delete = {
        def topoMap = TopoMap.get( [id:params.id])
        if(topoMap) {
            def username =  session.username;
            def mapName = topoMap.mapName;
            topoMap.remove();
            withFormat {
                html {
                    flash.message = "TopoMap ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

}