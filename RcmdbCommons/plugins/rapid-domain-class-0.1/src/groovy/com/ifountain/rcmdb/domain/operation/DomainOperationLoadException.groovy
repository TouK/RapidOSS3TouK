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
package com.ifountain.rcmdb.domain.operation

import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Aug 8, 2008
* Time: 5:12:58 PM
* To change this template use File | Settings | File Templates.
*/
class DomainOperationLoadException extends Exception{
    public DomainOperationLoadException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public static DomainOperationLoadException shouldInheritAbstractDomainOperation()
    {
        return new DomainOperationLoadException("Operation should inherit from ${AbstractDomainOperation.class.name}", null)
    }

    public static DomainOperationLoadException operationFileDoesnotExist(String path)
    {
        return new DomainOperationLoadException("Operation file ${path} does not exist", new FileNotFoundException(path, null));
    }

    public static DomainOperationLoadException operationLoadingIsDisabled(String path)
    {
        return new DomainOperationLoadException("Operation Loading is Disabled.", null);
    }

    public static DomainOperationLoadException compileException(Throwable compileException)
    {
        return new DomainOperationLoadException("Operation could not loaded successfully. Reason:"+compileException.toString(), compileException);
    }

    public static DomainOperationLoadException sameOperationClassIsLoaded()
    {
        return new DomainOperationLoadException("Operation could not loaded successfully. Same class is loaded. Remove operation directory from your classpath.", null);
    }
    public static DomainOperationLoadException operationPropertyIsNotDefined(String modelName)
    {
        return new DomainOperationLoadException("${RapidCMDBConstants.OPERATION_PROPERTY_NAME} property should be defined in model ${modelName}.", null);
    }
}