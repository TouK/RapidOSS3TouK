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
    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        if (ServletContextHolder.servletContext != null) {
            ApplicationContext appContext = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
            if (appContext.containsBean(PropertyDatasourceManagerBean.BEAN_ID))
            {
                PropertyDatasourceManagerBean bean = appContext.getBean(PropertyDatasourceManagerBean.BEAN_ID)
                if (bean.isFederated(domainObject.class, propertyName)) {
                    return getFederatedProperty(domainObject, bean, propertyName);
                }
            }
        }
        return super.getDomainClassProperty(domainObject, propertyName);
    }

    private void convertAndSetDomainClassProperty(Object domainObject, String propName, Object propValue, Class fieldType, BindingResult bindingResult)
    {
        try
        {
            if (propValue != null)
            {
                def converter = RapidConvertUtils.getInstance().lookup(fieldType);
                super.setDomainClassProperty(domainObject, propName, converter.convert(fieldType, propValue));
            }
        }
        catch (ConversionException t)
        {
            ValidationUtils.addFieldError(bindingResult, propName, propValue, "default.federation.property.conversion.exception", [propName, domainObject.class, t.toString()]);
            logger.warn("Exception occured while converting federated property ${propName} of ${domainObject.class.name}.Reason:${t.getMessage()}");
            logger.info("Exception occured while converting federated property ${propName} of ${domainObject.class.name}.Reason:${t.getMessage()}", t);
        }
    }
    private Object getFederatedProperty(Object domainObject, PropertyDatasourceManagerBean bean, String propName) {
        DatasourceProperty requestedPropertyConfiguration = bean.getPropertyConfiguration(domainObject.class, propName);
        def datasourceName = requestedPropertyConfiguration.datasourceName;
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        if (datasourceName) {
            def realDsName = datasourceName;
            if (requestedPropertyConfiguration.isDynamic)
            {
                realDsName = super.getDomainClassProperty(domainObject, datasourceName);
            }
            def mappedDatasourceNameConfiguration = bean.getMappedDatasourceName(domainObject.class, realDsName)
            if (mappedDatasourceNameConfiguration == null)
            {
                ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.definition.exception", [propName, domainObject.class, realDsName]);
                logger.warn("No datasource is defined with name ${realDsName} for property ${propName} in model ${domainObject.class.name}");
            }
            else
            {
                def baseDatasourceName = mappedDatasourceNameConfiguration.name;
                if (mappedDatasourceNameConfiguration.isProperty)
                {
                    baseDatasourceName = super.getDomainClassProperty(domainObject, baseDatasourceName);
                }
                if (requestedPropertyConfiguration.isLazy) {
                    def datsourceKeys = bean.getDatasourceKeys(domainObject.class, realDsName)
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
                            convertAndSetDomainClassProperty(domainObject, propName, propValue, requestedPropertyConfiguration.type, bindingResult);
                        }
                        catch (Throwable t)
                        {
                            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.exception", [propName, domainObject.class, baseDatasourceName, t.toString()]);
                            logger.warn("Exception occured while getting federated property ${propName} of ${domainObject.class.name} with getProperty method of ${baseDatasourceName}. Reason:${t.getMessage()}");
                        }

                    }
                    else
                    {
                        ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.not.exist", [propName, domainObject.class, baseDatasourceName]);
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
                        def keys = [:];
                        def datasourceProperties = bean.getDatasourceProperties(domainObject.class, datasourceName);
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
                                    convertAndSetDomainClassProperty(domainObject, prop.name, returnedProps[prop.nameInDatasource], prop.type, bindingResult);
                                }
                                isPropsLoadedMap[datasourceName] = true;

                            } catch (Throwable t)
                            {
                                ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.get.properties.exception", [domainObject.getClass().name, propName, baseDatasourceName, t.toString()]);
                                logger.warn("Exception occured while getting federated property ${propName} of ${domainObject.class.name} with getProperties method of ${baseDatasourceName}.Reason:${t.getMessage()}");
                            }

                        }
                        else
                        {
                            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.datasource.not.exist", [propName, domainObject.class, baseDatasourceName]);
                        }
                    }
                }
            }
        }
        else
        {
            ValidationUtils.addFieldError(bindingResult, propName, null, "default.federation.property.not.federated", [propName, domainObject.class]);
            logger.warn("Property ${propName} in model ${domainObject.class.name} is not a federated.");
        }
        if (bindingResult.hasErrors())
        {
            domainObject.errors = bindingResult;
        }
        return super.getDomainClassProperty(domainObject, propName);
    }

}