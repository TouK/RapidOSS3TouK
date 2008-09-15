package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import java.lang.reflect.Modifier

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
    def allDomainClassProperties = [];
    public GetPropertiesMethod(GrailsDomainClass dc) {
        this.dc = dc;
        def grailsDomainClassProperties = dc.getProperties();
        def relations = DomainClassUtils.getRelations(dc);
        def propsToBeFiltered = ["version", RapidCMDBConstants.ERRORS_PROPERTY_NAME, RapidCMDBConstants.OPERATION_PROPERTY_NAME, RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED]
        grailsDomainClassProperties.each{GrailsDomainClassProperty prop->
            if(!propsToBeFiltered.contains(prop.name))
            {
                allDomainClassProperties.add(new RapidDomainClassProperty(name:prop.name, isRelation:relations.containsKey(prop.name), isOperationProperty:false))
            }
        }
        allProperties = [];
        allProperties.addAll(allDomainClassProperties);
        Collections.sort (allProperties);
        allProperties = Collections.unmodifiableList(allProperties);
    }

    public void setOperationClass(Class operationClass)
    {
        allProperties = [];
        allProperties.addAll(allDomainClassProperties);
        if(operationClass != null)
        {
            def propsToBefiltered = ["domainObject", "class", "properties", "metaClass"]
            operationClass.metaClass.getProperties().each{MetaBeanProperty prop->
                if(!propsToBefiltered.contains(prop.name))
                {
                    def isPrivate = Modifier.isPrivate(prop.getModifiers())
                    def isMethodsPrivate = true;
                    if(prop.getter != null)
                    {
                        isMethodsPrivate = isMethodsPrivate && Modifier.isPrivate(prop.getGetter().getModifiers())
                    }
                    if(prop.setter != null)
                    {
                        isMethodsPrivate = isMethodsPrivate && Modifier.isPrivate(prop.getSetter().getModifiers())
                    }
                    if(!isPrivate && !isMethodsPrivate)
                    {
                        allProperties.add(new RapidDomainClassProperty(name:prop.name, isRelation:false, isOperationProperty:true));
                    }
                }
            }
            Collections.sort (allProperties);
            allProperties = Collections.unmodifiableList(allProperties);
        }
    }

    public List getDomainObjectProperties() {
        return allProperties;
    }
}

class RapidDomainClassProperty  implements Comparable
{
    String name;
    boolean isRelation;
    boolean isOperationProperty;

    public int compareTo(Object o) {
        RapidDomainClassProperty other = o;
        return name.compareTo(other.name);
    }

}