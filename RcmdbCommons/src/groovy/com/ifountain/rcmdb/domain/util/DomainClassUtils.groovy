package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.util.RapidCMDBConstants
import org.codehaus.groovy.grails.orm.hibernate.support.ClosureEventTriggeringInterceptor
import org.codehaus.groovy.grails.commons.ApplicationHolder

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
class DomainClassUtils
{
    def static getUppercasedPropertyName(String propName)
    {
        if(propName.length() == 1)
        {
            return propName.toUpperCase();
        }
        else
        {
            return propName.substring(0,1).toUpperCase()+propName.substring(1);
        }
    }

    def static getParentDomainClass(GrailsDomainClass dc, grailsDomainClasses)
    {
        def domainClassesMap = [:]
        grailsDomainClasses.each{
            domainClassesMap[it.clazz.name] = it;
        }
        def parentDomainClass = dc.clazz;
        while(domainClassesMap[parentDomainClass.superclass.name] != null)
        {
            parentDomainClass = parentDomainClass.superclass;
        }
        return parentDomainClass;
    }

    def static getSubClasses(GrailsDomainClass dc)
    {
        def classes = [];
        dc.getSubClasses().each{ GrailsDomainClass subDomainClass->
            classes += subDomainClass.clazz;
        }
        return classes;
    }

    def static getPersistantProperties(GrailsDomainClass dc, boolean includeRelations)
    {
        def propMap = [:]
        def domainObjectProperties = dc.getProperties();
        domainObjectProperties.each{GrailsDomainClassProperty prop->
            if(includeRelations || !prop.isAssociation())
            {
                if(prop.isPersistent())
                {
                    propMap[prop.name] = prop;
                }
            }
        }
        return propMap;
    }

    def static getRelations(String domainClassName){
       GrailsDomainClass domainClass = ApplicationHolder.getApplication().getDomainClass(domainClassName);
       if(domainClass){
          return getRelations(domainClass);
       }
       else{
           throw new Exception("DomainClass ${domainClassName} does not exist.");
       }
    }
    def static getRelations(GrailsDomainClass dc)
    {
        def allRelations = [:];
        def domainObjectProperties = dc.getProperties();
        def relations = [:];
        def tmpCls = dc.clazz;
        while(tmpCls && tmpCls != java.lang.Object.class)
        {
            def tmpVariableMap = GrailsClassUtils.getStaticPropertyValue (tmpCls, "relations");
            tmpVariableMap.each{key, value->
                value["cls"] = tmpCls;
                relations[key] = value;
            }
            tmpCls =  tmpCls.getSuperclass();
        }
        def cascadedObjects = getStaticMapVariable(dc.metaClass.getTheClass(), "cascaded");
        domainObjectProperties.each{GrailsDomainClassProperty prop->
            Map relationConfig = relations[prop.name];
            if(relationConfig != null)
            {
                def relationName = prop.name;
                def isMany = relationConfig.isMany;
                def otherSideName = relationConfig.reverseName;
                def isCascaded = cascadedObjects[relationName] == true;
                def otherSideClass = relationConfig.type;
                def cls = relationConfig.cls;
                def otherSideRelationConfiguration = getStaticMapVariable(otherSideClass, "relations");
                def isOtherSideMany = false;
                if(otherSideName)
                {
                    def otherSideConfig = otherSideRelationConfiguration[otherSideName];
                    if(otherSideConfig != null)
                    {
                        isOtherSideMany = otherSideRelationConfiguration[otherSideName].isMany;
                    }
                    else
                    {
                        throw new Exception("Invalid relation configuration for domainclass ${dc.name} and for relation ${relationName}. Reverse relation ${otherSideName} does not exist".toString());
                    }
                }
                def relType;
                if(isMany && isOtherSideMany)
                {
                    relType = RelationMetaData.MANY_TO_MANY
                }
                else if(isMany && !isOtherSideMany)
                {
                    relType = RelationMetaData.ONE_TO_MANY
                }
                else if(!isMany && isOtherSideMany)
                {
                    relType = RelationMetaData.MANY_TO_ONE
                }
                else
                {
                    relType = RelationMetaData.ONE_TO_ONE;
                }
                def rel = new RelationMetaData(relationName, otherSideName, cls, otherSideClass, relType);
                rel.isCascade = isCascaded;
                allRelations[relationName] = rel;
            }
        }

        return allRelations;
    }

