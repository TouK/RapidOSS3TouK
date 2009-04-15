package com.ifountain.rcmdb.util

import com.ifountain.comp.utils.WaitAction

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Mar 16, 2009
* Time: 11:59:45 AM
* To change this template use File | Settings | File Templates.
*/
public class ClosureWaitAction implements WaitAction{
    Closure closure;
    public ClosureWaitAction(Closure closure)
    {
        this.closure = closure;
    }
    public void check() {
        closure();
    }

}