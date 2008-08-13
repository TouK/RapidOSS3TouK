package com.ifountain.rcmdb.domain

import com.ifountain.rcmdb.test.util.RapidCmdbMockTestCase
import com.ifountain.rcmdb.domain.property.DefaultDomainClassPropertyInterceptor
import org.codehaus.groovy.grails.validation.ConstrainedProperty
import com.ifountain.rcmdb.domain.constraints.KeyConstraint
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 11, 2008
 * Time: 10:09:19 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidDomainClassGrailsPluginTest extends RapidCmdbMockTestCase
{
    def domainClassName = "DomainClass1"
    Class loadedDomainClass;

    public void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        loadedDomainClass = gcl.parseClass("""
            class ${domainClassName}{
                Long id;
                Long version;
                String prop1;
                Long prop2;
            }
        """)
        def classesTobeLoaded = [loadedDomainClass];
        configParams[RapidCMDBConstants.PROPERTY_INTERCEPTOR_CLASS_CONFIG_NAME] = DomainPropertyInterceptorDomainClassGrailsPluginImpl.name;
        def pluginsToLoad = [gcl.loadClass("RapidDomainClassGrailsPlugin")];
        initialize(classesTobeLoaded, pluginsToLoad)
    }

    public void testDoWithSpring()
    {
        assertTrue(appCtx.getBean("domainPropertyInterceptor") instanceof DomainPropertyInterceptorDomainClassGrailsPluginImpl);
        assertTrue(ConstrainedProperty.hasRegisteredConstraint(KeyConstraint.KEY_CONSTRAINT));
    }

    public void testPropertyIntercepting()
    {
        def instance = loadedDomainClass.newInstance();
        instance.prop1 = "prop1Value";
        assertEquals ("prop1Value", instance.prop1);
        DomainPropertyInterceptorDomainClassGrailsPluginImpl interceptor = appCtx.getBean("domainPropertyInterceptor");
        assertEquals(1, interceptor.getPropertyList.size());
        assertEquals(1, interceptor.setPropertyList.size());
    }

}

class DomainPropertyInterceptorDomainClassGrailsPluginImpl extends DefaultDomainClassPropertyInterceptor
{
    def setPropertyList = []
    def getPropertyList = []
    public void setDomainClassProperty(Object domainObject, String propertyName, Object value) {
        super.setDomainClassProperty(domainObject, propertyName, value);    //To change body of overridden methods use File | Settings | File Templates.
        setPropertyList += propertyName
    }

    public Object getDomainClassProperty(Object domainObject, String propertyName) {
        getPropertyList += propertyName
        return super.getDomainClassProperty(domainObject, propertyName);    //To change body of overridden methods use File | Settings | File Templates.
    }

}