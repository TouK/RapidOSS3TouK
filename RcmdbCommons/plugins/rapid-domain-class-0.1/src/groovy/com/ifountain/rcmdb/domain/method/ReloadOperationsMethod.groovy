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

import com.ifountain.rcmdb.domain.operation.DomainOperationManager
import com.ifountain.rcmdb.domain.operation.DomainOperationLoadException

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 8, 2008
 * Time: 6:10:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ReloadOperationsMethod extends AbstractRapidDomainMethod{
    List subclasses;
    DomainOperationManager manager;
    Object logger;
    public ReloadOperationsMethod(MetaClass mc, List subclasses, DomainOperationManager manager, Object logger) {
        super(mc);
        this.logger = logger;
        this.manager = manager;
        this.subclasses = subclasses;
    }

    public Object invoke(Object clazz, Object[] arguments) {
        def reloadSubclasses = !(arguments != null && arguments.length == 1 && arguments[0] == false)
        try
        {
            manager.loadOperation();
            if(reloadSubclasses != false )
            {
                subclasses.each{Class subClass->
                    String subClassNameNeedsToBeReloaded = subClass.name;
                    try
                    {
                        subClass.metaClass.invokeStaticMethod (subClass, "reloadOperations", [true] as Object[]);
                    }
                    catch(DomainOperationLoadException exception)
                    {
                        if(exception.getCause() == null || !(exception.getCause() instanceof FileNotFoundException))
                        {
                            logger.info("Operations of child model ${subClassNameNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading.", exception);
                            throw new RuntimeException("Operations of child model ${subClassNameNeedsToBeReloaded} could not reloaded. Please fix the problem an retry reloading. Reason:${exception.toString()}", exception)
                        }
                    }
                }

            }
            logger.warn("Operation for class ${mc.theClass.name} loaded successfully.");
        }
        catch(DomainOperationLoadException exception)
        {
            logger.warn(exception.getMessage());
            logger.info("",exception);
            throw exception;
        }
    }
}