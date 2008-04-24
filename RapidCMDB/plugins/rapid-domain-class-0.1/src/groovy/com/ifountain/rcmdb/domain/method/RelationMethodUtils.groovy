package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.exceptions.InvalidPropertyException
import com.ifountain.rcmdb.domain.util.Relation

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
 * Time: 1:45:43 PM
 * To change this template use File | Settings | File Templates.
 */
class RelationMethodUtils {
    private  RelationMethodUtils(){}

    def static checkInstanceOf(relation, value)
    {
        if(value && !relation.otherSideClass.isInstance(value))
        {
            throw new InvalidPropertyException ("Invalid relation value for ${relation.name} expected ${relation.otherSideClass.getName()} got ${value.class.name}");
        }
    }

    def static setOneToOne(Object domainObject, Relation relation, relationValue)
    {
        def previousValue = domainObject[relation.name];
        if(previousValue && relationValue && relationValue.id == previousValue.id)
        {
            return;
        }

        if(previousValue)
        {
            previousValue[relation.otherSideName] = null;
            previousValue.save();
        }
        if(relationValue)
        {
            if(relationValue[relation.otherSideName])
            {
                relationValue[relation.otherSideName][relation.name] = null;
                relationValue[relation.otherSideName].save();
            }
            relationValue[relation.otherSideName] = domainObject;
            relationValue.save();
        }
        domainObject[relation.name] = relationValue;
    }
}