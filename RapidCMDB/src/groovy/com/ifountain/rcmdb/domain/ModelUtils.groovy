package com.ifountain.rcmdb.domain
import org.apache.commons.io.FileUtils;
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
    public static def getDependeeModels(model)
    {
        def dependeeModels = [:]
        model.fromRelations.each
        {
            dependeeModels[it.secondModel.name] = it.secondModel ;
        }
        model.toRelations.each
        {
            dependeeModels[it.firstModel.name] = it.firstModel ;
        }
        return dependeeModels
    }
    public static def getDependentModels(model)
    {
        def dependentModels = [:]
        getDependentModels(model, dependentModels);
        return dependentModels;
    }
    public static def getDependentModels(model,dependentModels)
    {
        if(dependentModels.containsKey(model.name)) return;
        dependentModels[model.name] = model;
        if(model.parentModel)
        {
            getDependentModels(model.parentModel, dependentModels);
        }
        model.fromRelations.each
        {
            getDependentModels(it.secondModel, dependentModels);
        }
        model.toRelations.each
        {
            getDependentModels(it.firstModel, dependentModels);
        }
    }
}