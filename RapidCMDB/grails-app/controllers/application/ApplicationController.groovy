package application

import model.ModelAction
import model.PropertyAction
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import com.ifountain.rcmdb.util.ModelUtils
import com.ifountain.rcmdb.domain.generation.ExistingDataAnalyzer
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException;

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 1:35:56 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationController {
    public static final String RESTART_APPLICATION = "restart.application"
    def sessionFactory;
    def searchableService;
    def index = {render(view: "application")}
    def reload = {
        def oldDomainClasses = [:]
        def baseDir = grailsApplication.config.toProperties()["rapidCMDB.base.dir"];
        def tempBaseDir = grailsApplication.config.toProperties()["rapidCMDB.temp.dir"];
        def currentModelDir = "${baseDir}/grails-app/domain";
        def currentModelDirFile = new File(currentModelDir);
        def tempModelDir = "${tempBaseDir}/grails-app/domain";
        def tempModelDirFile = new File(tempModelDir);
        Collection tempModelFileList = [];
        if (tempModelDirFile.exists()) {
            tempModelFileList = FileUtils.listFiles(tempModelDirFile, ["groovy"] as String[], false);
        }
        Collection currentModelFileList = FileUtils.listFiles(currentModelDirFile, ["groovy"] as String[], false);
        currentModelFileList.each {File modelFile ->
            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            GrailsDomainClass cls = grailsApplication.getDomainClass(modelName);
            if (cls)
            {
                oldDomainClasses[modelName] = cls;
            }
        }
        def domainClassesWillBeGenerated = [];
        def newDomainClassesMap = [:];
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader().parent);
        gcl.setShouldRecompile(true);
        gcl.addClasspath(tempModelDir);
        gcl.addClasspath(baseDir + "/src/groovy");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        boolean isNewClassesLoadedSuccessfully = true;
        tempModelFileList.each {File modelFile ->

            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            try
            {
                def cls = gcl.loadClass(modelName);
                def domainClass = new DefaultGrailsDomainClass(cls);
                domainClassesWillBeGenerated += domainClass;
                newDomainClassesMap[modelName] = domainClass;
            }
            catch (MultipleCompilationErrorsException e)
            {
                e.printStackTrace();
                isNewClassesLoadedSuccessfully = false;
                if (e.getErrorCollector().getErrorCount() > 0)
                {
                    def exception = e.getErrorCollector().getError(0).getCause();
                    if (exception instanceof SyntaxException)
                    {
                        if (exception.startLine > 0)
                        {
                            def syntaxError = modelFile.readLines()[exception.startLine - 1]
                            if (syntaxError != null && exception.startColumn > 0 && exception.startColumn <= syntaxError.length())
                            {
                                syntaxError = syntaxError.substring(exception.startColumn - 1);
                            }
                            def errors = [message(code: "default.property.name.invalid", args: [syntaxError, modelName])]
                            flash.errors = errors;
                            return;
                        }
                    }

                }
                flash.message = "Could not reload because following exception occurred ${e.toString()}."
                return;
            }

        }
        if (!isNewClassesLoadedSuccessfully) {
            if (params.targetURI) {
                redirect(uri: params.targetURI);
            }
            else {
                render(view: "application", controller: "application");
            }
            return;
        }
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesWillBeGenerated as GrailsClass[], newDomainClassesMap);
        domainClassesWillBeGenerated.each {GrailsDomainClass newDomainClass ->
            GrailsDomainClass oldDomainClass = oldDomainClasses[newDomainClass.name];
            if (oldDomainClass)
            {
                List actions = ExistingDataAnalyzer.createActions(oldDomainClass, newDomainClass);
                actions.each {
                    if (it instanceof ModelAction && it.action == ModelAction.GENERATE_RESOURCES)
                    {
                        ModelUtils.generateModelArtefacts(newDomainClass, baseDir, baseDir);
                    }
                    else
                    {
                        if (it instanceof PropertyAction)
                        {
                            PropertyAction.add(propName: it.propName, action: it.action, modelName: it.modelName);
                        }
                        else
                        {
                            ModelAction.add(action: it.action, modelName: it.modelName);
                        }
                    }
                }
            }
            else
            {
                ModelUtils.generateModelArtefacts(newDomainClass, baseDir, baseDir);
            }
        }

        oldDomainClasses.each {String oldClassName, GrailsDomainClass oldDomainClass ->
            if (!newDomainClassesMap.containsKey(oldClassName))
            {
                ModelUtils.deleteModelArtefacts(baseDir, oldClassName);
                new File(tempModelDir + "/" + oldClassName + ".groovy").delete()
                oldDomainClass.clazz.metaClass.invokeStaticMethod(oldDomainClass.clazz, "unindex", [] as Object[]);
            }
        }
        if (tempModelDirFile.exists()) {
            FileUtils.copyDirectory(tempModelDirFile, currentModelDirFile);
        }
        flash.message = "Reloading application."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }

    def reloadControllers = {
        org.codehaus.groovy.grails.plugins.PluginManagerHolder.getPluginManager().getGrailsPlugin("controllers").checkForChanges()
        flash.message = "Controllers reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }

    def reloadViews = {
        GroovyPagesTemplateEngine.pageCache.clear();
        flash.message = "Views reloaded successfully."
        if (params.targetURI) {
            redirect(uri: params.targetURI);
        }
        else {
            render(view: "application", controller: "application");
        }
    }
}
