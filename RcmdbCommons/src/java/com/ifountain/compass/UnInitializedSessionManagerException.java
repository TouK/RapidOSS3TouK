package com.ifountain.compass;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 28, 2008
 * Time: 2:42:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnInitializedSessionManagerException extends RuntimeException{
    public UnInitializedSessionManagerException() {
        super("initialize session manager");
    }
}
