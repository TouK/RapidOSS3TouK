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

        Map keyMap = [:];
        keys.each{key->
            keyMap[key] = target.getProperty(key);
        }
        Object res = constraintOwningClass.'getFromHierarchy'(keyMap);
        if(res != null && target.id != res.id)
        {
            List args = [constraintPropertyName, constraintOwningClass, propertyValue ];
            super.rejectValue(target, errors, DEFAULT_NOT_UNIQUE_MESSAGE_CODE, args as Object[], getDefaultMessage(DEFAULT_NOT_UNIQUE_MESSAGE_CODE));
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