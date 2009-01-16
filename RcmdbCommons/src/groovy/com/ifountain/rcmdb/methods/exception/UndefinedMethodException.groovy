package com.ifountain.rcmdb.methods.exception
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 16, 2009
 * Time: 8:52:47 AM
 * To change this template use File | Settings | File Templates.
 */
class UndefinedMethodException extends Exception{
    public UndefinedMethodException(String method)
    {
        super("Undefined method "+method);        
    }
}