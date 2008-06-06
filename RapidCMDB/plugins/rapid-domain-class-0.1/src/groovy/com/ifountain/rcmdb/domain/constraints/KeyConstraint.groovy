package com.ifountain.rcmdb.domain.constraints

import org.codehaus.groovy.grails.validation.AbstractConstraint
import org.springframework.validation.Errors

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 5:11:21 PM
* To change this template use File | Settings | File Templates.
*/
class KeyConstraint extends AbstractConstraint{
    public static final String DEFAULT_NOT_UNIQUE_MESSAGE_CODE = "default.not.unique.message";
    public static final String KEY_CONSTRAINT = "key";
    List keys = new ArrayList();
    protected void processValidate(Object target, Object propertyValue, Errors errors)
    {
        if(target["id"] != null) return;   
        Map keyMap = [:];
        keys.each{key->
            keyMap[key] = target.getProperty(key);
        }
        Object res = ((MetaClass)constraintOwningClass.metaClass).invokeStaticMethod(constraintOwningClass, "get", [keyMap] as Object[]);
        if(res != null)
        {
            List args = [constraintPropertyName, constraintOwningClass, propertyValue ];
            super.rejectValue(target, errors, KEY_CONSTRAINT, args as Object[], getDefaultMessage(DEFAULT_NOT_UNIQUE_MESSAGE_CODE));
        }
    }

    public List getKeys()
    {
        return new ArrayList(keys);
    }

    public boolean isKey()
    {
        return !keys.isEmpty();
    }

    public boolean supports(Class type) {
        return true;
    }

    public String getName() {
        return KEY_CONSTRAINT; //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setParameter(Object constraintParameter) {
        if(constraintParameter instanceof List)
        {
            keys.addAll (constraintParameter);
            keys.add (getPropertyName());
            super.setParameter(constraintParameter); //To change body of overridden methods use File | Settings | File Templates.
        }
        else
        {
            throw new IllegalArgumentException("Parameter for constraint ["+KEY_CONSTRAINT+"] of property ["+constraintPropertyName+"] of class ["+constraintOwningClass+"] must be a list");
        }
    }




}