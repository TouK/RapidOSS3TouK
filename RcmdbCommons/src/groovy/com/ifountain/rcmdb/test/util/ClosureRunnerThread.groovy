package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 27, 2008
 * Time: 4:11:56 PM
 * To change this template use File | Settings | File Templates.
 */
class ClosureRunnerThread extends Thread{
    Closure closure;
    boolean isFinished;
    boolean isStarted;
    Throwable exception;
    Object result;

    public void run()
    {
        isStarted = true;
        try
        {
            result = closure();
        }
        catch(Throwable t)
        {
            exception = t;
        }
        isFinished = true;

    }
}