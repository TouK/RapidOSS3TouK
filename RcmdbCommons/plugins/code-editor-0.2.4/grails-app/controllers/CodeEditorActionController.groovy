import groovy.xml.MarkupBuilder

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Mar 23, 2009
 * Time: 11:45:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class CodeEditorActionController
{
    def codeEditorActionService;    
    def index = {
        def actionToBeExecuted = params.actionToBeExecuted;
        if(actionToBeExecuted == null)
        {
            addError("code.editor.missing.parameter", ["actionToBeExecuted"]);
            render(text: errorsToXml(), contentType: "text/xml")
        }
        else
        {
            try
            {
                def res = codeEditorActionService."${actionToBeExecuted}"(params);
                render(contentType:'text/xml')
                {

                    if(res != null)
                    {
                        successful(message:res);
                    }
                    else
                    {
                        successful(message:"Action ${actionToBeExecuted} executed successfully.");
                    }
                }
            }
            catch(Throwable e)
            {
                addError("code.editor.execute.action.exception", [actionToBeExecuted, e.toString()]);
                render(text: errorsToXml(), contentType: "text/xml")
            }
        }
    }

    def getActionConfiguration = {
        Map configMap = codeEditorActionService.actionConfigurationMap
        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.Actions{
            configMap.each{String actionName, Map actionConfig->
                builder.Action(name:actionName, displayName:actionConfig.displayName)
                {
                    builder.Filters{
                        actionConfig.filters.each{String filterExp->
                            builder.Filter(expression:filterExp);
                        }
                    }
                }
            }
        }
        render(contentType:'text/xml', text:sw.toString());
    }
}