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
        def modelsWillBeChanged = ChangedModel.list();
        def distinctList = [:];
        modelsWillBeChanged.each
        {
            if(!distinctList.containsKey(it.modelName))
            {
                distinctList[it.modelName] = it;
            }
        }
        def changedModelProperties = [:]
        def changedProps = PropertyShouldBeCleared.list();
        println changedProps
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
                    def modelProps = changedModelProperties[modelName];
                    if(modelProps == null)
                    {
                        modelProps = [:];
                        changedModelProperties[modelName] = modelProps;
                    }
                    println "MODEL PROP ${propName}"
                    if(!modelProps.containsKey(propName))
                    {
                        println "DISTINCT PROP ${propName}"
                        if(modelDomainObject.getPropertyByName(propName) != null)
                        {
                            println "ADDED  ${propName}"

                            modelProps.put(propName, new PropertySummary(currentDomainObject, modelDomainObject, modelName, propName,isRelation));
                        }
                        else
                        {
                            println "COULD NOT ADDED  ${propName}"
                        }
                    }
                }
                propShouldBeCleared.delete();
            }
        }

        println "PROPS:${changedModelProperties}"
        println "MODELS:${distinctList}"
        int batch = 1000;
        distinctList.each{modelName, ChangedModel changedModel->

            def modelProps = changedModelProperties[modelName];

            boolean modelShouldBeRemoved = modelShouldBeRemoved(changedModel, modelProps);

            DefaultGrailsDomainClass currentDomainObject = grailsApplication.getDomainClass(modelName);
            if(currentDomainObject)
            {
                Class currentModelClass = currentDomainObject.clazz;
                int index = 0;
                while(true)
                {
                    def res = currentModelClass.metaClass.invokeStaticMethod (currentModelClass, "search", ["id:[0 TO *]",[max:batch, offset:index]] as Object[]);
                    res.results.each{modelInstance->
                        if(!modelShouldBeRemoved)
                        {
                            println "REINDEXING"
                            modelProps.each{changedProp, PropertySummary changedPropSummary->
                                changedPropSummary.applyChange (modelInstance);
                            }
                            modelInstance.reindex();
                            println "REINDEXED"
                        }
                        else
                        {
                            println "UNINDEXING"
                            modelInstance.unindex();
                            println "UNINDEXED"
                        }

                    }
                    index += batch;
                    if(res.total < index)
                    {
                        break;
                    }
                }

            }
            changedModel.delete();
        }
    }

    def modelShouldBeRemoved(changedModel, modelProps)
    {
        if(changedModel.isDeleted) return true;
        boolean shouldBeRemoved = false;
        modelProps.each{changedProp, PropertySummary changedPropSummary->
            shouldBeRemoved = changedPropSummary.willBeRemoved();
            if(shouldBeRemoved)
            {
                return;
            }
        }
        return shouldBeRemoved;
    }

    def deneme = {
        ImprovedNamingStrategy st = new ImprovedNamingStrategy();
        println "Collection tABLE: " + st.collectionTableName(null, st.tableName("NewAuthor"), null, null, st.tableName("NewBook"))
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

class PropertySummary
{
    String modelName;
    String propName;
    boolean isRelation;
    boolean isUnique;
    boolean isNullable;
    boolean wasUnique;
    boolean wasNullable;
    Class propType;
    Object defaultValue;
    public PropertySummary(GrailsDomainClass currentDomainObject, DefaultGrailsDomainClass newDomainObject, String modelName, String propName, boolean isRelation)
    {
        this.modelName = modelName;
        this.isRelation = isRelation;
        this.propName = propName;
        this.propType = newDomainObject.getPropertyByName(propName).type;
        this.defaultValue = newDomainObject.newInstance()[propName];
        ConstrainedProperty oldProp = currentDomainObject.getConstrainedProperties()[propName];
        ConstrainedProperty newProp = newDomainObject.getConstrainedProperties()[propName]
        KeyConstraint oldConst = oldProp.getAppliedConstraint(KeyConstraint.KEY_CONSTRAINT);
        KeyConstraint newConst = newProp.getAppliedConstraint(KeyConstraint.KEY_CONSTRAINT);
        wasUnique = oldConst?oldConst.isKey():false;
        wasNullable = oldProp.isNullable();
        isUnique = newConst?newConst.isKey():false;
        isNullable = newProp.isNullable();
    }

    def willBeRemoved()
    {
        return wasUnique && !isUnique;    
    }

    def applyChange(modelInstance)
    {
        if(isRelation)
        {
            if(modelInstance[propName] instanceof Collection)
            {
                modelInstance[propName].clear();
            }
            else
            {
                modelInstance[propName] = null;
            }
        }
        else
        {
            if(wasNullable && !isNullable)
            {
                modelInstance[propName] = getDefaultValue();    
            }
        }
    }

    def getDefaultValue()
    {
        if(defaultValue) return defaultValue;
        if(propType instanceof String)
        {
            return "RCMDB_Default"
        }
        else if(propType instanceof Number)
        {
            return "-1111";
        }
        else if(propType instanceof Date)
        {
            return new Date(0);
        }
        return null;
    }

}
      /*
currentDomainObject.getConstrainedProperties().each{name, prop->
    println prop.getProperty(KeyConstraint.KEY_CONSTRAINT);
}    */