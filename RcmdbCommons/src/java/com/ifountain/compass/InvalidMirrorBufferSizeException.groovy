package com.ifountain.compass
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 26, 2008
 * Time: 4:36:45 AM
 * To change this template use File | Settings | File Templates.
 */
class InvalidMirrorBufferSizeException extends RuntimeException{

    public InvalidMirrorBufferSizeException(String setting, String value, String reason) {
        super("Invalid mirror dirType ${setting} value ${value}. Reason:${reason}".toString());    //To change body of overridden methods use File | Settings | File Templates.
    }

}