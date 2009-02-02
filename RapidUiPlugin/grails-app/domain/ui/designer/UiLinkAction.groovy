package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLinkAction extends UiAction 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
        storageType "File"

    };
    String url;
    static relations = [:]

    static constraints={
        url(nullable:false)
    }

    static propertyConfiguration= [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}