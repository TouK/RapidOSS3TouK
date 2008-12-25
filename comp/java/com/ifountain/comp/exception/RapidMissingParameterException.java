package com.ifountain.comp.exception;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 25, 2008
 * Time: 11:10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class RapidMissingParameterException extends RException{
    public RapidMissingParameterException(String parameter) {
        super("Parameter " + parameter + " does not exist.");
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RapidMissingParameterException)
        {
            RapidMissingParameterException other = (RapidMissingParameterException) obj;
            return other.getMessage().equals(getMessage());
        }
        return super.equals(obj);
    }
}
