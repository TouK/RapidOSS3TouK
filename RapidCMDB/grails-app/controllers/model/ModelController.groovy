package model;
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.domain.generation.ModelUtils
import org.hibernate.cfg.ImprovedNamingStrategy
import org.codehaus.groovy.grails.commons.ApplicationAttributes
import org.springframework.orm.hibernate3.LocalSessionFactoryBean
import org.hibernate.cfg.Configuration
import org.hibernate.mapping.Table
import org.hibernate.mapping.ForeignKey

class ModelController {
    def static String MODEL_DOESNOT_EXIST = "Model does not exist";
    def scaffold = model.Model;

    def save = {
        def model = new Model(params)
        if (!model.hasErrors() && model.save()) {
            flash.message = "Model ${model.id} created"
            redirect(action: show, id: model.id)
        }
        else {
            render(view: 'create', model: [model: model])
        }
    }

    def update = {
        def model = Model.get(params.id)
        if (model) {
            //validating parentModel, should be implemented in validator
            if (model.parentModel && (params.parentModel.id == null || params.parentModel.id == "null")) {
                def keyMappingReference = false;
                def invalidDatasource = null;
                def invalidKeyMapping = null;
                def invalidProperty = null;
                model.datasources.each {ds ->
                    ds.keyMappings.each {keyMapping ->
                        if (keyMapping.property.model == model.parentModel) {
                            keyMappingReference = true;
                            invalidDatasource = ds;
                            invalidKeyMapping = keyMapping;
                            return;

                        }
                    }
                }
                if (keyMappingReference) {
                    def errors = [message(code: "model.parent.keymapping.reference", args: [invalidKeyMapping, invalidDatasource, model])]
                    flash.errors = errors;
                    render(view: 'edit', model: [model: model])
                    return;
                }

                def modelDatasourceReference = false;
                model.modelProperties.each {p ->
                    if (p.propertyDatasource?.model == model.parentModel) {
                        modelDatasourceReference = true;
                        invalidDatasource = p.propertyDatasource;
                        invalidProperty = p;
                        return;
                    }
                }
                if (modelDatasourceReference) {
                    def errors = [message(code: "model.parent.modelDatasource.reference", args: [invalidProperty, model, invalidDatasource])]
                    flash.errors = errors;
                    render(view: 'edit', model: [model: model])
                    return;
                }
            }
            model.properties = params
            if (!model.hasErrors() && model.save()) {
                flash.message = "Model ${params.id} updated"
                redirect(action: show, id: model.id)
            }
            else {
                render(view: 'edit', model: [model: model])
            }
        }
        else {
            flash.message = "Model not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def reloadOperations = {
        def model = Model.get(params.id)
        if (model) {
            def modelClass = grailsApplication.getClassForName(model.name)
            if (modelClass)
            {
                try
                {

                    modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                    flash.message = "Model reloaded"
                    redirect(action: show, id: model.id)
                } catch (t)
                {
                    flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                    redirect(action: show, id: model.id)
                }
            }
            else
            {
                flash.message = "Model currently not loaded by application. You should reload application."
                redirect(action: show, id: model.id)
            }

        }
        else {
            flash.message = MODEL_DOESNOT_EXIST
            redirect(action: list, controller: 'model')
        }
    }

    def show = {
        def modelPropertySortProp = params.modelPropertySortProp != null ? params.modelPropertySortProp : "name"
        def modelPropertySortOrder = params.modelPropertySortOrder != null ? params.modelPropertySortOrder : "asc"
        def modelDatasourceSortProp = params.modelDatasourceSortProp != null ? params.modelDatasourceSortProp : "datasource"
        def modelDatasourceSortOrder = params.modelDatasourceSortOrder != null ? params.modelDatasourceSortOrder : "asc"
        def modelRelationSortProp = params.modelRelationSortProp != null ? params.modelRelationSortProp : "name"
        def modelRelationSortOrder = params.modelRelationSortOrder != null ? params.modelRelationSortOrder : "asc"
        def modelOpertionSortProp = params.modelOpertionSortProp != null ? params.modelOpertionSortProp : "name"
        def modelOpertionSortOrder = params.modelOpertionSortOrder != null ? params.modelOpertionSortOrder : "asc"

        def model = Model.get(params.id)
        if (!model) {
            flash.message = MODEL_DOESNOT_EXIST
            redirect(action: list)
        }
        else {return [model: model,
                modelPropertySortProp: modelPropertySortProp, modelPropertySortOrder: modelPropertySortOrder,
                modelDatasourceSortProp: modelDatasourceSortProp, modelDatasourceSortOrder: modelDatasourceSortOrder,
                modelRelationSortProp: modelRelationSortProp, modelRelationSortOrder: modelRelationSortOrder,
                modelOpertionSortProp: modelOpertionSortProp, modelOpertionSortOrder: modelOpertionSortOrder
        ]}
    }

    def delete = {
        if (params.id)
        {
            def model = Model.get(params.id)
            if (model) {
                def oldDependentModels = getOldDependentModels(model)
                try {
                    model.delete(flush: true)
                }
                catch (e)
                {
                    def errors = [message(code: "default.couldnot.delete", args: [Model.class.getName(), model])]
                    flash.errors = errors;
                    redirect(action: show, controller: 'model', id: model?.id)
                    return;

                }
                ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model.name);
                try
                {
                    oldDependentModels.each {key, value ->
                        value.refresh();
                        ModelGenerator.getInstance().generateModel(value);
                    }
                    def oldModel = GeneratedModel.findByModelName(model.name);
                    handleGenerationOfModels(oldDependentModels);
                    if (oldModel) {
                        GeneratedModelRelation.findAllByFirstModel(oldModel)*.delete(flush:true);
                        GeneratedModelRelation.findAllBySecondModel(oldModel)*.delete(flush:true);
                        oldModel.refresh();
                        oldModel.delete(flush:true);
                    }
                    flash.message = "Model ${params.id} deleted"
                    redirect(action: list, controller: 'model');
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    flash.message = "Model deleted but and unexpected exception occured while generating dependent models. Reason:${e.getMessage()}";
                    redirect(action: list, controller: 'model')
                }

            }
            else {
                flash.message = MODEL_DOESNOT_EXIST
                redirect(action: list, controller: 'model')
            }
        }
        else
        {
            redirect(action: list, controller: 'model')
        }
    }


