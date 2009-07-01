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
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import com.ifountain.rcmdb.domain.property.FederatedPropertyManager
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.util.RapidCMDBConstants
import java.lang.reflect.Modifier
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 12, 2008
 * Time: 1:24:09 PM
 * To change this template use File | Settings | File Templates.
 */
class GetPropertiesMethod
{
    GrailsDomainClass dc;
    def allProperties;
    def federatedProperties;
    def relationProperties;
    def nonFederatedProperties;
    def allDomainClassProperties = [];
    public GetPropertiesMethod(GrailsDomainClass dc, FederatedPropertyManager manager) {
        this.dc = dc;
        def grailsDomainClassProperties = dc.getProperties();
        def relations = DomainClassUtils.getRelations(dc);
        def propsToBeFiltered = ["version", RapidCMDBConstants.ERRORS_PROPERTY_NAME, RapidCMDBConstants.OPERATION_PROPERTY_NAME, RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED, ""]
        def keyProps = DomainClassUtils.getKeys(dc);
        grailsDomainClassProperties.each {GrailsDomainClassProperty prop ->
            if (!propsToBeFiltered.contains(prop.name))
            {
                def isKey = keyProps.contains(prop.name);
                RelationMetaData relation = relations.get(prop.name);
                if (relation == null)
                {
                    if (ModelGenerator.VALID_PROPERTY_TYPE_CLASSES.contains(prop.getType()) || prop.getType().isPrimitive())
                    {
                        allDomainClassProperties.add(new RapidDomainClassProperty(name: prop.name, isOperationProperty: false, isKey: isKey, type:prop.getType(), isFederated:manager.isFederated(dc.clazz, prop.name)))
                    }
                }
                else
                {
                    allDomainClassProperties.add(new RapidDomainClassRelation(name: prop.name, relatedModel: relation.otherSideCls, reverseName: relation.otherSideName, type: relation.getType(), isOperationProperty: false, isKey: isKey))
                }

            }
        }

        allProperties = [];
        allProperties.addAll(allDomainClassProperties);
        Collections.sort(allProperties);
        allProperties = Collections.unmodifiableList(allProperties);
        constructCategories();

    }

    private void constructCategories()
    {
        federatedProperties = [];
        nonFederatedProperties = [];
        relationProperties = [];
        allProperties.each{RapidDomainClassProperty prop->
            if(prop.isFederated)
            {
                federatedProperties.add(prop);
            }
            else if(prop.isRelation)
            {
                relationProperties.add(prop);
            }
            else if(!prop.isOperationProperty)
            {
                nonFederatedProperties.add(prop);
            }
        }
        federatedProperties = Collections.unmodifiableList(federatedProperties);
        nonFederatedProperties = Collections.unmodifiableList(nonFederatedProperties);
        relationProperties = Collections.unmodifiableList(relationProperties);
    }

    public void setOperationClass(Class operationClass)
    {
        allProperties = [];
        allProperties.addAll(allDomainClassProperties);
        if (operationClass != null)
        {
            def propsToBeFiltered = ["domainObject", "class", "properties", "metaClass", "rsSetPropertyWillUpdate", "rsUpdatedProps"]
            
            allDomainClassProperties.each{ RapidDomainClassProperty prop ->
                 if(!propsToBeFiltered.contains(prop.name))
                 {
                     propsToBeFiltered.add(prop.name);
                 }
            }

            operationClass.metaClass.getProperties().each {MetaBeanProperty prop ->            
                if (!propsToBeFiltered.contains(prop.name))
                {
                    def isPrivate = Modifier.isPrivate(prop.getModifiers())
                    def isMethodsPrivate = true;
                    if (prop.getter != null)
                    {
                        isMethodsPrivate = isMethodsPrivate && Modifier.isPrivate(prop.getGetter().getModifiers())
                    }
                    if (prop.setter != null)
                    {
                        isMethodsPrivate = isMethodsPrivate && Modifier.isPrivate(prop.getSetter().getModifiers())
                    }
                    if (!isPrivate && !isMethodsPrivate && !Modifier.isStatic(prop.getModifiers()))
                    {
                        allProperties.add(new RapidDomainClassProperty(name: prop.name, isRelation: false, isOperationProperty: true, type:prop.getType()));
                    }
                }
            }
            Collections.sort(allProperties);
            allProperties = Collections.unmodifiableList(allProperties);
        }
    }

    public List getDomainObjectProperties() {
        return allProperties;
    }

    public List getFederatedProperties()
    {
        return federatedProperties;        
    }

    public List getRelations()
    {
        return relationProperties;
    }
    public List getNonFederatedProperties()
    {
        return nonFederatedProperties;
    }
}

class RapidDomainClassProperty implements Comparable
{
    String name;
    boolean isRelation;
    Object type;
    boolean isKey;
    boolean isOperationProperty;
    boolean isFederated;

    public int compareTo(Object o) {
        RapidDomainClassProperty other = o;
        return name.compareTo(other.name);
    }

}

class RapidDomainClassRelation extends RapidDomainClassProperty
{
    String reverseName;
    Class relatedModel;
    boolean isMany;

    public RapidDomainClassRelation()
    {
        this.isRelation = true;
    }

    def isOneToOne()
    {
        return type == RelationMetaData.ONE_TO_ONE;
    }

    def isOneToMany()
    {
        return type == RelationMetaData.ONE_TO_MANY;
    }

    def isManyToOne()
    {
        return type == RelationMetaData.MANY_TO_ONE;
    }

    def isManyToMany()
    {
        return type == RelationMetaData.MANY_TO_MANY;
    }
}