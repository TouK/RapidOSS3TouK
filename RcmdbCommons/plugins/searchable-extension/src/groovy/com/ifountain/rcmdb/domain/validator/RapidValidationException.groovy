package com.ifountain.rcmdb.domain.validator
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 26, 2009
 * Time: 12:11:18 AM
 * To change this template use File | Settings | File Templates.
 */
class RapidValidationException extends Exception{

    public RapidValidationException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static com.ifountain.rcmdb.domain.validator.RapidValidationException propertySetException()
    {
        return new RapidValidationException("Can not set property in validation");
    }
}