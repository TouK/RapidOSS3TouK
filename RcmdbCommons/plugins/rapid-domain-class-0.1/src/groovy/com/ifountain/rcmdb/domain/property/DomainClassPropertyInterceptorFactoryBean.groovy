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

import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 9:51:57 AM
 * To change this template use File | Settings | File Templates.
 */
class DomainClassPropertyInterceptorFactoryBean implements FactoryBean, InitializingBean{


    String propertyInterceptorClassName;
    ClassLoader classLoader;
    DomainClassPropertyInterceptor propertyInterceptor;

    public DomainClassPropertyInterceptorFactoryBean() {
    }
    public void afterPropertiesSet() {

        def propertyInterceptorClass = DefaultDomainClassPropertyInterceptor;
        try
        {               
            def tmpPropertyInterceptorClass = classLoader.loadClass(propertyInterceptorClassName);
            if(DomainClassPropertyInterceptor.isAssignableFrom(tmpPropertyInterceptorClass))
            {
                propertyInterceptorClass = tmpPropertyInterceptorClass;
            }
        }
        catch(Throwable t)
        {
            org.apache.log4j.Logger.getRootLogger().warn("[DomainClassPropertyInterceptorFactoryBean]: Using default propertyInterceptor, Can not use ${propertyInterceptorClassName} propertyInterceptor. Reason : ${t.toString()}");
        }
        propertyInterceptor = (DomainClassPropertyInterceptor)propertyInterceptorClass.newInstance();
    }

    public Object getObject() {
        return propertyInterceptor;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Class getObjectType() {
        return DomainClassPropertyInterceptor;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isSingleton() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

}