    def static getKeys(GrailsDomainClass dc)
    {
        def keys = [];
        def constrainedPropertiesMap = dc.getConstrainedProperties();
        constrainedPropertiesMap.each{String propName, ConstrainedProperty  prop->
            def keyConst = prop.getAppliedConstraint (KeyConstraint.KEY_CONSTRAINT);
            if(keyConst && keyConst.isKey())
            {
                keys = keyConst.getKeys();
                return;
            }
        }
        return keys;
    }

    def static getStaticMapVariable(Class tempObj, String variableName)
    {
        def variableMap = [:];
        while(tempObj && tempObj != java.lang.Object.class)
        {
            def tmpVariableMap = GrailsClassUtils.getStaticPropertyValue (tempObj, variableName);
            if(tmpVariableMap)
            {
                variableMap.putAll(tmpVariableMap);
            }
            tempObj =  tempObj.getSuperclass();
        }
        return variableMap;
    }
    
    def static getStaticListVariable(Class tempObj, String variableName)
    {
        def variableList = [];
        while(tempObj && tempObj != java.lang.Object.class)
        {
            def tmpVariableList = GrailsClassUtils.getStaticPropertyValue (tempObj, variableName);
            if(tmpVariableList)
            {
                variableList.addAll(tmpVariableList);
            }
            tempObj =  tempObj.getSuperclass();
        }
        return variableList;
    }

    def static getPropertyRealValue(propType, value)
    {
        if(propType.isInstance(value))
        {
            return value;
        }
        else  if(value instanceof String)
        {
            String propTypeName =  propType.name;
            if(propTypeName.indexOf(".") > 0)
            {
                propTypeName = propTypeName.substring(propTypeName.lastIndexOf(".")+1)
            }
            return value."to${propTypeName}"();
        }
        else
        {
            return value;
        }
    }


    public static void addErrorsOnInstance(Object target, ObjectError objectError) {
        BeanPropertyBindingResult errors = target.errors;
        if(!errors){
            errors = new BeanPropertyBindingResult(target, target.getClass().getName());
            target.metaClass.setProperty(target, RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors);
        }
        errors.addError(objectError);
    }

    def static getFilteredProperties(String domainClassName, List extraFilters, boolean excludeTransients) {
        def excludedProps = ["version", RapidCMDBConstants.ERRORS_PROPERTY_NAME,
                RapidCMDBConstants.OPERATION_PROPERTY_NAME,
                RapidCMDBConstants.DYNAMIC_PROPERTY_STORAGE,
                ClosureEventTriggeringInterceptor.ONLOAD_EVENT,
                ClosureEventTriggeringInterceptor.BEFORE_DELETE_EVENT,
                ClosureEventTriggeringInterceptor.BEFORE_INSERT_EVENT,
                ClosureEventTriggeringInterceptor.BEFORE_UPDATE_EVENT];

       def domainClass = ApplicationHolder.getApplication().getDomainClass(domainClassName);
       if(domainClass){
           excludedProps.addAll(extraFilters);
           if(excludeTransients){
               excludedProps.addAll(getStaticListVariable(domainClass.clazz, "transients"));
           }
           return domainClass.properties.findAll {!excludedProps.contains(it.name)}
       }
       else{
           throw new Exception("DomainClass ${domainClassName} does not exist.");
       }
    }

    def static getFilteredProperties(String domainClassName, List extraFilters) {
        return getFilteredProperties(domainClassName, extraFilters, true)
    }

    def static getFilteredProperties(String domainClassName){
        return getFilteredProperties(domainClassName, []);
    }
}