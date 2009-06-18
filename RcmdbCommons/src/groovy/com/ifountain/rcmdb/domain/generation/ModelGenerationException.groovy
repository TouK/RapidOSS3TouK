package com.ifountain.rcmdb.domain.generation
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Mar 30, 2008
 * Time: 1:25:57 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelGenerationException extends Exception{

    public ModelGenerationException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public ModelGenerationException(String message, Throwable cause) {
        super(message, cause); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static ModelGenerationException masterDatasourceDoesnotExists(String modelName)
    {
        return new ModelGenerationException("Master datasource doesnot exist for model $modelName")
    }

    public static ModelGenerationException datasourceDoesnotExists(String modelName, String datasourceName, String propertyName)
    {
        return new ModelGenerationException("Datasource $datasourceName doesnot exist for property $propertyName in model $modelName");
    }
    public static ModelGenerationException datasourcePropertyDoesnotExists(String modelName, String dsPropertyName, String propertyName)
    {
        return new ModelGenerationException("Datasource property $dsPropertyName doesnot exist for property $propertyName in model $modelName");
    }

    public static ModelGenerationException mappedNamePropertyDoesNotExist(String modelName, String dsName, String propertyName)
    {
        return new ModelGenerationException("mappedNameProperty ${propertyName} for datasource ${dsName} is not defined in model ${modelName}");
    }
    public static ModelGenerationException cannotStartWith(String modelName, String propName, String prefix, isRelation)
    {
        return new ModelGenerationException("Invalid ${isRelation?"relation":"property"} ${propName} for model ${modelName}. Model properties can not start with ${prefix}.");
    }

    public static ModelGenerationException duplicateParentDatasource(String dsName, String modelName)
    {
        return new ModelGenerationException("Duplicate datasource definition in model ${modelName} and parent model for datasource ${dsName}")
    }
    public static ModelGenerationException duplicateDatasource(String dsName, String modelName)
    {
        return new ModelGenerationException("Duplicate datasource definition in model ${modelName} for datasource ${dsName}")
    }

    public static ModelGenerationException couldNotDeleteOldControllerFile(String modelName)
    {
        return new ModelGenerationException("Could not delete old controller file of model ${modelName}")
    }

    public static ModelGenerationException moreThanOnemasterDatasourceDefined(String modelName)
    {
        return new ModelGenerationException("Only one master datasource should be specified for $modelName")
    }
    public static ModelGenerationException noKeySpecifiedForDatasource(String datasourceName, String modelName)
    {
        return new ModelGenerationException("No keys specified for datasource $datasourceName in model $modelName");
    }
    public static ModelGenerationException couldNotDeleteOldController(String modelName)
    {
        return new ModelGenerationException("Could not deleted controller of model $modelName");
    }
    public static ModelGenerationException undefinedParentModel(String modelName, String parentModelName)
    {
        return new ModelGenerationException("Parent model ${parentModelName} is not defined. Model ${modelName} could not be created.");
    }
    public static ModelGenerationException undefinedRelatedModel(String modelName, String relationName, String relatedModelName)
    {
        return new ModelGenerationException("Could not created model ${modelName}. Reason: Related model ${relatedModelName} for relation ${relationName} is not defined.");
    }
    public static ModelGenerationException invalidModelName(String modelName)
    {
        return new ModelGenerationException("Invalid modelname ${modelName}.");
    }
    public static ModelGenerationException invalidModelPropertyName(String modelName, String propertyName)
    {
        return new ModelGenerationException("Invalid model property name ${propertyName} for model ${modelName}.");
    }
    public static ModelGenerationException invalidModelRelationName(String modelName, String relationName)
    {
        return new ModelGenerationException("Invalid model relation name ${relationName} for model ${modelName}.");
    }
    public static ModelGenerationException childModelCannotDefineKey(String modelName)
    {
        return new ModelGenerationException("Child model ${modelName} can not define key.");
    }

    public static ModelGenerationException invalidStorageType(String modelName, String storageType)
    {
        return new ModelGenerationException("Invalid storageType ${storageType} for model ${modelName}.");
    }

    public static ModelGenerationException duplicateRelation(String modelName, String relationName)
    {
        return new ModelGenerationException("Duplicate relation ${relationName} in model ${modelName}.");
    }
    public static ModelGenerationException duplicateProperty(String modelName, String propertyName)
    {
        return new ModelGenerationException("Duplicate property ${propertyName} in model ${modelName}.");
    }
    public static ModelGenerationException samePropertyWithDifferentType(String propOwnerModel1, String propOwnerModel2, String propName)
    {
        return new ModelGenerationException("Property ${propName} is defined with different type in models ${propOwnerModel1} and ${propOwnerModel2}.");
    }
}