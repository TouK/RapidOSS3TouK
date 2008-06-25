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
}