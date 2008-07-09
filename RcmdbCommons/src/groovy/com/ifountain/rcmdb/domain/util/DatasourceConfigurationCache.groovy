package com.ifountain.rcmdb.domain.util

import org.codehaus.groovy.grails.commons.GrailsClassUtils
import datasource.BaseDatasource

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
 * Time: 2:32:49 PM
 * To change this template use File | Settings | File Templates.
 */
class DatasourceConfigurationCache
{
    def datasources;
    def domainMetaClass;
    def masterName;
    public DatasourceConfigurationCache(domainClass)
    {
        datasources = [:];
        constructDatasources(domainClass);
        domainMetaClass = domainClass.metaClass;
        datasources.each{dsName, ds->
            if(ds.master)
            {
                masterName = dsName;
            }
        }
    }

    def hasDatasources()
    {
        return datasources.size() > 0;
    }

    def constructDatasources(domainClass)
    {
        def realClass = domainClass.metaClass.getTheClass();
        def superClass = realClass.getSuperclass();
        if(superClass && superClass != Object.class)
        {
            constructDatasources(superClass);
        }
        if(domainClass.metaClass.hasProperty(domainClass, "datasources"))
        {
            def dataSources = GrailsClassUtils.getStaticPropertyValue (realClass, "datasources");
            if(dataSources)
            {
                datasources.putAll(dataSources);
            }
        }
    }

    def getKeys(domainObject, datasourceName)
    {
        def datasourceConfig = datasources[datasourceName];
        if(datasourceConfig)
        {
            def keyConfiguration = datasourceConfig.keys;
            def keys = [:];
            def isNull = false;
            keyConfiguration.each{key,value->
                def nameInDs = key;
                if(value && value.nameInDs)
                {
                    nameInDs = value.nameInDs;
                }
                def keyValue =   domainMetaClass.getMetaProperty(key).getProperty(domainObject);
                if(keyValue)
                {
                    keys[nameInDs] = keyValue;
                }
                else
                {
                    isNull = true;
                    return;
                }
            }
            return !isNull?keys:null;
        }
        return null;
    }

    def createPropertyDatasource(propertyConfig, domainObject)
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
        if(datasourceName)
        {
           return BaseDatasource.findByName(datasourceName)
        }
    }
}