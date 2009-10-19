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

import com.ifountain.rcmdb.domain.util.ValidationUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants
import datasource.BaseDatasource
import org.apache.commons.beanutils.ConversionException
import org.apache.commons.beanutils.ConvertUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import com.ifountain.rcmdb.converter.RapidConvertUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 3:15:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidCmdbDomainPropertyInterceptor extends DefaultDomainClassPropertyInterceptor {
    def logger = Logger.getLogger(RapidCmdbDomainPropertyInterceptor.class);
    PropertyDatasourceManagerBean bean;
    public Object getDomainClassProperty(MetaClass domainMetaClass, Class domainClass, Object domainObject, String propertyName) {
        PropertyDatasourceManagerBean bean = getBean();
        if (bean.isFederated(domainClass, propertyName)) {
            return getFederatedProperty(domainMetaClass, domainClass, domainObject, bean, propertyName);
        }
        return super.getDomainClassProperty(domainMetaClass, domainClass, domainObject, propertyName);
    }

    private PropertyDatasourceManagerBean getBean()
    {
        if(bean == null){
            if (ServletContextHolder.servletContext != null) {
                ApplicationContext appContext = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
                if (appContext.containsBean(PropertyDatasourceManagerBean.BEAN_ID))
                {
                    bean = appContext.getBean(PropertyDatasourceManagerBean.BEAN_ID)
                }
            }
        }
        return bean;
    }

    private Object convertAndSetDomainClassProperty(MetaClass mtCls, Class cls, Map federatedPropertyCache, String propName, Object propValue, Class fieldType, BindingResult bindingResult)
    {
        try
        {
            if (propValue != null)
            {
                def converter = RapidConvertUtils.getInstance().lookup(fieldType);
                propValue = converter.convert(fieldType, propValue);
                federatedPropertyCache.put(propName, propValue);
                return propValue;
            }
        }
        catch (ConversionException t)
        {
            ValidationUtils.addFieldError(bindingResult, propName, propValue, "default.federation.property.conversion.exception", [propName, cls, t.toString()]);
            logger.warn("Exception occured while converting federated property ${propName} of ${cls.name}.Reason:${t.getMessage()}");
            logger.info("Exception occured while converting federated property ${propName} of ${cls.name}.Reason:${t.getMessage()}", t);
        }
        return null;
    }
    private Object getFederatedProperty(MetaClass mtCls, Class cls, Object domainObject, PropertyDatasourceManagerBean bean, String propName) {
        DatasourceProperty requestedPropertyConfiguration = bean.getPropertyConfiguration(cls, propName);
        def datasourceName = requestedPropertyConfiguration.datasourceName;
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(domainObject, cls.getName());
        if (datasourceName) {
            def realDsName = datasourceName;
            if (requestedPropertyConfiguration.isDynamic)
            {
                realDsName = super.getDomainClassProperty(mtCls, cls, domainObject, datasourceName);
            }
            def mappedDatasourceNameConfiguration = bean.getMappedDatasourceName(cls, realDsName)
            if (mappedDatasourceNameConfiguration == null)
            {
                ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.definition.exception", [propName, cls, realDsName]);
                logger.warn("No datasource is defined with name ${realDsName} for property ${propName} in model ${cls.name}");
            }
            else
            {
                def baseDatasourceName = mappedDatasourceNameConfiguration.name;
                if (mappedDatasourceNameConfiguration.isProperty)
                {
                    baseDatasourceName = super.getDomainClassProperty(mtCls, cls, domainObject, baseDatasourceName);
                }
                def isPropsLoadedMap = domainObject[RapidCMDBConstants.DYNAMIC_PROPERTY_STORAGE];
                if (isPropsLoadedMap == null) {
                    isPropsLoadedMap = [:];
                    super.setDomainClassProperty(mtCls, cls, domainObject, RapidCMDBConstants.DYNAMIC_PROPERTY_STORAGE, isPropsLoadedMap);
                }
                if (requestedPropertyConfiguration.isLazy) {
                    def datsourceKeys = bean.getDatasourceKeys(cls, realDsName)
                    def keys = [:];
                    datsourceKeys.each {DatasourceProperty key ->
                        keys[key.nameInDatasource] = domainObject[key.name];
                    }
                    BaseDatasource datasourceObject = BaseDatasource.getOnDemand(name: baseDatasourceName);
                    if (datasourceObject)
                    {

                        def propValue = null;
                        try
                        {
                            propValue = datasourceObject.getProperty(keys, requestedPropertyConfiguration.nameInDatasource);
                            propValue = convertAndSetDomainClassProperty(mtCls, cls, isPropsLoadedMap, propName, propValue, requestedPropertyConfiguration.type, bindingResult);
                            if(propValue != null)
                            {
                                return propValue
                            }
                        }
                        catch (Throwable t)
                        {
                            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.exception", [propName, cls, baseDatasourceName, t.toString()]);
                            logger.warn("Exception occured while getting federated property ${propName} of ${cls.name} with getProperty method of ${baseDatasourceName}. Reason:${t.getMessage()}");
                        }

                    }
                    else
                    {
                        ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.not.exist", [propName, cls, baseDatasourceName]);
                    }
                }
                else {
                    if(isPropsLoadedMap.containsKey(propName))
                    {
                        return isPropsLoadedMap.get(propName); 
                    }
                    def datsourceKeys = bean.getDatasourceKeys(cls, realDsName)
                    def keys = [:];
                    def datasourceProperties = bean.getDatasourceProperties(cls, datasourceName);
                    datsourceKeys.each {DatasourceProperty key ->
                        keys[key.nameInDatasource] = domainObject[key.name];
                    }
                    def props = [];
                    datasourceProperties.each {DatasourceProperty prop ->
                        if (!prop.isLazy) {
                            props << prop.nameInDatasource;
                        }
                    }

                    def datasourceObject = BaseDatasource.getOnDemand(name: baseDatasourceName);
                    if (datasourceObject)
                    {
                        Map returnedProps;

                        try
                        {
                            returnedProps = datasourceObject.getProperties(keys, props);
                            datasourceProperties.each {DatasourceProperty prop ->
                                convertAndSetDomainClassProperty(mtCls, cls, isPropsLoadedMap, prop.name, returnedProps[prop.nameInDatasource], prop.type, bindingResult);
                            }
                            def propValue = isPropsLoadedMap[propName]
                            if(propValue != null)
                            {
                                return propValue
                            }
                        } catch (Throwable t)
                        {
                            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.get.properties.exception", [cls.name, propName, baseDatasourceName, t.toString()]);
                            logger.warn("Exception occured while getting federated property ${propName} of ${cls.name} with getProperties method of ${baseDatasourceName}.Reason:${t.getMessage()}");
                        }

                    }
                    else
                    {
                        ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.not.exist", [propName, cls, baseDatasourceName]);
                    }
                }
            }
        }
        else
        {
            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.not.federated", [propName, cls]);
            logger.warn("Property ${propName} in model ${cls.name} is not a federated.");
        }
        if (bindingResult.hasErrors())
        {
            domainObject.errors = bindingResult;
        }
        return super.getDomainClassProperty(mtCls, cls, domainObject, propName);
    }

}