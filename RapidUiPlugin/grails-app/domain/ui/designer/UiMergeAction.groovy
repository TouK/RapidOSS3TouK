package ui.designer
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 2, 2009
 * Time: 1:43:32 PM
 * To change this template use File | Settings | File Templates.
 */
class UiMergeAction extends UiRequestAction{
    static searchable = {
        except = [];
        storageType "FileAndMemory"

    };
    String removeAttribute = "";
    org.springframework.validation.Errors errors ;
    static relations = [:]

    static constraints={
        removeAttribute(blank:true, nullable:true)
    }

    static propertyConfiguration= [:]
    static transients = [];
    //AUTO_GENERATED_CODE
}