package com.ifountain.rcmdb.domain.util

import org.springframework.validation.*

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 23, 2008
* Time: 8:33:24 AM
* To change this template use File | Settings | File Templates.
*/
class ValidationUtils {
    public static Errors validate(Validator validator, Object target)
    {
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        if(validator != null) {
            validator.validate(target,errors);
        }

        return errors;
    }

    public static void addFieldError(BindingResult errors, String propName, Object propValue, String messageCode, List params)
    {
        FieldError error = new FieldError( errors.getObjectName(),propName,propValue,false,[messageCode] as String[], params as Object[], "");
        (( BindingResult ) errors).addError( error );
    }

    public static void  addObjectError(BindingResult errors, String messageCode, List params)
    {
        ObjectError error = new ObjectError( errors.getObjectName(),[messageCode] as String[], params as Object[], "");
        (( BindingResult ) errors).addError( error );
    }

    public static Object createValidationBean(Object domainObject, Map props, Map relationMetData, Map fields, boolean clearOldValues = false)
    {
        def emptyBean = domainObject.class.newInstance()
        fields.each{String propName, type->
            def propValue = domainObject[propName];
            if(relationMetData.containsKey(propName) && props.containsKey(propName))
            {
                def newValue = props[propName];
                def metaData = relationMetData[propName]
                def value = domainObject[propName];
                if(metaData.isOneToOne() || metaData.isManyToOne())
                {
                    value = newValue instanceof Collection?newValue[0]:newValue;
                }
                else
                {
                    value = !clearOldValues && value instanceof Collection?value:[];
                    def isCollection = newValue instanceof Collection;
                    if(newValue != null && !isCollection)
                    {
                        value.add(newValue)
                    }
                    else if(isCollection)
                    {
                        value.addAll(newValue);
                    }
                }
                emptyBean.setProperty(propName, value, false);
            }
            else
            {
                emptyBean.setProperty(propName, propValue, false);
            }
        }
        return emptyBean;
    }
}