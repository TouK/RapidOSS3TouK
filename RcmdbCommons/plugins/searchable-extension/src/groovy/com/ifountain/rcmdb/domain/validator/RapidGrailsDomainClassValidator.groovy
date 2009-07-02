package com.ifountain.rcmdb.domain.validator
import org.codehaus.groovy.grails.validation.ConstrainedProperty;

import org.springframework.validation.Errors
import org.codehaus.groovy.grails.validation.GrailsDomainClassValidator
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.springframework.context.MessageSource
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.BeanWrapper
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.runtime.InvokerHelper

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 25, 2009
* Time: 6:10:40 PM
* To change this template use File | Settings | File Templates.
*/
class RapidGrailsDomainClassValidator implements IRapidValidator {
//    private static final List EMBEDDED_EXCLUDES = new ArrayList()
//    {{
//        add(GrailsDomainClassProperty.IDENTITY);
//        add(GrailsDomainClassProperty.VERSION);
//    }};

    private Class targetClass;
    private GrailsDomainClass domainClass;
    private MessageSource messageSource;
    private static final String ERRORS_PROPERTY = "errors";
    public boolean supports(Class clazz) {
        return this.targetClass.equals(clazz);
    }


    public void setDomainClass(GrailsDomainClass domainClass) {
        this.domainClass = domainClass;
        this.domainClass.setValidator(this);
        this.targetClass = this.domainClass.getClazz();
    }

    public GrailsDomainClass getDomainClass() {
        return domainClass;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validate(Object wrappedObject, Object realObject, Errors errors) {
        BeanWrapper bean = new BeanWrapperImpl(realObject);
         def constarainedProps = domainClass.getConstrainedProperties();
        targetClass.getNonFederatedPropertyList().each{prop->
            def constrainedProperty = constarainedProps[prop.name]
            if(constrainedProperty != null)
            {
                validatePropertyWithConstraint(constrainedProperty, wrappedObject, errors, bean);
            }
        }
//        for (int i = 0; i < persistentProperties.length; i++) {
//            GrailsDomainClassProperty persistentProperty = persistentProperties[i];
//            String propertyName = persistentProperty.getName();
//            if(constrainedProperties.containsKey(propertyName)) {
//                validatePropertyWithConstraint(propertyName, obj, errors, bean, constrainedProperties);
//            }
//
//            if((persistentProperty.isAssociation() || persistentProperty.isEmbedded()) && cascade) {
//                cascadeToAssociativeProperty(errors, bean, persistentProperty);
//            }
//        }

          wrappedObject.setProperty(ERRORS_PROPERTY, errors);
    }

    private void validatePropertyWithConstraint(ConstrainedProperty c, Object obj, Errors errors, BeanWrapper bean) {
        c.setMessageSource(this.messageSource);
        c.validate(obj, obj.getProperty(c.getPropertyName()), errors);
    }

    public void validate(Object o, Errors errors) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}