    def generate = {
        if (params.id)
        {
            def model = Model.get(params.id);
            if (model)
            {
                try
                {
                    ModelGenerator.getInstance().generateModel(model);
                    def oldDependentModels = getOldDependentModels(model);
                    def newDependentModels = ModelUtils.getAllDependentModels(model);
                    def modelsToBeGenerated = [:]
                    modelsToBeGenerated.putAll(newDependentModels);
                    newDependentModels.each {modelName, newDependentModel ->
                        oldDependentModels.remove(modelName);
                    }
                    oldDependentModels.each {String modelName, Model oldDepModel ->
                        ModelGenerator.getInstance().generateModel(oldDepModel);
                    }
                    modelsToBeGenerated.putAll(oldDependentModels);
                    handleGenerationOfModels(modelsToBeGenerated);
                    flash.message = "Model $model.name genarated successfully"
                    redirect(action: show, controller: 'model', id: model?.id)
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    log.error("Exception occurred while generating model ${model.name}", e);
                    flash.message = e.getMessage();
                    redirect(action: show, controller: 'model', id: model?.id)
                }
            }
            else
            {
                flash.message = MODEL_DOESNOT_EXIST
                redirect(action: list, controller: 'model')
            }
        }
        else
        {
            redirect(action: list, controller: 'model')
        }
    }

