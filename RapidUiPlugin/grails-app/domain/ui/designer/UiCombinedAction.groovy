package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiCombinedAction extends UiAction 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["actions"];
        storageType "File"

    };
    List actions = [];
    Long timeout = 60;
    static relations = [
            actions:[type:UiAction, isMany:true]
    ]

    static constraints={
        timeout(nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = ["actions"];
    //AUTO_GENERATED_CODE
}