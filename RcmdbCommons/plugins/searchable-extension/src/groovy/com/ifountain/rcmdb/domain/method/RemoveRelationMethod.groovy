package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.util.RelationMetaData
import com.ifountain.rcmdb.domain.property.RelationUtils
import relation.Relation
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import com.ifountain.rcmdb.domain.util.ValidationUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants

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
 * Time: 1:42:09 PM
 * To change this template use File | Settings | File Templates.
 */
class RemoveRelationMethod extends AbstractRapidDomainMethod{

    def relations;
    public RemoveRelationMethod(MetaClass mc, Map relations) {
        super(mc);
        this.relations = relations;
    }

    public boolean isWriteOperation() {
        return true;
    }

    protected Object _invoke(Object domainObject, Object[] arguments) {
        def props = arguments[0];
        def flush = true;
        if(arguments.length == 2)
        {
            if(arguments[1] == false)
            {
                flush = false;
            }
        }
        def changedInstances = [:]
        boolean isChanged = false;
        props.each{key,value->
            RelationMetaData relation = relations.get(key);
            def storage = [];
            if(relation)
            {
                if(value)
                {
                    value = value instanceof Collection?value:[value]
                    def validValues = [];
                    Errors errors = new BeanPropertyBindingResult(domainObject, domainObject.getClass().getName());
                    value.each{relatedObject->
                        if(!relation.otherSideCls.isInstance(relatedObject))
                        {
                            ValidationUtils.addFieldError (errors, key, relatedObject, "rapidcmdb.invalid.relation.type", [relatedObject.class.name, relation.otherSideCls.name]);
                        }
                        else if(relatedObject.id == null)
                        {
                            ValidationUtils.addFieldError (errors, key, relatedObject, "rapidcmdb.relation.with.nonpersistant.object", [relatedObject]);
                        }
                        else
                        {
                            validValues.add(relatedObject);
                        }
                    }
                    value = validValues;
                    if(errors.hasErrors())
                    {
                        domainObject.setProperty(RapidCMDBConstants.ERRORS_PROPERTY_NAME, errors, false);
                    }

                    RelationUtils.removeRelations(domainObject, relation, value);
                }
            }
            return domainObject;
        }

    }
}