package org.ifountain.grails.domain;

import org.codehaus.groovy.grails.commons.*;
import org.codehaus.groovy.grails.exceptions.NewInstanceCreationException;
import org.springframework.validation.Validator;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import groovy.lang.MetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Mar 18, 2008
 * Time: 12:53:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class RapidGrailsDomainClass extends DefaultGrailsDomainClass{
    Map propertyConfiguration;
    Map dataSources;
    public RapidGrailsDomainClass(DefaultGrailsDomainClass domainClass) {
        super(domainClass.getClazz());
        Map mappedBy = (Map) GrailsClassUtils.getStaticPropertyValue(domainClass.getClazz(), GrailsDomainClassProperty.MAPPED_BY);
        MetaClass mc = domainClass.getMetaClass();
        if(domainClass.hasProperty("propertyConfiguration") && domainClass.hasProperty("dataSources"))
        {
            dataSources = (Map)domainClass.getPropertyValue("dataSources");
            propertyConfiguration = (Map)domainClass.getPropertyValue("propertyConfiguration");
        }
        else
        {
            dataSources = new HashMap();
            propertyConfiguration = new HashMap();
        }

        GrailsDomainClassProperty[] props = getProperties();
//        for(int i=0; i<props.length; i++)
//        {
//            DefaultGrailsDomainClassProperty prop = props[i];
//        }

    }

    protected Object getPropertyOrStaticPropertyOrFieldValue(String name, Class type) {
        System.out.println("Prop Name:"+name);
        return super.getPropertyOrStaticPropertyOrFieldValue(name, type);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Object getPropertyValue(String name, Class type) {
        System.out.println("Ret:"+name);
        Object val = super.getPropertyValue(name, type);
        System.out.println("Ret:"+name+"=="+val);
        return val;
    }

    public Object getPropertyValue(String name) {
        System.out.println("Ret:"+name);
        Object val = super.getPropertyValue(name);
        System.out.println("Ret:"+name+"=="+val);
        return val;
    }

}
