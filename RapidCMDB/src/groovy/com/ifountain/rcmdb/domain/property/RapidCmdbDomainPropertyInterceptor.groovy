package com.ifountain.rcmdb.domain.property

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.util.RapidCMDBConstants
import datasource.BaseDatasource
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 3:15:14 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbDomainPropertyInterceptor extends DefaultDomainClassPropertyInterceptor {

    public void setDomainClassProperty(Object domainObject, String propertyName, Object value) {
        super.setDomainClassProperty(domainObject, propertyName, value);
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        if(ServletContextHolder.servletContext != null){
            PropertyDatasourceManagerBean bean = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("propertyDatasourceManager")
            if (bean.isFederated(domainObject.class, propertyName)) {
                return getFederatedProperty(domainObject, bean, propertyName);
            }
        }
        else {
            return super.getDomainClassProperty(domainObject, propertyName);
        }
    }
    

    def getFederatedProperty(Object domainObject, PropertyDatasourceManagerBean bean, String propName) {
        DatasourceProperty requestedPropertyConfiguration = bean.getPropertyConfiguration(domainObject.class, propName);
        def datasourceName = requestedPropertyConfiguration.datasourceName;
        def realDsName = datasourceName;
        if(requestedPropertyConfiguration.isDynamic)
        {
            realDsName = super.getDomainClassProperty(domainObject, datasourceName);
        }
        if (datasourceName) {
            if (requestedPropertyConfiguration.isLazy) {
                def datsourceKeys = bean.getDatasourceKeys(domainObject.class, realDsName)
                def keys = [:];
                datsourceKeys.each {DatasourceProperty key ->
                    keys[key.nameInDatasource] = domainObject[key.name];
                }
                BaseDatasource datasourceObject = BaseDatasource.get(name: realDsName);
                if(datasourceObject)
                {
                    try
                    {
                        def propValue = datasourceObject.getProperty(keys, requestedPropertyConfiguration.nameInDatasource);
                        super.setDomainClassProperty(domainObject, propName, propValue);
                    }catch(Throwable t)
                    {
                    }
                }
            }
            else {
                def isPropsLoadedMap = domainObject[RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED];
                if (isPropsLoadedMap == null) {
                    isPropsLoadedMap = [:];
                    super.setDomainClassProperty(domainObject, RapidCMDBConstants.IS_FEDERATED_PROPERTIES_LOADED, isPropsLoadedMap);
                }


                if (!isPropsLoadedMap[datasourceName]) {
                    def datsourceKeys = bean.getDatasourceKeys(domainObject.class, realDsName)
                    def datasourceProperties = bean.getDatasourceProperties(domainObject.class, datasourceName);
                    def keys = [:];
                    datsourceKeys.each {DatasourceProperty key ->
                        keys[key.nameInDatasource] = domainObject[key.name];
                    }
                    def props = [];
                    datasourceProperties.each {DatasourceProperty prop ->
                        if (!prop.isLazy) {
                            props << prop.nameInDatasource;
                        }
                    }

                    def datasourceObject = BaseDatasource.get(name: realDsName);
                    if(datasourceObject)
                    {
                        Map returnedProps;
                        try
                        {
                            returnedProps = datasourceObject.getProperties(keys, props);
                            datasourceProperties.each {DatasourceProperty prop ->
                                super.setDomainClassProperty(domainObject, prop.name, returnedProps[prop.nameInDatasource]);
                            }
                            isPropsLoadedMap[datasourceName] = true;
                        }catch(Throwable t)
                        {
                        }
                    }
                }
            }
            return super.getDomainClassProperty(domainObject, propName);
        }

        return null;
    }

}