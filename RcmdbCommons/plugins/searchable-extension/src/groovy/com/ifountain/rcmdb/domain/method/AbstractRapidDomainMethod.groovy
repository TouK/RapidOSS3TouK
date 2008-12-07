package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.metaclass.AbstractDynamicMethodInvocation
import com.ifountain.rcmdb.domain.DomainLockManager
import org.apache.commons.transaction.locking.GenericLock
import com.ifountain.rcmdb.domain.DomainMethodExecutor

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
 * Time: 1:35:27 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractRapidDomainMethod {
    MetaClass mc;
    Class rootParentClass;
    public AbstractRapidDomainMethod(MetaClass mc) {
        this.mc = mc;
        rootParentClass = mc.theClass;
        while(rootParentClass.superclass.name != Object.name)
        {
            rootParentClass = rootParentClass.superclass;
        }
    }

    public final Object invoke(Object domainObject, Object[] arguments) {

        if (isWriteOperation()) {
            String lockName = getLockName(domainObject);
            def executionClosure = {
                return _invoke(domainObject, arguments);
            }
            return DomainMethodExecutor.executeAction(Thread.currentThread(), lockName, executionClosure)
        }
        else {
            return _invoke(domainObject, arguments);
        }

    }

    public String getLockName(Object domainObject) {
        StringBuffer bf = new StringBuffer(rootParentClass.name);
        def keys = domainObject.'keySet'();
        if(keys.isEmpty())
        {
            bf.append (domainObject["id"])
        }
        else
        {
            keys.each{prop->
                bf.append(domainObject[prop.name]);
            }
        }
        return bf.toString();
    }

    abstract boolean isWriteOperation()

    ;

    abstract protected Object _invoke(Object domainObject, Object[] arguments)

    ;
}