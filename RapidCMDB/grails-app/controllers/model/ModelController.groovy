package model;
import com.ifountain.rcmdb.domain.ModelGenerator
import com.ifountain.rcmdb.domain.ModelUtils
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
                    def tableConstraints = createTableConstraintsMap();
                    ImprovedNamingStrategy st = new ImprovedNamingStrategy();
                    def oldModel = GeneratedModel.findByModelName(model.name);
                    if (oldModel) {
                        def dropTableSqls = [];
                        addDropTableSqls(oldModel, st, tableConstraints, dropTableSqls);
                        dropTableSqls.each {
                            def sqlStm = new ModelModificationSqls(sqlStatement: it);
                            sqlStm.save();
                        }
                    }
                    handleGenerationOfModels(oldDependentModels, tableConstraints, st);
                    //                    if(oldModel){
                    //                        oldModel.delete(flush:true);
                    //                    }
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
                    def tableConstraints = createTableConstraintsMap();
                    ImprovedNamingStrategy st = new ImprovedNamingStrategy();
                    handleGenerationOfModels(modelsToBeGenerated, tableConstraints, st);
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

    def handleGenerationOfModels(modelsToBeGenerated, tableConstraints, st) {
        def tablesToBeDropped = getTablesToBeDropped(modelsToBeGenerated);
        def sqlWillBeExecuted = [];
        tablesToBeDropped.each {key, value ->
            def oldModel = GeneratedModel.findByModelName(key);
            if (oldModel) {
                addDropTableSqls(oldModel, st, tableConstraints, sqlWillBeExecuted);
            }
        }
        def newGeneratedModels = [:];

        modelsToBeGenerated.each {modelName, newDependentModel ->
            def isDropped = false;
            String tablename = st.tableName(modelName);
            def generatedModel = GeneratedModel.findByModelName(modelName);
            if (generatedModel)
            {
                def generatedProperties = [:]
                generatedModel.modelProperties.each {GeneratedModelProperty prop ->
                    generatedProperties[prop.propId] = prop;
                }
                def masterDatasource = ModelDatasource.findByModelAndMaster(newDependentModel, true);
                def currentIdSize = 0;
                if (masterDatasource) {
                    currentIdSize = masterDatasource.keyMappings.size();
                }
                if (generatedModel.idSize != currentIdSize) {
                    def tablesInHierarchy = [:]
                    getModelHierarchyForDrop(modelName, tablesInHierarchy);
                    tablesInHierarchy.each {key, value ->
                        def oldModel = GeneratedModel.findByModelName(key);
                        if (oldModel) {
                            addDropTableSqls(oldModel, st, tableConstraints, sqlWillBeExecuted);
                        }
                    }
                    tablesToBeDropped.putAll(tablesInHierarchy);
                }
                if (!tablesToBeDropped.containsKey(modelName)) {
                    newDependentModel.modelProperties.each {ModelProperty prop ->
                        GeneratedModelProperty generatedPropertyValue = generatedProperties.remove(prop.id);
                        def isFederated = isFederated(prop);
                        def isUnique = isPropertyUnique(prop);
                        def isBlank = prop.blank;
                        if (generatedPropertyValue)
                        {
                            def columnname = st.columnName(generatedPropertyValue.propName)
                            if (!isUnique && generatedPropertyValue.isUnique || isUnique && !generatedPropertyValue.isUnique)
                            {
                                def tablesInHierarchy = [:]
                                getModelHierarchyForDrop(modelName, tablesInHierarchy);
                                tablesInHierarchy.each {key, value ->
                                    def oldModel = GeneratedModel.findByModelName(key);
                                    if (oldModel) {
                                        addDropTableSqls(oldModel, st, tableConstraints, sqlWillBeExecuted);
                                    }
                                }
                                tablesToBeDropped.putAll(tablesInHierarchy);
                                return;
                            }
                            else if (isFederated && !generatedPropertyValue.isFederated)
                            {
                                sqlWillBeExecuted += "ALTER TABLE ${tablename} DROP COLUMN ${columnname}"
                                println "delete";
                            }
                            else if (!isFederated && generatedPropertyValue.isFederated) {
                                addNewNonBlankColumn(tablename, st.columnName(prop.name), prop.type, sqlWillBeExecuted);
                            }
                            else {
                                if (generatedPropertyValue.propName != prop.name) {
                                    def newColumnName = st.columnName(prop.name);
                                    sqlWillBeExecuted += "ALTER TABLE ${tablename} ALTER COLUMN ${columnname} RENAME TO ${newColumnName}"
                                    columnname = newColumnName;
                                }
                                if (generatedPropertyValue.type != prop.type) {
                                    sqlWillBeExecuted += "ALTER TABLE ${tablename} DROP COLUMN ${columnname}"
                                    def databaseType;
                                    def defaultValue;
                                    if (prop.type == ModelProperty.stringType) {
                                        databaseType = "VARCHAR (255)"
                                        defaultValue = "'RCMDB_Default'";
                                    }
                                    else if (prop.type == ModelProperty.numberType) {
                                        databaseType = "BIGINT"
                                        defaultValue = "-1111";
                                    }
                                    else {
                                        databaseType = "TIMESTAMP"
                                        defaultValue = "1970-01-01 00:00:00.000000000";
                                    }

                                    if (isBlank) {
                                        sqlWillBeExecuted += "ALTER TABLE ${tablename} ADD COLUMN ${columnname} ${databaseType} NULL"
                                    }
                                    else {
                                        sqlWillBeExecuted += "ALTER TABLE ${tablename} ADD COLUMN ${columnname} ${databaseType} NULL"
                                        sqlWillBeExecuted += "UPDATE ${tablename} SET ${columnname}=${defaultValue} WHERE ${columnname} IS NULL";
                                        sqlWillBeExecuted += "ALTER TABLE ${tablename} ALTER COLUMN ${columnname} SET NOT NULL"
                                    }
                                }
                                else if (isBlank && !generatedPropertyValue.isBlank)
                                {
                                    sqlWillBeExecuted += "ALTER TABLE ${tablename} ALTER COLUMN ${columnname} SET NULL"
                                }
                                else if (!isBlank && generatedPropertyValue.isBlank)
                                {
                                    def defaultValue;
                                    if (prop.type == ModelProperty.stringType) {
                                        defaultValue = "'RCMDB_Default'";
                                    }
                                    else if (prop.type == ModelProperty.numberType) {
                                        defaultValue = "-1111";
                                    }
                                    else {
                                        defaultValue = "1970-01-01 00:00:00.000000000";
                                    }
                                    sqlWillBeExecuted += "UPDATE ${tablename} SET ${columnname}=${defaultValue} WHERE ${columnname} IS NULL";
                                    sqlWillBeExecuted += "ALTER TABLE ${tablename} ALTER COLUMN ${columnname} SET NOT NULL"
                                }
                            }
                        }
                        else {
                            def columnname = st.columnName(prop.name)
                            if (isUnique)
                            {
                                def tablesInHierarchy = [:]
                                getModelHierarchyForDrop(modelName, tablesInHierarchy);
                                tablesInHierarchy.each {key, value ->
                                    def oldModel = GeneratedModel.findByModelName(key);
                                    if (oldModel) {
                                        addDropTableSqls(oldModel, st, tableConstraints, sqlWillBeExecuted);
                                    }
                                }
                                tablesToBeDropped.putAll(tablesInHierarchy);
                                return;
                            }
                            else if (!isBlank)
                            {
                                addNewNonBlankColumn(tablename, columnname, prop.type, sqlWillBeExecuted);
                            }
                        }
                    }
                }
                if (!tablesToBeDropped.containsKey(modelName)) {
                    generatedProperties.each {propId, GeneratedModelProperty gProp ->
                        def columnname = st.columnName(gProp.propName)
                        sqlWillBeExecuted += "ALTER TABLE ${tablename} DROP COLUMN ${columnname}"
                    }
                    def oldFromRelations = [:]
                    generatedModel.fromRelations.each {GeneratedModelRelation oldRelation ->
                        oldFromRelations.put(oldRelation.relationId, oldRelation)
                    }
                    newDependentModel.fromRelations.each {ModelRelation relation ->
                        GeneratedModelRelation oldRelation = oldFromRelations.remove(relation.id);
                        if (oldRelation) {
                            if (oldRelation.firstCardinality != relation.firstCardinality ||
                                    oldRelation.secondCardinality != relation.secondCardinality ||
                                    oldRelation.secondModel.modelName != relation.secondModel.name) {
                                //drop relation
                                if (oldRelation.firstCardinality == ModelRelation.MANY && oldRelation.secondCardinality == ModelRelation.MANY) {
                                    addDropManyToManyRelationSql(oldRelation, st, sqlWillBeExecuted);
                                }
                                else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.ONE) {
                                    addDropOneToOneRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                                }
                                else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.MANY) {
                                    addDropOneToManyRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                                }
                                else if (oldRelation.firstCardinality == ModelRelation.MANY && oldRelation.secondCardinality == ModelRelation.ONE) {
                                    addDropManyToOneRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                                }
                            }
                            else {
                                if (oldRelation.firstName != relation.firstName || oldRelation.secondName != relation.secondName) {
                                    if (oldRelation.firstCardinality == ModelRelation.MANY && oldRelation.secondCardinality == ModelRelation.MANY) {
                                        def relationTableName = st.collectionTableName(null, st.tableName(oldRelation.firstModel.modelName), null, null, st.tableName(oldRelation.secondModel.modelName))
                                        if (oldRelation.firstName != relation.firstName) {
                                            def firstColumnName = st.columnName(oldRelation.firstName) + "_id";
                                            def newFirstColumnName = st.columnName(relation.firstName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${relationTableName} ALTER COLUMN ${firstColumnName} RENAME TO ${newFirstColumnName}"
                                        }
                                        if (oldRelation.secondName != relation.secondName) {
                                            def secondColumnName = st.columnName(oldRelation.secondName) + "_id";
                                            def newSecondColumnName = st.columnName(relation.secondName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${relationTableName} ALTER COLUMN ${secondColumnName} RENAME TO ${newSecondColumnName}"
                                        }
                                    }
                                    else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.ONE) {
                                        if (oldRelation.firstName != relation.firstName) {
                                            def firstTableName = st.tableName(oldRelation.firstModel.modelName);
                                            def firstColumnName = st.columnName(oldRelation.firstName) + "_id";
                                            def newFirstColumnName = st.columnName(relation.firstName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${firstTableName} ALTER COLUMN ${firstColumnName} RENAME TO ${newFirstColumnName}"
                                        }
                                        if (oldRelation.secondName != relation.secondName) {
                                            def secondTableName = st.tableName(oldRelation.secondModel.modelName);
                                            def secondColumnName = st.columnName(oldRelation.secondName) + "_id";
                                            def newSecondColumnName = st.columnName(relation.secondName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${secondTableName} ALTER COLUMN ${secondColumnName} RENAME TO ${newSecondColumnName}"
                                        }
                                    }
                                    else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.MANY) {
                                        if (oldRelation.secondName != relation.secondName) {
                                            def tableName = st.tableName(oldRelation.secondModel.modelName);
                                            def columnName = st.columnName(oldRelation.secondName) + "_id";
                                            def newColumnName = st.columnName(relation.secondName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${tableName} ALTER COLUMN ${columnName} RENAME TO ${newColumnName}"
                                        }
                                    }
                                    else {
                                        if (oldRelation.firstName != relation.firstName) {
                                            def tableName = st.tableName(oldRelation.firstModel.modelName);
                                            def columnName = st.columnName(oldRelation.firstName) + "_id";
                                            def newColumnName = st.columnName(relation.firstName) + "_id";
                                            sqlWillBeExecuted += "ALTER TABLE ${tableName} ALTER COLUMN ${columnName} RENAME TO ${newColumnName}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                    oldFromRelations.each {relationId, GeneratedModelRelation oldRelation ->
                        if (oldRelation.firstCardinality == ModelRelation.MANY && oldRelation.secondCardinality == ModelRelation.MANY) {
                            addDropManyToManyRelationSql(oldRelation, st, sqlWillBeExecuted);
                        }
                        else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.ONE) {
                            addDropOneToOneRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                        }
                        else if (oldRelation.firstCardinality == ModelRelation.ONE && oldRelation.secondCardinality == ModelRelation.MANY) {
                            addDropOneToManyRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                        }
                        else {
                            addDropManyToOneRelationSql(oldRelation, st, tableConstraints, sqlWillBeExecuted)
                        }
                    }
                }
            }
            GeneratedModel newGeneratedModel = createGeneratedModelInfo(newDependentModel);
            newGeneratedModels.put(newGeneratedModel.modelName, newGeneratedModel);
        }
        newGeneratedModels.each {modelName, generatedModel ->
            createGeneratedModelRelationInfo(generatedModel, newGeneratedModels);
        }
        sqlWillBeExecuted.each {
            def sqlStm = new ModelModificationSqls(sqlStatement: it);
            sqlStm.save();
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

    def createTableConstraintsMap() {
        def tableConstraints = [:];
        def springContext = servletContext.getAttribute(ApplicationAttributes.APPLICATION_CONTEXT)
        LocalSessionFactoryBean bean = springContext.getBean("&sessionFactory");
        Configuration config = bean.getConfiguration()
        def tables = config.getTableMappings().toList();
        for (table in tables) {
            def tableMap = ["foreignKeys": [:], "referencedKeys": []];
            tableConstraints.put(table.getName(), tableMap);
        }
        for (table in tables) {
            Iterator foreignKeyIterator = table.getForeignKeyIterator();
            def tableName = table.getName();
            def tableMap = tableConstraints.get(tableName);
            while (foreignKeyIterator.hasNext()) {
                ForeignKey foreignKey = foreignKeyIterator.next();
                tableMap["foreignKeys"].put(foreignKey.getColumn(0).getName(), foreignKey);
                tableConstraints[foreignKey.getReferencedTable().getName()]?.get("referencedKeys").add(foreignKey);
            }
        }
        return tableConstraints;

    }

    def addNewNonBlankColumn(tableName, columnName, type, sqlList) {
        def defaultValue;
        def databaseType;
        if (type == ModelProperty.stringType) {
            defaultValue = "'RCMDB_Default'";
            databaseType = "VARCHAR (255)"
        }
        else if (type == ModelProperty.numberType) {
            defaultValue = "-1111";
            databaseType = "BIGINT"
        }
        else {
            defaultValue = "1970-01-01 00:00:00.000000000";
            databaseType = "TIMESTAMP"
        }
        sqlList.add("ALTER TABLE ${tableName} ADD COLUMN ${columnName} ${databaseType} NULL");
        sqlList.add("UPDATE ${tableName} SET ${columnName}=${defaultValue} WHERE ${columnName} IS NULL");
        sqlList.add("ALTER TABLE ${tableName} ALTER COLUMN ${columnName} SET NOT NULL");
    }

    def addDropTableSqls(generatedModel, namingStrategy, tableConstraintsMap, sqlList) {
        def tableName = namingStrategy.tableName(generatedModel.modelName);
        def referencedForeignKeys = tableConstraintsMap[tableName]?.get("referencedKeys");
        def relations = GeneratedModelRelation.findAllByFirstModel(generatedModel);
        relations.addAll(GeneratedModelRelation.findAllBySecondModel(generatedModel))
        relations.each {
            if (it.firstCardinality == ModelRelation.MANY && it.secondCardinality == ModelRelation.MANY) {
                addDropManyToManyRelationSql(it, namingStrategy, sqlList);
            }
        }
        referencedForeignKeys?.each {ForeignKey foreignKey ->
            sqlList.add("ALTER TABLE ${foreignKey.getTable().getName()} DROP CONSTRAINT ${foreignKey.getName()}");
            sqlList.add("ALTER TABLE ${foreignKey.getTable().getName()} DROP COLUMN ${foreignKey.getColumn(0).getName()}");
        }
        sqlList.add("DROP TABLE ${tableName}");
    }

    def addDropManyToManyRelationSql(generatedModelRelation, namingStrategy, sqlList) {
        def relationTableName;
        if (generatedModelRelation.isReverse) {
            relationTableName = namingStrategy.collectionTableName(null, namingStrategy.tableName(generatedModelRelation.secondModel.modelName), null, null, namingStrategy.tableName(generatedModelRelation.firstModel.modelName))
        }
        else {
            relationTableName = namingStrategy.collectionTableName(null, namingStrategy.tableName(generatedModelRelation.firstModel.modelName), null, null, namingStrategy.tableName(generatedModelRelation.secondModel.modelName))
        }
        sqlList.add("DROP TABLE ${relationTableName}")
    }

    def addDropOneToOneRelationSql(oldRelation, st, tableConstraints, sqlList) {
        def firstTableName = st.tableName(oldRelation.firstModel.modelName);
        def secondTableName = st.tableName(oldRelation.secondModel.modelName);
        def firstColumnName = st.columnName(oldRelation.firstName) + "_id";
        def secondColumnName = st.columnName(oldRelation.secondName) + "_id";
        def firstConstraint = tableConstraints.get(firstTableName)?.get("foreignKeys").get(firstColumnName);
        def secondConstraint = tableConstraints.get(secondTableName)?.get("foreignKeys").get(secondColumnName);
        sqlList.add("ALTER TABLE ${firstTableName} DROP CONSTRAINT ${firstConstraint.getName()}")
        sqlList.add("ALTER TABLE ${secondTableName} DROP CONSTRAINT ${secondConstraint.getName()}")
        sqlList.add("ALTER TABLE ${firstTableName} DROP COLUMN ${firstColumnName}")
        sqlList.add("ALTER TABLE ${secondTableName} DROP COLUMN ${secondColumnName}")
    }
    def addDropOneToManyRelationSql(oldRelation, st, tableConstraints, sqlList) {
        def tableName = st.tableName(oldRelation.secondModel.modelName);
        def columnName = st.columnName(oldRelation.secondName) + "_id";
        def constraint = tableConstraints.get(tableName)?.get("foreignKeys").get(columnName);
        sqlList.add("ALTER TABLE ${tableName} DROP CONSTRAINT ${constraint.getName()}")
        sqlList.add("ALTER TABLE ${tableName} DROP COLUMN ${columnName}")
    }
    def addDropManyToOneRelationSql(oldRelation, st, tableConstraints, sqlList) {
        def tableName = st.tableName(oldRelation.firstModel.modelName);
        def columnName = st.columnName(oldRelation.firstName) + "_id";
        def constraint = tableConstraints.get(tableName)?.get("foreignKeys").get(columnName);
        sqlList.add("ALTER TABLE ${tableName} DROP CONSTRAINT ${constraint.getName()}")
        sqlList.add("ALTER TABLE ${tableName} DROP COLUMN ${columnName}")
    }

    def getTablesToBeDropped(newModels) {
        def tables = [:];
        newModels.each {String modelName, Model model ->
            GeneratedModel gModel = GeneratedModel.findByModelName(model.name);
            if (gModel && gModel.parentModelName != model.parentModel?.name) {
                getModelHierarchyForDrop(model.name, tables);
            }
        }
        return tables;
    }

    def getModelHierarchyForDrop(modelName, tablesToBeDropped) {
        tablesToBeDropped.put(modelName, modelName);
        def children = GeneratedModel.findAllByParentModelName(modelName);
        children.each {
            getModelHierarchyForDrop(it.modelName, tablesToBeDropped);
        }
    }

}
