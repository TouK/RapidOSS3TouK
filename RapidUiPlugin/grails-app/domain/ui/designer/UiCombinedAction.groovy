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
    static relations = [
            actions:[type:UiAction, isMany:true]
    ]

    static constraints={
    }

    static propertyConfiguration= [:]
    static transients = ["actions"];
    //AUTO_GENERATED_CODE
}