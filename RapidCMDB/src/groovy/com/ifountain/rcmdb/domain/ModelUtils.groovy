package com.ifountain.rcmdb.domain
import org.apache.commons.io.FileUtils
import model.Model
import model.ModelRelation
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 1, 2008
 * Time: 4:52:00 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelUtils {
    public static def deleteModelArtefacts(String baseDir, String modelName)
    {
        def modelFile = new File("$baseDir/grails-app/domain/${modelName}.groovy");
        def modelControllerFile = new File("$baseDir/grails-app/controllers/${modelName}Controller.groovy");
        def modelViewsDir = new File("$baseDir/grails-app/views/${modelName}");
        modelFile.delete();
        modelControllerFile.delete();
        FileUtils.deleteDirectory (modelViewsDir);

    }

    public static def generateModelArtefacts(GrailsDomainClass domainClass)
    {
			def generator = new DefaultGrailsTemplateGenerator();
			generator.overwrite = true;
            generator.generateViews(domainClass,".");
            def viewsDir = new File("${System.getProperty ("base.dir")}/grails-app/views/${domainClass.propertyName}")
            DefaultGrailsTemplateGenerator.LOG.info("Generating create view for domain class [${domainClass.fullName}]")
            def addToFile = new File("${viewsDir}/addTo.gsp")
            addToFile.withWriter { w ->
                generator.generateView(domainClass, "addTo", w)
            }
            DefaultGrailsTemplateGenerator.LOG.info("AddTo view generated at ${addToFile.absolutePath}")
            generator.generateController(domainClass,".")
    }
    public static def getDependeeModels(model)
    {
        def dependeeModels = [:]
        def childModels = Model.findAllByParentModel(model);
        def modelRelations = ModelRelation.findAllByFirstModel(model);
        def reverseModelRelations = ModelRelation.findAllBySecondModel(model);
        childModels.each
        {
            dependeeModels[it.name] = it;
        }
        modelRelations.each
        {
            dependeeModels[it.secondModel.name] = it.secondModel;
        }
        reverseModelRelations.each
        {
            dependeeModels[it.firstModel.name] = it.firstModel;
        }
        return dependeeModels
    }
    
    public static def getAllDependentModels(model)
    {
        def dependentModels = [:]
        getAllDependentModels(model, dependentModels);
        return dependentModels;
    }
    public static def getAllDependentModels(model,dependentModels)
    {
        if(dependentModels.containsKey(model.name)) return;
        dependentModels[model.name] = model;
        def dependeeeModels = getDependeeModels(model);
        if(model.parentModel)
        {
            getAllDependentModels(model.parentModel, dependentModels);
        }
        dependeeeModels.each{key, value->
            getAllDependentModels(value, dependentModels);
        }
    }
}