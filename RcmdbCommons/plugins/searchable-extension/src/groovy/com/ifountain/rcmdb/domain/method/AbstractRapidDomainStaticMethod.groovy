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
package com.ifountain.rcmdb.domain.method

import org.codehaus.groovy.grails.commons.metaclass.AbstractStaticMethodInvocation

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 24, 2008
* Time: 1:09:05 PM
* To change this template use File | Settings | File Templates.
*/
abstract class AbstractRapidDomainStaticMethod{
    MetaClass mc;
    public AbstractRapidDomainStaticMethod(MetaClass mc) {
        this.mc = mc;
    }

    public Object invoke(Class clazz, Object[] arguments) {
//        if(isWriteOperation())
//        {
//            synchronized (WriteOperationSynchronizer.writeOperationLock)
//            {
//                return _invoke(clazz, arguments);
//            }
//        }
//        else
//        {
            return _invoke(clazz, arguments);
//        }

    }
    abstract boolean isWriteOperation();
    abstract protected Object _invoke(Class clazz, Object[] arguments);
    
}