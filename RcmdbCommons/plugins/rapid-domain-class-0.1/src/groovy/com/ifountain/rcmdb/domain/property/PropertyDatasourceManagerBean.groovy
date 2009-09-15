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
package com.ifountain.rcmdb.domain.property

import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.commons.GrailsClassUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 12, 2008
 * Time: 10:17:05 AM
 * To change this template use File | Settings | File Templates.
 */
class PropertyDatasourceManagerBean implements InitializingBean, FederatedPropertyManager
{
    public static BEAN_ID = "propertyDatasourceManager";
    private Map datasourceProperties;
    private Map datasourceKeys;
    private Map mappedDatasourceNamesConfiguration;
    private Map propertyConfiguration;
    public void afterPropertiesSet()
    {
        mappedDatasourceNamesConfiguration = new HashMap()
        propertyConfiguration = new HashMap()
        datasourceKeys = new HashMap()
        datasourceProperties = new HashMap()
    }

    public List getDatasourceKeys(Class domainClass, String datasourceName)
    {
        def domainObjectDatasourceKeys=  datasourceKeys.get(domainClass.name);
        if(domainObjectDatasourceKeys == null)
        {
            domainObjectDatasourceKeys = [:]
            constructKeyConfiguration(domainClass, domainObjectDatasourceKeys)
            datasourceKeys.put(domainClass.name, domainObjectDatasourceKeys);
        }
        return domainObjectDatasourceKeys[datasourceName];
    }

    public MappedDatasourceName getMappedDatasourceName(Class domainObject, String datasourceName) {
        def mappedDatasourceNames =  mappedDatasourceNamesConfiguration[domainObject.name]
        if(mappedDatasourceNames == null)
        {
            mappedDatasourceNames = [:]
            constructMappedDatasourceNames(domainObject, mappedDatasourceNames);
        }
        return mappedDatasourceNames[datasourceName];
    }
    def getPropertyConfiguration(Class domainObject, String propName) {
        return  getPropertyConfigurations(domainObject)[propName];
    }

    public String getPropertyDatasource(Class domainClass, String propName)
    {
        return getPropertyConfigurations(domainClass)[propName]?.datasourceName   
    }

    public boolean isFederated(Class domainClass, String propName)
    {
        def propConfig = getPropertyConfigurations(domainClass)[propName];
        if(propConfig != null)
        {
            return propConfig.isFederated();
        }
        return false;
    }

    public boolean isLazy(Class domainClass, String propName)
    {
        return getPropertyConfigurations(domainClass)[propName]?.isLazy;
    }

    private Map getPropertyConfigurations(Class domainClass)
    {
        def domainObjectProps=  propertyConfiguration.get(domainClass.name)
        if(domainObjectProps == null)
        {
            domainObjectProps = [:]
            constructPropertyConfiguration(domainClass, domainObjectProps)
            propertyConfiguration[domainClass.name] = domainObjectProps;
        }
        return domainObjectProps; 
    }

    public List getDatasourceProperties(Class domainClass, String datasourceName)
    {
        def domainObjectDatasourceProps=  datasourceProperties.get(domainClass.name)
        if(domainObjectDatasourceProps == null)
        {
            domainObjectDatasourceProps = [:]
            def propertyConfigurations = getPropertyConfigurations(domainClass);
            propertyConfigurations.each{String propName, DatasourceProperty propConfig->
                def dsConf = domainObjectDatasourceProps[propConfig.datasourceName];
                if(dsConf == null)
                {
                    dsConf = [];
                    domainObjectDatasourceProps[propConfig.datasourceName] = dsConf;
                }
                dsConf << propConfig;
            }
            datasourceProperties[domainClass.name] = domainObjectDatasourceProps 
        }
        return domainObjectDatasourceProps[datasourceName];
    }
    private void constructKeyConfiguration(Class domainClass, Map keys)
    {
        def superClass = domainClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructKeyConfiguration(superClass, keys);
        }
        def propertyConfigs = GrailsClassUtils.getStaticPropertyValue (domainClass, "datasources");
        if(propertyConfigs)
        {
            propertyConfigs.each{String datasourceName, Map datsourceConfig->
                def dsConf = [];
                keys[datasourceName] = dsConf;
                datsourceConfig.keys.each{String dsKey, Map dsKeyConfig->
                    def type = domainClass.metaClass.getMetaProperty(dsKey).type;                    
                    dsConf << new DatasourceProperty(type:type,name:dsKey, nameInDatasource:dsKeyConfig.nameInDs, datasourceName:datasourceName);
                }

            }
        }
    }

    private void constructMappedDatasourceNames(Class domainClass, Map mappedDatasourceNames)
    {
        def superClass = domainClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructMappedDatasourceNames(superClass, mappedDatasourceNames);
        }
        def propertyConfigs = GrailsClassUtils.getStaticPropertyValue (domainClass, "datasources");
        if(propertyConfigs)
        {
            propertyConfigs.each{String datasourceName, Map datsourceConfig->
                def mappedName = datsourceConfig.mappedName
                boolean isProperty = false;
                if(mappedName == null)
                {
                    mappedName = datsourceConfig.mappedNameProperty;
                    if(mappedName != null)
                    {
                        isProperty = true;
                    }
                    else
                    {
                        mappedName = datasourceName;
                    }
                }
                mappedDatasourceNames[datasourceName] = new MappedDatasourceName(mappedName, isProperty);
            }
        }
    }
    
    private void constructPropertyConfiguration(Class domainClass, Map propertiesByName)
    {
        def superClass = domainClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructPropertyConfiguration(superClass, propertiesByName);
        }
        def propertyConfigs = GrailsClassUtils.getStaticPropertyValue (domainClass, "propertyConfiguration");
        if(propertyConfigs)
        {
            propertyConfigs.each{String propName, Map propConfig->
                def type = domainClass.metaClass.getMetaProperty(propName).type;
                def isDynamic =  propConfig.datasource == null;
                def dsName = propConfig.datasource != null?propConfig.datasource:propConfig.datasourceProperty
                propertiesByName[propName] = new DatasourceProperty(type:type, name:propName, nameInDatasource:propConfig.nameInDs, datasourceName:dsName, isLazy:propConfig.lazy, isDynamic:isDynamic);
            }

        }
    }
}

class MappedDatasourceName
{
    String name;
    boolean isProperty;
    public MappedDatasourceName(String name, boolean isProperty)
    {
        this.name = name;
        this.isProperty = isProperty;
    }
}
class DatasourceProperty
{
    String name;
    String nameInDatasource;
    String datasourceName;
    boolean isDynamic;
    boolean isLazy;
    Class type;
    
    boolean isFederated()
    {
        return datasourceName != null && datasourceName != "";
    }

    public boolean equals(Object obj) {
        if(obj instanceof DatasourceProperty)
        {
            return name == obj.name && nameInDatasource == obj.nameInDatasource && datasourceName == obj.datasourceName  && type == obj.type;
        }
        return false;
    }

}