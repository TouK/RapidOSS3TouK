package com.ifountain.rcmdb.scripting
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 18, 2009
 * Time: 11:28:56 AM
 * To change this template use File | Settings | File Templates.
 */
class ScriptStoppedByUserException extends Exception {
    public ScriptStoppedByUserException()
    {
        super("!Warning: Script stopped by user.");
    }
    public ScriptStoppedByUserException(String message)
    {
        super("!Warning Script stopped by user. Reason :"+message);
    }

}