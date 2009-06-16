package com.ifountain.rcmdb.aol.security;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:28:55 PM
 */
public class SecureSessionException extends Exception{
    public SecureSessionException() { }

    public SecureSessionException(String message) {
        super(message);
    }

    public SecureSessionException(Throwable cause) {
        super(cause);
    }

    public SecureSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
