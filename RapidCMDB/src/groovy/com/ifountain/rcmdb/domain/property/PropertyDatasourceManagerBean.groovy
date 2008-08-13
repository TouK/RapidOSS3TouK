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
class PropertyDatasourceManagerBean implements InitializingBean
{
    private Map datasourceProperties;
    private Map datasourceKeys;
    private Map propertyConfiguration;
    public void afterPropertiesSet()
    {
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

    def getPropertyConfiguration(Class domainObject, String propName) {
        return  getPropertyConfigurations(domainObject)[propName];
    }

    public String getPropertyDatasource(Class domainClass, String propName)
    {
        return getPropertyConfigurations(domainClass)[propName]?.datasourceName   
    }

    public boolean isFederated(Class domainClass, String propName)
    {
        return getPropertyConfigurations(domainClass)[propName]?.isFederated();
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
                    dsConf << new DatasourceProperty(name:dsKey, nameInDatasource:dsKeyConfig.nameInDs, datasourceName:datasourceName);
                }

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
                def isDynamic =  propConfig.datasource == null;
                def dsName = propConfig.datasource != null?propConfig.datasource:propConfig.datasourceProperty
                propertiesByName[propName] = new DatasourceProperty(name:propName, nameInDatasource:propConfig.nameInDs, datasourceName:dsName, isLazy:propConfig.lazy, isDynamic:isDynamic);
            }

        }
    }
}

class DatasourceProperty
{
    String name;
    String nameInDatasource;
    String datasourceName;
    boolean isDynamic;
    boolean isLazy;
    boolean isFederated()
    {
        return datasourceName != null && datasourceName != "";
    }

    public boolean equals(Object obj) {
        if(obj instanceof DatasourceProperty)
        {
            return name == obj.name && nameInDatasource == obj.nameInDatasource && datasourceName == obj.datasourceName
        }
        return false;
    }

}