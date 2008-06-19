package com.ifountain.rcmdb.domain.generation

import model.Model
import model.ModelRelation
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.scaffolding.DefaultGrailsTemplateGenerator

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 1, 2008
 * Time: 4:52:00 PM
 * To change this template use File | Settings | File Templates.
 */
class ModelUtils {
    public static String OPERATIONS_CLASS_EXTENSION = "Operations"
    
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

    public static def isPropertyName(model,dependentModels)
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