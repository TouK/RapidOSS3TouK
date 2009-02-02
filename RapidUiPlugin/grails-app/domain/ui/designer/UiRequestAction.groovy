package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiRequestAction extends UiAction 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["parameters", "components"];
        storageType "File"

    };
    String url;
    Long timeout = 60;
    List parameters = [];
    List components = [];
    static relations = [
            parameters:[type:UiRequestParameter, reverseName:"action", isMany:true],
            components:[type:UiComponent, isMany:true]
    ]

    static constraints={
        timeout(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["parameters", "components"];
    //AUTO_GENERATED_CODE
}