package com.ifountain.rcmdb.domain.operation
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
        return new DomainOperationLoadException("Operation could not loaded successfully", new FileNotFoundException(path, null));
    }

    public static DomainOperationLoadException compileException(Throwable compileException)
    {
        return new DomainOperationLoadException("Operation could not loaded successfully", compileException);
    }
}