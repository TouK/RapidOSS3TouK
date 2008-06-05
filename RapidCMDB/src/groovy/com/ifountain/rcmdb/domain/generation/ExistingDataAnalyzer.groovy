package com.ifountain.rcmdb.domain.generation

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import model.PropertyAction
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import model.ModelAction
import model.ModelRelation
import org.codehaus.groovy.grails.commons.GrailsClassUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 3, 2008
* Time: 5:06:23 PM
* To change this template use File | Settings | File Templates.
*/

class ExistingDataAnalyzer
{
    def static correctModelData(Map newDomainClassMap, Map currentDomainClassMap)
    {
    }

    public static List createActions(GrailsDomainClass currentDomainObject, GrailsDomainClass newDomainObject)
    {
        def actions = [];
        def oldClassProperties = getPropertyMap(currentDomainObject);
        def newClassProperties = getPropertyMap(newDomainObject);
        Map oldRelations = getRelationshipProperties(currentDomainObject.clazz);
        Map newRelations = getRelationshipProperties(newDomainObject.clazz);
        def oldKeyProperties = getKeyProperties(currentDomainObject);
        def newKeyProperties = getKeyProperties(newDomainObject);
        if(oldKeyProperties && newKeyProperties)
        {
            oldKeyProperties.removeAll (newKeyProperties);
        }
        if(oldKeyProperties && !oldKeyProperties.isEmpty())
        {
            actions += new ModelAction(modelName:currentDomainObject.name, action:ModelAction.DELETE_ALL_INSTANCES);
        }
        else
        {
            newClassProperties.each{String propName, GrailsDomainClassProperty prop->
                GrailsDomainClassProperty oldProperty = oldClassProperties[propName];
                if(oldProperty == null || oldProperty.type != prop.type)
                {
                    actions += new PropertyAction(modelName:currentDomainObject.name, propName:propName, action:PropertyAction.SET_DEFAULT_VALUE);
                }
            }
            newRelations.each{String relationName, String reverseRelationName->
                if(oldRelations.containsKey(relationName))
                {

                    boolean isOldMany = isMany(currentDomainObject.clazz, relationName);
                    boolean isNewMany = isMany(newDomainObject.clazz, relationName);

                    def oldOthersideClass = !isOldMany?currentDomainObject.getPropertyByName(relationName).type:currentDomainObject.getRelatedClassType(relationName);
                    def newOthersideClass = !isNewMany?newDomainObject.getPropertyByName(relationName).type:newDomainObject.getRelatedClassType(relationName);
                    boolean isOldOthersideMany = isMany(oldOthersideClass, reverseRelationName)
                    boolean isNewOthersideMany = isMany(newOthersideClass, reverseRelationName)
                    if(isOldMany != isNewMany || isOldOthersideMany != isNewOthersideMany)
                    {
                        actions += new PropertyAction(modelName:currentDomainObject.name, propName:relationName, action:PropertyAction.CLEAR_RELATION);
                    }
                }


            }
        }
        
        return actions;
    }

    private static Map getRelationshipProperties(Class domainObjectClass)
    {
        Map mappedBy=  GrailsClassUtils.getStaticPropertyValue(domainObjectClass, "mappedBy");
        return mappedBy;
    }

    private static boolean isMany(Class domainObjectClass, String relName)
    {
        Map hasMany=  GrailsClassUtils.getStaticPropertyValue(domainObjectClass, "hasMany");
        return hasMany.containsKey(relName)
    }

    private static Map getPropertyMap(GrailsDomainClass domainObject)
    {
        def newClassProperties = [:];
        domainObject.getProperties().each{GrailsDomainClassProperty prop->
           if(!prop.isAssociation())
           {
                newClassProperties[prop.name] = prop;    
           }
        }
        return newClassProperties;

    }

    private static Map getPropertyLookupMap(GrailsDomainClass domainObject)
    {
        def looupMap = [:];
        def props = getPropertyMap(domainObject);
        props.each{propName, prop->
            looupMap.put (propName, propName);
        }
        domainObject.getAssociationMap().each{relName, relClass->
            looupMap.put (relName, relName);
        }
        return looupMap;
    }

    private static List getKeyProperties(GrailsDomainClass domainObject)
    {
        List keyProperties = null;
        domainObject.getConstrainedProperties().each{String propName, ConstrainedProperty prop->
           KeyConstraint keyConst = prop.getAppliedConstraint (KeyConstraint.KEY_CONSTRAINT);
           if(keyConst)
           {
               keyProperties = keyConst.keys;
               return;
           }
        }
        return keyProperties;

    }
}