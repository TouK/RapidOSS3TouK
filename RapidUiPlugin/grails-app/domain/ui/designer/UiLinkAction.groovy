package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLinkAction extends UiAction 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
        storageType "FileAndMemory"

    };
    String url;
    org.springframework.validation.Errors errors ;
    static relations = [:]

    static constraints={
        url(blank:false)
    }

    static propertyConfiguration= [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}