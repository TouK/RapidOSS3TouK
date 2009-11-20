import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import script.CmdbScript
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.grails.codeeditor.utils.CodeEditorFileUtils;
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Mar 23, 2009
 * Time: 11:48:39 PM
 * To change this template use File | Settings | File Templates.
 */

public class CodeEditorActionService
{

    boolean transactional = false
    def actionConfigurationMap = [
            reloadControllers:[displayName:"Reload Controllers", filters:[".*controllers/.*\\.groovy"]],
            reloadViews:[displayName:"Reload Views", filters:[".*\\.gsp"]],
            runScript:[displayName:"Run Script", filters:["scripts/.*\\.groovy"]],
            reloadScript:[displayName:"Reload Script", filters:["scripts/.*\\.groovy"]],
            createScript:[displayName:"Create Script", filters:["scripts/.*\\.groovy"]],
            reloadOperation:[displayName:"Reload Operation", filters:["operations/.*Operations\\.groovy"]]
    ]

    def reloadControllers(params){
        PluginManagerHolder.getPluginManager().getGrailsPlugin("controllers").checkForChanges()
        return "Controllers reloaded successfully";
    }

    def reloadOperation(params){
        Class domainClass = getDomainClass(params)
        domainClass.reloadOperations();
        return "Operation for ${domainClass.name} reloaded successfully";
    }

    def reloadScript(params){
    	def cmdbScript = getScript(params);
        if(cmdbScript == null)
        {
        	throw new Exception("Script does not exist");
        }
        else
        {
        	cmdbScript.reload();
        }
        return "Script ${cmdbScript.name} reloaded successfully";
    }

    def runScript(params){
    	reloadScript(params);
        def cmdbScript = getScript(params);
        if(cmdbScript == null)
        {
        	throw new Exception("Script does not exist");
        }
        else
        {
        	return CmdbScript.runScript(cmdbScript, params);
        }
    }

    def createScript(params){
    	def scriptName = getScriptName(params);
        CmdbScript.addScript(name:scriptName);
        return "Script ${scriptName} created successfully";
    }
    def getScriptName(params)
    {
    	def scriptFile = CodeEditorFileUtils.getFileRelativeToBaseDir(params.fileName);
    	def scriptName = scriptFile.name;
    	scriptName = scriptName.substring(0, scriptName.indexOf(".groovy"));
    	return scriptName;
    }

    private Class getDomainClass(params)
    {
    	def operationFile = CodeEditorFileUtils.getFileRelativeToBaseDir(params.fileName);
    	def domainName = CodeEditorFileUtils.getRelativeFilePath(CodeEditorFileUtils.getFileRelativeToBaseDir("RapidSuite/operations"), operationFile);
    	domainName = domainName.replaceAll("\\\\","/")
    	domainName = domainName.substring(1, domainName.indexOf(".groovy"));
        domainName = domainName.replaceAll("/", ".")
        domainName = domainName.substring(0, domainName.length()- "Operations".length())
        def domainClass = ApplicationHolder.application.getDomainClass(domainName);
        
    	return domainClass?.clazz;
    }
    def getScript(params)
    {
    	def scriptName = getScriptName(params);
        return CmdbScript.get(name:scriptName);
    }

    def reloadViews(params){
        GroovyPagesTemplateEngine.pageCache.clear();
    }
}