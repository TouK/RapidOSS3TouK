package com.ifountain.rcmdb.domain.util

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError


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

    def static getRelations(GrailsDomainClass dc, Map domainClasses)
    {
        def allRelations = [:];
        def hasMany = getStaticMapVariable(dc, "hasMany");
        def mappedBy = getStaticMapVariable(dc, "mappedBy");
        def cascadedObjects = getStaticMapVariable(dc, "cascaded");
        mappedBy.each{relationName, otherSideName->
            def isCascaded = cascadedObjects[relationName] == true;
            def cardinalityIsMany = true;
            def otherSideClass = hasMany[relationName];
            if(!otherSideClass)
            {
                otherSideClass = dc.getPropertyByName (relationName).getType();
                cardinalityIsMany = false;
            }

            def otherSideHasMany = getStaticMapVariable(domainClasses[otherSideClass.name], "hasMany");
            def otherSideCardinalityIsMany = otherSideHasMany[otherSideName]?true:false;
            def relType;
            if(otherSideCardinalityIsMany && cardinalityIsMany)
            {
                relType = Relation.MANY_TO_MANY
            }
            else if(!otherSideCardinalityIsMany && cardinalityIsMany)
            {
                relType = Relation.ONE_TO_MANY
            }
            else if(otherSideCardinalityIsMany && !cardinalityIsMany)
            {
                relType = Relation.MANY_TO_ONE
            }
            else
            {
                relType = Relation.ONE_TO_ONE;                
            }
            def rel = new Relation(relationName, otherSideName, dc.getClazz(), otherSideClass, relType);
            rel.isCascade = isCascaded;
            allRelations[relationName] = rel;

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

    def static getStaticMapVariable(GrailsDomainClass dc, String variableName)
    {
        def variableMap = [:];
        def tempObj = dc.metaClass.getTheClass();
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
            target.metaClass.setProperty(target, "errors", errors);    
        }
        errors.addError(objectError);
    }
}