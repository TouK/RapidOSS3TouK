package ui.designer;
import com.ifountain.core.domain.annotations.*;

class UiFunctionAction extends UiAction 
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["component", "arguments"];
        storageType "FileAndMemory"

    };
    UiComponent component;
    String function;
    List arguments = [];
    org.springframework.validation.Errors errors ;
    static relations = [component:[type:UiComponent, isMany:false],
    arguments:[type:UiFunctionArgument, reverseName:"action", isMany:true]]

    static constraints={
        function(blank:false)
    }

    static propertyConfiguration= [:]
    static transients = ["component", "arguments"];
    //AUTO_GENERATED_CODE
}