    def handleGenerationOfModels(modelsToBeGenerated) {
        def newGeneratedModels = [:];
        modelsToBeGenerated.each {modelName, newDependentModel ->
            GeneratedModel newGeneratedModel = createGeneratedModelInfo(newDependentModel);
            newGeneratedModels.put(newGeneratedModel.modelName, newGeneratedModel);
        }
        newGeneratedModels.each {modelName, generatedModel ->
            createGeneratedModelRelationInfo(generatedModel, newGeneratedModels);
        }
    }

    def getOldDependentModels(Model model)
    {
        Map dependentModels = [:]
        GeneratedModel modelChangeLog = GeneratedModel.findByModelName(model.name);
        if (modelChangeLog)
        {
            def childModels = GeneratedModel.findAllByParentModelName(model.name);
            childModels.each {GeneratedModel oldChildModel ->
                addOldDependentModel(oldChildModel.modelName, dependentModels);
            }
            def relatedModels = GeneratedModelRelation.findAllByFirstModel(modelChangeLog);
            relatedModels.each {GeneratedModelRelation relation ->
                addOldDependentModel(relation.secondModel.modelName, dependentModels);
            }
            relatedModels = GeneratedModelRelation.findAllBySecondModel(modelChangeLog);
            relatedModels.each {GeneratedModelRelation relation ->
                addOldDependentModel(relation.firstModel.modelName, dependentModels);
            }
        }
        return dependentModels;
    }

    def addOldDependentModel(String name, Map dependentModels)
    {
        if (!dependentModels.containsKey(name))
        {
            def depModel = Model.findByName(name);
            if (depModel)
            {
                dependentModels[name] = depModel;
            }
        }
    }

    def isPropertyUnique(ModelProperty prop)
    {
        boolean isKey = false;
        if (prop.propertyDatasource && prop.propertyDatasource.master)
        {

            prop.propertyDatasource.keyMappings.each {ModelDatasourceKeyMapping mapping ->
                if (mapping.property.id == prop.id)
                {
                    isKey = true;
                }
            }
        }
        return isKey;
    }

    def isFederated(ModelProperty prop)
    {
        if (prop.propertyDatasource)
        {
            return !prop.propertyDatasource.master;
        }
        return true;
    }

    def createGeneratedModelInfo(Model model)
    {
        def oldModel = GeneratedModel.findByModelName(model.name);
        if (oldModel) {
            GeneratedModelRelation.findAllByFirstModel(oldModel)*.delete();
            GeneratedModelRelation.findAllBySecondModel(oldModel)*.delete();
            oldModel.delete();
        }
        def generatedModel = new GeneratedModel(modelName: model.name);
        def masterDatasource = ModelDatasource.findByModelAndMaster(model, true);
        if (masterDatasource) {
            generatedModel.idSize = masterDatasource.keyMappings.size();
        }
        else {
            generatedModel.idSize = 0;
        }

        if (model.parentModel)
        {
            generatedModel.parentModelName = model.parentModel.name;
        }
        generatedModel = generatedModel.save();
        model.modelProperties.each {ModelProperty prop ->
            def isFederated = isFederated(prop);
            def isUnique = isPropertyUnique(prop)
            def generatedModelProperty = new GeneratedModelProperty(model: generatedModel, propId: prop.id, propName: prop.name, isBlank: prop.blank, isFederated: isFederated, isUnique: isUnique, type: prop.type);
            generatedModelProperty.save();
        }
        return generatedModel;
    }

    def createGeneratedModelRelationInfo(GeneratedModel gModel, Map newGeneratedModels) {
        def model = Model.findByName(gModel.modelName);
        model.fromRelations.each {ModelRelation relation ->
            def generatedModelRelation = new GeneratedModelRelation(firstModel: gModel, secondModel: newGeneratedModels.get(relation.secondModel.name),
                    firstName: relation.firstName, secondName: relation.secondName,
                    firstCardinality: relation.firstCardinality, secondCardinality: relation.secondCardinality,
                    isReverse: false, relationId: relation.id);
            generatedModelRelation.save();
        }
    }

}
