package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import org.codehaus.groovy.grails.plugins.CoreGrailsPlugin
import com.ifountain.rcmdb.domain.property.DomainClassPropertyInterceptor
import com.ifountain.rcmdb.domain.property.DomainClassPropertyInterceptorFactoryBean
import com.ifountain.rcmdb.domain.property.DefaultDomainClassPropertyInterceptor
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:09:19 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidDomainClassGrailsPluginTest extends RapidCmdbMockTestCase
{

    public void onBeforeSetup() {
        super.onBeforeSetup();    //To change body of overridden methods use File | Settings | File Templates.
        pluginsToLoad << gcl.loadClass(CoreGrailsPlugin.name);
        pluginsToLoad << gcl.loadClass("RapidDomainClassGrailsPlugin");
    }

    public void onAfterSetup() {
        super.onAfterSetup();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testDoWithSpring()
    {
        assertTrue(appCtx.getBean("domainPropertyInterceptor") instanceof DefaultDomainClassPropertyInterceptor);
        assertTrue(ConstrainedProperty.hasRegisteredConstraint(KeyConstraint.KEY_CONSTRAINT));
    }

}