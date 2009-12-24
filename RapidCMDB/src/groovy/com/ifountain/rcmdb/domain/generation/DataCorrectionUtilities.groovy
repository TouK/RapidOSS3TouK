/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.domain.property.RelationUtils
import com.ifountain.rcmdb.util.ModelUtils
import model.ModelAction
import model.PropertyAction
import org.codehaus.groovy.grails.commons.*
import org.compass.core.CompassHit
import org.compass.core.Resource
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.control.MultipleCompilationErrorsException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 16, 2008
 * Time: 11:40:13 AM
 * To change this template use File | Settings | File Templates.
 */
class DataCorrectionUtilities
{
    public static void dataCorrectionBeforeReloadStep(String baseDir, String tempBaseDir)
    {
        def oldDomainClasses = [:]
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
            GrailsDomainClass cls = ApplicationHolder.application.getDomainClass(modelName);
            if (cls)
            {
                oldDomainClasses[modelName] = cls;
            }
        }
        def domainClassesWillBeGenerated = [];
        def newDomainClassesMap = [:];
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(DataCorrectionUtilitiesClassLoaderFactory.getClassLoaderForDomainClasses());
        gcl.setShouldRecompile(true);
        gcl.addClasspath(tempModelDir);
        gcl.addClasspath(baseDir + "/src/groovy");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        for (int i = 0; i < tempModelFileList.size(); i++) {
            File modelFile = tempModelFileList[i];
            String modelName = StringUtils.substringBefore(modelFile.name, ".groovy");
            def cls = gcl.loadClass(modelName);
            def domainClass = new DefaultGrailsDomainClass(cls);
            domainClassesWillBeGenerated += domainClass;
            newDomainClassesMap[modelName] = domainClass;
        }
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(domainClassesWillBeGenerated as GrailsClass[], newDomainClassesMap);
        createModelActions(baseDir, domainClassesWillBeGenerated, oldDomainClasses);
        oldDomainClasses.each {String oldClassName, GrailsDomainClass oldDomainClass ->
            if (!newDomainClassesMap.containsKey(oldClassName))
            {
                ModelUtils.deleteModelArtefacts(baseDir, oldClassName);
                new File(tempModelDir + "/" + oldClassName + ".groovy").delete()
                deleteAllInstances(oldDomainClass.clazz);
            }
        }
    }

    private static void createModelActions(String baseDir, List domainClassesWillBeGenerated, Map oldDomainClasses) {
        ModelAction.removeAll();
        PropertyAction.removeAll();
        domainClassesWillBeGenerated.each {GrailsDomainClass newDomainClass ->

            GrailsDomainClass oldDomainClass = oldDomainClasses[newDomainClass.name];
            if (oldDomainClass)
            {
                List actions = ExistingDataAnalyzer.createActions(oldDomainClass, newDomainClass);
                actions.each {
                    if (it instanceof ModelAction)
                    {
                        if (it.action == ModelAction.GENERATE_RESOURCES)
                        {
                            ModelUtils.createModelOperationsFile(newDomainClass.clazz, new File(baseDir + "/operations"), []);
                        }
                        else if (it.action == ModelAction.DELETE_ALL_INSTANCES)
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
                        PropertyAction.add(propName: it.propName, action: it.action, modelName: it.modelName, reverseName: it.reverseName, propTypeName: it.propTypeName);
                    }
                }
            }
            else
            {
                ModelUtils.createModelOperationsFile(newDomainClass.clazz, new File(baseDir + "/operations"), []);
            }
        }
    }

    private static void deleteAllInstances(clazz)
    {
        clazz.'searchEvery'("alias:*", [raw: {hits, session ->
            hits.iterator().each {hit ->
                Resource res = hit.getResource();
                session.delete(res);
            }
        }]);
    }

    public static void dataCorrectionAfterReloadStep()
    {
        def domainClasses = [:];
        ApplicationHolder.application.getDomainClasses().each {
            domainClasses[it.clazz.name] = it;
        }
        ModelAction.list().each {ModelAction modelAction ->
            if (modelAction.action == ModelAction.REFRESH_DATA)
            {
                Class currentModelClass = domainClasses[modelAction.modelName].clazz;
                def propList = currentModelClass.'getPropertiesList'();
                def propNames = propList.findAll {!it.isRelation && !it.isOperationProperty}.name
                propNames.add("id");
                currentModelClass.'searchEvery'("alias:*", [raw: {hits, session ->
                    hits.iterator().each {hit ->
                        Resource res = hit.getResource();
                        def newObj = currentModelClass.newInstance();
                        propNames.each {
                            newObj.setProperty(it, res.getObject(it), false);
                        }
                        session.save(newObj);
                    }
                }])
            }
            modelAction.remove();
        }
        def changedModelProperties = [:]
        def propActions = PropertyAction.list();
        propActions.each {PropertyAction propAction ->
            if (!propAction.willBeDeleted)
            {
                DefaultGrailsDomainClass currentDomainObject = domainClasses[propAction.modelName];
                def modelProps = changedModelProperties[propAction.modelName];
                if (modelProps == null)
                {
                    modelProps = [:]
                    changedModelProperties[propAction.modelName] = modelProps;
                }
                if (propAction.action != PropertyAction.CLEAR_RELATION)
                {
                    propAction.defaultValue = currentDomainObject.clazz.newInstance()[propAction.propName];
                    propAction.propType = currentDomainObject.getPropertyByName(propAction.propName).type;
                }
                modelProps[propAction.propName] = propAction;
            }
        }
        def simplePropetyNamesList = [:];
        domainClasses.each {String className, modelClass ->

            def propList = modelClass.clazz.'getPropertiesList'();
            def propNames = propList.findAll {!it.isRelation && !it.isOperationProperty}.name
            propNames.add("id");
            simplePropetyNamesList[className] = propNames;
        }
        changedModelProperties.each {String modelName, Map modelProps ->
            DefaultGrailsDomainClass currentDomainObject = domainClasses[modelName];
            if (currentDomainObject)
            {
                Class currentModelClass = currentDomainObject.clazz;
                currentModelClass.'searchEvery'("alias:*", [raw: {hits, session ->
                    hits.iterator().each {CompassHit hit ->
                        Resource res = hit.getResource();
                        def objectRealClass = domainClasses[hit.alias()];
                        if (objectRealClass)
                        {
                            def newObj = objectRealClass.newInstance();
                            def newProps = [:]
                            modelProps.each {propName, PropertyAction action ->
                                if (action.action == PropertyAction.CLEAR_RELATION)
                                {
                                    def id = res.getObject("id");
                                    RelationUtils.removeExistingRelationsById(id, action.propName, action.reverseName)
                                }
                                else if (action.action == PropertyAction.SET_DEFAULT_VALUE)
                                {
                                    newProps[propName] = action.defaultValue;

                                }
                            }
                            if (!newProps.isEmpty())
                            {
                                simplePropetyNamesList.get(res.getAlias()).each {propName ->
                                    def newPropVal = newProps[propName];

                                    if (newPropVal == null)
                                    {
                                        newPropVal = res.getObject(propName)
                                    }
                                    newObj.setProperty(propName, newPropVal, false);
                                }
                                session.delete(res);
                                session.save(newObj);
                            }
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
//for testing
public class DataCorrectionUtilitiesClassLoaderFactory {
    private static ClassLoader classLoader;
    public static ClassLoader getClassLoaderForDomainClasses() {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader().parent;
        }
        return classLoader
    }
    public static setMockClassLoader(ClassLoader cl) {
        classLoader = cl;
    }

}