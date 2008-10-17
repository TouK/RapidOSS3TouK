package com.ifountain.rcmdb.domain.generation

import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import model.ModelAction
import model.PropertyAction
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jun 3, 2008
* Time: 5:06:23 PM
* To change this template use File | Settings | File Templates.
*/

class ExistingDataAnalyzer
{
    public static final Map excludedProps = ["id":"id", "version":"version"];
    def static correctModelData(Map newDomainClassMap, Map currentDomainClassMap)
    {
    }

    public static List createActions(GrailsDomainClass currentDomainObject, GrailsDomainClass newDomainObject)
    {
        def actions = [];
        def oldClassProperties = getPropertyMap(currentDomainObject);
        def newClassProperties = getPropertyMap(newDomainObject);

        Map oldRelations = DomainClassUtils.getRelations(currentDomainObject);
        Map oldConstrainedProps = currentDomainObject.getConstrainedProperties();
        Map newRelations = DomainClassUtils.getRelations(newDomainObject);
        def oldKeyProperties = getKeyProperties(currentDomainObject);
        def newKeyProperties = getKeyProperties(newDomainObject);
        Map newConstrainedProps = newDomainObject.getConstrainedProperties();
        boolean willDeleteAll = false;
        boolean willResourcesBeRegenerated = false;

        if(String.valueOf(currentDomainObject.clazz.superclass) != String.valueOf(newDomainObject.clazz.superclass) || (oldKeyProperties.isEmpty() || oldKeyProperties.size() == 1 && oldKeyProperties.contains("id")) && !(newKeyProperties.isEmpty() || newKeyProperties.size() == 1 && newKeyProperties.contains("id")))
        {
            willDeleteAll = true;
        }
        else
        {
            oldKeyProperties.each{String propname->
                if(!(newKeyProperties.contains(propname) && newClassProperties[propname].type == oldClassProperties[propname].type))
                {
                    willDeleteAll = true;
                    return;
                }
            }
        }
        if(willDeleteAll)
        {
            def action = new ModelAction();
            action.setProperty("modelName", currentDomainObject.name, false);
            action.setProperty("action", ModelAction.DELETE_ALL_INSTANCES, false);
            actions += action;
            willResourcesBeRegenerated = true; 
        }
        else if(newKeyProperties.size() > oldKeyProperties.size())
        {
            willResourcesBeRegenerated = true;            
        }

        if(currentDomainObject.subClasses.isEmpty() && !newDomainObject.subClasses.isEmpty() )
        {
            def action = new ModelAction();
            action.setProperty("modelName", currentDomainObject.name, false);
            action.setProperty("action", ModelAction.REFRESH_DATA, false);
            actions += action;
        }
        
        newClassProperties.each{String propName, GrailsDomainClassProperty prop->
            GrailsDomainClassProperty oldProperty = oldClassProperties.remove(propName);
            ConstrainedProperty oldConstrainedProp = oldConstrainedProps[propName];
            ConstrainedProperty newConstrainedProp = newConstrainedProps[propName];
            if(oldProperty == null || oldProperty.type != prop.type || oldConstrainedProp == null || newConstrainedProp == null)
            {
                if(!willDeleteAll)
                {
                    def action = new PropertyAction();
                    action.setProperty("modelName", currentDomainObject.name, false);
                    action.setProperty("propName", propName, false);
                    action.setProperty("action", PropertyAction.SET_DEFAULT_VALUE, false);
                    actions += action;
                }
                if(oldProperty == null || oldProperty.type != prop.type)
                {
                    willResourcesBeRegenerated = true;
                }
            }
        }
        if(!oldClassProperties.isEmpty())
        {
            willResourcesBeRegenerated = true;    
        }
        newRelations.each{String relationName, RelationMetaData newRelation->
            RelationMetaData oldRelation = oldRelations.remove(relationName)
            if(oldRelation)
            {

                int isOldMany = oldRelation.isOneToMany() || oldRelation.isManyToMany()?1:0;
                int isNewMany = newRelation.isOneToMany() || newRelation.isManyToMany()?1:0;

                int isOldOthersideMany = oldRelation.hasOtherSide() && (oldRelation.isManyToOne() || oldRelation.isManyToMany())?1:0
                int isNewOthersideMany = newRelation.hasOtherSide() && (newRelation.isManyToOne() || newRelation.isManyToMany())?1:0
                if(isOldMany > isNewMany || isOldOthersideMany > isNewOthersideMany)
                {
                    if(!willDeleteAll)
                    {
                        def action = new PropertyAction();
                        action.setProperty("modelName", currentDomainObject.name, false);
                        action.setProperty("propName", relationName, false);
                        action.setProperty("action", PropertyAction.CLEAR_RELATION, false);
                        action.setProperty("propTypeName", oldRelation.getOtherSideCls().name, false);
                        action.setProperty("reverseName", oldRelation.getOtherSideName(), false);
                        actions += action;
                    }
                    willResourcesBeRegenerated = true;
                }
                else if(isOldMany != isNewMany || isOldOthersideMany != isNewOthersideMany)
                {
                    willResourcesBeRegenerated = true;
                }
            }
            else
            {
                willResourcesBeRegenerated = true;
            }
        }
        if(!oldRelations.isEmpty())
        {
            willResourcesBeRegenerated = true;
            if(!willDeleteAll)
            {
                oldRelations.each{relationName, oldRelation->
                    def action = new PropertyAction();
                    action.setProperty("modelName", currentDomainObject.name, false);
                    action.setProperty("propName", relationName, false);
                    action.setProperty("action", PropertyAction.CLEAR_RELATION, false);
                    action.setProperty("propTypeName", oldRelation.getOtherSideCls().name, false);
                    action.setProperty("reverseName", oldRelation.getOtherSideName(), false);
                    actions += action;

                }
            }
        }

        if(willResourcesBeRegenerated)
        {
            def action = new ModelAction();
            action.setProperty("modelName", currentDomainObject.name, false);
            action.setProperty("action", ModelAction.GENERATE_RESOURCES, false);
            actions += action;   
        }
        
        return actions;
    }

    private static Map getPropertyMap(GrailsDomainClass domainObject)
    {

        def newClassProperties = [:];
        def relations = DomainClassUtils.getRelations(domainObject);
        domainObject.getProperties().each{GrailsDomainClassProperty prop->
           if(!relations.containsKey(prop.name) && !excludedProps.containsKey(prop.name))
           {
                newClassProperties[prop.name] = prop;    
           }
        }
        return newClassProperties;

    }


    private static List getKeyProperties(GrailsDomainClass domainObject)
    {
        List keyProperties = [];
        domainObject.getConstrainedProperties().each{String propName, ConstrainedProperty prop->
           KeyConstraint keyConst = prop.getAppliedConstraint (KeyConstraint.KEY_CONSTRAINT);
           if(keyConst && keyConst.isKey())
           {
               keyProperties = keyConst.getKeys();
               return;
           }
        }
        return keyProperties;

    }
}