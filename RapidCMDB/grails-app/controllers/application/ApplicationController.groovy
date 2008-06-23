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
        tempModelFileList.each {File modelFile ->
            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            def cls = gcl.loadClass(modelName);
            def domainClass = new DefaultGrailsDomainClass(cls);
            domainClassesWillBeGenerated += domainClass;
            newDomainClassesMap[modelName] = domainClass;
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
                        if(it instanceof PropertyAction)
                        {
                            PropertyAction.add(propName:it.propName, action:it.action, modelName:it.modelName);
                        }
                        else
                        {
                            ModelAction.add(action:it.action, modelName:it.modelName);
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
                new File(tempModelDir + "/" + name + ".groovy")
                ModelUtils.getGeneratedModelFile(oldClassName).delete();
                oldDomainClass.clazz.metaClass.invokeStaticMethod(oldDomainClass.clazz, "unindex", [] as Object[]);
            }
        }
        if (tempModelDirFile.exists()) {
            FileUtils.copyDirectory(tempModelDirFile, currentModelDirFile);
        }
        flash.message = "Reloading application."
        render(view: "application", controller: "application");
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }

    


//    def exportConfiguration = {
//        def exportDir = params.dir;
//        if (!exportDir)
//        {
//            exportDir = "backup"
//        }
//        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(System.getProperty("base.dir") + "/grails-app/templates/xml", log);
//        def configurationItems = [];
//        configurationItems.addAll(BaseDatasource.list());
//        configurationItems.addAll(DatasourceName.list());
//        configurationItems.addAll(Connection.list());
//        configurationItems.addAll(Model.list());
//        configurationItems.addAll(CmdbScript.list());
//        impExpUtils.export(exportDir, configurationItems);
//        flash.message = "Configuration data successfully exported to dir ${exportDir}."
//        render(view: "application", controller: "application");
//    }
//
//    def importConfiguration = {
//        def importDir = params.dir;
//        if (!importDir)
//        {
//            importDir = "backup"
//        }
//        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(System.getProperty("base.dir") + "/grails-app/templates/xml", log);
//        impExpUtils.importConfiguration(importDir);
//        redirect(action: reload, controller: 'application');
//    }
}
