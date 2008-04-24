package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.GrailsDomainClass
import com.ifountain.rcmdb.domain.util.Relation
import org.codehaus.groovy.grails.exceptions.InvalidPropertyException
import com.ifountain.rcmdb.domain.util.DomainClassUtils

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
    public RemoveRelationMethod(MetaClass mc, GrailsDomainClass domainClass) {
        super(mc, domainClass);
        relations = DomainClassUtils.getRelations(domainClass);

    }

    public Object invoke(Object domainObject, Object[] arguments) {
        def props = arguments[0];
        def flush = arguments[1];
        props.each{key,value->
            if(value)
            {
                Relation relation = relations.get(key);
                if(relation)
                {
                    if(relation.isOneToOne())
                    {
                        RelationMethodUtils.checkInstanceOf(relation, value);
                        RelationMethodUtils.setOneToOne(domainObject, relation, null);
                    }
                    else if(relation.isManyToOne())
                    {
                        domainObject.setProperty(relation.name, null);
                        value."removeFrom${relation.upperCasedOtherSideName}"(domainObject);
                    }
                    else if(relation.isOneToMany())
                    {
                        if(value instanceof Collection)
                        {
                            def childDomains = new ArrayList(value);

                            for(childDomain in childDomains)
                            {
                                RelationMethodUtils.checkInstanceOf(relation, childDomain);
                                childDomain.setProperty(relation.otherSideName, null);
                                childDomain.save();
                                domainObject."removeFrom${relation.upperCasedName}"(childDomain);
                            }
                        }
                        else
                        {
                            RelationMethodUtils.checkInstanceOf(relation, value);
                            value.setProperty(relation.otherSideName, null);
                            value.save();
                            domainObject."removeFrom${relation.upperCasedName}"(value);
                        }
                    }
                    else
                    {
                        if(value instanceof Collection)
                        {
                            def childDomains = new ArrayList(value);
                            for(childDomain in childDomains)
                            {
                                RelationMethodUtils.checkInstanceOf(relation, childDomain);
                                domainObject."removeFrom${relation.upperCasedName}"(childDomain);
                                childDomain."removeFrom${relation.upperCasedOtherSideName}"(domainObject);
                            }
                        }
                        else
                        {
                            RelationMethodUtils.checkInstanceOf(relation, value);
                            domainObject."removeFrom${relation.upperCasedName}"(value);
                            value."removeFrom${relation.upperCasedOtherSideName}"(domainObject);
                        }
                    }
                }
            }
        }
        def res = domainObject.save(flush:flush);
        if(!res)
        {
            return domainObject;
        }
        else
        {
            return res;
        }
    }
}