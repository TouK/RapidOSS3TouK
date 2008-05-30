package application

import model.Model
import com.ifountain.rcmdb.domain.generation.ModelUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.commons.GrailsClass
import org.hibernate.cfg.ImprovedNamingStrategy
import com.ifountain.rcmdb.utils.ConfigurationImportExportUtils
import datasource.BaseDatasource
import connection.Connection
import script.CmdbScript
import model.PropertyShouldBeCleared
import model.ChangedModel
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.orm.hibernate.validation.UniqueConstraint
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import model.ModelProperty
import model.PropertyAction

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
    def index = { render(view:"application") }
    def reload = {
        def models = Model.findAllByResourcesWillBeGenerated(true);
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader().parent);
        gcl.setShouldRecompile(true);
        String baseDirectory = System.getProperty("base.dir")
        gcl.addClasspath(baseDirectory + "/grails-app/domain");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        def domainClassesWillBeGenerated = [];
        def domainClassesMap = [:];
        models.each {Model model ->
            try
            {
                def cls = gcl.loadClass(model.name);
                def domainClass = new DefaultGrailsDomainClass(cls);
                domainClassesWillBeGenerated += domainClass;
                domainClassesMap[model.name] = domainClass;
            }
            catch (t)
            {
                log.error("Exception occurred while creating controller, view and operations files of model ${model.name}", StackTraceUtils.deepSanitize(t));
            }
        }
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesWillBeGenerated as GrailsClass[], domainClassesMap);
        models.each {Model model ->
            def domainClass = domainClassesMap[model.name];
            if (domainClass)
            {
                ModelUtils.generateModelArtefacts(domainClass, baseDirectory);
                model.resourcesWillBeGenerated = false;
                model.save(flush: true);
            }
        }
        correctModelData(domainClassesMap);
        flash.message = "Reloading application."
        render(view: "application", controller: "application");
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }

    def correctModelData(Map domainClassMap)
    {
        PropertyAction.list().each
        {
            if(it.willBeDeleted) it.delete(flush:true);
        }
        def modelsWillBeChanged = ChangedModel.list();
        def distinctList = [:];
        modelsWillBeChanged.each
        {
            if(!distinctList.containsKey(it.modelName))
            {
                distinctList[it.modelName] = it;
            }
        }
        def changedProps = PropertyShouldBeCleared.list();
        changedProps.each{PropertyShouldBeCleared propShouldBeCleared->
            def modelName = propShouldBeCleared.modelName;
            if(distinctList.containsKey(modelName))
            {
                def propName = propShouldBeCleared.propertyName;
                def isRelation = propShouldBeCleared.isRelation;
                GrailsDomainClass modelDomainObject = domainClassMap[modelName];
                GrailsDomainClass currentDomainObject = grailsApplication.getDomainClass(modelName);
                if(modelDomainObject && currentDomainObject)
                {
                    if(modelDomainObject.getPropertyByName(propName) != null)
                    {
                        PropertyActionFactory.createPropertyAction (currentDomainObject, modelDomainObject, modelName, propName,isRelation);
                    }
                }
                propShouldBeCleared.delete();
            }
        }

        int batch = 1000;
        distinctList.each{modelName, ChangedModel changedModel->
            DefaultGrailsDomainClass currentDomainObject = grailsApplication.getDomainClass(modelName);
            if(currentDomainObject)
            {
                Class currentModelClass = currentDomainObject.clazz;
                if(changedModel.isDeleted)
                {
                    currentModelClass.metaClass.invokeStaticMethod (currentModelClass, "unindexAll", [] as Object[]);
                }
            }
            changedModel.delete();
        }

    }



    def exportConfiguration = {
        def exportDir = params.dir;
        if(!exportDir)
        {
            exportDir = "backup"
        }
        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(System.getProperty("base.dir") + "/grails-app/templates/xml", log);
        def configurationItems = [];
        configurationItems.addAll (BaseDatasource.list());
        configurationItems.addAll (Connection.list());
        configurationItems.addAll (Model.list());
        configurationItems.addAll (CmdbScript.list());
        impExpUtils.export (exportDir, configurationItems);
        flash.message = "Configuration data successfully exported to dir ${exportDir}."
        render(view: "application", controller: "application");
    }

    def importConfiguration = {
        def importDir = params.dir;
        if(!importDir)
        {
            importDir = "backup"
        }
        ConfigurationImportExportUtils impExpUtils = new ConfigurationImportExportUtils(System.getProperty("base.dir") + "/grails-app/templates/xml", log);
        impExpUtils.importConfiguration(importDir);
        redirect(action:reload,controller:'application');
    }
}

class PropertyActionFactory
{
    def static createPropertyAction(GrailsDomainClass currentDomainObject, DefaultGrailsDomainClass newDomainObject, String modelName, String propName, boolean isRelation)
    {

        boolean isUnique = false;
        boolean isNullable = false;
        boolean wasUnique = false;
        boolean wasNullable = false;
        def newPropType = newDomainObject.getPropertyByName(propName).type;
        def oldPropType = currentDomainObject.hasProperty(propName)?currentDomainObject.getPropertyByName(propName).type:null;
        def defaultValue = newDomainObject.newInstance()[propName];
        ConstrainedProperty oldProp = currentDomainObject.getConstrainedProperties()[propName];
        ConstrainedProperty newProp = newDomainObject.getConstrainedProperties()[propName]
        if(oldProp)
        {
            KeyConstraint oldConst = oldProp.getAppliedConstraint(KeyConstraint.KEY_CONSTRAINT);
            wasUnique = oldConst?oldConst.isKey():false;
            wasNullable = oldProp.isNullable();
        }
        KeyConstraint newConst = newProp.getAppliedConstraint(KeyConstraint.KEY_CONSTRAINT);
        isUnique = newConst?newConst.isKey():false;
        isNullable = newProp.isNullable();
        if(isRelation)
        {
            PropertyAction action = new PropertyAction(modelName:modelName, propName: propName, action:PropertyAction.CLEAR_RELATION);
            action.save();
        }
        else
        {
            if(wasUnique && !isUnique)
            {
                PropertyAction action = new PropertyAction(modelName:modelName, propName: propName, action:PropertyAction.DELETE_ALL_INSTANCES);
                action.save();
            }
            else if(oldPropType != null && wasNullable && !isNullable || oldPropType != null && newPropType != oldPropType || oldPropType == null && !isNullable)
            {
                PropertyAction action = new PropertyAction(modelName:modelName, propName: propName, action:PropertyAction.SET_DEFAULT_VALUE);
                action.save();
            }

        }

    }
}
