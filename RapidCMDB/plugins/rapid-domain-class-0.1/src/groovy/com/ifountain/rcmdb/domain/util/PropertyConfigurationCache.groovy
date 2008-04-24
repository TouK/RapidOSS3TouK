package com.ifountain.rcmdb.domain.util

import com.ifountain.comp.utils.CaseInsensitiveMap
import org.codehaus.groovy.grails.commons.GrailsClassUtils

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
 * User: Administrator
 * Date: Apr 24, 2008
 * Time: 2:33:24 PM
 * To change this template use File | Settings | File Templates.
 */
class PropertyConfigurationCache
{
    def propertiesByName;
    def propertiesByNameInDs;
    def datasourceProperties;
    def domainMetaClass;
    public PropertyConfigurationCache(domainClass)
    {
        domainMetaClass = domainClass.metaClass;
        propertiesByName = [:];
        propertiesByNameInDs = new CaseInsensitiveMap();
        datasourceProperties = [:];
        constructPropertyConfiguration(domainClass);
        propertiesByName.each{key, value->
            value.name = key;
            def nameInDs = getNameInDs(value);
            propertiesByNameInDs[nameInDs] = value;
            def propertyDs = value.datasource;
            if(!propertyDs)
            {
                propertyDs =  value.datasourceProperty;
            }
            if(!value.lazy)
            {
                def dsProps = datasourceProperties[propertyDs];
                if(!dsProps)
                {
                    dsProps = [:];
                    datasourceProperties[propertyDs] = dsProps;
                }
                dsProps[key] = nameInDs;
            }

        }
    }

    def hasPropertyConfiguration()
    {
        return propertiesByName.size() > 0;
    }

    def getDatasourceName(domainObject, propertyName)
    {
        def propertyConfig = propertiesByName[propertyName];
        if(propertyConfig)
        {
            def datasourceName =  propertyConfig.datasource;
            if(!datasourceName)
            {
                def referencedDatasourceName =  propertyConfig.datasourceProperty;
                if(referencedDatasourceName)
                {
                    def metaProp = domainMetaClass.getMetaProperty(referencedDatasourceName);
                    if(metaProp)
                    {
                        datasourceName = metaProp.getProperty(domainObject);
                    }
                }
            }
            return datasourceName;
        }
        return null;

    }


    def constructPropertyConfiguration(domainClass)
    {
        def realClass = domainClass.metaClass.getTheClass();
        def superClass = realClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructPropertyConfiguration(superClass);
        }
        if(domainClass.metaClass.hasProperty(domainClass, "propertyConfiguration"))
        {
            def propertyConfig = GrailsClassUtils.getStaticPropertyValue (realClass, "propertyConfiguration");
            if(propertyConfig)
            {
                propertiesByName.putAll(propertyConfig);
            }
        }
    }

    def getPropertyConfigByName(propName)
    {
        return propertiesByName[propName];
    }

    def getPropertyConfigByNameInDs(propName)
    {
        return propertiesByNameInDs[propName];
    }

    def getDatasouceProperties(domainObject, propertyName, isPropsLoaded)
    {
        def propConfig = propertiesByName[propertyName];
        if(isPropsLoaded != true)
        {
            def dsName = propConfig.datasource;
            if(!dsName)
            {
                dsName = propConfig.datasourceProperty;
            }
            if(datasourceProperties[dsName])
            {
                def requestedProps = new HashMap(datasourceProperties[dsName]);
                requestedProps[propertyName] = getNameInDs(propConfig);
                return new ArrayList(requestedProps.values());
            }
            else
            {
                return [getNameInDs(propConfig)];
            }
        }
        else
        {
            return [getNameInDs(propConfig)];
        }
    }

    def getNameInDs(propertyConfig)
    {
        def nameInDs = propertyConfig.nameInDs;
        if(!nameInDs)
        {
            nameInDs = propertyConfig.name;
        }
        return nameInDs;
    }


    def getDatasource(domainObject, propertyName)
    {
        def datasourceName = getDatasourceName(domainObject, propertyName);
        if(datasourceName)
        {
           return BaseDatasource.findByName(datasourceName)
        }
        return null;
    }
}