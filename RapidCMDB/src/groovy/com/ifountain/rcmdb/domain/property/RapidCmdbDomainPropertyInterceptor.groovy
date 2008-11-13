package com.ifountain.rcmdb.domain.property

import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.util.RapidCMDBConstants
import datasource.BaseDatasource
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.apache.commons.beanutils.ConversionException
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.apache.log4j.Logger
import org.apache.commons.beanutils.ConvertUtilsBean
import org.apache.commons.beanutils.ConvertUtils
import org.apache.commons.beanutils.converters.DoubleConverter

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 3:15:14 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidCmdbDomainPropertyInterceptor extends DefaultDomainClassPropertyInterceptor {
    def logger=Logger.getLogger(RapidCmdbDomainPropertyInterceptor.class);
    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        if(ServletContextHolder.servletContext != null){
            ApplicationContext appContext = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
            if(appContext.containsBean(PropertyDatasourceManagerBean.BEAN_ID))
            {
                PropertyDatasourceManagerBean bean = appContext.getBean(PropertyDatasourceManagerBean.BEAN_ID)
                if (bean.isFederated(domainObject.class, propertyName)) {
                    return getFederatedProperty(domainObject, bean, propertyName);
                }
            }
        }
        return super.getDomainClassProperty(domainObject, propertyName);
    }
    
    private void  convertAndSetDomainClassProperty(Object domainObject,String propName,Object propValue,Class fieldType)
    {
        try
        {
            if(propValue != null)
            {
                def converter = ConvertUtils.lookup(fieldType);
                //IF a new instance is not created then converter assigns default values for invalid castings
                //by using a new instance of converter we guarantee that a conversion exception will by thrown 
                converter = converter.class.newInstance();
                super.setDomainClassProperty(domainObject, propName, converter.convert(fieldType, propValue));
            }
            else
            {
                super.setDomainClassProperty(domainObject, propName, propValue);
            }
        }
        catch(ConversionException t)
        {               
            logger.warn("Exception occured while converting federated property ${propName} of ${domainObject.class.name}.Reason:${t.getMessage()}");
            logger.info("Exception occured while converting federated property ${propName} of ${domainObject.class.name}.Reason:${t.getMessage()}", t);
        }
    }
    public Object getFederatedProperty(Object domainObject, PropertyDatasourceManagerBean bean, String propName) {
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

                    def propValue=null;
                    try
                    {
                        propValue = datasourceObject.getProperty(keys, requestedPropertyConfiguration.nameInDatasource);
                        convertAndSetDomainClassProperty(domainObject, propName,propValue,requestedPropertyConfiguration.type);
                    }
                    catch(Throwable t)
                    {
                        logger.warn("Exception occured while getting federated property ${propName} of ${domainObject.class.name}.Reason:${t.getMessage()}");
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
                                convertAndSetDomainClassProperty(domainObject, prop.name,returnedProps[prop.nameInDatasource],prop.type);
                            }
                            isPropsLoadedMap[datasourceName] = true;

                        }catch(Throwable t)
                        {
                            logger.warn("Exception occured while getting federated properties of ${domainObject.class.name}.Reason:${t.getMessage()}");
                        }

                    }
                }
            }
            return super.getDomainClassProperty(domainObject, propName);
        }

        return null;
    }

}