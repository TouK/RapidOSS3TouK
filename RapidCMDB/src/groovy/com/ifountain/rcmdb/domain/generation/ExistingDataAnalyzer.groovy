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
        oldKeyProperties.each{String propname->
            if(!(newKeyProperties.contains(propname) && newClassProperties[propname].type == oldClassProperties[propname].type))
            {
                willDeleteAll = true;
                return;
            }
        }
        if(willDeleteAll)
        {
            actions += new ModelAction(modelName:currentDomainObject.name, action:ModelAction.DELETE_ALL_INSTANCES);
        }
        
        newClassProperties.each{String propName, GrailsDomainClassProperty prop->
            GrailsDomainClassProperty oldProperty = oldClassProperties.remove(propName);
            ConstrainedProperty oldConstrainedProp = oldConstrainedProps[propName];
            ConstrainedProperty newConstrainedProp = newConstrainedProps[propName];
            if(oldProperty == null || oldProperty.type != prop.type || oldConstrainedProp == null || newConstrainedProp == null)
            {
                if(!willDeleteAll)
                {
                    actions += new PropertyAction(modelName:currentDomainObject.name, propName:propName, action:PropertyAction.SET_DEFAULT_VALUE);
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

                boolean isOldMany = oldRelation.isOneToMany() || oldRelation.isManyToMany();
                boolean isNewMany = newRelation.isOneToMany() || newRelation.isManyToMany();

                boolean isOldOthersideMany = oldRelation.hasOtherSide() && (oldRelation.isManyToOne() || oldRelation.isManyToMany())
                boolean isNewOthersideMany = newRelation.hasOtherSide() && (newRelation.isManyToOne() || newRelation.isManyToMany())
                if(isOldMany != isNewMany || isOldOthersideMany != isNewOthersideMany)
                {
                    if(!willDeleteAll)
                    {
                        actions += new PropertyAction(modelName:currentDomainObject.name, propName:relationName, action:PropertyAction.CLEAR_RELATION);
                    }
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
        }

        if(willResourcesBeRegenerated)
        {
            actions += new ModelAction(modelName:currentDomainObject.name, action:ModelAction.GENERATE_RESOURCES);   
        }
        
        return actions;
    }

    private static Map getPropertyMap(GrailsDomainClass domainObject)
    {

        def newClassProperties = [:];
        domainObject.getProperties().each{GrailsDomainClassProperty prop->
           if(!prop.isAssociation() && !excludedProps.containsKey(prop.name))
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