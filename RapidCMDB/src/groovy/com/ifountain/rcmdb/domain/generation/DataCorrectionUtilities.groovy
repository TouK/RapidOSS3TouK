package com.ifountain.rcmdb.domain.generation

import org.codehaus.groovy.grails.commons.GrailsDomainConfigurationUtil
import org.codehaus.groovy.grails.commons.GrailsClass
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import model.ModelAction
import com.ifountain.rcmdb.util.ModelUtils
import model.PropertyAction
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.domain.property.RelationUtils
import relation.Relation
import org.compass.core.Resource

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 16, 2008
 * Time: 11:40:13 AM
 * To change this template use File | Settings | File Templates.
 */
class DataCorrectionUtilities
{
    public static void dataCorrectionBeforeReloadStep(String baseDir, String tempModelDir, Map oldDomainClasses, List domainClassesWillBeGenerated,  Map domainClassesMap)
    {
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesWillBeGenerated as GrailsClass[], domainClassesMap);
        domainClassesWillBeGenerated.each {GrailsDomainClass newDomainClass ->

            GrailsDomainClass oldDomainClass = oldDomainClasses[newDomainClass.name];
            if (oldDomainClass)
            {
                List actions = ExistingDataAnalyzer.createActions(oldDomainClass, newDomainClass);
                actions.each {
                    if (it instanceof ModelAction)
                    {
                        if(it.action == ModelAction.GENERATE_RESOURCES)
                        {
                            ModelUtils.generateModelArtefacts(newDomainClass, baseDir, baseDir);
                        }
                        else if(it.action == ModelAction.DELETE_ALL_INSTANCES)
                        {
                            deleteAllInstances(oldDomainClasses[it.modelName].clazz);
                        }
                        else
                        {
                            ModelAction.add(action: it.action, modelName: it.modelName);
                        }

                    }
                    else
                    {
                        PropertyAction.add(propName: it.propName, action: it.action, modelName: it.modelName, reverseName:it.reverseName, propTypeName:it.propTypeName);
                    }
                }
            }
            else
            {
                ModelUtils.generateModelArtefacts(newDomainClass, baseDir, baseDir);
            }
        }


        oldDomainClasses.each {String oldClassName, GrailsDomainClass oldDomainClass ->
            if (!domainClassesMap.containsKey(oldClassName))
            {
                ModelUtils.deleteModelArtefacts(baseDir, oldClassName);
                new File(tempModelDir + "/" + oldClassName + ".groovy").delete()
                deleteAllInstances(oldDomainClass.clazz);
            }
        }
    }

    private static void deleteAllInstances(clazz)
    {
        clazz.'searchEvery'("alias:*", [raw:{hits, session->
            hits.each{hit->
                Resource res = hit.getResource();
                session.delete(res);
            }
        }]);
    }

    public static void dataCorrectionAfterReloadStep()
    {
        ModelAction.list().each {ModelAction modelAction ->
            if (modelAction.action == ModelAction.REFRESH_DATA)
            {
                Class currentModelClass = ApplicationHolder.application.getDomainClass(modelAction.modelName).clazz;
                def propList = currentModelClass.'getPropertiesList'();
                def propNames = propList.findAll {!it.isRelation && !it.isOperationProperty}.name
                propNames.add("id");
                currentModelClass.'searchEvery'("alias:*", [raw:{hits, session->
                    hits.each{hit->
                        Resource res = hit.getResource();
                        def newObj = currentModelClass.newInstance();
                        propNames.each{
                            newObj.setProperty(it, res.getObject(it), false);
                        }
                        session.save(newObj);
                    }
                }])
            }
            modelAction.remove();
        }
        def changedModelProperties = [:]
        PropertyAction.list().each {PropertyAction propAction ->
            if (!propAction.willBeDeleted)
            {
                DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(propAction.modelName);
                def modelProps = changedModelProperties[propAction.modelName];
                if (modelProps == null)
                {
                    modelProps = [:]
                    changedModelProperties[propAction.modelName] = modelProps;
                }
                if(propAction.action != PropertyAction.CLEAR_RELATION)
                {
                    propAction.defaultValue = currentDomainObject.clazz.newInstance()[propAction.propName];
                    propAction.propType = currentDomainObject.getPropertyByName(propAction.propName).type;
                }
                modelProps[propAction.propName] = propAction;
            }
        }
        changedModelProperties.each {String modelName, Map modelProps ->
            DefaultGrailsDomainClass currentDomainObject = ApplicationHolder.application.getDomainClass(modelName);
            if (currentDomainObject)
            {
                Class currentModelClass = currentDomainObject.clazz;
                def propList = currentModelClass.'getPropertiesList'();
                def propNames = propList.findAll {!it.isRelation && !it.isOperationProperty}.name
                propNames.add("id");
                currentModelClass.'searchEvery'("alias:*", [raw:{hits, session->
                    hits.each{hit->
                        Resource res = hit.getResource();
                        def newObj = currentModelClass.newInstance();
                        def newProps = [:]
                        modelProps.each {propName, PropertyAction action ->
                            if (action.action == PropertyAction.CLEAR_RELATION)
                            {
                                def id = res.getObject("id");
                                def reverseRels = RelationUtils.getReverseRelationObjectsById(id, action.reverseName, action.propTypeName)
                                def selfRels = Relation.search("objectId:${id} AND name:${action.propName}").results;
                                def allRels = []
                                allRels.addAll (selfRels);
                                allRels.addAll (reverseRels);
                                allRels*.remove();
                            }
                            else if (action.action == PropertyAction.SET_DEFAULT_VALUE)
                            {
                                newProps[propName] = action.defaultValue;
                                
                            }
                        }
                        if(!newProps.isEmpty())
                        {
                            propNames.each{propName->
                                def newPropVal = newProps[propName];
                                if(newPropVal == null)
                                {
                                    newPropVal = res.getObject(propName)
                                }
                                newObj.setProperty(propName, newPropVal, false);
                            }
                            session.delete(res);
                            session.save(newObj);
                        }
                    }
                }]);

                modelProps.each {propName, PropertyAction action ->
                    action.remove();
                }
            }
        }
    }
}