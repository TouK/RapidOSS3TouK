package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.domain.IdGenerator
import org.apache.commons.beanutils.ConvertUtils
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils
import org.springframework.validation.Errors
import com.ifountain.rcmdb.domain.util.ValidationUtils
import org.springframework.validation.Validator
import org.apache.commons.beanutils.ConversionException
import org.springframework.validation.BeanPropertyBindingResult

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 24, 2008
 * Time: 2:06:19 PM
 * To change this template use File | Settings | File Templates.
 */
class UpdateMethod extends AbstractRapidDomainMethod{

    def relations;
    def keys;
    def fieldTypes = [:]
    Validator validator;
    public UpdateMethod(MetaClass mc, Validator validator, Map relations, List keys) {
        super(mc); //To change body of overridden methods use File | Settings | File Templates.
        this.validator = validator;
        def fields = mc.getProperties();
        fields.each{field->
            fieldTypes[field.name] = field.type;
        }
        this.relations = relations;
        this.keys = keys;
    }

    public Object invoke(Object domainObject, Object[] arguments) {
        def props = arguments[0];
        def relationToBeAddedMap = [:]
        def relationToBeRemovedMap = [:]
        Errors errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
        props.each{key,value->
            if(!relations.containsKey(key))
            {
                def fieldType = fieldTypes[key];
                if(fieldType)
                {
                    if(value != null)
                    {
                        try
                        {
                            def converter = RapidConvertUtils.getInstance().lookup (fieldType);
                            domainObject.setProperty (key, converter.convert(fieldType, value));
                        }
                        catch(ConversionException exception)
                        {
                            ValidationUtils.addFieldError (errors, key, value, "rapidcmdb.invalid.property.type", [fieldType.name, value.class.name]);
                        }
                    }
                    else
                    {
                        domainObject.setProperty (key, value);
                    }
                }
            }
            else
            {
                if(value)
                {
                    relationToBeAddedMap[key] = value;
                }
                else
                {
                    def currentRelatedObjects = domainObject[key];
                    if(currentRelatedObjects)
                    {
                        relationToBeRemovedMap[key] = currentRelatedObjects;
                    }
                }
            }
        }
        if(!errors.hasErrors())
        {
            validator.validate (domainObject, errors);
        }
        if(!errors.hasErrors())
        {
            domainObject.index(domainObject);
            domainObject.addRelation(relationToBeAddedMap);
            domainObject.removeRelation(relationToBeRemovedMap);
        }
        else
        {
            domainObject.setProperty("errors", errors);
        }
        return domainObject;
    }


}