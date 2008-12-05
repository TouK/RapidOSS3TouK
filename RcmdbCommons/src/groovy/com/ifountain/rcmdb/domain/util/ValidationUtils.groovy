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
            if(relationMetData.containsKey(propName))
            {
                def value = null
                if(domainObject.id != null)
                {
                    value = domainObject[propName];
                }
                if(props.containsKey(propName))
                {
                    def newValue = props[propName];
                    def metaData = relationMetData[propName]
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
                }
                emptyBean.setProperty(propName, value, false);
            }
            else
            {
                def propValue = domainObject[propName];
                emptyBean.setProperty(propName, propValue, false);
            }
        }
        return emptyBean;
    }
}