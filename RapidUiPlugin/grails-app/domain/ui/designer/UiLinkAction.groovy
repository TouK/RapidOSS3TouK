package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiLinkAction extends UiAction 
{

    //AUTO_GENERATED_CODE
    public static String SELF = "self"
    public static String BLANK = "blank"
    static searchable = {
        except = [];
        storageType "FileAndMemory"

    };
    String url;
    String target = SELF;
    org.springframework.validation.Errors errors ;
    static relations = [:]

    static constraints={
        url(blank:false)
        target(blank:false, inList:[SELF, BLANK])
    }

    static propertyConfiguration= [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}