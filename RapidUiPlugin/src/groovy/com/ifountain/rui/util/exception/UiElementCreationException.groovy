package com.ifountain.rui.util.exception
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 30, 2009
 * Time: 2:43:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class UiElementCreationException extends Exception{
    Class uiClass;
    public UiElementCreationException(Class uiClass, String error)
    {
        super(error);
        this.uiClass = uiClass;
    }
}